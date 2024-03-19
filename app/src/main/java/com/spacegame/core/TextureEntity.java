package com.spacegame.core;

import android.util.Log;
import com.spacegame.utils.Vector2D;

public class TextureEntity extends Quad {

  int gl_texture_ptr; // I don't really want to keep this here, but it's the easiest way to get it
  private float destX; // destination x position
  private float destY; // destination y position
  private float speed = 1000f; // Speed in pixels per second (I guess, not sure, but it's fast)
  private float lastRotationRad = 0f;
  protected boolean hasColorOverlay = false;

  protected float[] colorOverlay = {1.0f, 1.0f, 1.0f, 1.0f}; // RGBA

  // Rewrite Data:
  private float[] auxData =
      new float
          [28]; // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A for each vertex/corner

  public TextureEntity(float x, float y, float width, float height, int gl_texture_ptr) {
    super(x, y, width, height);
    this.destX = x;
    this.destY = y;
    this.gl_texture_ptr = gl_texture_ptr;
    this.updateauxData();
  }

  public TextureEntity(
      float x, float y, float width, float height, int gl_texture_ptr, float[] colorOverlay) {
    this(x, y, width, height, gl_texture_ptr);
    this.colorOverlay = colorOverlay;
    this.updateauxData();
  }

  @Override
  protected void updateauxData() {
    // EXAMPLE:
    //    float auxData[] = {
    //      // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A
    //        0.0f, 0.0f, 2.0f, 1.0f, 0.0f, 0.0f, 1.0f, // Solid red
    //        1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, // Textured white
    //        0.0f, 1.0f, 2.0f, 0.0f, 1.0f, 0.0f, 1.0f, // Solid green
    //        1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f  // Textured white
    //    }
    if (!hasColorOverlay) {
      this.auxData =
          new float[] {
            // Flag = 0 for texture
            // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A
            0.0f, 0.0f, 2.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 2.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f
          };
    } else {
      this.auxData =
          new float[] {
            // Flag = 1 for texture + color overlay
            // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A
            0.0f, 0.0f, 1.0f, colorOverlay[0], colorOverlay[1], colorOverlay[2],
                colorOverlay[3], // Solid red
            1.0f, 0.0f, 1.0f, colorOverlay[0], colorOverlay[1], colorOverlay[2],
                colorOverlay[3], // Textured white
            0.0f, 1.0f, 2.0f, colorOverlay[0], colorOverlay[1], colorOverlay[2],
                colorOverlay[3], // Solid green
            1.0f, 1.0f, 1.0f, colorOverlay[0], colorOverlay[1], colorOverlay[2],
                colorOverlay[3] // Textured white
          };
    }
  }

  //  public void draw() {
  //    // Static pointer not loaded, skip draw frame
  //    // Log.d("Entity", "GL Pointer: " + EngineRenderer.gl_a_Position_ptr);
  //    if (EngineRenderer.gl_a_Position_ptr == -1) {
  //      Log.e("Entity", "GL Pointer not set!!");
  //      return;
  //    }
  //    // Give position vertex data to the shader
  //    vertexData.position(0);
  //    glVertexAttribPointer(
  //        EngineRenderer.gl_a_Position_ptr, 4, GL_FLOAT, false, FLOATS_PER_VERTEX * 4,
  // vertexData);
  //    glEnableVertexAttribArray(EngineRenderer.gl_a_Position_ptr);
  //    // Give texture vertex data to the shader
  //    vertexData.position(4);
  //    glVertexAttribPointer(
  //        EngineRenderer.gl_a_TexCoordinate_ptr,
  //        2,
  //        GL_FLOAT,
  //        false,
  //        FLOATS_PER_VERTEX * 4,
  //        vertexData);
  //    glEnableVertexAttribArray(EngineRenderer.gl_a_TexCoordinate_ptr);
  //
  //    // Bind the texture
  //    glActiveTexture(GL_TEXTURE0);
  //    glBindTexture(GL_TEXTURE_2D, this.gl_texture_ptr);
  //
  //    // Draw the rectangles using the index buffer
  //    // Note: Using GL_UNSIGNED_SHORT because our indices are stored as short
  //    GLES20.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, indexBuffer);
  //  }

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
    float targetRotationRad = -(float) Math.atan2(dy, dx);

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
      float angleDifference = targetRotationRad - this.rotationRad;
      Log.d("Entity", "Angle Difference: " + Math.toDegrees(angleDifference));
      // angleDifference -=
      // (float) (Math.floor((angleDifference + Math.PI) / (2 * Math.PI)) * (2 * Math.PI));

      // Adjust rotation speed based on the distance and angle difference to ensure smooth turning
      // The rotation speed could be adjusted to make the turn smoother or more immediate
      float rotationSpeed = 20f; // This can be adjusted for quicker or slower rotations
      this.rotationRad +=
          Math.signum(angleDifference)
              * Math.min(rotationSpeed * deltaTime, Math.abs(angleDifference));
    } else {
      // If the entity is close enough to the destination, set its position to the destination
      this.position[0] = this.x = this.destX;
      this.position[1] = this.y = this.destY;
    }

    // Ensure rotation is within the range [-π, π)
    if (this.rotationRad >= Math.PI) {
      this.rotationRad -= 2 * Math.PI;
    } else if (this.rotationRad < -Math.PI) {
      this.rotationRad += 2 * Math.PI;
    }

    if (this.rotationRad != this.lastRotationRad) {
      //      Log.d("Entity", "Rotation in Radians: " + this.rotationRad);
      //      Log.d("Entity", "Rotation in Degrees: " + Math.toDegrees(this.rotationRad));
      //      Log.d("Entity", "Destination Rotation:" + Math.toDegrees(targetRotationRad));
      this.lastRotationRad = this.rotationRad;
    }
  }

  /**
   * Updates the entity's position, orientation, and vertex data based on the time elapsed since the
   * last update.
   *
   * @param delta The time elapsed since the last update.
   */
  public void update(float delta) {
    // Log.d("Entity", "x: " + this.x + " y:" + this.y);
    // Update the entity's position
    this.updatePosition(delta);
    // Update the entity's vertex data
    this.updatePositionData();
  }

  /**
   * Updates the entity's vertex data to reflect its current position, orientation, and size. This
   * includes recalculating the vertices for the two triangles that make up the entity's rectangular
   * shape based on its current position (x, y), size (width, height), and rotation. The method also
   * sets texture coordinates for each vertex. These are static and do not change with the entity's
   * transformation.
   */
  //  void updateVertexData() {
  //    /// MOST LIKELY NOT NEEDED ANYMORE HUZZAHHH!
  //
  //    // Calculate the sine and cosine of the rotation angle for efficient use in vertex rotation
  //    float cosTheta = (float) Math.cos(rotationRad);
  //    float sinTheta = (float) Math.sin(rotationRad);
  //
  //    // Adjust vertex data based on current position, size, and rotation
  //    float[] adjustedVertexData = {
  //      // 0 Bottom-left vertex
  //      x - cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X
  //      y + sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y
  //      0f,
  //      1f,
  //      0f,
  //      0f, // Texture coordinates remain unchanged
  //
  //      // 1 Bottom-right vertex
  //      x + cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X
  //      y - sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y
  //      0f,
  //      1f,
  //      1f,
  //      0f, // Texture coordinates remain unchanged
  //
  //      // 2 Top-left vertex
  //      x - cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X
  //      y + sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y
  //      0f,
  //      1f,
  //      0f,
  //      1f, // Texture coordinates remain unchanged
  //
  //      // 3 Top-right vertex
  //      x + cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X
  //      y - sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y
  //      0f,
  //      1f,
  //      1f,
  //      1f, // Texture coordinates remain unchanged
  //    };
  //
  //    // Reset the buffer to write the new vertex data
  //    vertexData.clear();
  //    vertexData.put(adjustedVertexData);
  //    vertexData.position(0);
  //  }

  public void setDestination(float destX, float destY) {
    this.destX = destX;
    this.destY = destY;
  }

  public float getDestX() {
    return destX;
  }

  public void setDestX(float destX) {
    this.destX = destX;
  }

  public float getDestY() {
    return destY;
  }

  public void setDestY(float destY) {
    this.destY = destY;
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public float[] getAuxData() {
    return auxData;
  }

  public int getGl_texture_ptr() {
    return gl_texture_ptr;
  }
}
