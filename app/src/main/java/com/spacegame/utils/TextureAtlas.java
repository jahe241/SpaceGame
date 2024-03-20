package com.spacegame.utils;

import android.util.Log;
import androidx.annotation.NonNull;

public class TextureAtlas {
  private final int textureId;
  private final int spriteWidth;
  private final int spriteHeight;
  private final int atlasWidth;
  private final int atlasHeight;

  /**
   * Constructor for TextureAtlas This class is used to store the texture id and the sprite width
   * and height it assumes that the atlas is a grid of sprites
   *
   * @param textureId the openGL texture id
   * @param spriteWidth the sprite width in pixels
   * @param spriteHeight the sprite height in pixels
   * @param atlasWidth the atlas width in pixels
   * @param atlasHeight the atlas height in pixels
   */
  public TextureAtlas(
      int textureId, int spriteWidth, int spriteHeight, int atlasWidth, int atlasHeight) {
    this.textureId = textureId;
    this.spriteWidth = spriteWidth;
    this.spriteHeight = spriteHeight;
    this.atlasWidth = atlasWidth;
    this.atlasHeight = atlasHeight;
    Log.d("TextureAtlas", "Created: " + this);
    Log.d("TextureAtlas", "coords:" + Constants.getAllCoords(this));
  }

  /**
   * Returns the UV coordinates for a sprite in the texture atlas. The UV coordinates are normalized
   * values between 0 and 1 that represent the position of the sprite in the texture atlas.
   *
   * <p>The TextureAtlas is 0-Indexed starting from the top left (0,0) to bottom right (max,max)
   *
   * @param spriteX The x position of the sprite in the texture atlas grid.
   * @param spriteY The y position of the sprite in the texture atlas grid.
   * @return An array of floats representing the UV coordinates of the sprite in the texture atlas.
   */
  public float[] getUVs(int spriteX, int spriteY) {
    Log.d("TextureAtlas", "Sprite: (" + spriteX + ", " + spriteY + ")");
    assert spriteX - 1 <= atlasWidth / spriteWidth;
    assert spriteY - 1 <= atlasHeight / spriteHeight;

    float u = (float) spriteX * spriteWidth / atlasWidth;
    float v = (float) spriteY * spriteHeight / atlasHeight;
    float u2 = u + (float) spriteWidth / atlasWidth;
    float v2 = v + (float) spriteHeight / atlasHeight;
    Log.d("TextureAtlas", "UVs: " + u + " " + v + " " + u2 + " " + v2);
    return new float[] {u, v, u2, v2};
  }

  /**
   * Returns the OpenGL texture ID of the texture atlas.
   *
   * @return The OpenGL texture ID of the texture atlas.
   */
  public int getTexturePtr() {
    return textureId;
  }

  /**
   * Returns the number of sprites in the X-Direction. The number is calculated by dividing the
   * width of the atlas by the width of a sprite.
   *
   * @return The width of the texture atlas grid.
   */
  public int getGridWidth() {
    return atlasWidth / spriteWidth;
  }

  /**
   * Returns the number of sprites in the Y-Direction. The number is calculated by dividing the
   * height of the atlas by the height of a sprite.
   *
   * @return The height of the texture atlas grid.
   */
  public int getGridHeight() {
    return atlasHeight / spriteHeight;
  }

  @NonNull
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("TextureAtlas[")
        .append(textureId)
        .append("] ")
        .append(spriteWidth)
        .append("x")
        .append(spriteHeight)
        .append(" ")
        .append(atlasWidth)
        .append("x")
        .append(atlasHeight)
        .append(" | Grid: ")
        .append(getGridWidth())
        .append("x")
        .append(getGridHeight());
    return sb.toString();
  }
}
