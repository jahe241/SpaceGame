package com.spacegame.graphics;

import static android.opengl.GLES20.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import com.spacegame.R;
import com.spacegame.core.Game;
import com.spacegame.core.GameInterface;
import com.spacegame.entities.ColorEntity;
import com.spacegame.entities.Entity;
import com.spacegame.utils.TextResourceReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import org.xmlpull.v1.XmlPullParserException;

public class EngineRenderer implements GLSurfaceView.Renderer {
  // GL Pointers
  public int program = 0;
  public static int gl_u_ProjectionMatrix_ptr;
  public static int gl_a_Position_ptr;
  public static int gl_a_TexCoordinate_ptr;

  public float[] projectionMatrix = new float[16];

  // Render Buffers
  private static final int VERTICES_IN_BUFFER = 32;
  private static final int BATCH_SIZE = VERTICES_IN_BUFFER / 4;
  private FloatBuffer vertexBuffer;
  private int indexBufferId;
  private int vertexBufferId;
  ShortBuffer indexBuffer;

  private long lastFrameTime = System.nanoTime(); // We have to initialize it here
  private final Context context;
  private final Game game;
  private final GameInterface gameInterface;
  private TextureAtlas textureAtlas;

  private int drawCallsCurrentFrame = 0;

  public EngineRenderer(Context context, Game game, GameInterface gameInterface) {
    super();
    this.game = game;
    this.context = context;
    this.gameInterface = gameInterface;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    Log.d("EngineRenderer", "Surface created on Thread: " + Thread.currentThread().getName());

    // Compile vertex shader code
    String vertexShaderSource =
        TextResourceReader.readTextFileFromResource(this.context, R.raw.vertex_shader);
    int compiledVertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
    // Compile fragment shader code
    String fragmentShaderSource =
        TextResourceReader.readTextFileFromResource(this.context, R.raw.fragment_shader);
    int compiledFragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

    // Link the shaders into a program
    this.program = ShaderHelper.linkProgram(compiledVertexShader, compiledFragmentShader);
    Log.d("EngineRenderer", "Program: " + this.program);
    if (this.program == 0) {
      Log.e("EngineRenderer", "Failed to link program");
      return;
    }

    // Check if the program is valid
    boolean programValidate = ShaderHelper.validateProgram(this.program);
    if (!programValidate) {
      Log.e("EngineRenderer", "Program is not valid");
      return;
    }

    glUseProgram(this.program);
    // Get the pointers to the shader variables
    EngineRenderer.gl_a_Position_ptr = glGetAttribLocation(this.program, "a_Position");
    EngineRenderer.gl_a_TexCoordinate_ptr = glGetAttribLocation(this.program, "a_TexCoordinate");
    EngineRenderer.gl_u_ProjectionMatrix_ptr =
        glGetUniformLocation(this.program, "u_ProjectionMatrix");

    Log.d(
        "EngineRenderer",
        "GL Pointers: "
            + EngineRenderer.gl_a_Position_ptr
            + " "
            + EngineRenderer.gl_a_TexCoordinate_ptr
            + " "
            + EngineRenderer.gl_u_ProjectionMatrix_ptr);
    try {
      if (this.textureAtlas == null) {
        loadTextures();
      }
    } catch (XmlPullParserException | IOException e) {
      e.printStackTrace();
    }
    this.initializeBuffers();
    this.gameInterface.start();
    this.game.start();
  }

  private void initializeBuffers() {
    // Number of floats needed for the vertex data
    int totalVertexFloats = VERTICES_IN_BUFFER * VertexBufferObject.STRIDE;

    // Create a new float buffer for the vertex data
    FloatBuffer vertexDataBuffer = createFloatBuffer(new float[totalVertexFloats]);

    // Precompute the indices for quads based on the number of vertices
    short[] indices = makeIndexArray(EngineRenderer.BATCH_SIZE);
    ShortBuffer indexDataBuffer = createShortBuffer(indices);

    // Generate new buffer objects for both vertex and index buffers
    int[] buffers = new int[2];
    glGenBuffers(2, buffers, 0);
    vertexBufferId = buffers[0]; // First buffer is for vertices
    indexBufferId = buffers[1]; // Second buffer is for indices

    // Bind and upload data to the vertex buffer
    glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
    glBufferData(
        GL_ARRAY_BUFFER, vertexDataBuffer.capacity() * 4, vertexDataBuffer, GL_STATIC_DRAW);

    // Bind and upload data to the index buffer
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
    glBufferData(
        GL_ELEMENT_ARRAY_BUFFER, indexDataBuffer.capacity() * 2, indexDataBuffer, GL_STATIC_DRAW);

    // Unbind buffers to avoid unintentional modifications (optional, but recommended for clarity)
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    createVertexBuffer();
    createIndexBuffer();
  }

  public void createVertexBuffer() {
    // Create a new float buffer for the aux data
    vertexBuffer = createFloatBuffer(new float[VERTICES_IN_BUFFER * VertexBufferObject.STRIDE]);
  }

  // dynamically create a index buffer and precompute the indices for a quad, based on the number of
  // vertices
  private void createIndexBuffer() {
    // Create a new short buffer for the index data
    short[] indices = makeIndexArray(EngineRenderer.BATCH_SIZE);
    // Generate a new buffer object
    int[] buffers = new int[1];
    glGenBuffers(1, buffers, 0);
    indexBufferId = buffers[0];

    // Bind the buffer object
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);

    // Create a new short buffer for the index data
    ShortBuffer ib = createShortBuffer(indices);
    this.indexBuffer = ib;
    // send the index data to the GPU
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib.capacity() * 2, ib, GL_STATIC_DRAW);
  }

  @NonNull
  private static short[] makeIndexArray(int quadCount) {
    short[] indices = new short[quadCount * 6];
    for (int i = 0; i < quadCount; i++) {
      indices[i * 6] = (short) (i * 4);
      indices[i * 6 + 1] = (short) (i * 4 + 1);
      indices[i * 6 + 2] = (short) (i * 4 + 2);
      indices[i * 6 + 3] = (short) (i * 4 + 2);
      indices[i * 6 + 4] = (short) (i * 4 + 1);
      indices[i * 6 + 5] = (short) (i * 4 + 3);
    }
    System.out.println("Indices: " + Arrays.toString(indices));
    return indices;
  }

  /**
   * Loads the texture atlas from a resource and creates a TextureAtlas object. The method first
   * attempts to load a texture from the provided resource ID. If the texture loading is successful,
   * it creates a new TextureAtlas object with the loaded texture and the provided sprite and atlas
   * dimensions. The created TextureAtlas object is then stored in the textureAtlas field of the
   * EngineRenderer class and the game object.
   */
  private void loadTextures() throws XmlPullParserException, IOException {
    int atlasPtr = loadTexture(R.drawable.atlas);
    if (atlasPtr == 0) {
      Log.e("EngineRenderer", "Failed to load texture");
      return;
    }
    Log.i("EngineRenderer", "Pepe texture loaded successfully!");

    this.textureAtlas = new TextureAtlas(context, R.raw.atlas, atlasPtr);
    game.textureAtlas = this.textureAtlas;
  }

  /**
   * Loads a texture from a resource.
   *
   * <p>This method generates a new OpenGL texture object, then attempts to create a Bitmap from the
   * provided resource ID. If the Bitmap creation is successful, the Bitmap is loaded into the
   * texture object. The method then returns the OpenGL ID of the texture object. If any step fails,
   * the method cleans up any created resources and returns 0.
   *
   * @param resourceId The resource ID of the texture to load.
   * @return The OpenGL ID of the loaded texture, or 0 if the texture could not be loaded.
   */
  private int loadTexture(int resourceId) {
    // Generate a new texture object and save id in textureObjectIds
    final int[] textureObjectIds = new int[1];
    glGenTextures(1, textureObjectIds, 0);

    // If the texture object was not created, return 0
    if (textureObjectIds[0] == 0) {
      return 0;
    }

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = false;

    // Create Bitmap from resource return 0 if failed
    final Bitmap bitmap =
        BitmapFactory.decodeResource(this.context.getResources(), resourceId, options);
    if (bitmap == null) {
      glDeleteTextures(1, textureObjectIds, 0);
      return 0;
    }

    glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

    // Generate Mipmap for the texture
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    // Load the bitmap into the texture
    GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
    glGenerateMipmap(GL_TEXTURE_2D);

    // Free the bitmap and unbind the texture
    glBindTexture(GL_TEXTURE_2D, 0);
    bitmap.recycle();

    return textureObjectIds[0];
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    // Set the OpenGL viewport to fill the entire surface
    gl.glViewport(0, 0, width, height);

    // Set the projection matrix
    if (width > height) {
      // Landscape
      android.opengl.Matrix.orthoM(projectionMatrix, 0, 0, width, height, 0, -10f, 20f);
    } else {
      // Portrait or square
      android.opengl.Matrix.orthoM(projectionMatrix, 0, 0, width, height, 0, -10f, 20f);
    }
    // Log the SurfaceView size
    Log.d("SurfaceView", "Width: " + width + " Height: " + height);
  }

  /**
   * Create a new float buffer and load the data into it sets the position of the buffer to 0 Buffer
   * size is 4 (float size in bytes) * data.length
   *
   * @param data The data to load into the buffer
   * @return The buffer with the data loaded into it
   */
  private FloatBuffer createFloatBuffer(float[] data) {
    System.out.println("Making buffer for " + data.length + " floats");
    FloatBuffer buffer =
        ByteBuffer.allocateDirect(data.length * 4) /* Allocate a direct buffer to hold float data */
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
    buffer.put(data).position(0); /* Load data into buffer */
    return buffer;
  }

  /**
   * Create a new short buffer and load the data into it sets the position of the buffer to 0 Buffer
   * size is 2 (short size in bytes) * data.length
   *
   * @param data The data to load into the buffer
   * @return The buffer with the data loaded into it
   */
  private ShortBuffer createShortBuffer(short[] data) {
    System.out.println("Making buffer for " + data.length + " shorts");
    ShortBuffer buffer =
        ByteBuffer.allocateDirect(data.length * 2) /* Allocate a direct buffer to hold short data */
            .order(ByteOrder.nativeOrder())
            .asShortBuffer();
    buffer.put(data).position(0); /* Load data into buffer */
    return buffer;
  }

  private List<Entity> fetchEntities() {
    // fetch all visible entities, sorted by ther Z-Value - streams are fun
    List<Entity> batch =
        Stream.concat(
                this.game.getEntities().stream(),
                this.gameInterface.getInterfaceElements().stream())
            .filter(Entity::isVisible)
            .sorted((a, b) -> Float.compare(a.getZ(), b.getZ()))
            .collect(Collectors.toList());
    return batch;
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    drawCallsCurrentFrame = 0;
    // Clear the rendering surface
    gl.glClearColor(0f, 0f, 0f, 0f);
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

    // Pass the projection matrix to the shader
    glUniformMatrix4fv(gl_u_ProjectionMatrix_ptr, 1, false, this.projectionMatrix, 0);

    var batch = fetchEntities();
    renderBatchedEntities(gl, batch);
  }

  public void renderBatchedEntities(GL10 gl, List<Entity> batch) {
    // Step 1: Split the list into sublists of size BATCH_SIZE
    for (int i = 0; i < batch.size(); i += BATCH_SIZE) {
      List<Entity> subBatch = batch.subList(i, Math.min(batch.size(), i + BATCH_SIZE));

      // Step 2: Concatenate the vertex arrays of all entities in the sublist
      float[] concatenatedVertexArrays = new float[VERTICES_IN_BUFFER * VertexBufferObject.STRIDE];
      int arrayIndex = 0;

      for (Entity entity : subBatch) {
        float[] entityVertexArray = entity.vbo().getVertexArray();
        for (float v : entityVertexArray) {
          concatenatedVertexArrays[arrayIndex++] = v;
        }
      }
      // Fill the rest of the array with zeros
      Arrays.fill(concatenatedVertexArrays, arrayIndex, concatenatedVertexArrays.length, 0f);
      System.out.println();
      // Step 3: Render the concatenated vertex arrays
      renderVertexArray(gl, concatenatedVertexArrays);
    }
  }

  private void renderVertexArray(GL10 gl, float[] batchArray) {
    System.out.println(Arrays.toString(batchArray));
    // Reset the buffer's position to the beginning
    // Bind the VBO before setting up attribute pointers
    glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
    vertexBuffer.clear();

    // Update the buffer with the new vertex data
    vertexBuffer.put(batchArray);
    vertexBuffer.position(0);

    glBufferSubData(GL_ARRAY_BUFFER, 0, batchArray.length * 4, FloatBuffer.wrap(batchArray));

    // Get the attribute locations
    int gl_a_TexCoordinate_ptr = glGetAttribLocation(program, "a_TexCoord");
    int gl_a_Flag_ptr = glGetAttribLocation(program, "a_Flag");
    int gl_a_Color_ptr = glGetAttribLocation(program, "a_Color");
    System.out.println(
        "GL Pointers: "
            + ""
            + gl_a_Position_ptr
            + " "
            + gl_a_TexCoordinate_ptr
            + " "
            + gl_a_Flag_ptr
            + " "
            + gl_a_Color_ptr);

    // Enable the attribute arrays
    final int floatSize = 4; // Size of a float in bytes
    // Enable the vertex attribute arrays
    glEnableVertexAttribArray(gl_a_Position_ptr);
    glEnableVertexAttribArray(gl_a_TexCoordinate_ptr);
    glEnableVertexAttribArray(gl_a_Flag_ptr);
    glEnableVertexAttribArray(gl_a_Color_ptr);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, textureAtlas.getTexturePtr());
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

    // Set the vertex attribute pointers
    glVertexAttribPointer(
        gl_a_Position_ptr, 3, GL_FLOAT, false, VertexBufferObject.STRIDE * floatSize, 0);
    System.out.println("glVertexAttribPointer Position:" + glGetError());

    glVertexAttribPointer(
        gl_a_TexCoordinate_ptr,
        2,
        GL_FLOAT,
        false,
        VertexBufferObject.STRIDE * floatSize,
        floatSize * VertexBufferObject.OFFSET_TEXTURE);
    System.out.println("glVertexAttribPointer TexCoord:" + glGetError());

    glVertexAttribPointer(
        gl_a_Flag_ptr,
        1,
        GL_FLOAT,
        false,
        VertexBufferObject.STRIDE * floatSize,
        floatSize * VertexBufferObject.OFFSET_FLAG);
    System.out.println("glVertexAttribPointer Flag:" + glGetError());

    glVertexAttribPointer(
        gl_a_Color_ptr,
        4,
        GL_FLOAT,
        false,
        VertexBufferObject.STRIDE * floatSize,
        floatSize * VertexBufferObject.OFFSET_COLOR);
    System.out.println("glVertexAttribPointer Color:" + glGetError());

    // Bind the index buffer
    System.out.println("Drawing: ");
    // Draw the vertices as triangles using the index buffer

    int indicesCount = EngineRenderer.BATCH_SIZE * 6; // 6 indices per quad
    glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_SHORT, 0);

    System.out.println("glDrawElements:" + glGetError());
    // Disable the vertex array
    glDisableVertexAttribArray(gl_a_Position_ptr);
    glDisableVertexAttribArray(gl_a_TexCoordinate_ptr);
    glDisableVertexAttribArray(gl_a_Flag_ptr);
    glDisableVertexAttribArray(gl_a_Color_ptr);
  }

  private void drawEntities(GL10 gl, Entity entity) {
    //    int lastTexture = -1;
    //    // test pointers TODO: move into constants section, once working
    //    int positionHandle = glGetAttribLocation(program, "a_Position");
    //    int auxHandle1 = glGetAttribLocation(program, "a_TexCoordFlag");
    //    int auxHandle2 = glGetAttribLocation(program, "a_Color");
    //    if (entity == null) {
    //      return;
    //    }
    //    if (entity.getAuxData() == null) {
    //      Log.e("EngineRenderer", "Entity has no aux data:");
    //      return;
    //    }
    //    if (entity.getVertexPositionData() == null) {
    //      Log.e("EngineRenderer", "Entity has no position data:");
    //      return;
    //    }
    //    if (entity.getIndices() == null) {
    //      Log.e("EngineRenderer", "Entity has no indices:");
    //      return;
    //    }
    //
    //    // Bind the texture
    //    if (lastTexture != entity.getGl_texture_ptr()) {
    //      lastTexture = entity.getGl_texture_ptr();
    //      glActiveTexture(GL_TEXTURE0);
    //      glBindTexture(GL_TEXTURE_2D, entity.getGl_texture_ptr());
    //      glEnable(GL_BLEND);
    //      glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    //      //        gl.glEnable(GL10.GL_DEPTH_TEST);
    //      //        gl.glDepthFunc(GL10.GL_LESS);
    //    }
    //    // Set up vertex data
    //    positionBuffer = createFloatBuffer(entity.getVertexPositionData());
    //    auxBuffer = createFloatBuffer(entity.getAuxData());
    //    indexBuffer = createShortBuffer(entity.getIndices());
    //
    //    // Bind the Aux data (color and stuff)
    //    auxBuffer.position(0); // Start from the beginning of your auxBuffer
    //    glVertexAttribPointer(
    //        auxHandle1,
    //        4,
    //        GL_FLOAT,
    //        false,
    //        AUX_DATA_SIZE * 4,
    //        auxBuffer); // 4 components per vertex for this attribute
    //    glEnableVertexAttribArray(auxHandle1);
    //
    //    auxBuffer.position(4); // Skip the first four floats to reach the start of the color data
    //    glVertexAttribPointer(
    //        auxHandle2,
    //        3,
    //        GL_FLOAT,
    //        false,
    //        AUX_DATA_SIZE * 4,
    //        auxBuffer); // 3 components per vertex for this attribute, adjust if using vec4
    //    glEnableVertexAttribArray(auxHandle2);
    //
    //    // Bind position data
    //    positionBuffer.position(0);
    //    glVertexAttribPointer(
    //        positionHandle,
    //        POSITION_DATA_SIZE,
    //        GL_FLOAT,
    //        false,
    //        POSITION_DATA_SIZE * 4,
    //        positionBuffer);
    //    glEnableVertexAttribArray(positionHandle);
    //
    //    glDrawElements(GL_TRIANGLES, entity.getIndices().length, GL_UNSIGNED_SHORT, indexBuffer);
  }

  private void drawColorEntities(GL10 gl, ColorEntity entity) {
    //    // test pointers TODO: move into constants section, once working
    //    int positionHandle = glGetAttribLocation(program, "a_Position");
    //    int auxHandle1 = glGetAttribLocation(program, "a_TexCoordFlag");
    //    int auxHandle2 = glGetAttribLocation(program, "a_Color");
    //    if (entity == null) {
    //      return;
    //    }
    //    if (entity.getAuxData() == null) {
    //      Log.e("EngineRenderer", "Entity has no aux data:");
    //      return;
    //    }
    //    if (entity.getVertexPositionData() == null) {
    //      Log.e("EngineRenderer", "Entity has no position data:");
    //      return;
    //    }
    //    if (entity.getIndices() == null) {
    //      Log.e("EngineRenderer", "Entity has no indices:");
    //      return;
    //    }
    //    // make sure the texture is unbound
    //    glBindTexture(GL_TEXTURE_2D, 0);
    //    glEnable(GL_BLEND);
    //    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    //    //        gl.glEnable(GL10.GL_DEPTH_TEST);
    //    //        gl.glDepthFunc(GL10.GL_LESS);
    //
    //    // Set up vertex data
    //    positionBuffer = createFloatBuffer(entity.getVertexPositionData());
    //    auxBuffer = createFloatBuffer(entity.getAuxData());
    //    indexBuffer = createShortBuffer(entity.getIndices());
    //
    //    // Bind the Aux data (color and stuff)
    //    auxBuffer.position(0); // Start from the beginning of your auxBuffer
    //    glVertexAttribPointer(
    //        auxHandle1,
    //        4,
    //        GL_FLOAT,
    //        false,
    //        AUX_DATA_SIZE * 4,
    //        auxBuffer); // 4 components per vertex for this attribute
    //    glEnableVertexAttribArray(auxHandle1);
    //
    //    auxBuffer.position(4); // Skip the first four floats to reach the start of the color data
    //    glVertexAttribPointer(
    //        auxHandle2,
    //        3,
    //        GL_FLOAT,
    //        false,
    //        AUX_DATA_SIZE * 4,
    //        auxBuffer); // 3 components per vertex for this attribute, adjust if using vec4
    //    glEnableVertexAttribArray(auxHandle2);
    //
    //    // Bind position data
    //    positionBuffer.position(0);
    //    glVertexAttribPointer(
    //        positionHandle,
    //        POSITION_DATA_SIZE,
    //        GL_FLOAT,
    //        false,
    //        POSITION_DATA_SIZE * 4,
    //        positionBuffer);
    //    glEnableVertexAttribArray(positionHandle);
    //
    //    glDrawElements(GL_TRIANGLES, entity.getIndices().length, GL_UNSIGNED_SHORT, indexBuffer);
  }
}
