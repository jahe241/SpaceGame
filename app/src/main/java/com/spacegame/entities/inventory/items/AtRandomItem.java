package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.Inventory;

public abstract class AtRandomItem extends Item {

  public AtRandomItem(int id, String name, String description, Inventory inventory) {
    super(id, ItemClass.AT_RANDOM, name, description, inventory);
  }

  public abstract void tick(Actor actor);
}
