package com.spacegame.entities.inventory.items;

import com.spacegame.core.Game;
import com.spacegame.entities.Actor;
import com.spacegame.entities.CollisionMask;
import com.spacegame.utils.Constants;
import com.spacegame.utils.Vector2D;
import java.util.ArrayList;
import java.util.List;

// TODO: The hitbox is a bit wierd, maybe we can fix this

public class LaserBeamProjectile extends Actor {
  LaserBeam from;

  public static final float timeToLive = 0.5f;

  private float timeLeft = LaserBeamProjectile.timeToLive;

  public LaserBeamProjectile(LaserBeam from) {
    super(
        Game.game.textureAtlas,
        Constants.BLUE_PROJECTILE,
        from.inventory.actor.getX(),
        from.inventory.actor.getY(),
        700,
        150);
    this.from = from;
    this.collidable = true;
    this.collisionMask = CollisionMask.PLAYER_PROJECTILE;
    this.collidesWith = new ArrayList<>(List.of(CollisionMask.ENEMY));
    this.baseSpeed = 1;
  }

  @Override
  public void onCollision(Actor other) {
    super.onCollision(other);
    other.setDiscard(true);
    Game.game.createExplosion(other.getX(), other.getY(), 100);
  }

  @Override
  public void update(float delta) {
    this.timeLeft -= delta;
    if (this.timeLeft <= 0) {
      this.setDiscard(true);
      return;
    }

    super.update(delta);
    Actor actor = this.from.inventory.actor;
    Vector2D basePoint =
        new Vector2D(actor.getX(), actor.getY()).add(this.getDirection().mult(this.getWidth() / 2));
    this.setX(basePoint.getX());
    this.setY(basePoint.getY());
  }

  public static LaserBeamProjectile create(LaserBeam from, Vector2D direction) {
    LaserBeamProjectile ret = new LaserBeamProjectile(from);
    ret.setDirection(direction);
    Game.game.addEntity(ret);
    return ret;
  }
}
