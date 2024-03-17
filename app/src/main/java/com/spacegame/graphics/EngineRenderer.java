package com.spacegame.graphics;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.spacegame.R;
import com.spacegame.core.Entity;
import com.spacegame.core.Game;
import com.spacegame.utils.TextResourceReader;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class EngineRenderer implements GLSurfaceView.Renderer {

  //  Entity pepe;

  public static int gl_u_ProjectionMatrix_ptr;
  public static int gl_a_Position_ptr;
  public static int gl_a_TexCoordinate_ptr;

  private long lastFrameTime;
  private Context context;
  private Game game;

  public float[] projectionMatrix = new float[16];

  public int program = 0;

  public EngineRenderer(Context context, Game game) {
    super();
    this.game = game;
    this.context = context;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    // Setting OpenGL Parameters to allow png transparency, we might change this up once we
    // implemented the textuire atlas
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    // Set the background frame color to black
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    // Compile vertex shader code
    String vertexShaderSource =
        TextResourceReader.readTextFileFromResource(this.context, R.raw.simple_vertex_shader);
    int compiledVertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
    // Compile fragment shader code
    String fragmentShaderSource =
        TextResourceReader.readTextFileFromResource(this.context, R.raw.simple_fragment_shader);
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
    this.game.setPlayer(new Entity(500f, 500f, 200f, 100f, pepeTexture));
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

  @Override
  public void onDrawFrame(GL10 gl) {
    // calculate time between frames
    long currentTime = System.nanoTime();
    float deltaTime = (currentTime - lastFrameTime) / 1000000000.0f;
    lastFrameTime = currentTime;

    // Pass the projection matrix to the shader
    glUniformMatrix4fv(gl_u_ProjectionMatrix_ptr, 1, false, this.projectionMatrix, 0);

    gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

    // TODO: Draw the game objects
    // Draw the pepe
    if (this.game.player == null) {
      Log.e("EngineRenderer", "Player is null");
      return;
    }
    this.game.update(deltaTime);
  }
}
