package com.spacegame.entities.inventory.items;

import com.spacegame.core.Game;
import com.spacegame.entities.Actor;
import com.spacegame.entities.AnimationOptions;
import com.spacegame.entities.CollisionMask;
import com.spacegame.utils.Constants;
import com.spacegame.utils.Vector2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RocketLauncherProjectile extends Actor {
  RocketLauncher from;
  Actor currentTarget;

  public RocketLauncherProjectile(RocketLauncher from, Actor initialTarget) {
    super(
        Game.game.textureAtlas,
        from.inventory.actor.getX(),
        from.inventory.actor.getY(),
        80,
        50,
        new AnimationOptions(.3f, true, "projectile_waveform-", false));
    this.from = from;
    this.currentTarget = initialTarget;
    this.collidable = true;
    this.collisionMask = CollisionMask.PLAYER_PROJECTILE;
    this.collidesWith = new ArrayList<>(List.of(CollisionMask.ENEMY));
    this.baseSpeed = 1500;
  }

  @Override
  public void onCollision(Actor other) {
    super.onCollision(other);
    this.setDiscard(true);
    Game.game.createExplosion(this.getX(), this.getY(), 100);
    other.setDiscard(true);
    this.from.inventory.onEnemyHit(other);
  }

  @Override
  public void update(float delta) {
    // If the projectile is not in the games bounds, discard it
    if (!Game.game.isInBounds(this.getX(), this.getY())) {
      this.setDiscard(true);
      return;
    }
    super.update(delta);
    // Track closest enemy
    // If the current target gets discarded or becomes null, switch target;
    if (currentTarget == null || currentTarget.getDiscard()) {
      this.currentTarget = Game.game.getClosestEnemy(this.getX(), this.getY());
      // If there are no enemies in the game currently
      if (this.currentTarget == null) {
        return;
      }
    }
    Vector2D newDirection =
        new Vector2D(this.getX(), this.getY())
            .to(new Vector2D(currentTarget.getX(), currentTarget.getY()));
    this.setDirection(newDirection);
    if (this.anim != null) anim.update(delta);
  }

  /**
   * Creates a Projectile for the Rocket Launcher, also adds it to the game
   *
   * @param from
   * @return
   */
  public static RocketLauncherProjectile create(RocketLauncher from) {
    // Get closest Enemy
    Actor closestEnemy =
        Game.game.getClosestEnemy(from.inventory.actor.getX(), from.inventory.actor.getY());

    RocketLauncherProjectile ret = new RocketLauncherProjectile(from, closestEnemy);
    Random rand = new Random();
    float initialX = (rand.nextFloat() * 2) - 1;
    float initialY = (rand.nextFloat() * 2) - 1;
    Vector2D initialVel = new Vector2D(initialX, initialY).toSize(ret.baseSpeed);
    ret.setVelocity(initialVel);
    Game.game.addEntity(ret);
    return ret;
  }
}
