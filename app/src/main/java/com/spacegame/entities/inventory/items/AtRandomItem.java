package com.spacegame.entities.inventory.items;

public abstract class AtRandomItem extends Item {

  public AtRandomItem() {
    this.itemClass = ItemClass.AT_RANDOM;
  }

  public abstract void tick();
}
