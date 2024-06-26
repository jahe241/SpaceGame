package com.spacegame.entities.inventory.items;

import com.spacegame.core.Game;
import com.spacegame.entities.Actor;
import com.spacegame.entities.CollisionMask;
import com.spacegame.utils.Constants;
import com.spacegame.utils.DebugLogger;
import com.spacegame.utils.Vector2D;
import java.util.ArrayList;
import java.util.List;

public class LaserCanonProjectile extends Actor {
  LaserCanon from;

  public LaserCanonProjectile(LaserCanon from) {
    super(
        Game.game.textureAtlas,
        Constants.BLUE_PROJECTILE,
        from.inventory.actor.getX(),
        from.inventory.actor.getY(),
        50,
        50);
    this.from = from;
    this.collidable = true;
    this.collisionMask = CollisionMask.PLAYER_PROJECTILE;
    this.collidesWith = new ArrayList<>(List.of(CollisionMask.ENEMY));
    this.baseSpeed = 1000;
  }

  @Override
  public void onCollision(Actor other) {
    super.onCollision(other);
    this.setDiscard(true);
    other.setDiscard(true);
    this.from.inventory.onEnemyHit(other);
    DebugLogger.log("PROJECTILE", "PLAYER PROJECTILE HIT SOMETHING!");
  }

  public static LaserCanonProjectile create(LaserCanon from, Vector2D direction) {
    LaserCanonProjectile ret = new LaserCanonProjectile(from);
    ret.setDirection(direction);
    Game.game.addEntity(ret);
    return ret;
  }
}
