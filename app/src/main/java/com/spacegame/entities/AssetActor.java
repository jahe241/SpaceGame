package com.spacegame.entities;

import com.spacegame.graphics.Sprite;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.Vector2D;

public class AssetActor extends Actor {
  private final float parallaxFactor;

  public AssetActor(
      TextureAtlas textureAtlas,
      Sprite sprite,
      float x,
      float y,
      float width,
      float height,
      float parallaxFactor) {
    super(textureAtlas, sprite, x, y, width, height);
    this.parallaxFactor = parallaxFactor;
    this.collidable = false;
  }

  @Override
  public Vector2D getVelocity() {
    if (this.playerVelocity != null)
      return this.velocity.add(this.playerVelocity.inversed()).mult(this.parallaxFactor);
    else return new Vector2D(this.velocity);
  }

  public float getParallaxFactor() {
    return parallaxFactor;
  }
}
