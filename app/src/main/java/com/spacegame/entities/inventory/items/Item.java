package com.spacegame.entities.inventory.items;

import com.spacegame.entities.inventory.Inventory;
import com.spacegame.utils.Constants;

public abstract class Item {
  public final int id;
  public final String name;
  public final String description;
  protected Inventory inventory;
  public final ItemClass itemClass;
  public String spriteName = Constants.SCIFI_INVENTORY;

  protected Item(
      int id, ItemClass itemClass, String name, String description, Inventory inventory) {
    this.id = id;
    this.itemClass = itemClass;
    this.name = name;
    this.description = description;
    this.inventory = inventory;
  }

  public void tick(float delta) {}

  public abstract void onAdd(Inventory inventory);

  public abstract void onRemove(Inventory inventory);

  public String getSpriteName() {
    System.out.println("item sprite name:" + this.name);
    return Constants.ITEM_ICONS.get(this.name);
  }
}
