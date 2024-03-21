package com.spacegame.graphics;

/**
 * A record representing a Sprite in the game. A Sprite is a 2D graphic object that is part of a
 * larger graphical scene (such as a game). It has a name, position (x, y), dimensions (width and
 * height), and texture coordinates (UVs).
 *
 * @param name The name of the sprite.
 * @param x The x-coordinate of the sprite's position in the TextureAtlas.
 * @param y The y-coordinate of the sprite's position in the TextureAtlas.
 * @param w The width of the sprite.
 * @param h The height of the sprite.
 * @param uvs The texture coordinates of the sprite. This is an array of floats where the first two
 *     elements represent the lower-left corner (U1, V1) and the last two elements represent the
 *     upper-right corner (U2, V2) of the texture.
 * @see TextureAtlas
 */
public record Sprite(String name, int x, int y, int w, int h, float[] uvs) {}
