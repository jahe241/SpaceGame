package com.spacegame.entities.inventory.items;

import com.spacegame.entities.inventory.Inventory;

public class Shield extends OnDamageTakenItem {

  float activationTime = 5f;

  boolean active = true;

  float timeUntilCharged = 0f;

  public Shield(Inventory ownerInventory) {
    super(
        Items.AllItems.Shield.ordinal(),
        "Shield",
        "Shields you from the next incomming attack",
        ownerInventory);
  }

  @Override
  public void tick(float delta) {
    if (active) return;
    timeUntilCharged -= delta;
    if (timeUntilCharged <= 0) {
      active = true;
    }
  }

  @Override
  public void onDamageTaken() {
    if (!active) return;
    timeUntilCharged = activationTime;
    active = false;
  }

  @Override
  public void onAdd(Inventory inventory) {
    ShieldEntity.create(this);
  }

  @Override
  public void onRemove(Inventory inventory) {}

  public boolean isActive() {
    return active;
  }
}
