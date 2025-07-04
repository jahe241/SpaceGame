package com.spacegame.core;

import com.spacegame.entities.BaseEnemy;
import com.spacegame.entities.enemies.AllEnemies;
import com.spacegame.entities.enemies.Sniper;
import com.spacegame.entities.enemies.Stalker;
import com.spacegame.utils.DebugLogger;
import com.spacegame.utils.Vector2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The class managing the spawning of enemies for the game.
 * It also controls the different events coming up in the game
 */
public class SpawnManager {
  /**
   * The current {@link Game} instance of the thread
   */
  private final Game game;

  /**
   * The rng instance for generating random values
   */
  private final ThreadLocalRandom rng = ThreadLocalRandom.current();

  /**
   * At which game time the stalker event should happen, in seconds
   */
  private static final int STALKER_EVENT_TIME = 30;

  /**
   * If the stalker event happened
   */
  private boolean stalkerEvent = false;

  /**
   * The current time passed in the game
   */
  private float timePassed = 0f;

  /**
   * The credits for spawning enemies. These are spent for spawning enemies
   */
  private float spawnCredits = 0;

  public SpawnManager(Game game) {
    this.game = game;
  }

  public void update(float delta) {
    // time passed in seconds
    this.timePassed += delta;
    int timePassedInSeconds = (int) this.timePassed;
    // Increase the amount of spawn credits given, the longer the game goes
    // Every 30 seconds the credits given per frame are increased exponentially
    this.spawnCredits += delta * ((timePassedInSeconds / 30) + 1);
    // Spend tickets every 5 seconds
    if (timePassedInSeconds % 5 == 0) {
      while (this.spawnCredits >= 1f) {
        // Get the rng, which is between the credits to spend and the maximum numbers of enemy types
        int rngVal = rng.nextInt(Math.min((int) spawnCredits, AllEnemies.values().length));
        AllEnemies enemyPick = AllEnemies.values()[rngVal];
        this.spawnEnemy(enemyPick);
        this.spawnCredits -= rngVal + 1;
      }
    }
    // Initiate stalker event
    if (this.stalkerEvent) return;
    if (timePassedInSeconds >= STALKER_EVENT_TIME) createStalkerEvent();
  }

  /**
   * Spawn an enemy at a random position
   *
   * @param enemyType
   */
  public void spawnEnemy(AllEnemies enemyType) {
    Vector2D screenMiddle = new Vector2D(game.width / 2f, game.height / 2f);
    float rngX = this.rng.nextFloat() * 2 - 1;
    float rngY = this.rng.nextFloat() * 2 - 1;
    // Make sure enemies get spawned outside of game screen
    Vector2D spawnPosition =
        new Vector2D(rngX, rngY)
            .toSize(Math.max(game.width / 2, game.height / 2))
            .add(screenMiddle);
    spawnEnemy(enemyType, spawnPosition);
  }

  /**
   * Spawns an enemy at the given position
   *
   * @param enemyType
   * @param spawnPosition
   */
  public void spawnEnemy(AllEnemies enemyType, Vector2D spawnPosition) {
    // Make sure enemies get spawned outside of game screen
    BaseEnemy enemy = null;
    switch (enemyType) {
      case Stalker -> enemy = new Stalker(spawnPosition.getX(), spawnPosition.getY());
      case Sniper -> enemy = new Sniper(spawnPosition.getX(), spawnPosition.getY());
    }
    assert (enemy != null);
    game.addEntity(enemy);
  }

  /**
   * Resets the spawner
   */
  public void reset() {
    this.spawnCredits = 0;
    this.timePassed = 0;
    this.stalkerEvent = false;
  }

  /**
   * Adds spawn credits to the spawner
   * @param credits
   */
  public void addCredits(float credits) {
    this.spawnCredits += credits;
  }

  /**
   * Creates the stalker event
   */
  public void createStalkerEvent() {
    float length = Game.game.getNormalizedScreenWidth();
    Vector2D playerPos = Game.game.getPlayer().getPosition();
    List<Vector2D> spawnPositions = new ArrayList<>();
    for (float i = -1; i <= 1; i += .5f) {
      for (float j = -1; j <= 1; j += .5f) {
        if (i == 0 && j == 0) continue; // Null vector needs to be skipped
        spawnPositions.add(new Vector2D(i, j).toSize(length).add(playerPos));
      }
    }
    for (Vector2D pos : spawnPositions) {
      spawnEnemy(AllEnemies.Stalker, pos);
    }
    this.stalkerEvent = true;
    DebugLogger.log("Spawner", "Stalker Event!");
  }
}
