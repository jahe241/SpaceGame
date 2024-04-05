package com.spacegame.core;

import android.util.Log;
import android.view.MotionEvent;
import com.spacegame.entities.Actor;
import com.spacegame.entities.AnimatedActor;
import com.spacegame.entities.AnimatedEntity;
import com.spacegame.entities.BaseEnemy;
import com.spacegame.entities.Entity;
import com.spacegame.entities.Player;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.ColorHelper;
import com.spacegame.utils.Constants;
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
  /** The running state of the game. True if the game is running, false otherwise. */
  volatile boolean running = false;

  /** The list of entities in the game. */
  public final List<Entity> entities = Collections.synchronizedList(new ArrayList<>());

  /** The player entity. */
  public Player player;

  /** The pointer to the texture atlas. */
  public int textureAtlasPointer = -1;

  /** The texture atlas that contains the game's textures. */
  public TextureAtlas textureAtlas;

  volatile GameState state = GameState.PLAYING;

  /** Sceen height */
  int height;

  /** Screen width */
  int width;

  int score = 0;

  ThreadLocalRandom rng = ThreadLocalRandom.current(); // RNG is seeded with current thread

  public Game(int height, int width) {
    super();
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
    }
    setupGame();
  }

  /**
   * The main run method for the Game thread. It sets up the game and then enters a loop where it
   * updates the game and sleeps for the remainder of the frame time.
   */
  @Override
  public void run() {
    Log.d("Game", "Game Thread started on Thread: " + Thread.currentThread().getName());
    long timePerFrame = 1000 / 120; // Time for each frame in milliseconds
    running = true;
    // Set up the game
    setupGame();
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
      long startTime = System.currentTimeMillis();

      update(timePerFrame / 1000.0f); // Convert to seconds

      long endTime = System.currentTimeMillis();
      long timeSpent = endTime - startTime;

      if (timeSpent < timePerFrame) {
        try {
          Thread.sleep(timePerFrame - timeSpent);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /** Sets up the game by adding the player character to the entities list. */
  private void setupGame() {
    // Add the player character
    float playerX = this.width / 2f;
    float playerY = this.height / 2f;
    Player player = new Player(this.textureAtlas, Constants.PLAYER, playerX, playerY, 192f, 192f);
    player.setGame(this);
    this.setPlayer(player);
    addEntity(new BaseEnemy(this.textureAtlas, "ship_red_01", 500f, 500f, 338f, 166f));
    //    addEntity(new ColorEntity(500f, 500f, 100f, 100f, new float[] {1f, 0f, 1f, 1f}));
    this.state = GameState.PLAYING;
  }

  /** Pauses the game. */
  public void pauseGame() {
    synchronized (this) {
      Log.d("Game", "Game Thread paused: " + Thread.currentThread().getName());
      this.state = GameState.PAUSED;
    }
  }

  /** Resumes the game. */
  public void resumeGame() {
    synchronized (this) {
      Log.d("Game", "Game Thread resumed: " + Thread.currentThread().getName());
      this.state = GameState.PLAYING;
      notify();
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
    synchronized (entities) {
      // Remove the entities that are marked for deletion
      entities.removeIf(Entity::getDiscard);

      Vector2D playerVelocity = this.getPlayerVelocity();

      for (Entity entity : entities) {
        entity.update(deltaTime);

        List<Entity> otherEntities = new ArrayList<>(entities);
        otherEntities.remove(entity);
        if (entity.collidesWithAny(otherEntities)) {
          entity.onCollision();
          entity.setColorOverlay(ColorHelper.RED);
        } else {
          entity.disableColorOverlay();
        }

        if (entity instanceof Actor actor) {
          actor.setPlayerVelocity(playerVelocity);
        } else if (entity instanceof AnimatedActor actor) {
          actor.setPlayerVelocity(playerVelocity);
        }
      }
    }
    // TODO: Physics / Interaction-Checks here
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
   * Returns the player entity.
   *
   * @return The player entity.
   */
  public Entity getPlayer() {
    return player;
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

  /**
   * Handles a touch event. If the game is not paused, it creates an explosion at the touch location
   * and passes the touch event to the player.
   *
   * @param event The touch event to handle.
   * @see MotionEvent
   */
  public void handleTouchEvent(MotionEvent event) {
    Log.d("Game", "Touch event at: " + event.getX() + ", " + event.getY());
    Log.d("Game", "Touch Type: " + event.getActionMasked());
    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
      if (this.state == GameState.PAUSED) return;
      Log.d("Game", "Touch event at: " + event.getX() + ", " + event.getY());
      var explosion =
          new AnimatedEntity(
              this.textureAtlas,
              Constants.animation_EXPLOSION,
              event.getX(),
              event.getY(),
              192f,
              192f,
              0.03f, // Animation speed in seconds
              true);
      this.addEntity(explosion);
      if (player != null) player.onTouch(event);
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

  private void spawnRandomEntity(float x, float y) {
    AnimatedActor explosion =
        new AnimatedActor(
            this.textureAtlas,
            Constants.animation_EXPLOSION,
            x,
            y,
            192f,
            192f,
            0.03f, // Animation speed in seconds
            false);
    explosion.setZ(0);
    String randomEnemy = Constants.ENEMIES[rng.nextInt(Constants.ENEMIES.length)];
    var randomDude = new BaseEnemy(this.textureAtlas, randomEnemy, x, y, 338f, 166f);
    randomDude.scale(randomDude.getSprite().w(), randomDude.getSprite().h());
    randomDude.setZ(-1);
    randomDude.setColorOverlay(
        new float[] {rng.nextFloat(), rng.nextFloat(), rng.nextFloat(), rng.nextFloat()});
    randomDude.setRotationRad(rng.nextFloat() * (float) (2 * Math.PI));
    this.addEntity(randomDude);
    this.addEntity(explosion);
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

  public int setScore(int score) {
    this.score = score;
    return this.score;
  }

  public int getScore() {
    return this.score;
  }
}
