package com.spacegame.entities.inventory.items;

public abstract class OnDamageTakenItem extends Item {
  public OnDamageTakenItem() {
    this.itemClass = ItemClass.ON_DAMAGE_TAKEN;
  }

  public abstract void onDamageTaken();
}
