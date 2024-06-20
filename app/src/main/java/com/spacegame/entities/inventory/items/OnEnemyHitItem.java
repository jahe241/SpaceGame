package com.spacegame.entities.inventory.items;

public abstract class OnEnemyHitItem extends Item {
  public OnEnemyHitItem(int id, String name, String description) {
    super(id, ItemClass.ON_ENEMY_HIT, name, description);
  }

  public abstract void onEnemyHit();
}
