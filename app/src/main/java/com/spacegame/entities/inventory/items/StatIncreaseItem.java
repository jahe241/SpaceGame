package com.spacegame.entities.inventory.items;

public abstract class StatIncreaseItem extends Item {
  public StatIncreaseItem() {
    this.itemClass = ItemClass.STAT_INCREASE;
  }

  public abstract void calcStatIncrease();
}
