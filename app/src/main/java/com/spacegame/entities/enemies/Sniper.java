package com.spacegame.entities.enemies;

import com.spacegame.core.Game;
import com.spacegame.entities.BaseEnemy;
import com.spacegame.entities.Player;
import com.spacegame.utils.Constants;
import com.spacegame.utils.Vector2D;

public class Sniper extends BaseEnemy {
  Player player;

  SniperCanon canon = new SniperCanon(this);

  static final float playerGap = 500f;

  static final float fuzzyThreshold = 50;

  public Sniper(float x, float y) {
    super(
        AllEnemies.Sniper.ordinal(), Game.game.textureAtlas, Constants.ENEMIES[3], x, y, 100, 100);
    this.player = Game.game.getPlayer();
    this.baseSpeed = 500;
  }

  @Override
  public void update(float delta) {
    super.update(delta);
    this.canon.tick(delta);
    if (this.player == null) return;
    // Fly near the player, but always keep a set distance
    Vector2D toPlayer = this.getPosition().to(player.getPosition());
    if (toPlayer.length() < Sniper.playerGap) {
      this.setDirection(toPlayer.inversed());
    } else if (Math.abs(toPlayer.length() - Sniper.playerGap) <= Sniper.fuzzyThreshold) {
      setDirection(new Vector2D(0, 0));
    } else {
      setDirection(toPlayer);
    }
  }

  @Override
  public void updateRotation(float deltaTime) {
    Vector2D toPlayer = this.player.getPosition().to(this.getPosition());
    // Set the rotation, that the sniper is always facing the player
    float newRotation = -toPlayer.calcAngle(new Vector2D(1, 0));
    // Ensure rotation is within the range [-π, π]
    if (newRotation >= Math.PI) {
      newRotation -= 2 * Math.PI;
    } else if (newRotation < -Math.PI) {
      newRotation += 2 * Math.PI;
    }
    this.setRotationRad(newRotation);
  }

  @Override
  public float getAcceleration() {
    return 10;
  }
}
