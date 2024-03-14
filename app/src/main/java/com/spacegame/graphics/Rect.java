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

import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Rect {

  private static final int BYTES_PER_FLOAT = 4;
  private final FloatBuffer vertexData;
  private float x;
  private float y;
  private float width;
  private float height;

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

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public Rect(float x, float y, float width, float height) {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
    vertexData = ByteBuffer
        .allocateDirect(VERTEX_DATA.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();

    setVertexData(x, y, width, height);
  }

  public void setVertexData(float x, float y, float width, float height) {
    float[] adjustedVertexData = {
        // Triangle 1
        x - width / 2, y - height / 2, 0.0f, 1.0f,
        x + width / 2, y - height / 2, 1.0f, 1.0f,
        x - width / 2, y + height / 2, 0.0f, 0.0f,
        // Triangle 2
        x - width / 2, y + height / 2, 0.0f, 0.0f,
        x + width / 2, y - height / 2, 1.0f, 1.0f,
        x + width / 2, y + height / 2, 1.0f, 0.0f
    };

    vertexData.clear();
    vertexData.put(adjustedVertexData);
    vertexData.position(0);
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

  /**
   * This method is used to move the rectangle by updating the x and y coordinates of each vertex.
   * It also updates the x and y instance variables to reflect the new position of the rectangle.
   *
   * @param dx The change in x-coordinate.
   * @param dy The change in y-coordinate.
   */
  public void addVelocity(float dx, float dy) {
    // Create a new array to store the updated vertex data
    float[] updatedVertexData = new float[vertexData.capacity()];

    // Update the x and y coordinates of each vertex
    for (int i = 0; i < vertexData.capacity(); i += 4) {
      updatedVertexData[i] = vertexData.get(i) + dx; // Update x coordinate
      updatedVertexData[i + 1] = vertexData.get(i + 1) + dy; // Update y coordinate
      updatedVertexData[i + 2] = vertexData.get(
          i + 2); // Copy over the existing texture coordinates
      updatedVertexData[i + 3] = vertexData.get(
          i + 3); // Copy over the existing texture coordinates
    }

    // Update the vertex data
    vertexData.clear();
    vertexData.put(updatedVertexData);
    vertexData.position(0);

    // Update the x and y instance variables
    this.x += dx;
    this.y += dy;

    Log.d("Rect", "move: " + vertexData.get(0) + " " + vertexData.get(1));
  }

  public void setColor(float r, float g, float b, float a, int uColorLocation) {
    glUniform4f(uColorLocation, r, g, b, a);
  }

}
