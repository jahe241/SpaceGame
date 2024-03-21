package com.spacegame.core;

import android.util.Log;
import com.spacegame.graphics.Sprite;
import com.spacegame.graphics.TextureAtlas;
import java.util.List;

public class AnimatedEntity extends Entity {
  // Animation fields
  private List<Sprite>
      animationFrames; // The indices of the sprites in the animation, must be divisible by 2!
  private int animationStep; // The current frame of the animation
  private float frameDuration; // The duration of each frame in seconds
  private float timeSinceLastFrame; // The time elapsed since the last frame change

  private boolean isLooping; // Whether the animation should loop or not

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
   */
  public AnimatedEntity(
      TextureAtlas textureAtlas,
      String animationName,
      float x,
      float y,
      float width,
      float height,
      float frameDuration,
      boolean isLooping) {
    super(textureAtlas, null, x, y, width, height);
    this.animationFrames = textureAtlas.getAnimationSprites(animationName);
    this.animationStep = 0;
    this.sprite = animationFrames.get(animationStep);
    this.frameDuration = frameDuration;
    this.timeSinceLastFrame = 0.0f;
    this.isLooping = isLooping;
    this.updateauxData();
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
      if (this.animationStep >= this.animationFrames.size()) {
        if (this.isLooping) {
          this.animationStep = 0; // Reset the animation if it is looping
        } else {
          this.setDiscard(true); // End the animation and possibly discard the entity
          break; // Exit the loop if the animation ends
        }
      } else {
        // Update the sprite to the current frame
        this.sprite = this.animationFrames.get(this.animationStep);
        this.updateauxData();
        Log.d("AnimatedEntity", "Advancing sprite to frame to index " + this.animationStep);
      }
    }
  }
}
