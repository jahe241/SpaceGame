package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.Inventory;

/**
 * Class for items that trigger, when damage is taken
 */
public abstract class OnDamageTakenItem extends Item {

  public OnDamageTakenItem(int id, String name, String description, Inventory inventory) {
    super(id, ItemClass.ON_DAMAGE_TAKEN, name, description, inventory);
  }

  /**
   * Callback when damage was being taken
   * @param damage
   * @param from
   * @return
   */
  public abstract int onDamageTaken(int damage, Actor from);
}
