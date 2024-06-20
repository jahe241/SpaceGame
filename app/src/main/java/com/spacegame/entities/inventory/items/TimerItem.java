package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;

public abstract class TimerItem extends Item {

  protected float time;

  public TimerItem(int id, String name, String description, float time) {
    super(id, ItemClass.TIMER, name, description);
    this.time = time;
  }

  public abstract void activate(Actor self);
}
