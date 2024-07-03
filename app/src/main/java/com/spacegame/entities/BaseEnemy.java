package com.spacegame.entities;

import com.spacegame.core.Game;
import com.spacegame.graphics.TextureAtlas;
import java.util.ArrayList;

public class BaseEnemy extends Actor {

  public int id;

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
      int id,
      TextureAtlas textureAtlas,
      String spriteName,
      float x,
      float y,
      float width,
      float height) {
    super(textureAtlas, spriteName, x, y, width, height);
    // Collision stuff
    this.id = id;
    this.collidable = true;
    this.collisionMask = CollisionMask.ENEMY;
    ArrayList<CollisionMask> temp = new ArrayList<>();
    temp.add(CollisionMask.PLAYER);
    this.collidesWith = temp;
    this.collisionDamage = 1;
    this.baseSpeed = 100;
  }

  @Override
  public void onCollision(Actor other) {
    this.takeDamage(other);
  }

  @Override
  public void onCollisionEnd() {
    this.disableColorOverlay();
  }

  @Override
  public void setDiscard(boolean discard) {
    super.setDiscard(discard);
    Game.game.onEnemyDeath(this);
  }
}
