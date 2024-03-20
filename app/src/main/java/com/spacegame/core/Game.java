package com.spacegame.core;

import android.util.Log;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends Thread {
  volatile boolean running = false;
  volatile boolean paused = false;
  public final List<TextureEntity> entities = Collections.synchronizedList(new ArrayList<>());
  public Player player;
  public int textureAtlasPointer = -1;

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
    // Set up the game
    this.setPlayer(
        new Player(
            500f, 500f, 200f, 100f, textureAtlasPointer, new float[] {0.5f, 0.5f, 0.5f, 1f}));
    player.setZ(1); // incredibly hacky way to make sure the player is drawn on top
    // Pause "Button"
    this.addEntity(new ColorEntity(50f, 50f, 100, 100, 0));
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
      for (Quad entity : entities) {
        if (!(entity instanceof Player) && !(entity instanceof ColorEntity)) {
          entity.setRotationRad(entity.getRotationRad() + ThreadLocalRandom.current().nextFloat());
        }
        entity.update(deltaTime);
      }
    }
    // TODO: Physics / Interaction-Checks here

    // Draw the entities
    //    for (TextureEntity textureEntity : entities) {
    //      textureEntity.draw();
    //    }
  }

  public void setPlayer(Player player) {
    this.player = player;
    entities.add(player);
  }

  public TextureEntity getPlayer() {
    return player;
  }

  public void addEntity(TextureEntity textureEntity) {
    synchronized (entities) {
      entities.add(textureEntity);
    }
  }

  public void removeEntity(TextureEntity textureEntity) {
    synchronized (entities) {
      entities.remove(textureEntity);
    }
  }

  public List<TextureEntity> getEntities() {
    synchronized (entities) {
      return new ArrayList<>(entities);
    }
  }

  public void handleTouchEvent(MotionEvent event) {
    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
      // Here we might check if the coordinates are over a button or something, otherwise implement
      // the controller
      if (event.getX() < 100 && event.getY() < 100) {
        // Pause the game
        if (paused) {
          resumeGame();
        } else {
          pauseGame();
        }
        return;
      }
      if (this.paused) return;
      addEntity(new TextureEntity(event.getX(), event.getY(), 50f, 50f, textureAtlasPointer));
      if (player != null) player.onTouch(event);
    }
  }
}
