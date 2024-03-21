package com.spacegame.core;


import android.util.Log;
import com.spacegame.utils.TextureAtlas;
import com.spacegame.utils.Vector2D;

public class Entity extends Quad {

  /** The pointer to the OpenGL texture that should be used to render this entity. */
  int gl_texture_ptr; // I don't really want to keep this here, but it's the easiest way to get it

  /**
   * The velocity of the entity. This is a vector that represents the direction at which the entity
   * is moving.
   */
  Vector2D velocity = new Vector2D(0, 0);

  /**
   * The acceleration of the entity. This is used for simulating changing directions and speeding up
   * the entity's movement. The acceleration should be used as pixels per second.
   */
  float acceleration = 50;

  /**
   * The direction of the entity. This is a unit vector that represents the direction in which the
   * entity will move to.
   */
  Vector2D direction = new Vector2D(0, 0);

  /** The current speed of the entity. This is the speed at which the entity is currently moving. */
  float currentSpeed = 0f;

  /**
   * The base speed of the entity. This is the speed at which the entity moves when it is not
   * affected by any external forces.
   */
  int baseSpeed = 1000;

  /**
   * The last rotation angle in radians. This is used to determine if the entity's rotation has
   * changed since the last update and to snap to the target angle if close enough.
   */
  float lastRotationRad = 0f;

  /**
   * Whether the entity has a color overlay applied to its texture. If true, the entity's texture is
   * rendered with the color overlay specified in the colorOverlay array.
   */
  protected boolean hasColorOverlay = false;

  /**
   * The color overlay to apply to the entity's texture. This is an array of four floats
   * representing the RGBA color values to apply to the texture. The values should be in the range
   * [0, 1].
   */
  protected boolean hasTexture = false;
  TextureAtlas textureAtlas;
  private int spriteX;
  private int spriteY;
  protected float[] colorOverlay = {1.0f, 1.0f, 1.0f, 1.0f}; // RGBA

  private boolean discard = false; // Whether the entity should be by the game-loop

  // Rewrite Data:
  /**
   * The auxiliary data array for the entity. This array contains additional data for each vertex of
   * the entity's quad. The data is stored in the following format: [Tex U, Tex V, Flag, Color R,
   * Color G, Color B, Color A] for each vertex/corner of the quad.
   */
  float[] auxData =
      new float
          [28]; // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A for each vertex/corner

  public Entity(
      TextureAtlas textureAtlas,
      int spriteX,
      int spriteY,
      float x,
      float y,
      float width,
      float height) {
    super(x, y, width, height);
    this.destX = x;
    this.destY = y;
    if (textureAtlas != null) {
      this.textureAtlas = textureAtlas;
      this.gl_texture_ptr = textureAtlas.getTexturePtr();
    }
    this.spriteX = spriteX;
    this.spriteY = spriteY;
    this.updateauxData();
  }

  public Entity(
      TextureAtlas textureAtlas,
      int spriteX,
      int spriteY,
      float x,
      float y,
      float width,
      float height,
      float[] colorOverlay) {
    this(textureAtlas, spriteX, spriteY, x, y, width, height);
    this.colorOverlay = colorOverlay;
    this.hasColorOverlay = true;
    this.updateauxData();
  }

  /**
   * Updates the entity's auxiliary data array based on its color overlay and texture flag.
   * Implementations should override this method to update the auxiliary data array with the
   * appropriate values for the entity.
   */
  @Override
  protected void updateauxData() {
    float[] uvs = textureAtlas.getUVs(this.spriteX, this.spriteY);
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
            uvs[0], uvs[1], 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            uvs[2], uvs[1], 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            uvs[0], uvs[3], 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            uvs[2], uvs[3], 0.0f, 0.0f, 1.0f, 1.0f, 1.0f
          };
    } else {
      this.auxData =
          new float[] {
            // Flag = 1 for texture + color overlay
            // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A
            uvs[0], uvs[1], 1.0f, colorOverlay[0], colorOverlay[1], colorOverlay[2],
                colorOverlay[3], // Solid red
            uvs[2], uvs[1], 1.0f, colorOverlay[0], colorOverlay[1], colorOverlay[2],
                colorOverlay[3], // Textured white
            uvs[0], uvs[3], 1.0f, colorOverlay[0], colorOverlay[1], colorOverlay[2],
                colorOverlay[3], // Solid green
            uvs[2], uvs[3], 1.0f, colorOverlay[0], colorOverlay[1], colorOverlay[2],
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
    // Update the velocity based on the acceleration
    this.velocity = this.velocity.add(this.direction.mult(this.acceleration));
    // Limit the velocity to the base speed
    if (this.velocity.length() > this.baseSpeed) {
      this.velocity = this.velocity.toSize(this.baseSpeed);
    }

    // Calculate the nextFrameTargetPosition next frame
    Vector2D nextFrameTargetPosition = this.position.add(this.velocity.mult(deltaTime));

    // Calculate target rotation angle towards the destination point
    float roationAngleRad = -this.position.calcAngle(nextFrameTargetPosition);

    // Define a small threshold distance, so the entity doesn't overshoot the destination
    // Needed because of floating point precision issues
    float threshold = 30f;

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

  /**
   * Sets the velocity of the entity.
   *
   * @param velocity
   */
  public void setVelocity(Vector2D velocity) {
    this.velocity = velocity.normalized();
  }

  /**
   * Gets the velocity of the entity.
   *
   * @return
   */
  public Vector2D getVelocity() {
    return this.velocity;
  }

  /**
   * Applies a velocity vector to the entity based on the impact factor. The impact factor is used
   * to scale the velocity vector before it is applied to the entity.
   *
   * @param velocity
   * @param impact
   */
  public void applyVelocity(Vector2D velocity, float impact) {
    this.velocity = this.velocity.add(velocity.mult(impact).normalized()).normalized();
  }

  /**
   * Gets the direction of the entity.
   *
   * @param direction
   */
  public void setDirection(Vector2D direction) {
    this.direction = direction.normalized();
  }

  /**
   * Gets the direction of the entity.
   *
   * @return
   */
  public Vector2D getDirection() {
    return this.direction;
  }

  /**
   * Gets the base speed of the entity.
   *
   * @return
   */
  public float getBaseSpeed() {
    return baseSpeed;
  }

  /**
   * Sets the base speed of the entity.
   *
   * @param baseSpeed
   */
  public void setBaseSpeed(int baseSpeed) {
    this.baseSpeed = baseSpeed;
  }

  /**
   * Gets the auxiliary data array for the entity.
   *
   * @return
   */
  public float[] getAuxData() {
    return auxData;
  }

  /**
   * Gets the OpenGL texture pointer for the entity.
   *
   * @return
   */
  public int getGl_texture_ptr() {
    return gl_texture_ptr;
  }

  public void setColorOverlay(float[] color) {
    this.colorOverlay = color;
    this.hasColorOverlay = true;
  }

  public void disableColorOverlay() {
    this.hasColorOverlay = false;
  }

  public void setHasTexture(boolean hasTexture) {
    this.hasTexture = hasTexture;
  }

  public void setSpriteX(int spriteX) {
    this.spriteX = spriteX;
    this.updateauxData();
  }

  public void setSpriteY(int spriteY) {
    this.spriteY = spriteY;
    this.updateauxData();
  }

  public void setDiscard(boolean discard) {
    this.discard = discard;
  }

  public boolean getDiscard() {
    return this.discard;
  }
}
