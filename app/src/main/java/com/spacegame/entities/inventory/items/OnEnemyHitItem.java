package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.Inventory;

/**
 * Class for items that trigger, when an enemy is hit
 */
public abstract class OnEnemyHitItem extends Item {
  public OnEnemyHitItem(int id, String name, String description, Inventory inventory) {
    super(id, ItemClass.ON_ENEMY_HIT, name, description, inventory);
  }

  public abstract void onEnemyHit(Actor target);
}
