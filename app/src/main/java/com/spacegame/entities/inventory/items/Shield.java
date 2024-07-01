package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.Inventory;

public class Shield extends OnDamageTakenItem {

  ShieldEntity shieldEntity = null;

  float activationTime = 5f;

  boolean active = true;

  float timeUntilCharged = 0f;

  public Shield(Inventory ownerInventory) {
    super(
        Items.AllItems.Shield.ordinal(), "Shield", "Shields next incoming attack", ownerInventory);
  }

  @Override
  public void tick(float delta) {
    if (this.shieldEntity == null) return;
    if (active) return;
    timeUntilCharged -= delta;
    if (timeUntilCharged <= 0) {
      active = true;
      this.shieldEntity.vbo().setOpacity(0.3f);
    }
  }

  @Override
  public int onDamageTaken(int damage, Actor from) {
    // If the incoming damage is already 0 the shield shouldn't activate
    // This could be when other items already reduced the damage to 0
    if (damage <= 0) return 0;
    if (!active) return damage;
    timeUntilCharged = activationTime;
    active = false;
    this.shieldEntity.vbo().setOpacity(0f);
    return 0;
  }

  @Override
  public void onAdd(Inventory inventory) {
    this.shieldEntity = ShieldEntity.create(this);
  }

  @Override
  public void onRemove(Inventory inventory) {}

  public boolean isActive() {
    return active;
  }
}
