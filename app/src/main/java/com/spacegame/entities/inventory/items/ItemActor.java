package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.CollisionMask;
import com.spacegame.graphics.TextureAtlas;
import java.util.ArrayList;

public abstract class ItemActor extends Actor {

  Items.AllItems item;

  public ItemActor(
      TextureAtlas textureAtlas, String spriteName, float x, float y, Items.AllItems item) {
    super(textureAtlas, spriteName, x, y, 20, 20);
    this.item = item;
    this.collidesWith = new ArrayList<>();
    this.collidesWith.add(CollisionMask.PLAYER);
    this.collidable = true;
    this.collisionMask = CollisionMask.ITEM;
  }

  @Override
  public void onCollision(Actor other) {
    super.onCollision(other);
    other.inventory.addItem(Items.createItem(this.item, this.inventory));
  }
}
