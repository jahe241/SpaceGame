package com.spacegame.entities.enemies;

import com.spacegame.core.Game;
import com.spacegame.entities.Actor;
import com.spacegame.entities.AnimationOptions;
import com.spacegame.entities.CollisionMask;
import com.spacegame.entities.Player;
import com.spacegame.utils.Vector2D;
import java.util.ArrayList;
import java.util.List;

public class SniperCanonProjectile extends Actor {

  public static final float SPEED = 500;

  public SniperCanonProjectile(Sniper from, Vector2D direction) {
    super(
        Game.game.textureAtlas,
        from.getX(),
        from.getY(),
        100,
        50,
        new AnimationOptions(.3f, true, "projectile_pulse-", false));
    this.setDirection(direction);
    this.baseSpeed = SPEED;
    this.collisionMask = CollisionMask.ENEMY_PROJECTILE;
    this.collidesWith = new ArrayList<>(List.of(CollisionMask.PLAYER));
    this.collisionDamage = 1;
    //    this.setColorOverlay(new float[] {.5f, 0, 0, 1});
  }

  @Override
  public void onCollision(Actor other) {
    super.onCollision(other);
    this.setDiscard(true);
    if (other instanceof Player p) {
      p.takeDamage(other);
    }
  }

  @Override
  public void update(float delta) {
    super.update(delta);
  }

  public static SniperCanonProjectile create(Sniper from, Actor target) {
    // Needed to nerf this as the shots are too difficult to dodge, if prediction is applied
    /*
    Vector2D targetPosition = target.getPosition();
    Vector2D targetVelocity = target.getVelocity();
    Vector2D toTarget = from.getPosition().to(targetPosition);
    float distance = toTarget.length();
    float secondsToArrive = distance / SPEED;
    Vector2D predictedPosition = targetPosition.add(targetVelocity.mult(secondsToArrive));

    Vector2D shootDirection = from.getPosition().to(predictedPosition);

    SniperCanonProjectile projectile = new SniperCanonProjectile(from, shootDirection);

    Game.game.addEntity(projectile);
    return projectile;

     */
    Vector2D targetPosition = target.getPosition();
    Vector2D fromPosition = from.getPosition();
    Vector2D shootDirection = fromPosition.to(targetPosition);

    SniperCanonProjectile projectile = new SniperCanonProjectile(from, shootDirection);
    Game.game.addEntity(projectile);
    return projectile;
  }
}
