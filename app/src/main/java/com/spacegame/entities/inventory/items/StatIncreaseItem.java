package com.spacegame.entities.inventory.items;

import com.spacegame.entities.inventory.Inventory;

public abstract class StatIncreaseItem extends Item {
  public StatIncreaseItem(int id, String name, String description, Inventory inventory) {
    super(id, ItemClass.STAT_INCREASE, name, description, inventory);
  }
}
