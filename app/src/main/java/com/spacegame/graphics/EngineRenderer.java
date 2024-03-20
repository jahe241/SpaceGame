package com.spacegame.graphics;

import static android.opengl.GLES20.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.spacegame.R;
import com.spacegame.core.Player;
import com.spacegame.core.Game;
import com.spacegame.core.TextureEntity;
import com.spacegame.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class EngineRenderer implements GLSurfaceView.Renderer {
  // GL Pointers
  public static int gl_u_ProjectionMatrix_ptr;
  public static int gl_a_Position_ptr;
  public static int gl_a_TexCoordinate_ptr;

  // Render Buffers
  private FloatBuffer positionBuffer, auxBuffer;
  private ShortBuffer indexBuffer;

  // CONSTANTS according to our vertex design
  private static final int POSITION_DATA_SIZE = 3; // (x, y, z)
  private static final int AUX_DATA_SIZE = 7; // (u, v, flag, colorR, colorG, colorB, colorA)

  private long lastFrameTime = System.nanoTime(); // We have to initialize it here
  private final Context context;
  private final Game game;

  public float[] projectionMatrix = new float[16];

  public int program = 0;

  public EngineRenderer(Context context, Game game) {
    super();
    this.game = game;
    this.context = context;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    Log.d("EngineRenderer", "Surface created on Thread: " + Thread.currentThread().getName());
    // Setting OpenGL Parameters to allow png transparency, we might change this up once we
    // implemented the textuire atlas
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // Basically: 1 - source alpha

    // Enable depth testing (Z Coordinates)
    glEnable(GL10.GL_DEPTH_TEST);

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

    // Load the textures
    int pepeTexture = loadTexture(R.drawable.peepo);
    if (pepeTexture == 0) {
      Log.e("EngineRenderer", "Failed to load texture");
      return;
    }
    Log.i("EngineRenderer", "Pepe texture loaded successfully!");
    this.game.textureAtlasPointer = pepeTexture;
    this.game.start();
  }

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

  private int[] loadTextures() {
    return new int[1];
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    // Set the OpenGL viewport to fill the entire surface.
    gl.glViewport(0, 0, width, height);

    // Set the projection matrix
    if (width > height) {
      // Landscape
      android.opengl.Matrix.orthoM(projectionMatrix, 0, 0, width, height, 0, -1f, 1f);
    } else {
      // Portrait or square
      android.opengl.Matrix.orthoM(projectionMatrix, 0, 0, width, height, 0, -1f, 1f);
    }
    // Log the SurfaceView size
    Log.d("SurfaceView", "Width: " + width + " Height: " + height);
  }

  private FloatBuffer createFloatBuffer(float[] data) {
    FloatBuffer buffer =
        ByteBuffer.allocateDirect(data.length * 4) /* Allocate a direct buffer to hold float data */
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
    buffer.put(data).position(0); /* Load data into buffer */
    return buffer;
  }

  private ShortBuffer createShortBuffer(short[] data) {
    ShortBuffer buffer =
        ByteBuffer.allocateDirect(data.length * 2) /* Allocate a direct buffer to hold short data */
            .order(ByteOrder.nativeOrder())
            .asShortBuffer();
    buffer.put(data).position(0); /* Load data into buffer */
    return buffer;
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    // calculate time between frames
    long currentTime = System.nanoTime();
    float deltaTime = (currentTime - lastFrameTime) / 1000000000.0f;
    lastFrameTime = currentTime;
    int lastTexture = -1;

    // Clear the rendering surface
    gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

    // Pass the projection matrix to the shader
    glUniformMatrix4fv(gl_u_ProjectionMatrix_ptr, 1, false, this.projectionMatrix, 0);

    // test pointers TODO: move into constants section, once working
    int positionHandle = glGetAttribLocation(program, "a_Position");
    int auxHandle1 = glGetAttribLocation(program, "a_TexCoordFlag");
    int auxHandle2 = glGetAttribLocation(program, "a_Color");

    // FIXME: Proof of Concept, will cause a overflow if we have too many entities
    // TODO: PROOF OF CONCEPT: we'll have to group the according to their texture / color / overlay
    for (TextureEntity entity : this.game.getEntities()) {
      if (entity == null) {
        continue;
      }
      if (entity.getAuxData() == null) {
        Log.e("EngineRenderer", "Entity has no aux data:");
        continue;
      }
      if (entity.getPositionData() == null) {
        Log.e("EngineRenderer", "Entity has no position data:");
        continue;
      }
      if (entity.getIndices() == null) {
        Log.e("EngineRenderer", "Entity has no indices:");
        continue;
      }
      // Bind the texture
      if (lastTexture != entity.getGl_texture_ptr()) {
        lastTexture = entity.getGl_texture_ptr();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, entity.getGl_texture_ptr());
      }
      // Set up vertex data
      positionBuffer = createFloatBuffer(entity.getPositionData());
      auxBuffer = createFloatBuffer(entity.getAuxData());
      indexBuffer = createShortBuffer(entity.getIndices());

      // Bind the Aux data (color and stuff)
      auxBuffer.position(0); // Start from the beginning of your auxBuffer
      glVertexAttribPointer(
          auxHandle1,
          4,
          GL_FLOAT,
          false,
          AUX_DATA_SIZE * 4,
          auxBuffer); // 4 components per vertex for this attribute
      glEnableVertexAttribArray(auxHandle1);

      auxBuffer.position(4); // Skip the first four floats to reach the start of the color data
      glVertexAttribPointer(
          auxHandle2,
          3,
          GL_FLOAT,
          false,
          AUX_DATA_SIZE * 4,
          auxBuffer); // 3 components per vertex for this attribute, adjust if using vec4
      glEnableVertexAttribArray(auxHandle2);

      // Bind position data
      positionBuffer.position(0);
      glVertexAttribPointer(
          positionHandle,
          POSITION_DATA_SIZE,
          GL_FLOAT,
          false,
          POSITION_DATA_SIZE * 4,
          positionBuffer);
      glEnableVertexAttribArray(positionHandle);

      // Bind the indeces?
      glDrawElements(GL_TRIANGLES, entity.getIndices().length, GL_UNSIGNED_SHORT, indexBuffer);
    }

    //    this.game.update(deltaTime);
  }
}
