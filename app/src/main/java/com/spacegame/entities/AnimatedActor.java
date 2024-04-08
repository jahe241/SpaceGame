package com.spacegame.entities;

import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.Vector2D;

public class AnimatedActor extends AnimatedEntity {
  /**
   * The velocity of the player. This is used to calculate the next position based on where the
   * player is moving. Used for the illusion of movement of the player, while only the actors are
   * moving, based on the player's velocity.
   */
  Vector2D playerVelocity;

  /**
   * Constructor for the AnimatedEntity class. This constructor initializes a new AnimatedEntity
   * object by calling the superclass constructor with the provided parameters. It also initializes
   * the animation frames, animation step, frame duration, and time since last frame.
   *
   * @param textureAtlas The TextureAtlas object that contains the sprites for this entity.
   * @param animationName The name of the animation in the texture atlas to use for this entity.
   * @param x The initial x-coordinate of the entity.
   * @param y The initial y-coordinate of the entity.
   * @param width The width of the entity.
   * @param height The height of the entity.
   * @param frameDuration The duration of each frame in seconds.
   * @param isLooping
   */
  public AnimatedActor(
      TextureAtlas textureAtlas,
      String animationName,
      float x,
      float y,
      float width,
      float height,
      float frameDuration,
      boolean isLooping) {
    super(textureAtlas, animationName, x, y, width, height, frameDuration, isLooping);
    this.collidable = true;
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
