package com.spacegame.core;

import android.util.Log;
import android.view.MotionEvent;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.sound.SoundEngine;
import com.spacegame.utils.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends Thread {
  volatile boolean running = false;
  volatile boolean paused = false;
  public final List<Entity> entities = Collections.synchronizedList(new ArrayList<>());
  public Player player;
  public int textureAtlasPointer = -1;
  public TextureAtlas textureAtlas;

  @Override
  public void run() {
    Log.d("Game", "Game Thread started on Thread: " + Thread.currentThread().getName());
    long timePerFrame = 1000 / 60; // Time for each frame in milliseconds
    running = true;
    // Set up the game
    setupGame();
    // Game Loop
    while (running) {
      synchronized (this) {
        while (paused) {
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

  private void setupGame() {
    // Add the player character
    this.setPlayer(new Player(this.textureAtlas, Constants.PLAYER, 500f, 1000f, 500f, 200f));
    this.player.setZ(1); // incredibly hacky way to make sure the player is drawn on top
    //    this.player.setColorOverlay(new float[] {1f, 0f, 0f, 1f});

    // Pause "Button"
    var pauseButton = new ColorEntity(100f, 100f, 200, 200, new float[] {1f, 0f, 1f, 1f});
    pauseButton.setZ(5); // Draw on top of everything
    this.addEntity(pauseButton);
  }

  public void pauseGame() {
    synchronized (this) {
      Log.d("Game", "Game Thread paused: " + Thread.currentThread().getName());
      paused = true;
    }
  }

  public void resumeGame() {
    synchronized (this) {
      Log.d("Game", "Game Thread resumed: " + Thread.currentThread().getName());
      paused = false;
      notify();
    }
  }

  @Override
  public void interrupt() {
    running = false;
  }

  public void update(float deltaTime) {
    // Calls the update method for each entity: Updates Position and adjusts the vertex data based
    // on the new position
    synchronized (entities) {
      // Remove the entities that are marked for deletion
      entities.removeIf(Entity::getDiscard);

      for (Quad entity : entities) {
        if (!(entity instanceof Player)
            && !(entity instanceof ColorEntity)
            && !(entity instanceof AnimatedEntity)) {
          entity.setRotationRad(entity.getRotationRad() + ThreadLocalRandom.current().nextFloat());
        }
        entity.update(deltaTime);
      }
    }
    // TODO: Physics / Interaction-Checks here
  }

  public void setPlayer(Player player) {
    this.player = player;
    entities.add(player);
  }

  public Entity getPlayer() {
    return player;
  }

  public void addEntity(Entity entity) {
    synchronized (entities) {
      entities.add(entity);
    }
  }

  public void removeEntity(Entity entity) {
    synchronized (entities) {
      entities.remove(entity);
    }
  }

  public List<Entity> getEntities() {
    synchronized (entities) {
      return new ArrayList<>(entities);
    }
  }

  public void handleTouchEvent(MotionEvent event) {
    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
      if (this.paused) return;
      var explosion =
          new AnimatedEntity(
              this.textureAtlas,
              Constants.animation_EXPLOSION,
              event.getX(),
              event.getY(),
              192f,
              192f,
              0.03f, // Animation speed in seconds
              false);
      this.addEntity(explosion);
      if (player != null) player.onTouch(event);
    }
  }
}
