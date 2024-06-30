package com.spacegame.entities.inventory.items;

import com.spacegame.core.Game;
import com.spacegame.entities.Actor;
import com.spacegame.entities.CollisionMask;
import com.spacegame.utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class ItemPickup extends Actor {

  Items.AllItems item;

  public ItemPickup(float x, float y, Items.AllItems item) {
    super(Game.game.textureAtlas, Constants.SCIFI_INVENTORY, x, y, 50, 50);
    this.item = item;
    this.collidable = true;
    this.collisionMask = CollisionMask.ITEM;
    this.collidesWith = new ArrayList<>(List.of(CollisionMask.PLAYER));
  }

  public static ItemPickup create(Items.AllItems item, float x, float y) {
    ItemPickup ret = new ItemPickup(x, y, item);
    Game.game.addEntity(ret);
    return ret;
  }

  @Override
  public void onCollision(Actor other) {
    super.onCollision(other);
    other.inventory.addItem(Items.createItem(item, other.inventory));
    this.setDiscard(true);
  }
}
