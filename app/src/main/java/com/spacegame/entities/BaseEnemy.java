package com.spacegame.entities;

import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.Vector2D;

public class BaseEnemy extends Entity {

  Vector2D playerVelocity;

  /**
   * Constructor for the Entity class. This constructor initializes a new Entity object by setting
   * its position, size, texture atlas, and sprite. If the texture atlas is not null, it sets the
   * texture atlas and OpenGL texture pointer of the entity. If both the texture atlas and sprite
   * name are not null, it sets the sprite of the entity and updates its auxiliary data.
   *
   * @param textureAtlas The TextureAtlas object to use for the entity. This object contains the
   *     texture atlas used to render the entity's sprite.
   * @param spriteName The name of the sprite to use for the entity. The sprite is retrieved from
   *     the provided texture atlas.
   * @param x The initial x-coordinate of the entity.
   * @param y The initial y-coordinate of the entity.
   * @param width The width of the entity.
   * @param height The height of the entity.
   */
  public BaseEnemy(
      TextureAtlas textureAtlas, String spriteName, float x, float y, float width, float height) {
    super(textureAtlas, spriteName, x, y, width, height);
  }

  @Override
  public void update(float delta) {
    if (playerVelocity == null) return;
    Vector2D oldVel = new Vector2D(this.velocity);
    this.velocity = this.velocity.add(this.playerVelocity.inversed());
    updatePosition(delta);
    this.velocity = oldVel;
    updateRotation(delta);
    updateVertexPositionData();
  }

  public void setPlayerVelocity(Vector2D playerVelocity) {
    this.playerVelocity = playerVelocity;
  }
}
