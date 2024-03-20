package com.spacegame.core;

import com.spacegame.utils.Vector2D;

public class TextureEntity extends Quad {

  int gl_texture_ptr; // I don't really want to keep this here, but it's the easiest way to get it
  private Vector2D velocity = new Vector2D(0, 0);
  private float baseSpeed = 1000f; // Speed in pixels per second (I guess, not sure, but it's fast)
  private float lastRotationRad = 0f;
  protected boolean hasColorOverlay = false;

  protected float[] colorOverlay = {1.0f, 1.0f, 1.0f, 1.0f}; // RGBA

  // Rewrite Data:
  private float[] auxData =
      new float
          [28]; // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A for each vertex/corner

  public TextureEntity(float x, float y, float width, float height, int gl_texture_ptr) {
    super(x, y, width, height);
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
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f
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
            0.0f, 1.0f, 1.0f, colorOverlay[0], colorOverlay[1], colorOverlay[2],
                colorOverlay[3], // Solid green
            1.0f, 1.0f, 1.0f, colorOverlay[0], colorOverlay[1], colorOverlay[2],
                colorOverlay[3] // Textured white
          };
    }
  }

  /**
   * Updates the entity's position based on its destination. It also smoothly adjusts the entity's
   * rotation so that it faces towards its destination as it begins to move.
   *
   * @param deltaTime The time elapsed since the last update.
   */
  private void updatePosition(float deltaTime) {

    // Calculate the nextFrameTargetPosition next frame
    Vector2D direction = this.velocity.mult(deltaTime * this.baseSpeed);
    Vector2D nextFrameTargetPosition = this.position.add(direction);

    // Calculate target rotation angle towards the destination point
    float roationAngleRad = -this.position.calcAngle(nextFrameTargetPosition);

    // Define a small threshold distance, so the entity doesn't overshoot the destination
    // Needed because of floating point precision issues
    float threshold = 10f;

    // Update position
    this.position = nextFrameTargetPosition;

    // Smooth rotation towards the target
    // Calculate the shortest angular distance between the current angle and the target angle
    float angleDifference = roationAngleRad - this.rotationRad;
    // Log.d("Entity", "Angle Difference: " + Math.toDegrees(angleDifference));
    angleDifference -=
        (float) (Math.floor((angleDifference + Math.PI) / (2 * Math.PI)) * (2 * Math.PI));

    // Adjust rotation speed based on the distance and angle difference to ensure smooth turning
    // The rotation speed could be adjusted to make the turn smoother or more immediate
    float rotationSpeed = 20f; // This can be adjusted for quicker or slower rotations
    this.rotationRad +=
        Math.signum(angleDifference)
            * Math.min(rotationSpeed * deltaTime, Math.abs(angleDifference));

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
    this.updateVertexPositionData();
  }

  public void setVelocity(Vector2D velocity) {
    this.velocity = velocity.normalized();
  }

  public void applyVelocity(Vector2D velocity) {
    this.velocity = this.velocity.add(velocity).normalized();
  }

  public float getBaseSpeed() {
    return baseSpeed;
  }

  public void setBaseSpeed(float baseSpeed) {
    this.baseSpeed = baseSpeed;
  }

  public float[] getAuxData() {
    return auxData;
  }

  public int getGl_texture_ptr() {
    return gl_texture_ptr;
  }
}
