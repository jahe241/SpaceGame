package com.spacegame.entities.inventory.items;

public abstract class OnDamageTakenItem extends Item {

  public OnDamageTakenItem(int id, String name, String description) {
    super(id, ItemClass.ON_DAMAGE_TAKEN, name, description);
  }

  public abstract void onDamageTaken();
}
