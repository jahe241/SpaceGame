package com.spacegame.entities.enemies;

import com.spacegame.core.Game;

public class SniperCanon {
  /** The time to charge the attack */
  public static final float CHARGE_TIME = 2;

  private float timeUntilCharged = CHARGE_TIME;

  Sniper from;

  public SniperCanon(Sniper from) {
    this.from = from;
  }

  public void tick(float delta) {
    if (this.timeUntilCharged > 0) {
      this.timeUntilCharged -= delta;
      return;
    }
    this.timeUntilCharged = CHARGE_TIME;
    SniperCanonProjectile.create(this.from, Game.game.getPlayer());
  }
}
