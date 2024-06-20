package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.Inventory;

public abstract class TimerItem extends Item {

  protected float time;

  public TimerItem(int id, String name, String description, float time, Inventory inventory) {
    super(id, ItemClass.TIMER, name, description, inventory);
    this.time = time;
  }

  public abstract void activate(Actor self);
}
