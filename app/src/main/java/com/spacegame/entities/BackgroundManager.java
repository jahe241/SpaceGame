package com.spacegame.entities;

import com.spacegame.graphics.Sprite;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.Constants;
import com.spacegame.utils.Vector2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BackgroundManager {
  private final TextureAtlas textureAtlas;
  private final List<AssetActor> backgroundAssets = new ArrayList<>(512);
  private final List<Sprite> backgroundSprites;
  private final float sizeFactor;
  private final ThreadLocalRandom rng = ThreadLocalRandom.current();
  float width;
  float height;

  public BackgroundManager(TextureAtlas textureAtlas, float width, float height, float sizeFactor) {
    this.textureAtlas = textureAtlas;
    this.width = width;
    this.height = height;
    this.sizeFactor = sizeFactor;
    backgroundSprites = textureAtlas.getAnimationSprites(Constants.PLANETS);
  }

  private void addBackgroundAssets(int num) {
    for (int i = 0; i < num; i++) {
      var sprite = backgroundSprites.get(rng.nextInt(0, backgroundSprites.size()));
      // random points within the screen
      var x = rng.nextFloat() * width;
      var y = rng.nextFloat() * height;
      addBackgroundAsset(
          sprite, x, y, sprite.w() * sizeFactor, sprite.h() * sizeFactor, rng.nextFloat());
    }
  }

  private void addBackgroundAsset(
      Sprite sprite, float x, float y, float width, float height, float parallaxFactor) {
    AssetActor asset = new AssetActor(textureAtlas, sprite, x, y, width, height, parallaxFactor);
    backgroundAssets.add(asset);
  }

  public void update(float deltaTime, Vector2D playerVelocity) {
    for (var asset : backgroundAssets) {
      asset.update(deltaTime);
    }
  }
}
