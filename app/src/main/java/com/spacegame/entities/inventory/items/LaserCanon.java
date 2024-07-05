package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.Inventory;
import com.spacegame.utils.Vector2D;

/**
 * The laser canon item
 */
public class LaserCanon extends TimerItem {

  public LaserCanon(Inventory inventory) {
    super(Items.AllItems.LaserCanon.ordinal(), "Laser Canon", "Pew pew", 1f, inventory);
  }

  @Override
  public void activate(Actor self) {
    LaserCanonProjectile.create(this, new Vector2D(1, 0));
    LaserCanonProjectile.create(this, new Vector2D(-1, 0));
    LaserCanonProjectile.create(this, new Vector2D(0, -1));
    LaserCanonProjectile.create(this, new Vector2D(0, 1));
  }

  @Override
  public void onAdd(Inventory inventory) {}

  @Override
  public void onRemove(Inventory inventory) {}
}
