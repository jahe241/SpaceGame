package com.spacegame.entities;

import com.spacegame.graphics.Sprite;
import com.spacegame.graphics.TextureAtlas;
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
  float baseSpeed = 1000;

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

  /**
   * The TextureAtlas object associated with the entity. This object contains the texture atlas used
   * to render the entity's sprite.
   */
  TextureAtlas textureAtlas; // I'm not sure if we need to keep this here

  /**
   * The Sprite object associated with the entity. This object contains the size, position, and UV
   * coordinates for the entity's sprite.
   */
  Sprite sprite;

  /**
   * The color overlay to apply to the entity's texture. This is an array of four floats
   * representing the RGBA color values to apply to the texture. The values should be in the range
   * [0, 1]. The default color overlay is white (1.0f, 1.0f, 1.0f, 1.0f).
   */
  protected float[] colorOverlay = {1.0f, 1.0f, 1.0f, 1.0f};

  /**
   * A flag indicating whether the entity should be discarded by the game loop. If set to true, the
   * entity will be removed from the game loop in the next iteration. Default value is false,
   * meaning the entity is active in the game loop.
   */
  boolean discard = false; // Whether the entity should be by the game-loop

  boolean isVisible = true; // Whether the entity has a texture

  // Rewrite Data:
  /**
   * The auxiliary data array for the entity. This array contains additional data for each vertex of
   * the entity's quad. The data is stored in the following format: [Tex U, Tex V, Flag, Color R,
   * Color G, Color B, Color A] for each vertex/corner of the quad.
   */
  float[] auxData =
      new float
          [28]; // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A for each vertex/corner

  /**
   * Constructor for the Entity class. This constructor initializes a new Entity object by setting
   * its position, size, texture atlas, and sprite. If the texture atlas is not null, it sets the
   * texture atlas and OpenGL texture pointer of the entity. If both the texture atlas and sprite
   * name are not null, it sets the sprite of the entity and updates its auxiliary data.
   *
   * @param textureAtlas The TextureAtlas object to use for the entity. This object contains the
   *     texture atlas used to render the entity's sprite.
   * @param spriteName The name of the sprite to use for the entity. The sprite is retrieved from
   *     the provided texture atlas.
   * @param x The initial x-coordinate of the entity.
   * @param y The initial y-coordinate of the entity.
   * @param width The width of the entity.
   * @param height The height of the entity.
   */
  public Entity(
      TextureAtlas textureAtlas, String spriteName, float x, float y, float width, float height) {
    super(x, y, width, height);
    if (textureAtlas != null) {
      this.textureAtlas = textureAtlas;
      this.gl_texture_ptr = textureAtlas.getTexturePtr();
    }
    if (textureAtlas != null && spriteName != null) {
      this.sprite = textureAtlas.getSprite(spriteName);
      assert this.sprite != null;
      this.updateauxData();
    }
  }

  /**
   * Updates the entity's auxiliary data array based on its color overlay and texture flag.
   * Implementations should override this method to update the auxiliary data array with the
   * appropriate values for the entity.
   */
  @Override
  protected void updateauxData() {
    if (hasColorOverlay) {
      this.auxData =
          new float[] {
            // Flag = 1 for texture * color overlay
            // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A
            this.sprite.uvs()[0], this.sprite.uvs()[1], 1.0f, colorOverlay[0], colorOverlay[1],
                colorOverlay[2], colorOverlay[3],
            this.sprite.uvs()[2], this.sprite.uvs()[1], 1.0f, colorOverlay[0], colorOverlay[1],
                colorOverlay[2], colorOverlay[3],
            this.sprite.uvs()[0], this.sprite.uvs()[3], 1.0f, colorOverlay[0], colorOverlay[1],
                colorOverlay[2], colorOverlay[3],
            this.sprite.uvs()[2], this.sprite.uvs()[3], 1.0f, colorOverlay[0], colorOverlay[1],
                colorOverlay[2], colorOverlay[3]
          };
    } else {
      this.auxData =
          new float[] {
            // Flag = 0.0 for texture
            // Tex U, Tex V, Flag, Color R, Color G, Color B, Color Alpha
            this.sprite.uvs()[0], this.sprite.uvs()[1], 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            this.sprite.uvs()[2], this.sprite.uvs()[1], 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            this.sprite.uvs()[0], this.sprite.uvs()[3], 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            this.sprite.uvs()[2], this.sprite.uvs()[3], 0.0f, 0.0f, 1.0f, 1.0f, 1.0f
          };
    }
  }

  /**
   * Updates the entity's position based on its destination. It also smoothly adjusts the entity's
   * rotation so that it faces towards its destination as it begins to move.
   *
   * @param deltaTime The time elapsed since the last update.
   */
  void updatePosition(float deltaTime) {
    // Update the velocity based on the acceleration
    this.velocity = this.velocity.add(this.direction.mult(this.acceleration));

    // If direction is zero Vector then deaccelerate
    if (this.direction.length() == 0) {
      if (this.velocity.length() < this.acceleration * 2) {
        this.velocity = new Vector2D(0, 0);
      } else {
        this.velocity = this.velocity.mult(1 - 1 / this.acceleration);
      }
    }

    // Limit the velocity to the base speed
    if (this.velocity.length() > this.baseSpeed) {
      this.velocity = this.velocity.toSize(this.baseSpeed);
    }

    // Calculate the nextFrameTargetPosition next frame
    Vector2D nextFrameTargetPosition = this.position.add(this.velocity.mult(deltaTime));

    // Calculate target rotation angle towards the destination point
    // If velocity is zero, keep the last rotation angle
    float roationAngleRad;
    if (this.velocity.length() == 0) roationAngleRad = this.lastRotationRad;
    else roationAngleRad = -this.position.calcAngle(nextFrameTargetPosition);

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

  /**
   * Sets the sprite of the entity based on the sprite name. The sprite is retrieved from the
   * entity's texture atlas. After setting the sprite, the entity's auxiliary data is updated.
   *
   * @param spriteName The name of the sprite to set.
   */
  public void setSprite(String spriteName) {
    this.sprite = textureAtlas.getSprite(spriteName);
    assert this.sprite != null;
    this.updateauxData();
  }

  /**
   * Sets the sprite of the entity based on the sprite name and a given texture atlas. The sprite is
   * retrieved from the given texture atlas. After setting the sprite and texture atlas, the
   * entity's auxiliary data is updated.
   *
   * @param textureAtlas The texture atlas to retrieve the sprite from.
   * @param spriteName The name of the sprite to set.
   */
  public void setSprite(TextureAtlas textureAtlas, String spriteName) {
    this.textureAtlas = textureAtlas;
    this.gl_texture_ptr = textureAtlas.getTexturePtr();
    this.sprite = textureAtlas.getSprite(spriteName);
    assert this.sprite != null;
    this.updateauxData();
  }

  /**
   * Sets the sprite of the entity directly. After setting the sprite, the entity's auxiliary data
   * is updated.
   *
   * @param sprite The sprite to set.
   */
  protected void setSprite(Sprite sprite) {
    this.sprite = sprite;
    this.updateauxData();
  }

  /**
   * Sets the texture atlas of the entity. The OpenGL texture pointer is also updated based on the
   * given texture atlas.
   *
   * @param textureAtlas The texture atlas to set.
   */
  public void setTextureAtlas(TextureAtlas textureAtlas) {
    this.textureAtlas = textureAtlas;
    this.gl_texture_ptr = textureAtlas.getTexturePtr();
  }

  /**
   * Sets the color overlay of the entity. The color overlay is an array of four floats representing
   * the RGBA color values. After setting the color overlay, the entity's hasColorOverlay flag is
   * set to true.
   *
   * @param color The color overlay to set.
   */
  public void setColorOverlay(float[] color) {
    this.colorOverlay = color;
    this.hasColorOverlay = true;
    this.updateauxData();
  }

  /**
   * Disables the color overlay of the entity. The entity's hasColorOverlay flag is set to false.
   */
  public void disableColorOverlay() {
    this.hasColorOverlay = false;
  }

  /**
   * Sets the hasTexture flag of the entity. This flag indicates whether the entity has a texture.
   *
   * @param hasTexture The value to set the hasTexture flag to.
   */
  public void setHasTexture(boolean hasTexture) {
    this.hasTexture = hasTexture;
  }

  /**
   * Sets the discard flag of the entity. If set to true, the entity will be removed from the game
   * loop in the next iteration.
   *
   * @param discard The value to set the discard flag to.
   */
  public void setDiscard(boolean discard) {
    this.discard = discard;
  }

  /**
   * Gets the discard flag of the entity. If the discard flag is true, the entity will be removed
   * from the game loop in the next iteration.
   *
   * @return The value of the discard flag.
   */
  public boolean getDiscard() {
    return this.discard;
  }

  /**
   * Returns the color overlay of the entity. The color overlay is an array of four floats
   * representing the RGBA color values.
   *
   * @return An array of four floats representing the RGBA color values of the entity's color
   *     overlay.
   */
  public float[] getColorOverlay() {
    return colorOverlay;
  }

  /**
   * Sets the visibility of the entity. If set to true, the entity will be visible. If set to false,
   * the entity will be hidden.
   *
   * @param visible A boolean value indicating whether the entity should be visible.
   */
  public void setVisible(boolean visible) {
    this.isVisible = visible;
  }

  /**
   * Returns the visibility of the entity. If the entity is visible, this method returns true. If
   * the entity is hidden, this method returns false.
   *
   * @return A boolean value indicating whether the entity is visible.
   */
  public boolean isVisible() {
    return this.isVisible;
  }

  /** Hides the entity. This method sets the visibility of the entity to false. */
  public void hide() {
    this.isVisible = false;
  }

  /** Shows the entity. This method sets the visibility of the entity to true. */
  public void show() {
    this.isVisible = true;
  }
}
