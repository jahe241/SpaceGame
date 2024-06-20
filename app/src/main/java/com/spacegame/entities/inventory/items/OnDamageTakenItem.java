package com.spacegame.entities.inventory.items;

import com.spacegame.entities.inventory.Inventory;

public abstract class OnDamageTakenItem extends Item {

  public OnDamageTakenItem(int id, String name, String description, Inventory inventory) {
    super(id, ItemClass.ON_DAMAGE_TAKEN, name, description, inventory);
  }

  public abstract void onDamageTaken();
}
