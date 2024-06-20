package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.Constants;
import com.spacegame.utils.Vector2D;

public class LaserCanonProjectile extends Actor {
  public LaserCanonProjectile(TextureAtlas atlas, Actor from, Vector2D direction) {
    super(atlas, Constants.BLUE_PROJECTILE, from.getX(), from.getY(), 20, 20);
    this.setDirection(direction);
  }
}
