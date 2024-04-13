package com.spacegame.entities;

import androidx.annotation.NonNull;
import com.spacegame.graphics.Sprite;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.Vector2D;

public class Entity extends Quad {

  Animation anim;

  /**
   * Whether the entity has a color overlay applied to its texture. If true, the entity's texture is
   * rendered with the color overlay specified in the colorOverlay array.
   */
  protected boolean hasColorOverlay = false;

  /**
   * The Sprite object associated with the entity. This object contains the size, position, and UV
   * coordinates for the entity's sprite.
   */
  protected Sprite sprite;

  /**
   * The color overlay to apply to the entity's texture. This is an array of four floats
   * representing the RGBA color values to apply to the texture. The values should be in the range
   * [0, 1]. The default color overlay is white (1.0f, 1.0f, 1.0f, 1.0f).
   */
  protected float[] colorOverlay = {1.0f, 1.0f, 1.0f, 1.0f};

  float decelerationFactor = 0.98f;

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
  float acceleration = 20f;

  /**
   * The direction of the entity. This is a unit vector that represents the direction in which the
   * entity will move to.
   */
  Vector2D direction = new Vector2D(0, 0);

  /**
   * The base speed of the entity. This is the speed at which the entity moves when it is not
   * affected by any external forces.
   */
  float baseSpeed = 500;

  /**
   * The TextureAtlas object associated with the entity. This object contains the texture atlas used
   * to render the entity's sprite.
   */
  TextureAtlas textureAtlas; // I'm not sure if we need to keep this here

  /**
   * A flag indicating whether the entity should be discarded by the game loop. If set to true, the
   * entity will be removed from the game loop in the next iteration. Default value is false,
   * meaning the entity is active in the game loop.
   */
  boolean discard = false; // Whether the entity should be by the game-loop

  boolean isVisible = true; // Whether the entity has a texture

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
      this.vbo.setFlagTexture();
    }
    if (textureAtlas != null && spriteName != null) {
      this.sprite = textureAtlas.getSprite(spriteName);
      assert this.sprite != null;
      this.vbo.updateTexture(this.sprite);
    }
  }

  public Entity(
      TextureAtlas textureAtlas,
      float x,
      float y,
      float width,
      float height,
      AnimationOptions anim) {
    super(x, y, width, height);
    assert anim != null;
    this.sprite = textureAtlas.getAnimationSprites(anim.animationTextureName).get(0);
    assert this.sprite != null;
    this.vbo.updateTexture(this.sprite);
    this.anim = new Animation(this, textureAtlas, anim);
  }

  public Entity(
      TextureAtlas textureAtlas, Sprite sprite, float x, float y, float width, float height) {
    super(x, y, width, height);
    assert sprite != null;
    assert textureAtlas != null;
    this.textureAtlas = textureAtlas;
  }

  /**
   * Updates the entity's position, orientation, and vertex data based on the time elapsed since the
   * last update.
   *
   * @param delta The time elapsed since the last update.
   */
  public void update(float delta) {
    this.updatePosition(delta);
    this.updateRotation(delta);
    this.vbo.updateVBOPosition(this.position, this.z_index, this.rotationRad);
    if (this.anim != null) anim.update(delta);
  }

  /**
   * Updates the entity's position based on its destination. It also smoothly adjusts the entity's
   * rotation so that it faces towards its destination as it begins to move.
   *
   * @param deltaTime The time elapsed since the last update.
   */
  void updatePosition(float deltaTime) {
    // Update the velocity based on the acceleration
    this.setVelocity(this.getVelocity().add(this.getDirection().mult(this.getAcceleration())));

    // If direction is zero Vector then decelerate
    if (this.getDirection().length() == 0) {
      if (this.getVelocity().length() < this.getAcceleration() / 2) {
        this.setVelocity(new Vector2D(0, 0));
      } else {
        this.setVelocity(
            this.getVelocity().toSize(this.getVelocity().length() - this.getAcceleration() / 2));
        // this.setVelocity(this.getVelocity().mult(this.getDecelerationFactor()));
      }
    }
    // Limit the velocity to the base speed
    if (this.getVelocity().length() > this.getBaseSpeed()) {
      this.setVelocity(this.getVelocity().toSize(this.getBaseSpeed()));
    }
    this.setPosition(this.getPosition().add(this.getVelocity().mult(deltaTime)));
  }

  /**
   * Calculates the entity's rotation based on its velocity and destination. The entity rotates
   * smoothly towards its destination point.
   *
   * @param deltaTime
   */
  public void updateRotation(float deltaTime) {
    if (this.getDirection().length() == 0) return;
    if (this.velocity.length() == 0) return;

    // Calculate the nextFrameTargetPosition next frame
    Vector2D nextFrameTargetPosition = this.getPosition().add(this.velocity.mult(deltaTime));

    // Calculate target rotation angle towards the destination point
    // If velocity is zero, keep the last rotation angle
    float rotationAngleRad;
    rotationAngleRad = -this.getPosition().calcAngle(nextFrameTargetPosition);

    // Update position

    // Smooth rotation towards the target
    // Calculate the shortest angular distance between the current angle and the target angle
    float angleDifference = rotationAngleRad - this.rotationRad;

    // Adjust rotation speed based on the distance and angle difference to ensure smooth turning
    // The rotation speed could be adjusted to make the turn smoother or more immediate
    this.rotationRad += angleDifference;

    // Ensure rotation is within the range [-π, π]
    if (this.rotationRad >= Math.PI) {
      this.rotationRad -= 2 * Math.PI;
    } else if (this.rotationRad < -Math.PI) {
      this.rotationRad += 2 * Math.PI;
    }
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
   * Sets the velocity of the entity.
   *
   * @param velocity
   */
  public void setVelocity(Vector2D velocity) {
    this.velocity = velocity;
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
   * Gets the direction of the entity.
   *
   * @param direction
   */
  public void setDirection(Vector2D direction) {
    this.direction = direction.normalized();
  }

  public float getAcceleration() {
    return this.acceleration;
  }

  public void setAcceleration(float acc) {
    this.acceleration = acc;
  }

  public float getDecelerationFactor() {
    return this.decelerationFactor;
  }

  public void setDecelerationFactor(float fac) {
    this.decelerationFactor = fac;
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
   * Updates the position of the vertex buffer object (VBO) associated with this entity. This method
   * is typically called when the position or rotation of the entity changes. The updated position
   * and rotation are used to recalculate the vertices of the VBO, which in turn affects how the
   * entity is rendered on the screen.
   */
  public void updatePositionVertex() {
    this.vbo.updateVBOPosition(this.position, this.z_index, this.rotationRad);
  }

  @NonNull
  @Override
  public String toString() {
    String spriteName = this.sprite == null ? "null" : this.sprite.name();
    spriteName += "(" + this.getClass() + ")";
    return "E[" + spriteName + " " + this.position + ", " + this.width + ", " + this.height + "]";
  }

  public float getCurrentSpeed() {
    return this.getVelocity().length();
  }

  public Sprite getSprite() {
    return sprite;
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
    this.vbo.updateTexture(this.sprite);
  }

  /**
   * Sets the sprite of the entity directly. After setting the sprite, the entity's auxiliary data
   * is updated.
   *
   * @param sprite The sprite to set.
   */
  public void setSprite(Sprite sprite) {
    this.sprite = sprite;
    this.vbo.updateTexture(this.sprite);
  }

  public float getX() {
    return this.position.getX();
  }

  public float getY() {
    return this.position.getY();
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
   * Gets the OpenGL texture pointer for the entity.
   *
   * @return
   */
  public int getGl_texture_ptr() {
    return gl_texture_ptr;
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
    this.vbo.updateTexture(this.sprite);
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
   * Disables the color overlay of the entity. The entity's hasColorOverlay flag is set to false.
   */
  public void disableColorOverlay() {
    this.hasColorOverlay = false;
    this.vbo.setFlagTexture();
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
   * Sets the discard flag of the entity. If set to true, the entity will be removed from the game
   * loop in the next iteration.
   *
   * @param discard The value to set the discard flag to.
   */
  public void setDiscard(boolean discard) {
    this.discard = discard;
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
   * Sets the color overlay of the entity. The color overlay is an array of four floats representing
   * the RGBA color values. After setting the color overlay, the entity's hasColorOverlay flag is
   * set to true.
   *
   * @param color The color overlay to set.
   */
  public void setColorOverlay(float[] color) {
    this.colorOverlay = color;
    this.hasColorOverlay = true;
    this.vbo.setColor(color);
    this.vbo.setFlagColorOverlay();
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

  /**
   * Sets the visibility of the entity. If set to true, the entity will be visible. If set to false,
   * the entity will be hidden.
   *
   * @param visible A boolean value indicating whether the entity should be visible.
   */
  public void setVisible(boolean visible) {
    this.isVisible = visible;
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
