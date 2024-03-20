package com.spacegame.utils;

public class TextureAtlas {
  private int textureId;
  private int spriteWidth;
  private int spriteHeight;
  private int atlasWidth;
  private int atlasHeight;

  /**
   * Constructor for TextureAtlas This class is used to store the texture id and the sprite width
   * and height it assumes that the atlas is a grid of sprites
   *
   * @param textureId the openGL texture id
   * @param spriteWidth the sprite width
   * @param spriteHeight the sprite height
   * @param atlasWidth the atlas width
   * @param atlasHeight the atlas height
   */
  public TextureAtlas(
      int textureId, int spriteWidth, int spriteHeight, int atlasWidth, int atlasHeight) {
    this.textureId = textureId;
    this.spriteWidth = spriteWidth;
    this.spriteHeight = spriteHeight;
    this.atlasWidth = atlasWidth;
    this.atlasHeight = atlasHeight;
  }

  /**
   * Returns the UV coordinates for a sprite in the texture atlas. The UV coordinates are normalized
   * values between 0 and 1 that represent the position of the sprite in the texture atlas.
   *
   * @param spriteX The x position of the sprite in the texture atlas grid.
   * @param spriteY The y position of the sprite in the texture atlas grid.
   * @return An array of floats representing the UV coordinates of the sprite in the texture atlas.
   */
  public float[] getUVs(int spriteX, int spriteY) {
    float u = (float) spriteX * spriteWidth / atlasWidth;
    float v = (float) spriteY * spriteHeight / atlasHeight;
    float u2 = u + (float) spriteWidth / atlasWidth;
    float v2 = v + (float) spriteHeight / atlasHeight;

    return new float[] {u, v, u2, v2};
  }

  /**
   * Returns the OpenGL texture ID of the texture atlas.
   *
   * @return The OpenGL texture ID of the texture atlas.
   */
  public int getTextureId() {
    return textureId;
  }

  /**
   * Returns the width of the texture atlas grid. = the number of sprites in the x direction. The
   * width of the grid is calculated by dividing the width of the atlas by the width of a sprite.
   *
   * @return The width of the texture atlas grid.
   */
  public int getGridWidth() {
    return atlasWidth / spriteWidth;
  }

  /**
   * Returns the height of the texture atlas grid. = the number of sprites in the y direction. The
   * height of the grid is calculated by dividing the height of the atlas by the height of a sprite.
   *
   * @return The height of the texture atlas grid.
   */
  public int getGridHeight() {
    return atlasHeight / spriteHeight;
  }
}
