package com.spacegame.core;

import com.spacegame.entities.BaseEnemy;
import com.spacegame.entities.enemies.AllEnemies;
import com.spacegame.entities.enemies.Sniper;
import com.spacegame.entities.enemies.Stalker;
import com.spacegame.utils.DebugLogger;
import com.spacegame.utils.PausableStopwatch;
import com.spacegame.utils.Vector2D;
import java.util.concurrent.ThreadLocalRandom;

public class SpawnManager {
  private final Game game;
  private final ThreadLocalRandom rng = ThreadLocalRandom.current();
  private final PausableStopwatch timer;
  private long lastSpawn = System.currentTimeMillis();

  private float spawnCredits = 0;

  public SpawnManager(Game game, PausableStopwatch timer) {
    this.game = game;
    this.timer = timer;
  }

  public void update(float delta) {
    // time passed in seconds
    int timePassed = (int) (timer.getElapsedTime() / 1000f);
    //
    this.spawnCredits += delta * ((timePassed / 10) + 1);
    // Spend tickets every 5 seconds

    if (timePassed % 5 == 0) {
      this.lastSpawn = System.currentTimeMillis();
      while (this.spawnCredits >= 1) {
        // Get the rng, which is between the credits to spend and the maximum numbers of enemy types
        int rng =
            (int) (this.rng.nextFloat() * Math.min(AllEnemies.values().length, this.spawnCredits));
        AllEnemies enemyPick = AllEnemies.values()[rng];
        this.spawnEnemy(enemyPick);
        this.spawnCredits -= rng + 1;
      }
    }

    // spawn enemies
    //        if (rng.nextInt(0, 100) < 5) {
    //            game.spawnEnemy();
    //        }
  }

  public void spawnEnemy(AllEnemies enemyType) {
    float rng = this.rng.nextFloat();
    float x = rng > 0.5f ? rng * game.width : rng * -game.width;
    float y = rng > 0.5f ? rng * game.height : rng * -game.height;
    // Make sure enemies get spawned outside of game screen
    Vector2D spawnPosition = new Vector2D(x, y).toSize(Math.max(game.width / 2, game.height / 2));
    BaseEnemy enemy = null;
    switch (enemyType) {
      case Stalker -> enemy = new Stalker(spawnPosition.getX(), spawnPosition.getY());
      case Sniper -> enemy = new Sniper(spawnPosition.getX(), spawnPosition.getY());
    }
    assert (enemy != null);
    game.addEntity(enemy);
    DebugLogger.log("Spawner", "Enemy spawned!");
  }
}
