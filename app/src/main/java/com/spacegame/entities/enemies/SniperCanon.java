package com.spacegame.entities.enemies;

import com.spacegame.core.Game;
import com.spacegame.sound.SoundType;

/**
 * The weapon of the {@link Sniper}
 */
public class SniperCanon {
  /** The time to charge the attack */
  public static final float CHARGE_TIME = 2;

  /**
   * The current time until the weapon is charged again
   */
  private float timeUntilCharged = CHARGE_TIME;

  /**
   * The {@link Sniper} holding this weapon
   */
  Sniper from;

  public SniperCanon(Sniper from) {
    this.from = from;
  }

  /**
   * Called every frame
   * @param delta
   */
  public void tick(float delta) {
    if (this.timeUntilCharged > 0) {
      this.timeUntilCharged -= delta;
      return;
    }
    this.timeUntilCharged = CHARGE_TIME;
    SniperCanonProjectile.create(this.from, Game.game.getPlayer());
    Game.game.soundEngine.playSound(SoundType.SNIPER);
  }
}
