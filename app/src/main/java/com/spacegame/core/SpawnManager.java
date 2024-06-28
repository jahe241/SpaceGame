package com.spacegame.core;

import com.spacegame.utils.PausableStopwatch;

import java.util.concurrent.ThreadLocalRandom;

public class SpawnManager {
  private final Game game;
  private final ThreadLocalRandom rng = ThreadLocalRandom.current();
  private final PausableStopwatch timer;

  public SpawnManager(Game game, PausableStopwatch timer) {
    this.game = game;
    this.timer = timer;
  }

  public void update(float delta) {
    // spawn enemies
    //        if (rng.nextInt(0, 100) < 5) {
    //            game.spawnEnemy();
    //        }
  }
}
