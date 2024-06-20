package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;

public abstract class AtRandomItem extends Item {

  public AtRandomItem(int id, String name, String description) {
    super(id, ItemClass.AT_RANDOM, name, description);
  }

  public abstract void tick(Actor actor);
}
