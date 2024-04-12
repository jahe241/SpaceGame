package com.spacegame.entities;

public class AnimationOptions {
  float animationDuration;
  boolean isLooping;

  String animationTextureName;

  boolean discardAfterEnd;

  public AnimationOptions(
      float animationDuration,
      boolean isLooping,
      String animationTextureName,
      boolean discardAfterEnd) {
    this.animationDuration = animationDuration;
    this.isLooping = isLooping;
    this.animationTextureName = animationTextureName;
    this.discardAfterEnd = discardAfterEnd;
  }
}
