package com.spacegame.core;

import android.util.Log;
import com.spacegame.entities.Actor;
import com.spacegame.entities.AnimationOptions;
import com.spacegame.entities.BaseEnemy;
import com.spacegame.entities.Entity;
import com.spacegame.entities.Player;
import com.spacegame.graphics.TextureAtlas;
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
  /** The list of entities in the game. */
  public final List<Entity> entities = Collections.synchronizedList(new ArrayList<>());

  public final PausableStopwatch timer = new PausableStopwatch();

  /** The player entity. */
  public Player player;

  /** The pointer to the texture atlas. */
  public int textureAtlasPointer = -1;

  /** The texture atlas that contains the game's textures. */
  public TextureAtlas textureAtlas;

  /** The running state of the game. True if the game is running, false otherwise. */
  volatile boolean running = false;

  volatile GameState state = GameState.PLAYING;

  /** Sceen height */
  int height;

  /** Screen width */
  int width;

  float scaleFactor;
  int score = 0;
  ThreadLocalRandom rng = ThreadLocalRandom.current(); // RNG is seeded with current thread

  public Game(int height, int width) {
    super("Game Thread");
    this.height = height;
    this.width = width;
  }

  public void setPlayerDirection(Vector2D stickDirection) {
    if (player != null) player.setDirection(stickDirection);
    Log.d("Game", "Setting Player Direction: " + stickDirection);
  }

  public Vector2D getScreenDimensions() {
    return new Vector2D(this.width, this.height);
  }

  public void resetGame() {
    synchronized (entities) {
      entities.clear();
      this.timer.reset();
    }
    setupGame();
  }

  /** Sets up the game by adding the player character to the entities list. */
  private void setupGame() {
    // Add the player character
    float playerX = this.width / 2f;
    float playerY = this.height / 2f;
    float size = Math.min(this.width, this.height) * 0.2f; // 20% of the screen size
    this.scaleFactor = size / Math.min(this.width, this.height);
    Player player = new Player(this.textureAtlas, Constants.PLAYER, playerX, playerY, size, size);
    player.setGame(this);
    this.setPlayer(player);
    addEntity(new BaseEnemy(this.textureAtlas, "ship_red_01", 500f, 500f, 338f, 166f));
    //    addEntity(new ColorEntity(500f, 500f, 100f, 100f, new float[] {1f, 0f, 1f, 1f}));
    this.state = GameState.PLAYING;
    this.timer.start();
  }

  /**
   * Adds a new entity to the entities list.
   *
   * @param entity The new entity to add.
   */
  public void addEntity(Entity entity) {
    synchronized (entities) {
      entities.add(entity);
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
      }
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
    // Calls the update method for each entity: Updates Position and adjusts the vertex data based
    // on the new position
    // Remove the entities that are marked for deletion
    entities.removeIf(Entity::getDiscard);

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
    // TODO: Physics / Interaction-Checks here
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
  public Entity getPlayer() {
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
   * Removes an entity from the entities list.
   *
   * @param entity The entity to remove.
   */
  public void removeEntity(Entity entity) {
    synchronized (entities) {
      entities.remove(entity);
    }
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
      return visibleEntities;
    }
  }

  public void spawnRandomEnemy(int numEnemies) {
    final int maxRetries = 10; // Define your maximum number of retries here
    for (int i = 0; i < numEnemies; i++) {
      float x = rng.nextFloat() * this.width;
      float y = rng.nextFloat() * this.height;
      int retryCount = 0;
      while (isPositionOccupied(x, y)) {
        if (++retryCount == maxRetries) {
          System.out.println("Warning: Maximum number of retries reached when spawning enemy.");
          break;
        }
        x = rng.nextFloat() * this.width;
        y = rng.nextFloat() * this.height;
      }
      if (retryCount < maxRetries) {
        spawnRandomEntity(x, y);
      }
    }
  }

  private boolean isPositionOccupied(float x, float y) {
    synchronized (entities) {
      for (Entity entity : entities) {
        if (Math.abs(entity.getX() - x) < entity.getWidth()
            && Math.abs(entity.getY() - y) < entity.getHeight()) {
          return true;
        }
      }
    }
    return false;
  }

  private void spawnRandomEntity(float x, float y) {
    String randomEnemy = Constants.ENEMIES[rng.nextInt(Constants.ENEMIES.length)];
    var randomDude = new BaseEnemy(this.textureAtlas, randomEnemy, x, y, 338f, 166f);
    randomDude.scale(randomDude.getSprite().w(), randomDude.getSprite().h());
    randomDude.setZ(-1);
    randomDude.setColorOverlay(new float[] {rng.nextFloat(), rng.nextFloat(), rng.nextFloat(), 1f});
    randomDude.setRotationRad(rng.nextFloat() * (float) (2 * Math.PI));
    this.addEntity(randomDude);
    float explosionSize = Math.max(randomDude.getWidth(), randomDude.getHeight()) * 1.8f;
    Actor explosion =
        new Actor(
            this.textureAtlas,
            x,
            y,
            explosionSize,
            explosionSize,
            new AnimationOptions(1f, false, Constants.animation_EXPLOSION, true));
    explosion.setZ(0);
    explosion.setRotationRad(randomDude.getRotationRad());
    this.addEntity(explosion);
  }

  public int setScore(int score) {
    this.score = score;
    return this.score;
  }

  public int getScore() {
    return this.score;
  }
}
