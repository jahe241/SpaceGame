package com.spacegame.entities;

import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.Vector2D;

public class Actor extends Entity {

  /**
   * The velocity of the player. This is used to calculate the next position based on where the
   * player is moving. Used for the illusion of movement of the player, while only the actors are
   * moving, based on the player's velocity.
   */
  Vector2D playerVelocity;

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
  public Actor(
      TextureAtlas textureAtlas, String spriteName, float x, float y, float width, float height) {
    super(textureAtlas, spriteName, x, y, width, height);
  }

  @Override
  public void updatePosition(float delta) {
    this.velocity = this.velocity.add(this.direction.mult(this.acceleration));

    // If direction is zero Vector then decelerate
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
    // Update the position based on the player velocity
    if (this.playerVelocity == null) {
      this.position = this.position.add(this.velocity.mult(delta));
    } else {
      Vector2D overallVelocity = this.velocity.add(this.playerVelocity.inversed());
      this.position = this.position.add(overallVelocity.mult(delta));
    }
  }

  /**
   * Set the player velocity.
   *
   * @param playerVelocity
   */
  public void setPlayerVelocity(Vector2D playerVelocity) {
    this.playerVelocity = playerVelocity;
  }
}
