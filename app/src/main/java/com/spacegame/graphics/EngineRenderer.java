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
import java.util.stream.Stream;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import org.xmlpull.v1.XmlPullParserException;

public class EngineRenderer implements GLSurfaceView.Renderer {
  // Buffer Constants, adjust to change the size of the buffers
  private static final int VERTICES_IN_BUFFER = 131072; // 32k quads, bit overkill TODO: adjust
  private static final int BATCH_SIZE = VERTICES_IN_BUFFER / 4;

  // GL Pointers
  public int gl_program_ptr = 0;
  public static int gl_u_ProjectionMatrix_ptr;
  public static int gl_a_Position_ptr;
  public static int gl_a_TexCoordinate_ptr;
  public static int gl_a_Flag_ptr;
  public static int gl_a_Color_ptr;
  public float[] projectionMatrix = new float[16];

  // Render Buffers
  private FloatBuffer vertexBuffer;
  ShortBuffer indexBuffer;

  // Buffer IDs
  private int indexBufferId;
  private int vertexBufferId;

  // Other Attributes
  private final Context context;
  private final Game game;
  private final GameInterface gameInterface;
  private TextureAtlas textureAtlas;

  // Stats TODO: actually use these
  private long lastFrameTime = System.nanoTime();
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

    // Load the shaders and create the program
    this.gl_program_ptr =
        ShaderHelper.linkProgram(
            ShaderHelper.compileVertexShader(
                TextResourceReader.readTextFileFromResource(this.context, R.raw.vertex_shader)),
            ShaderHelper.compileFragmentShader(
                TextResourceReader.readTextFileFromResource(this.context, R.raw.fragment_shader)));

    if (this.gl_program_ptr == 0 || !ShaderHelper.validateProgram(this.gl_program_ptr)) {
      Log.e("EngineRenderer", "Program is not valid");
      return;
    }

    glUseProgram(this.gl_program_ptr);

    // Get the attribute locations
    gl_a_Position_ptr = glGetAttribLocation(this.gl_program_ptr, "a_Position");
    gl_a_TexCoordinate_ptr = glGetAttribLocation(gl_program_ptr, "a_TexCoord");
    gl_a_Flag_ptr = glGetAttribLocation(gl_program_ptr, "a_Flag");
    gl_a_Color_ptr = glGetAttribLocation(gl_program_ptr, "a_Color");

    // Get the uniform locations
    gl_u_ProjectionMatrix_ptr = glGetUniformLocation(this.gl_program_ptr, "u_ProjectionMatrix");

    Log.d(
        "EngineRenderer",
        "Shader Pointers: "
            + gl_program_ptr
            + " "
            + gl_a_Position_ptr
            + " "
            + gl_a_TexCoordinate_ptr
            + " "
            + gl_a_Flag_ptr
            + " "
            + gl_a_Color_ptr
            + " "
            + gl_u_ProjectionMatrix_ptr);

    // Load texture atlas and initialize buffers
    try {
      loadTextures();
    } catch (XmlPullParserException | IOException e) {
      throw new RuntimeException(e);
    }
    this.initializeBuffers();

    // after everything is set up, start the game & game interface Threads
    this.gameInterface.start();
    this.game.start();
  }

  /**
   * This method is used to initialize the buffers for vertex and index data. It creates two buffers
   * and assigns their IDs to the instance variables vertexBufferId and indexBufferId. It then
   * prepares and uploads vertex data to the vertex buffer, and index data to the index buffer.
   * Finally, it unbinds the buffers, which is not necessary but is considered good practice.
   */
  private void initializeBuffers() {
    // Create buffers for vertex and index data
    int[] buffers = new int[2];
    glGenBuffers(2, buffers, 0);
    vertexBufferId = buffers[0];
    indexBufferId = buffers[1];

    // Prepare and upload vertex data
    vertexBuffer = createFloatBuffer(new float[VERTICES_IN_BUFFER * VertexBufferObject.STRIDE]);
    glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
    glBufferData(
        GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL_STATIC_DRAW);

    // Prepare and upload index data
    indexBuffer = createShortBuffer(makeIndexArray(EngineRenderer.BATCH_SIZE));
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
    glBufferData(
        GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * Short.BYTES, indexBuffer, GL_STATIC_DRAW);

    // Unbind the buffers (technically not necessary, but good practice)
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    // Log the buffer sizes
    Log.d(
        "EngineRenderer",
        "Initialized Buffers: Vertex Buffer Capacity "
            + (vertexBuffer.capacity()
                * 4.0f
                / (1024 * 1024)) // Multiply by 4 because a float is 4 bytes
            + " MB Index Buffer Capacity: "
            + (indexBuffer.capacity()
                * 2.0f
                / (1024 * 1024)) // Multiply by 2 because a short is 2 bytes
            + " MB");
  }

  /**
   * This method generates an array of indices for rendering quads in OpenGL. Each quad is
   * represented by 6 indices (2 triangles per quad), so the size of the array is 6 times the number
   * of quads. The indices are arranged in such a way that they represent the vertices of the quads
   * in a counter-clockwise order. This is important for face culling in OpenGL.
   *
   * @param quadCount The number of quads to generate indices for.
   * @return An array of indices for rendering the quads.
   */
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
    ShortBuffer buffer =
        ByteBuffer.allocateDirect(data.length * 2) /* Allocate a direct buffer to hold short data */
            .order(ByteOrder.nativeOrder())
            .asShortBuffer();
    buffer.put(data).position(0); /* Load data into buffer */
    return buffer;
  }

  /**
   * This method fetches all visible entities from the game and game interface, sorts them by their
   * Z-Value and returns them as a list. The Z-Value is used to determine the order in which
   * entities are drawn, with lower Z-Values being drawn first. This method uses Java 8 streams to
   * perform the operations.
   *
   * @return A list of all visible entities, sorted by their Z-Value.
   */
  private List<Entity> fetchEntities() {
    // fetch all visible entities, sorted by there Z-Value - streams are fun
    return Stream.concat(
            this.game.getEntities().stream(), this.gameInterface.getInterfaceElements().stream())
        .filter(Entity::isVisible)
        .sorted((a, b) -> Float.compare(a.getZ(), b.getZ()))
        .collect(Collectors.toList());
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    drawCallsCurrentFrame = 0;
    // Clear the rendering surface
    gl.glClearColor(0f, 0f, 0f, 1f);
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

    // Pass the projection matrix to the shader
    glUniformMatrix4fv(gl_u_ProjectionMatrix_ptr, 1, false, this.projectionMatrix, 0);

    var batch = fetchEntities();

    // Bind the texture atlas
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, textureAtlas.getTexturePtr());

    renderBatchedEntities(batch);

    // here we could unbind the texture atlas, but thats not necessary I think, since we only use
    // one texture
  }

  /**
   * This method is used to render a batch of entities. The entities are provided as a list, and the
   * method processes them in sub-batches of a size defined by the BATCH_SIZE constant. For each
   * sub-batch, the method concatenates the vertex arrays of all entities, fills the rest of the
   * array with zeros, and then renders the concatenated vertex arrays.
   *
   * @param batch The list of entities to be rendered.
   */
  public void renderBatchedEntities(List<Entity> batch) {
    // Split the list into sublists of size BATCH_SIZE
    for (int i = 0; i < batch.size(); i += BATCH_SIZE) {
      List<Entity> subBatch = batch.subList(i, Math.min(batch.size(), i + BATCH_SIZE));

      // Concatenate the vertex arrays of all entities in the sublist
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
      // Step 3: Render the concatenated vertex arrays
      renderVertexArray(concatenatedVertexArrays);
    }
  }

  private void renderVertexArray(float[] batchArray) {
    // Update the buffer with the new vertex data
    glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
    vertexBuffer.clear();
    vertexBuffer.put(batchArray).position(0);
    glBufferSubData(
        GL_ARRAY_BUFFER, 0, batchArray.length * Float.BYTES, FloatBuffer.wrap(batchArray));

    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

    // Set the vertex attribute pointers
    glEnableVertexAttribArray(gl_a_Position_ptr);
    glVertexAttribPointer(
        gl_a_Position_ptr, 3, GL_FLOAT, false, VertexBufferObject.STRIDE * Float.BYTES, 0);

    glEnableVertexAttribArray(gl_a_TexCoordinate_ptr);
    glVertexAttribPointer(
        gl_a_TexCoordinate_ptr,
        2,
        GL_FLOAT,
        false,
        VertexBufferObject.STRIDE * Float.BYTES,
        Float.BYTES * VertexBufferObject.OFFSET_TEXTURE);

    glEnableVertexAttribArray(gl_a_Flag_ptr);
    glVertexAttribPointer(
        gl_a_Flag_ptr,
        1,
        GL_FLOAT,
        false,
        VertexBufferObject.STRIDE * Float.BYTES,
        Float.BYTES * VertexBufferObject.OFFSET_FLAG);

    glEnableVertexAttribArray(gl_a_Color_ptr);
    glVertexAttribPointer(
        gl_a_Color_ptr,
        4,
        GL_FLOAT,
        false,
        VertexBufferObject.STRIDE * Float.BYTES,
        Float.BYTES * VertexBufferObject.OFFSET_COLOR);

    // Bind the index buffer and draw the vertices as triangles using the index buffer
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
    glDrawElements(GL_TRIANGLES, EngineRenderer.BATCH_SIZE * 6, GL_UNSIGNED_SHORT, 0);
    drawCallsCurrentFrame++;

    // Disable the vertex array
    glDisableVertexAttribArray(gl_a_Position_ptr);
    glDisableVertexAttribArray(gl_a_TexCoordinate_ptr);
    glDisableVertexAttribArray(gl_a_Flag_ptr);
    glDisableVertexAttribArray(gl_a_Color_ptr);
  }

  /**
   * This method is used to draw a single entity on the screen. It sets up the vertex data, enables
   * the vertex attribute arrays, and then draws the elements. After drawing, it disables the vertex
   * attribute arrays to clean up.
   *
   * @param entity The entity to be drawn.
   */
  private void drawEntity(Entity entity) {
    // I did not change this method, but I think it should be correct
    // Set up vertex data
    FloatBuffer vb = createFloatBuffer(entity.vbo().getVertexArray());
    ShortBuffer ib =
        createShortBuffer(makeIndexArray(1)); // Ensure this correctly generates your index buffer

    // Position data
    glEnableVertexAttribArray(gl_a_Position_ptr);
    vb.position(VertexBufferObject.OFFSET_POSITION);
    glVertexAttribPointer(
        gl_a_Position_ptr, 3, GL_FLOAT, false, VertexBufferObject.STRIDE * Float.BYTES, vb);

    // Texture data
    glEnableVertexAttribArray(gl_a_TexCoordinate_ptr);
    vb.position(VertexBufferObject.OFFSET_TEXTURE);
    glVertexAttribPointer(
        gl_a_TexCoordinate_ptr, 2, GL_FLOAT, false, VertexBufferObject.STRIDE * Float.BYTES, vb);

    // Flag data
    glEnableVertexAttribArray(gl_a_Flag_ptr);
    vb.position(VertexBufferObject.OFFSET_FLAG);
    glVertexAttribPointer(
        gl_a_Flag_ptr, 1, GL_FLOAT, false, VertexBufferObject.STRIDE * Float.BYTES, vb);

    // Color data
    glEnableVertexAttribArray(gl_a_Color_ptr);
    vb.position(VertexBufferObject.OFFSET_COLOR);
    glVertexAttribPointer(
        gl_a_Color_ptr, 4, GL_FLOAT, false, VertexBufferObject.STRIDE * Float.BYTES, vb);

    // Draw the elements
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, ib);

    // Cleanup: Disable the vertex attribute arrays
    glDisableVertexAttribArray(gl_a_Position_ptr);
    glDisableVertexAttribArray(gl_a_TexCoordinate_ptr);
    glDisableVertexAttribArray(gl_a_Flag_ptr);
    glDisableVertexAttribArray(gl_a_Color_ptr);
  }
}
