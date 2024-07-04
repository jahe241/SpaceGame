package com.spacegame.entities.inventory.items;

import com.spacegame.core.Game;
import com.spacegame.entities.Actor;
import com.spacegame.entities.AnimationOptions;
import com.spacegame.entities.CollisionMask;
import com.spacegame.utils.Vector2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LaserCanonProjectile extends Actor {
  LaserCanon from;

  /** To check, with which actor its already collided with */
  Map<UUID, Boolean> collisionMap = new HashMap<>();

  public LaserCanonProjectile(LaserCanon from) {
    super(
        Game.game.textureAtlas,
        from.inventory.actor.getX(),
        from.inventory.actor.getY(),
        50,
        50,
        new AnimationOptions(.3f, true, "projectile_spark-", false));
    this.from = from;
    this.collidable = true;
    this.collisionMask = CollisionMask.PLAYER_PROJECTILE;
    this.collidesWith = new ArrayList<>(List.of(CollisionMask.ENEMY));
    this.baseSpeed = 1000;
    this.collisionDamage = 1;
  }

  @Override
  public void onCollision(Actor other) {
    super.onCollision(other);
    // To make sure the projectile only collides once with every actor
    if (!this.collisionMap.containsKey(other.getActorId())) {
      this.from.inventory.onEnemyHit(other);
      other.takeDamage(this);
      this.collisionMap.put(other.getActorId(), true);
    }
  }

  @Override
  public void update(float delta) {
    // If the projectile is not in the game bounds discard it
    if (!Game.game.isInBounds(this.getX(), this.getY())) {
      this.setDiscard(true);
      return;
    }
    super.update(delta);
  }

  public static LaserCanonProjectile create(LaserCanon from, Vector2D direction) {
    LaserCanonProjectile ret = new LaserCanonProjectile(from);
    ret.setDirection(direction);
    Game.game.addEntity(ret);
    return ret;
  }
}
