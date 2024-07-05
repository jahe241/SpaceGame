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


/**
 * The laser beam projectile
 */
public class LaserBeamProjectile extends Actor {
  /**
   * The item this projectile is from
   */
  LaserBeam from;

  /**
   * A map that tracks which actor it already hit
   * This is for making sure that no actor is hit twice with one instance
   */
  Map<UUID, Boolean> collisionMap = new HashMap<>();

  /**
   * The time to live of this projectile
   */
  public static final float timeToLive = 0.5f;

  /**
   * The time left of this projectile
   */
  private float timeLeft = LaserBeamProjectile.timeToLive;

  public LaserBeamProjectile(LaserBeam from) {
    super(
        Game.game.textureAtlas,
        from.inventory.actor.getX(),
        from.inventory.actor.getY(),
        500,
        100,
        new AnimationOptions(.3f, true, "projectile_mirrored_bolt-", false));
    this.from = from;
    this.collidable = true;
    this.collisionMask = CollisionMask.PLAYER_PROJECTILE;
    this.collidesWith = new ArrayList<>(List.of(CollisionMask.ENEMY));
    this.baseSpeed = 1;
    this.collisionDamage = 3;
  }

  @Override
  public void onCollision(Actor other) {
    super.onCollision(other);
    if (!this.collisionMap.containsKey(other.getActorId())) {
      other.takeDamage(this);
      this.collisionMap.put(other.getActorId(), true);
    }
  }

  @Override
  public void update(float delta) {
    this.timeLeft -= delta;
    if (this.timeLeft <= 0) {
      this.setDiscard(true);
      return;
    }
    Actor actor = this.from.inventory.actor;
    float padding = (this.getWidth() / 2) + Math.max(actor.getHeight(), actor.getWidth()) / 2;
    Vector2D basePoint = actor.getPosition().add(this.getDirection().mult(padding));
    this.setX(basePoint.getX());
    this.setY(basePoint.getY());
    this.vbo().updateVBOPosition(this.getX(), this.getY(), this.getZ(), this.getRotationRad());
    if (this.anim != null) anim.update(delta);
  }

  @Override
  public void setDirection(Vector2D direction) {
    super.setDirection(direction);
    this.setRotationRad(this.calcAngleRad());
  }

  /**
   * Creates this projectile
   * @param from
   * @param direction
   * @return
   */
  public static LaserBeamProjectile create(LaserBeam from, Vector2D direction) {
    LaserBeamProjectile ret = new LaserBeamProjectile(from);
    ret.setDirection(direction);
    Game.game.addEntity(ret);
    return ret;
  }

  /**
   * Calculate the angle of this projectile
   * @return
   */
  public float calcAngleRad() {
    return (float) -Math.atan2(this.getDirection().getY(), this.getDirection().getX());
  }
}
