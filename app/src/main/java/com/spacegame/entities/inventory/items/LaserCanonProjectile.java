package com.spacegame.entities.inventory.items;

import com.spacegame.entities.Actor;
import com.spacegame.utils.Constants;
import com.spacegame.utils.Vector2D;

public class LaserCanonProjectile extends Actor {
  LaserCanon from;

  public LaserCanonProjectile(LaserCanon from) {
    super(
        from.inventory.actor.getGame().textureAtlas,
        Constants.BLUE_PROJECTILE,
        from.inventory.actor.getX(),
        from.inventory.actor.getY(),
        20,
        20);
    this.from = from;
  }

  @Override
  public void onCollision(Actor other) {
    super.onCollision(other);
    this.setDiscard(true);
    this.from.inventory.onEnemyHit(other);
  }

  public static LaserCanonProjectile create(LaserCanon from, Vector2D direction) {
    LaserCanonProjectile ret = new LaserCanonProjectile(from);
    ret.setDirection(direction);
    from.inventory.actor.getGame().addEntity(ret);
    return ret;
  }
}
