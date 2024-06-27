package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.Inventory;
import java.util.Random;

public class RocketLauncher extends OnEnemyHitItem {

  static final float probability = 0.5f;

  public RocketLauncher(Inventory inventory) {
    super(
        Items.AllItems.RocketLauncher.ordinal(),
        "Rocket Launcher",
        "Shoots rockets when an enemy was hit",
        inventory);
  }

  @Override
  public void onEnemyHit(Actor target) {
    Random rand = new Random();
    float randFloat = rand.nextFloat();
    if (randFloat <= RocketLauncher.probability) {
      RocketLauncherProjectile.create(this);
    }
  }

  @Override
  public void onAdd(Inventory inventory) {}

  @Override
  public void onRemove(Inventory inventory) {}
}
