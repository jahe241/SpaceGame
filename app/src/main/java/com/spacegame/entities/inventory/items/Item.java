package com.spacegame.entities.inventory.items;

import com.spacegame.entities.inventory.Inventory;
import com.spacegame.utils.Constants;

/**
 * The class for all items
 */
public abstract class Item {
  /**
   * The id of the item
   */
  public final int id;

  /**
   * The name of the item
   */
  public final String name;

  /**
   * The description of the item
   */
  public final String description;

  /**
   * The inventory this item belongs to
   */
  protected Inventory inventory;

  /**
   * The {@link ItemClass} of the item
   */
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

  /**
   * Called every frame
   * @param delta
   */
  public void tick(float delta) {}

  /**
   * Callback when this item gets added to the inventory
   * @param inventory
   */
  public abstract void onAdd(Inventory inventory);

  public abstract void onRemove(Inventory inventory);

  /**
   * Get the name of the spirte, mainly used for the {@link com.spacegame.core.ui.ItemPickupToast}
   * @return
   */
  public String getSpriteName() {
    System.out.println("item sprite name:" + this.name);
    return Constants.ITEM_ICONS.get(this.name);
  }
}
