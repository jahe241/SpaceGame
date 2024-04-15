package com.spacegame.entities.inventory.items;

public abstract class OnEnemyHitItem extends Item {
  public OnEnemyHitItem() {
    this.itemClass = ItemClass.ON_ENEMY_HIT;
  }

  public abstract void onEnemyHit();
}
