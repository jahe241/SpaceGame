package com.spacegame.entities;

import com.spacegame.graphics.Sprite;
import com.spacegame.graphics.TextureAtlas;
import java.util.List;

public class Animation {

  Entity entity;

  /** A list of Sprite objects that make up the frames of the animation. */
  private final List<Sprite> animationFrames;

  /** The current frame of the animation. This is an index into the animationFrames list. */
  private int animationStep;

  /** The Duration of the whole Animation */
  private float animationDuration;

  /** The time elapsed since the last frame change, in seconds. */
  private float timeSinceLastFrame;

  /** The duration of each frame in the animation, in seconds. */
  private float timePerFrame;

  /**
   * A flag indicating whether the animation should loop or not. If true, the animation will start
   * over from the beginning once it reaches the end. If false, the animation will stop once it
   * reaches the end.
   */
  private final boolean looping;

  private final boolean discardAfterEnd;

  public Animation(Entity entity, TextureAtlas textureAtlas, AnimationOptions options) {
    this.animationFrames = textureAtlas.getAnimationSprites(options.animationTextureName);
    assert !this.animationFrames.isEmpty();
    this.animationStep = 0;
    this.animationDuration = options.animationDuration;
    this.entity = entity;
    this.entity.setSprite(animationFrames.get(animationStep));
    this.timePerFrame = animationDuration / (float) this.animationFrames.size();
    this.timeSinceLastFrame = 0f;
    this.looping = options.isLooping;
    this.discardAfterEnd = options.discardAfterEnd;
  }

  /**
   * Advances the animation by one frame if enough time has passed.
   *
   * @param deltaTime The time elapsed since the last update call.
   */
  public void update(float deltaTime) {
    this.timeSinceLastFrame += deltaTime; // Add the elapsed time
    if (this.timeSinceLastFrame < this.timePerFrame) return;

    if (animationStep == this.animationFrames.size() - 1) {
      if (!looping && this.discardAfterEnd) {
        this.entity.setDiscard(true);
        return;
      } else if (looping) {
        this.animationStep = 0;
      }
    } else {
      this.animationStep++;
    }
    this.timeSinceLastFrame = 0f;
    this.entity.setSprite(this.animationFrames.get(animationStep));
  }

  public void setAnimationDuration(float duration) {
    this.animationDuration = duration;
    this.timePerFrame = this.animationDuration / (float) this.animationFrames.size();
  }
}
