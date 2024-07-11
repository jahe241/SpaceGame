package com.spacegame.core;

import android.content.Context;
import android.util.Log;
import com.spacegame.entities.Actor;
import com.spacegame.entities.AnimationOptions;
import com.spacegame.entities.BackgroundManager;
import com.spacegame.entities.BaseEnemy;
import com.spacegame.entities.Entity;
import com.spacegame.entities.Player;
import com.spacegame.entities.enemies.Sniper;
import com.spacegame.entities.inventory.items.ItemPickup;
import com.spacegame.entities.inventory.items.Items;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.sound.SoundEngine;
import com.spacegame.sound.SoundType;
import com.spacegame.utils.Constants;
import com.spacegame.utils.DebugLogger;
import com.spacegame.utils.PausableStopwatch;
import com.spacegame.utils.Vector2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The Game class extends the Thread class and represents the main game loop. It contains
 * information about the game's state, entities, and player.
 */
public class Game extends Thread {

  /**
   * The bounds of the game. All entities out side of these bound will be discarded.
   */
  public final float BOUNDS;

  /** The created game, accessable as a Singleton from everywhere */
  public static Game game;

  /** The list of entities in the game. */
  public final List<Entity> entities = Collections.synchronizedList(new ArrayList<>());

  /**
   * All enemies currently in the game
   */
  public final List<BaseEnemy> enemies = Collections.synchronizedList(new ArrayList<>());

  /**
   * The timer measuring the passed game time
   */
  public final PausableStopwatch timer = new PausableStopwatch();

  /** The player entity. */
  public Player player;

  /** The pointer to the texture atlas. */
  public int textureAtlasPointer = -1;

  /** The texture atlas that contains the game's textures. */
  public TextureAtlas textureAtlas;

  /** The running state of the game. True if the game is running, false otherwise. */
  volatile boolean running = false;

  /**
   * The current state of the game
   */
  volatile GameState state = GameState.PLAYING;

  /**
   * The spawn manager for the game
   */
  public SpawnManager spawnManager;

  /** Sceen height */
  public int height;

  /** Screen width */
  public int width;

  /**
   * The screen width or height, depending on which is higher
   */
  float normalizedScreenWidth;

  /**
   * The current score
   */
  int score = 0;

  /**
   * The {@link ThreadLocalRandom} for generating random  values
   */
  ThreadLocalRandom rng = ThreadLocalRandom.current(); // RNG is seeded with current thread

  /**
   * The {@link BackgroundManager}, spawning the background assets
   */
  BackgroundManager backgroundManager;

  public final SoundEngine soundEngine;

  Context context;

  public Game(Context context, int height, int width) {
    super("Game Thread");
    this.height = height;
    this.width = width;
    this.BOUNDS = Math.max(height, width) * 5;
    Game.game = this;
    this.spawnManager = new SpawnManager(this);
    this.context = context;
    this.soundEngine = new SoundEngine(this.context);
  }

  /**
   * Sets the player direction.
   * Important for all Actors to simulate the player flying through space
   * In reality the whole world moves around the player
   * @param stickDirection
   */
  public void setPlayerDirection(Vector2D stickDirection) {
    if (player != null) player.setDirection(stickDirection);
    Log.d("Game", "Setting Player Direction: " + stickDirection);
  }

  /**
   * Gets the screen dimensions of the app
   * @return
   */
  public Vector2D getScreenDimensions() {
    return new Vector2D(this.width, this.height);
  }

  /**
   * Resets the game to its starting state
   */
  public void resetGame() {
    for (Entity e : this.entities) {
      e.setDiscard(true);
    }
    this.timer.reset();
    this.score = 0;
    this.state = GameState.PLAYING;
    this.spawnManager.reset();
    notify();
    setupGame();
  }

  /** Sets up the game by adding the player character to the entities list. */
  private void setupGame() {
    // Add the player character
    float playerX = this.width / 2f;
    float playerY = this.height / 2f;
    float size = Math.min(this.width, this.height) * 0.15f; // 20% of the screen size
    this.normalizedScreenWidth = Math.min(this.width, this.height);
    Player player = new Player(this.context, this.textureAtlas, Constants.PLAYER, playerX, playerY, size, size);
    this.setPlayer(player);
    //    addEntity(new ColorEntity(500f, 500f, 100f, 100f, new float[] {1f, 0f, 1f, 1f}));
    this.state = GameState.PLAYING;
    this.backgroundManager =
        new BackgroundManager(this.textureAtlas, width, height, normalizedScreenWidth, this);
    this.timer.start();
    ItemPickup.create(Items.AllItems.LaserCanon, 1000, 1000);
  }

  /**
   * Adds a new entity to the entities list.
   *
   * @param entity The new entity to add.
   */
  public void addEntity(Entity entity) {
    synchronized (entities) {
      entities.add(entity);
      if (entity instanceof BaseEnemy enemy) {
        this.enemies.add(enemy);
      }
    }
  }

  /**
   * The main run method for the Game thread. It sets up the game and then enters a loop where it
   * updates the game and sleeps for the remainder of the frame time.
   */
  @Override
  public void run() {
    DebugLogger.log("Game", "Game Thread started on Thread: " + Thread.currentThread().getName());
    long timePerFrame = 1000 / 120; // Time for each frame in milliseconds
    running = true;
    // Set up the game
    setupGame();

    long lastFrameTime = System.nanoTime();

    // Game Loop
    while (running) {
      synchronized (this) {
        while (this.state == GameState.PAUSED) {
          try {
            wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        while (this.state == GameState.GAME_OVER) {
          try {
            GameInterface.gameInterface.onPlayerDeath();
            this.timer.pause();
            wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      // TODO: Handle game over state
      long startTime = System.nanoTime();
      float elapsed = (startTime - lastFrameTime) / 1_000_000f;

      update(elapsed / 1000.0f); // Convert to seconds

      lastFrameTime = startTime;
      if (elapsed < timePerFrame) {
        try {
          Thread.sleep((long) (timePerFrame - elapsed));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /** Stops the game thread. */
  @Override
  public void interrupt() {
    running = false;
  }

  /**
   * Updates the position and vertex data of each entity in the entities list.
   *
   * @param deltaTime The time since the last frame in seconds.
   */
  public void update(float deltaTime) {
    //    DebugLogger.log("DEBUG", this.player.toString());
    // Update the background
    backgroundManager.update(deltaTime);
    this.spawnManager.update(deltaTime);

    // Calls the update method for each entity: Updates Position and adjusts the vertex data based
    // on the new position
    // Remove the entities that are marked for deletion
    entities.removeIf(Entity::getDiscard);
    enemies.removeIf(Entity::getDiscard);

    Vector2D playerVelocity = this.getPlayerVelocity();

    List<Entity> otherEntities = new ArrayList<>(entities);

    for (int i = 0; i < entities.size(); i++) {
      Entity entity = entities.get(i);
      if (entity == null) break;
      entity.update(deltaTime);

      // Colision checks
      // Set player velocity
      if (entity instanceof Actor actor) {
        actor.collidesWithAny(otherEntities);
        actor.setPlayerVelocity(playerVelocity);
      }
    }
  }

  /**
   * Adds to the games score
   * @param i
   */
  private void addScore(int i) {
    this.score += i;
  }

  /**
   * Returns the player's velocity.
   *
   * @return
   */
  public Vector2D getPlayerVelocity() {
    if (this.player != null) return this.player.getVelocity();
    else return new Vector2D(0, 0);
  }

  /** Pauses the game. */
  public void pauseGame() {
    synchronized (this) {
      Log.d("Game", "Game Thread paused: " + Thread.currentThread().getName());
      this.timer.pause();
      this.state = GameState.PAUSED;
    }
  }

  /** Resumes the game. */
  public void resumeGame() {
    synchronized (this) {
      Log.d("Game", "Game Thread resumed: " + Thread.currentThread().getName());
      this.state = GameState.PLAYING;
      this.timer.resume();
      notify();
    }
  }

  /**
   * Returns the player entity.
   *
   * @return The player entity.
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Sets the player entity.
   *
   * @param player The new player entity.
   */
  public void setPlayer(Player player) {
    this.player = player;
    entities.add(player);
  }

  /**
   * Callback for when the player dies
   */
  public void onPlayerDeath() {
    this.state = GameState.GAME_OVER;
  }

  /**
   * Returns a new list containing the entities in the entities list.
   *
   * @return A new list containing the entities in the entities list.
   */
  public List<Entity> getEntities() {
    synchronized (entities) {
      return new ArrayList<>(entities);
    }
  }

  /**
   * Returns a new list containing the visible entities in the entities list.
   *
   * @return A new list containing the visible entities in the entities list.
   */
  public List<Entity> getVisibleEntities() {
    synchronized (entities) {
      List<Entity> visibleEntities = new ArrayList<>(entities.size());
      for (Entity entity : entities) {
        if (entity.isVisible()) {
          visibleEntities.add(entity);
        }
      }
      if (backgroundManager != null) visibleEntities.addAll(backgroundManager.backgroundAssets);
      return visibleEntities;
    }
  }

  /**
   * Spawns a {@link Sniper} in a random position
   * @param numEnemies
   */
  public void spawnRandomEnemy(int numEnemies) {
    float x = rng.nextFloat() * this.width;
    float y = rng.nextFloat() * this.height;
    this.addEntity(new Sniper(x, y));
  }

  /**
   * Setter for the score of the game
   * @param score
   * @return
   */
  public int setScore(int score) {
    this.score = score;
    return this.score;
  }

  /**
   * Gets the closest enemy from a specified point
   *
   * @param x
   * @param y
   * @return
   */
  public Actor getClosestEnemy(float x, float y) {
    Vector2D point = new Vector2D(x, y);
    float shortestDistance = Float.MAX_VALUE;
    Actor closestEnemy = null;
    for (BaseEnemy enemy : this.enemies) {
      float distance = new Vector2D(enemy.getX(), enemy.getY()).to(point).length();
      if (distance < shortestDistance) {
        shortestDistance = distance;
        closestEnemy = enemy;
      }
    }
    return closestEnemy;
  }

  /**
   * Callback, when an enemy dies
   * @param enemy
   */
  public void onEnemyDeath(BaseEnemy enemy) {
    this.addScore(enemy.id + 1);
    this.createExplosion(enemy.getX(), enemy.getY(), 100);
    // 15% chance on item drop, when an enemy dies
    float rng = this.rng.nextFloat() * 100f;
    if (rng <= 15f) {
      Items.createRandomPickupItem(enemy.getX(), enemy.getY());
    }
  }

  /**
   * Creates an explosion entity
   *
   * @param x
   * @param y
   * @param size
   * @return
   */
  public Actor createExplosion(float x, float y, float size) {
    Actor explosion =
        new Actor(
            this.textureAtlas,
            x,
            y,
            size,
            size,
            new AnimationOptions(.7f, false, Constants.animation_EXPLOSION, true));
    this.addEntity(explosion);
    soundEngine.playSound(SoundType.EXPLOSION);
    return explosion;
  }

  /**
   * Checks if the given point is in the game bounds
   *
   * @param x
   * @param y
   * @return
   */
  public boolean isInBounds(float x, float y) {
    Vector2D screenMiddle = this.player.getPosition();
    Vector2D toPoint = screenMiddle.to(new Vector2D(x, y));
    return toPoint.length() <= this.BOUNDS;
  }

  /**
   * Getter for the games score
   * @return
   */
  public int getScore() {
    return this.score;
  }

  /**
   * Getter for the normalized screen width
   * @return
   */
  public float getNormalizedScreenWidth() {
    return this.normalizedScreenWidth;
  }
}
