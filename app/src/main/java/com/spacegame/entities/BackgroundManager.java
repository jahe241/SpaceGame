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
  private static final int CAP_BACKGROUND_ASSETS = 500; // number of assets to spawn
  private static final float MAX_SCREEN_SPACE_PERCENTAGE = .25f; // max size a single asset can take
  private static final float MIN_SCREEN_SPACE_PERCENTAGE = .05f; // min size a single asset can take
  private static final float BACKGROUND_ASSET_PADDING = .1f; // in screen space percentage
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

  // test init, it fills the whole screen with looped background
  public void init() {

    var spriteWidth = (int) Math.ceil(screenPercentage);
    var spriteHeight = (int) Math.ceil(screenPercentage);
    DebugLogger.log("BackgroundManager", "Sprite Dimensions: " + spriteWidth + "x" + spriteHeight);

    int extendedWidth = (int) (width * 2);
    int extendedHeight = (int) (height * 2);

    int numX = (int) Math.ceil((double) extendedWidth / spriteWidth);
    int numY = (int) Math.ceil((double) extendedHeight / spriteHeight);
    DebugLogger.log(
        "BackgroundManager",
        "Number of sprites to fill extended area: " + numX + "x" + numY + " = " + numX * numY);

    for (int i = 0; i < numX; i++) {
      for (int j = 0; j < numY; j++) {
        var randomSprite = backgroundSprites.get(rng.nextInt(0, backgroundSprites.size()));
        addBackgroundAsset(
            randomSprite, i * (spriteWidth), j * (spriteHeight), spriteWidth, spriteHeight);
      }
    }
  }

  public void spawnBackgroundAssets() {
    float sizeRange = MAX_SCREEN_SPACE_PERCENTAGE - MIN_SCREEN_SPACE_PERCENTAGE;
    float padding = BACKGROUND_ASSET_PADDING * screenSizeConstant;

    for (int i = 0; i < CAP_BACKGROUND_ASSETS; i++) {
      float sizeFactor = MIN_SCREEN_SPACE_PERCENTAGE + rng.nextFloat() * sizeRange;
      float size = screenSizeConstant * sizeFactor;

      float x, y;
      // TODO: test this! might lag the game too much on launch!!
      boolean overlaps;
      do {
        overlaps = false;
        x = (rng.nextFloat() * screenSizeConstant) * LOOP_THRESHOLD_MULTIPLIER;
        y = (rng.nextFloat() * screenSizeConstant) * LOOP_THRESHOLD_MULTIPLIER;

        // Check if the new asset's position overlaps with any existing assets
        for (AssetActor asset : backgroundAssets) {
          float dx = Math.abs(asset.getX() - x);
          float dy = Math.abs(asset.getY() - y);
          if (dx < size + padding && dy < size + padding) {
            overlaps = true;
            break;
          }
        }
      } while (overlaps);

      Sprite sprite = backgroundSprites.get(rng.nextInt(backgroundSprites.size()));

      addBackgroundAsset(sprite, x, y, size, size);
    }
  }

  private void addBackgroundAsset(Sprite sprite, float x, float y, float width, float height) {
    // calc value between .1 and .9f
    float parralaxFactor = .1f + rng.nextFloat() * (.9f - .1f);
    AssetActor asset = new AssetActor(textureAtlas, sprite, x, y, width, height, parralaxFactor);
    DebugLogger.log(
        "BackgroundManager",
        "Adding background asset at " + x + "," + y + " with dimensions " + width + "x" + height);
    asset.scale(width, height);
    asset.setZ(0.0f - ((int) (parralaxFactor * 10)));
    //     calculate the overlay opacity based on the parallax layer
    asset.setColorOverlay(new float[] {parralaxFactor, parralaxFactor, parralaxFactor, 1.f});
    backgroundAssets.add(asset);
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
