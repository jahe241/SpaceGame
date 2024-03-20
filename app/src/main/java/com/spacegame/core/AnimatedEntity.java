package com.spacegame.core;

import android.util.Log;
import com.spacegame.utils.TextureAtlas;

public class AnimatedEntity extends Entity {
  // Animation fields
  private int[]
      animationFrames; // The indices of the sprites in the animation, must be divisible by 2!
  private int animationStep; // The current frame of the animation
  private float frameDuration; // The duration of each frame in seconds
  private float timeSinceLastFrame; // The time elapsed since the last frame change

  /**
   * Constructor for AnimatedEntity
   *
   * @param textureAtlas The texture atlas containing the sprites for the entity
   * @param spriteX The x position of the first animation frame sprite in the texture atlas grid
   * @param spriteY The y position of the first animation frame sprite in the texture atlas grid
   * @param x The x position of the entity (center)
   * @param y The y position of the entity (center)
   * @param width The width of the entity
   * @param height The height of the entity
   */
  private AnimatedEntity(
      TextureAtlas textureAtlas,
      int spriteX,
      int spriteY,
      float x,
      float y,
      float width,
      float height) {
    super(textureAtlas, spriteX, spriteY, x, y, width, height);
  }

  /**
   * Constructor for AnimatedEntity
   *
   * @param textureAtlas The texture atlas containing the sprites for the entity
   * @param animationFrames The indices of the sprites in the animation, must be divisible by 2!
   * @param x The x position of the entity (center)
   * @param y The y position of the entity (center)
   * @param width The width of the entity
   * @param height The height of the entity
   */
  public AnimatedEntity(
      TextureAtlas textureAtlas,
      int[] animationFrames,
      float x,
      float y,
      float width,
      float height,
      float frameDuration) {
    this(textureAtlas, animationFrames[0], animationFrames[1], x, y, width, height);
    assert animationFrames.length % 2 == 0;
    this.animationFrames = animationFrames;
    this.animationStep = 0;
    this.frameDuration = frameDuration;
    this.timeSinceLastFrame = 0.0f;
  }

  @Override
  public void update(float delta) {
    super.update(delta);
    this.advanceFrame(delta);
  }

  /**
   * Advances the animation by one frame if enough time has passed.
   *
   * @param deltaTime The time elapsed since the last update call.
   */
  private void advanceFrame(float deltaTime) {
    this.timeSinceLastFrame += deltaTime; // Add the elapsed time

    // Check if enough time has passed to advance the frame
    while (this.timeSinceLastFrame >= this.frameDuration) {
      this.animationStep++; // Increment the animation step
      this.timeSinceLastFrame -=
          this.frameDuration; // Decrease timeSinceLastFrame for the next frame

      // Check if the animation has reached the end
      if (this.animationStep >= this.animationFrames.length / 2) {
        this.setDiscard(true); // End the animation and possibly discard the entity
        break; // Exit the loop if the animation ends
      } else {
        // Update the sprite to the current frame
        int index = this.animationStep * 2; // Calculate the index for the current frame
        this.setSpriteX(this.animationFrames[index]);
        this.setSpriteY(this.animationFrames[index + 1]);
        Log.d(
            "AnimatedEntity",
            "Advancing sprite to frame at index "
                + index
                + ": ("
                + this.animationFrames[index]
                + ", "
                + this.animationFrames[index + 1]
                + ")");
      }
    }
  }
}
