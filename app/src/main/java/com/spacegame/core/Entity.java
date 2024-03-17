package com.spacegame.core;

import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static javax.microedition.khronos.opengles.GL10.GL_FLOAT;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLES;

import android.util.Log;
import android.view.MotionEvent;
import com.spacegame.graphics.EngineRenderer;
import com.spacegame.utils.Vector2D;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Entity {

  int gl_texture_ptr;
  public static final int FLOATS_PER_VERTEX = 6;
  public static final int VERTEX_PER_QUAD = 6;
  private float x; // current x position
  private float y;
  private float destX; // destination x position
  private float destY;
  private float width;
  private float height;
  private float speed = 1000f;
  private float rotationRad = 2f; // storing it in radians reduces the need for frequent conversion

  private FloatBuffer vertexData;

  public Entity(float x, float y, float width, float height, int gl_texture_ptr) {
    this.x = this.destX = x;
    this.y = this.destY = y;
    this.width = width;
    this.height = height;
    this.gl_texture_ptr = gl_texture_ptr;

    // Initialize the vertex data
    float[] initialVertexData = {
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
    };
    this.vertexData =
        ByteBuffer.allocateDirect(initialVertexData.length * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
    // Set vertex data

    vertexData.clear();
    vertexData.put(initialVertexData);
    vertexData.position(0);
  }

  public void draw() {
    // Static pointer not loaded, skip draw frame
    Log.d("Entity", "GL Pointer: " + EngineRenderer.gl_a_Position_ptr);
    if (EngineRenderer.gl_a_Position_ptr == -1) {
      Log.e("Entity", "GL Pointer not set!!");
      return;
    }
    // Give position vertex data to the shader
    vertexData.position(0);
    glVertexAttribPointer(
        EngineRenderer.gl_a_Position_ptr, 4, GL_FLOAT, false, FLOATS_PER_VERTEX * 4, vertexData);
    glEnableVertexAttribArray(EngineRenderer.gl_a_Position_ptr);
    // Give texture vertex data to the shader
    vertexData.position(4);
    glVertexAttribPointer(
        EngineRenderer.gl_a_TexCoordinate_ptr,
        2,
        GL_FLOAT,
        false,
        FLOATS_PER_VERTEX * 4,
        vertexData);
    glEnableVertexAttribArray(EngineRenderer.gl_a_TexCoordinate_ptr);

    // Bind the texture
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, this.gl_texture_ptr);

    // Draw the all triangles
    glDrawArrays(GL_TRIANGLES, 0, 6);
  }

  /**
   * Updates the entity's position based on its destination. It also smoothly adjusts the entity's
   * rotation so that it faces towards its destination as it begins to move.
   *
   * @param deltaTime The time elapsed since the last update.
   */
  private void updatePosition(float deltaTime) {
    float dx = this.destX - this.x;
    float dy = this.destY - this.y;
    float distance = Vector2D.calculateDistance(this.x, this.y, this.destX, this.destY);

    // Calculate target rotation angle towards the destination point
    float targetRotationRad = (float) Math.atan2(dy, dx);

    // Define a small threshold distance, so the entity doesn't overshoot the destination
    // Needed because of floating point precision issues
    float threshold = 10f;

    if (distance > threshold) {
      // Update position
      float step = this.speed / distance * deltaTime;
      this.x += dx * step;
      this.y += dy * step;

      // Smooth rotation towards the target
      // Calculate the shortest angular distance between the current angle and the target angle
      float angleDifference =
          (targetRotationRad - this.rotationRad + (float) Math.PI * 3) % ((float) Math.PI * 2)
              - (float) Math.PI;

      // Adjust rotation speed based on the distance and angle difference to ensure smooth turning
      // The rotation speed could be adjusted to make the turn smoother or more immediate
      float rotationSpeed = 20f; // This can be adjusted for quicker or slower rotations
      this.rotationRad +=
          Math.signum(angleDifference)
              * Math.min(rotationSpeed * deltaTime, Math.abs(angleDifference));
    } else {
      // If the entity is close enough to the destination, set its position to the destination
      this.x = this.destX;
      this.y = this.destY;
    }

    // Ensure rotation is within the range [0, 2π)
    this.rotationRad = (this.rotationRad + (float) Math.PI * 2) % ((float) Math.PI * 2);
  }

  public void update(float delta) {
    Log.d("Entity", "x: " + this.x + " y:" + this.y);
    // Update the entity's position
    this.updatePosition(delta);
    // Update the entity's vertex data
    this.updateVertexData();
  }

  /**
   * Updates the entity's vertex data to reflect its current position, orientation, and size. This
   * includes recalculating the vertices for the two triangles that make up the entity's rectangular
   * shape based on its current position (x, y), size (width, height), and rotation. The method also
   * sets texture coordinates for each vertex. These are static and do not change with the entity's
   * transformation.
   */
  private void updateVertexData() {
    // Calculate the sine and cosine of the rotation angle for efficient use in vertex rotation
    float cosTheta = (float) Math.cos(rotationRad);
    float sinTheta = (float) Math.sin(rotationRad);

    // Adjust vertex data based on current position, size, and rotation
    float[] adjustedVertexData = {
      // Triangle 1
      x - cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X for bottom-left vertex
      y - sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y for bottom-left vertex
      0f,
      1f,
      0f,
      0f, // Texture coordinates remain unchanged
      x + cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X for bottom-right vertex
      y + sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y for bottom-right vertex
      0f,
      1f,
      1f,
      0f, // Texture coordinates remain unchanged
      x - cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X for top-left vertex
      y - sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y for top-left vertex
      0f,
      1f,
      0f,
      1f, // Texture coordinates remain unchanged
      // Triangle 2
      x - cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X for top-left vertex (repeated)
      y - sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y for top-left vertex (repeated)
      0f,
      1f,
      0f,
      1f, // Texture coordinates remain unchanged
      x
          + cosTheta * width / 2
          + sinTheta * height / 2, // Adjusted X for bottom-right vertex (repeated)
      y
          + sinTheta * width / 2
          - cosTheta * height / 2, // Adjusted Y for bottom-right vertex (repeated)
      0f,
      1f,
      1f,
      0f, // Texture coordinates remain unchanged
      x + cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X for top-right vertex
      y + sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y for top-right vertex
      0f,
      1f,
      1f,
      1f // Texture coordinates remain unchanged
    };

    // Reset the buffer to write the new vertex data
    vertexData.clear();
    vertexData.put(adjustedVertexData);
    vertexData.position(0);
  }

  public void onTouch(MotionEvent event) {
    float touchX = event.getX();
    float touchY = event.getY();
    Log.d("Entity", "Setting Destination to touch Event: (" + touchX + ", " + touchY + ')');

    this.setDestination(touchX, touchY);
  }

  public void setDestination(float destX, float destY) {
    this.destX = destX;
    this.destY = destY;
  }
}
