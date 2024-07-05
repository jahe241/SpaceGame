package com.spacegame.entities.enemies;

import com.spacegame.core.Game;
import com.spacegame.entities.Actor;
import com.spacegame.entities.BaseEnemy;
import com.spacegame.entities.Player;
import com.spacegame.utils.Constants;
import com.spacegame.utils.Vector2D;

/**
 * The stalker enemy class. Flies in a steady pace to the player and tries to crash into the player.
 */
public class Stalker extends BaseEnemy {

  /**
   * The current {@link Player} instance
   */
  Player player;

  public Stalker(float x, float y) {
    super(
        AllEnemies.Stalker.ordinal(), Game.game.textureAtlas, Constants.ENEMIES[1], x, y, 100, 100);
    this.baseSpeed = 150;
    this.collisionDamage = 2;
    this.player = Game.game.getPlayer();
    this.setMaxHealth(2);
  }

  @Override
  public void update(float delta) {
    super.update(delta);
    if (this.getCurrentHealth() <= 0) {
      this.onDeath();
      return;
    }
    // Update direction to the player
    if (player == null) return;
    Vector2D newDirection = this.getPosition().to(player.getPosition()).normalized();
    this.setDirection(newDirection);
  }

  @Override
  public void onCollision(Actor other) {
    super.onCollision(other);
    if (other instanceof Player) {
      this.setDiscard(true);
      other.takeDamage(this);
    } else {
      this.takeDamage(other);
    }
    Game.game.createExplosion(this.getX(), this.getY(), 100);
  }
}
