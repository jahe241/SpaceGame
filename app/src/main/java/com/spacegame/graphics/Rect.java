package com.spacegame.graphics;

import static android.opengl.GLES10.glActiveTexture;
import static android.opengl.GLES10.glBindTexture;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;

import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Rect {

  // TODO: REFACTOR THIS TO ENTITY
  private static final float SPEED = 1f; // Adjust this value to control the speed of the transition

  private static final int BYTES_PER_FLOAT = 4;
  private final FloatBuffer vertexData;
  private float x;
  private float y;
  private float width;
  private float height;

  private float destinationX;
  private float destinationY;
  private float rotationAngle = 0f;
  private float currentAngle = 0f; // Angle in degrees

  // Define the vertices for two triangles that make up a rectangle
  // Add texture coordinates for each vertex
  private static final float[] VERTEX_DATA = {
    // Triangle 1
    -0.5f, -0.5f, 0f, 1f, 0.0f, 1.0f,
    0.5f, -0.5f, 0f, 1f, 1.0f, 1.0f,
    -0.5f, 0.5f, 0f, 1f, 0.0f, 0.0f,
    // Triangle 2
    -0.5f, 0.5f, 0f, 1f, 0.0f, 0.0f,
    0.5f, -0.5f, 0f, 1f, 1.0f, 1.0f,
    0.5f, 0.5f, 0f, 1f, 1.0f, 0.0f

    /*
    -0.5f, -0.5f, 0.0f, 1.0f,
    0.5f, -0.5f, 1.0f, 1.0f,
    -0.5f, 0.5f, 0.0f, 0.0f,
    // Triangle 2
    -0.5f, 0.5f, 0.0f, 0.0f,
    0.5f, -0.5f, 1.0f, 1.0f,
    0.5f, 0.5f, 1.0f, 0.0f
    */
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
    vertexData =
        ByteBuffer.allocateDirect(VERTEX_DATA.length * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    setVertexData(x, y, width, height);
  }

  public void setDestination(float x, float y) {
    this.destinationX = x;
    this.destinationY = y;
  }

  public void setVertexData(float x, float y, float width, float height) {
    float[] adjustedVertexData = {
      // Triangle 1
      x - width / 2,
      y - height / 2,
      0f,
      1f,
      0f,
      0f,
      x + width / 2,
      y - height / 2,
      0f,
      1f,
      1f,
      0f,
      x - width / 2,
      y + height / 2,
      0f,
      1f,
      0f,
      1f,
      // Triangle 2
      x - width / 2,
      y + height / 2,
      0f,
      1f,
      0f,
      1f,
      x + width / 2,
      y - height / 2,
      0f,
      1f,
      1f,
      0f,
      x + width / 2,
      y + height / 2,
      0f,
      1f,
      1f,
      1f
      /*
      // Triangle 1
      x - width / 2, y - height / 2, 0.0f, 0.0f,
      x + width / 2, y - height / 2, 1f,  0.0f,
      x - width / 2, y + height / 2, 1f, 1.0f,
      // Triangle 2
      x - width / 2, y + height / 2, 1f, 1.0f,
      x + width / 2, y - height / 2, 1f, 0.0f,
      x + width / 2, y + height / 2, 1f, 1.0f
           */

    };

    vertexData.clear();
    vertexData.put(adjustedVertexData);
    vertexData.position(0);
  }

  // Add texture coordinates for each vertex
  public void draw(int aPositionLocation, int aTextureCoordinatesLocation, int texture) {
    Log.d("PLAYER COORDS", "x: " + x + " y:" + y);

    vertexData.position(0);
    glVertexAttribPointer(aPositionLocation, 4, GL_FLOAT, false, 24, vertexData);
    glEnableVertexAttribArray(aPositionLocation);

    vertexData.position(4);
    glVertexAttribPointer(aTextureCoordinatesLocation, 2, GL_FLOAT, false, 24, vertexData);
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
    for (int i = 0; i < vertexData.capacity(); i += 6) {
      updatedVertexData[i] = vertexData.get(i) + dx; // Update x coordinate
      updatedVertexData[i + 1] = vertexData.get(i + 1) + dy; // Update y coordinate
      updatedVertexData[i + 2] =
          vertexData.get(i + 2); // Copy over the existing texture coordinates
      updatedVertexData[i + 3] =
          vertexData.get(i + 3); // Copy over the existing texture coordinates
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

  public void goTo(float goX, float goY, float deltaTime) {
    float dx = goX - this.x;
    float dy = goY - this.y;
    // Calculate the angle to the destination
    float targetAngle = (float) Math.toDegrees(Math.atan2(dy, dx));

    // Determine the rotation necessary from the current angle to the target angle
    // This could be optimized based on the direction of rotation needed (clockwise or
    // counterclockwise)
    float angleDifference = targetAngle - this.currentAngle;
    // Normalize the angle difference to be within -180 to 180 for proper rotation
    while (angleDifference > 180) {
      angleDifference -= 360;
    }
    while (angleDifference < -180) {
      angleDifference += 360;
    }

    // Apply rotation speed limit here if necessary (e.g., limit how fast the rectangle can rotate
    // per time step)
    float rotationSpeed = 60f; // degrees per second, adjust as needed
    float angleChange = rotationSpeed * deltaTime;
    if (Math.abs(angleDifference) < angleChange) {
      this.currentAngle = targetAngle; // Close enough, snap to the target angle
    } else {
      this.currentAngle +=
          Math.signum(angleDifference) * angleChange; // Rotate towards the target angle
    }

    // Move the rectangle towards the destination
    this.x += dx * SPEED * deltaTime;
    this.y += dy * SPEED * deltaTime;

    // Update the vertex data and apply rotation
    setVertexDataWithRotation(this.x, this.y, this.width, this.height, this.currentAngle);
  }

  public void setVertexDataWithRotation(float x, float y, float width, float height, float angle) {
    // Conversion from degrees to radians for math functions
    float angleInRadians = (float) Math.toRadians(angle);

    float[] rotatedVertexData = new float[VERTEX_DATA.length];
    for (int i = 0; i < VERTEX_DATA.length; i += 6) {
      // Apply rotation around the center (0, 0) then translate
      float originalX = VERTEX_DATA[i] * width;
      float originalY = VERTEX_DATA[i + 1] * height;

      rotatedVertexData[i] =
          x
              + (originalX * (float) Math.cos(angleInRadians)
                  - originalY * (float) Math.sin(angleInRadians));
      rotatedVertexData[i + 1] =
          y
              + (originalX * (float) Math.sin(angleInRadians)
                  + originalY * (float) Math.cos(angleInRadians));
      rotatedVertexData[i + 2] = VERTEX_DATA[i + 2];
      rotatedVertexData[i + 3] = VERTEX_DATA[i + 3];
      rotatedVertexData[i + 4] = VERTEX_DATA[i + 4]; // Copy texture coordinates
      rotatedVertexData[i + 5] = VERTEX_DATA[i + 5];
    }

    vertexData.clear();
    vertexData.put(rotatedVertexData);
    vertexData.position(0);
  }

  public void setColor(float r, float g, float b, float a, int uColorLocation) {
    glUniform4f(uColorLocation, r, g, b, a);
  }
}
