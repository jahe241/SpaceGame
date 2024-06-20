package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.Inventory;

public class LaserCanon extends TimerItem {

  public LaserCanon(Inventory inventory) {
    super(2, "Laser Canon", "Pew pew", 2.5f, inventory);
  }

  @Override
  public void activate(Actor self) {}

  @Override
  public void onAdd(Inventory inventory) {}

  @Override
  public void onRemove(Inventory inventory) {}
}
