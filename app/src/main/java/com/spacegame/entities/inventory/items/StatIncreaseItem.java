package com.spacegame.entities.inventory.items;

public abstract class StatIncreaseItem extends Item {
  public StatIncreaseItem(int id, String name, String description) {
    super(id, ItemClass.AT_RANDOM, name, description);
  }
}
