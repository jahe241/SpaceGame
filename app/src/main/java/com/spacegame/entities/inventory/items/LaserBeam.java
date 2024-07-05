package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.Inventory;
import com.spacegame.utils.Vector2D;

/**
 * The LaserBeam item
 */
public class LaserBeam extends TimerItem {

  public LaserBeam(Inventory ownerInventory) {
    super(
        Items.AllItems.LaserBeam.ordinal(),
        "Laser Beam",
        "Shoots a laser beam in all diagonal directions",
        1.5f,
        ownerInventory);
  }

  @Override
  public void activate(Actor self) {
    LaserBeamProjectile.create(this, new Vector2D(1, 1));
    LaserBeamProjectile.create(this, new Vector2D(-1, 1));
    LaserBeamProjectile.create(this, new Vector2D(1, -1));
    LaserBeamProjectile.create(this, new Vector2D(-1, -1));
  }

  @Override
  public void onAdd(Inventory inventory) {}

  @Override
  public void onRemove(Inventory inventory) {}
}
