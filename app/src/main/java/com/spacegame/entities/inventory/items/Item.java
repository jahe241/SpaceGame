package com.spacegame.entities.inventory.items;

import com.spacegame.entities.inventory.Inventory;

public abstract class Item {
  public final int id;
  public final String name;
  public final String description;
  protected Inventory inventory;
  public final ItemClass itemClass;

  protected Item(int id, ItemClass itemClass, String name, String description) {
    this.id = id;
    this.itemClass = itemClass;
    this.name = name;
    this.description = description;
  }

  public abstract void onAdd(Inventory inventory);

  public abstract void onRemove(Inventory inventory);
}
