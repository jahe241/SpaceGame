package com.spacegame.entities;

import com.spacegame.core.Game;
import com.spacegame.graphics.Sprite;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.Constants;
import com.spacegame.utils.DebugLogger;
import com.spacegame.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BackgroundManager {
  // SETTINGS FOR BACKGROUND UNUSED RN
  private static final int CAP_BACKGROUND_ASSETS = 3000; // number of assets to spawn
  private static final float MAX_SCREEN_SPACE_PERCENTAGE = .3f; // max size a single asset can take
  private static final float MIN_SCREEN_SPACE_PERCENTAGE = .05f; // min size a single asset can take
  private static final float BACKGROUND_ASSET_PADDING = .3f; // in screen space percentage
  private static final int LOOP_THRESHOLD_MULTIPLIER = 10; // distance from player to loop asset
  private final int screenSizeConstant;
  private final float loopThreshold;
  private final TextureAtlas textureAtlas;
  public final List<AssetActor> backgroundAssets = new ArrayList<>(512);
  private final List<Sprite> backgroundSprites;
  private final float screenPercentage;
  private final ThreadLocalRandom rng = ThreadLocalRandom.current();
  float width;
  float height;

  Game game;

  public BackgroundManager(
      TextureAtlas textureAtlas, float width, float height, float normScreenSize, Game game) {
    this.textureAtlas = textureAtlas;
    this.width = width;
    this.height = height;
    this.screenSizeConstant = Math.max((int) width, (int) height);
    this.loopThreshold = LOOP_THRESHOLD_MULTIPLIER * screenSizeConstant;
    // get 10% of the screen size
    this.screenPercentage = (float) (Math.sqrt(width * height) * .50f);
    this.game = game;

    backgroundSprites = textureAtlas.getAnimationSprites(Constants.PLANETS);
    DebugLogger.log(
        "BackgroundManager", "Loaded " + backgroundSprites.size() + " background sprites");
    spawnBackgroundAssets();
  }

  public void spawnBackgroundAssets() {
    float sizeRange = MAX_SCREEN_SPACE_PERCENTAGE - MIN_SCREEN_SPACE_PERCENTAGE;
    float padding = BACKGROUND_ASSET_PADDING * screenSizeConstant;

    int gridSize = (int) Math.sqrt(CAP_BACKGROUND_ASSETS);
    float cellWidth = (float) (screenSizeConstant * LOOP_THRESHOLD_MULTIPLIER) / gridSize;
    float cellHeight = (float) (screenSizeConstant * LOOP_THRESHOLD_MULTIPLIER) / gridSize;

    for (int i = 0; i < CAP_BACKGROUND_ASSETS; i++) {
      float sizeFactor = MIN_SCREEN_SPACE_PERCENTAGE + rng.nextFloat() * sizeRange;
      float size = screenSizeConstant * sizeFactor;

      int row = i / gridSize;
      int col = i % gridSize;

      float xBase = col * cellWidth;
      float yBase = row * cellHeight;

      // Apply staggered grid (hexagonal-like) pattern
      if (row % 2 == 1) {
        xBase += cellWidth / 2;
      }

      float x = xBase + rng.nextFloat() * (cellWidth - size - padding);
      float y = yBase + rng.nextFloat() * (cellHeight - size - padding);

      Sprite sprite = backgroundSprites.get(rng.nextInt(backgroundSprites.size()));

      addBackgroundAsset(sprite, x, y, size, size);
    }
  }

  private void addBackgroundAsset(Sprite sprite, float x, float y, float width, float height) {
    // calc value between .1 and .9f
    float parralaxFactor = randomFloat(.1f, .9f);
    AssetActor asset = new AssetActor(textureAtlas, sprite, x, y, width, height, parralaxFactor);
    float z_index = 0.0f - ((int) (parralaxFactor * 10));
    DebugLogger.log(
        "BackgroundManager",
        "Adding background asset at "
            + x
            + ","
            + y
            + " with dimensions "
            + width
            + "x"
            + height
            + " and z-index "
            + z_index);
    asset.scale(width, height);
    asset.setZ(z_index);
    asset.setRotationRad(rng.nextFloat() * 2 * (float) Math.PI);
    //     calculate the overlay opacity based on the parallax layer
    float dimFactor = (1 - parralaxFactor) * 1.5f;
    System.out.printf("dimFactor: %f\n", dimFactor);
    asset.setColorOverlay(
        new float[] {
          dimFactor - randomFloat(.01f, .2f),
          dimFactor - randomFloat(.01f, .2f),
          dimFactor - randomFloat(.01f, .2f),
          1.f
        });
    backgroundAssets.add(asset);
  }

  float randomFloat(float min, float max) {
    return min + rng.nextFloat() * (max - min);
  }

  public void update(float deltaTime) {
    Vector2D playerVelocity = this.game.getPlayerVelocity();
    for (var asset : backgroundAssets) {
      Vector2D assetVelocity = playerVelocity.mult(-1).mult(asset.getParallaxFactor());
      asset.setVelocity(assetVelocity);
      asset.update(deltaTime);

      Vector2D assetPosition = asset.getPosition();
      Vector2D playerPosition = new Vector2D(width / 2, height / 2);
      Vector2D distanceVector = playerPosition.to(assetPosition);
      float distance = distanceVector.length();

      if (distance > loopThreshold) {
        Vector2D direction = distanceVector.normalized();
        Vector2D displacement = direction.mult(loopThreshold);
        Vector2D newPosition = playerPosition.add(displacement.inversed());

        asset.setPosition(newPosition);
      }
    }
  }
}
