package com.spacegame.core;

import static android.opengl.GLES20.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import com.spacegame.R;
import com.spacegame.graphics.Rect;
import com.spacegame.graphics.ShaderHelper;
import com.spacegame.utils.TextResourceReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SpaceRenderer implements android.opengl.GLSurfaceView.Renderer {

  private static final int BYTES_PER_FLOAT = 4;
  private final FloatBuffer vertexData;
  private final Context context;
  private int program;
  private static final String U_COLOR = "u_Color";
  private int uColorLocation;
  private static final String A_POSITION = "a_Position";
  private int aPositionLocation;
  private Rect rect;


  private int pepeTexture;

  public SpaceRenderer(Context ctx) {
    this.context = ctx;
    float[] tableVerticesWithTriangles = {
        // Table: Triangle 1
        -0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f,
        // Table: Triangle 2
        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,
        // Line 1
        -0.5f, 0f,
        0.5f, 0f,
        // Mallets
        0f, -0.25f,
        0f, 0.25f,
        // Point in 0,0
        0f, 0f
    };
    vertexData = ByteBuffer
        .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    vertexData.put(tableVerticesWithTriangles);
  }

  private int loadTexture(Context context, int resourceId) {
    final int[] textureObjectIds = new int[1];
    glGenTextures(1, textureObjectIds, 0);

    if (textureObjectIds[0] == 0) {
      return 0;
    }

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = false;

    final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

    if (bitmap == null) {
      glDeleteTextures(1, textureObjectIds, 0);
      return 0;
    }

    glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

    ((Bitmap) bitmap).recycle();

    glGenerateMipmap(GL_TEXTURE_2D);

    glBindTexture(GL_TEXTURE_2D, 0);

    return textureObjectIds[0];
  }

  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    String vertexShaderSource = TextResourceReader.readTextFileFromResource(context,
        R.raw.simple_vertex_shader);
    String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context,
        R.raw.simple_fragment_shader);

    int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
    int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

    program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

    ShaderHelper.validateProgram(program);

    glUseProgram(program);

    uColorLocation = glGetUniformLocation(program, U_COLOR);
    aPositionLocation = glGetAttribLocation(program, A_POSITION);
    vertexData.position(0);
    glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, vertexData);
    glEnableVertexAttribArray(aPositionLocation);
    pepeTexture = loadTexture(context, R.drawable.peepo);
    rect = new Rect(-0, -0, 0.2f, 0.3f);
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    // Set the OpenGL viewport to fill the entire surface.
    // This should not be necessary as the App is locked to portrait mode
    gl.glViewport(0, 0, width, height);
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    // Clear the rendering surface.
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

    // Draw the table
    glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
    glDrawArrays(GL_TRIANGLES, 0, 6);

    // Draw the center line
    glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
    glDrawArrays(GL_LINES, 6, 2);

    // Draw the first mallet blue
    glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
    glDrawArrays(GL_POINTS, 8, 1);

    // Draw the second mallet red
    glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
    glDrawArrays(GL_POINTS, 9, 1);

    // Draw the first sprite here
    glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
    glDrawArrays(GL_POINTS, 11, 1);
    int aTextureCoordinatesLocation = glGetAttribLocation(program, "a_TexCoordinate");
    rect.draw(aPositionLocation, aTextureCoordinatesLocation, pepeTexture);

    rect.move((rect.getX() + 0.0005f), rect.getY() + 0.0005f);

  }

}
