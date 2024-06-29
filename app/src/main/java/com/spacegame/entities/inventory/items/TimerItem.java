package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.Inventory;

public abstract class TimerItem extends Item {

  protected float time;
  private float timeLeft;

  public TimerItem(int id, String name, String description, float time, Inventory inventory) {
    super(id, ItemClass.TIMER, name, description, inventory);
    this.time = time;
  }

  public abstract void activate(Actor self);

  @Override
  public void tick(float delta) {
    this.timeLeft -= delta;
    if (this.timeLeft <= 0) {
      this.activate(this.inventory.actor);
      this.timeLeft = time;
    }
  }
}
