package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.Inventory;

public class RocketLauncher extends OnEnemyHitItem {

  static final float probability = 1;

  public RocketLauncher(Inventory inventory) {
    super(Items.AllItems.RocketLauncher.ordinal(), "Rocket Launcher", "Bum bum", inventory);
  }

  @Override
  public void onEnemyHit(Actor target) {
    RocketLauncherProjectile projectile = RocketLauncherProjectile.create(this);
  }

  @Override
  public void onAdd(Inventory inventory) {}

  @Override
  public void onRemove(Inventory inventory) {}
}
