package com.spacegame.graphics;

import static android.opengl.GLES10.glActiveTexture;
import static android.opengl.GLES10.glBindTexture;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glVertexAttribPointer;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Quad {

  private static final int BYTES_PER_FLOAT = 4;
  private final FloatBuffer vertexData;

  // Define the vertices for two triangles that make up a rectangle
  // Add texture coordinates for each vertex
  private static final float[] VERTEX_DATA = {
      // Triangle 1
      -0.5f, -0.5f, 0.0f, 1.0f,
      0.5f, -0.5f, 1.0f, 1.0f,
      -0.5f, 0.5f, 0.0f, 0.0f,
      // Triangle 2
      -0.5f, 0.5f, 0.0f, 0.0f,
      0.5f, -0.5f, 1.0f, 1.0f,
      0.5f, 0.5f, 1.0f, 0.0f
  };

  public Quad() {
    vertexData = ByteBuffer
        .allocateDirect(VERTEX_DATA.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    vertexData.put(VERTEX_DATA);
  }

  // Add texture coordinates for each vertex
  public void draw(int aPositionLocation, int aTextureCoordinatesLocation, int texture) {
    vertexData.position(0);
    glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 16, vertexData);
    glEnableVertexAttribArray(aPositionLocation);

    vertexData.position(2);
    glVertexAttribPointer(aTextureCoordinatesLocation, 2, GL_FLOAT, false, 16, vertexData);
    glEnableVertexAttribArray(aTextureCoordinatesLocation);

    // Bind the texture to this unit
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture);

    // Draw the quad
    glDrawArrays(GL_TRIANGLES, 0, 6);
  }

  public void setColor(float r, float g, float b, float a, int uColorLocation) {
    glUniform4f(uColorLocation, r, g, b, a);
  }

}
