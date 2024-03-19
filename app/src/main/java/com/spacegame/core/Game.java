package com.spacegame.core;

import android.util.Log;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game extends Thread {
  volatile boolean running = false;
  public List<TextureEntity> entities = Collections.synchronizedList(new ArrayList<>());
  public Player player;
  public int textureAtlasPointer = -1;

  @Override
  public void run() {
    Log.d("Game", "Game Thread started on Thread: " + Thread.currentThread().getName());
    long timePerFrame = 1000 / 60; // Time for each frame in milliseconds
    running = true;
    this.setPlayer(
        new Player(
            500f, 500f, 200f, 100f, textureAtlasPointer, new float[] {0.5f, 0.5f, 0.5f, 1f}));

    while (running) {
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

  @Override
  public void interrupt() {
    running = false;
  }

  public void update(float deltaTime) {
    // Calls the update method for each entity: Updates Position and adjusts the vertex data based
    // on the new position
    for (TextureEntity textureEntity : entities) {
      textureEntity.update(deltaTime);
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
    entities.add(textureEntity);
  }

  public void removeEntity(TextureEntity textureEntity) {
    entities.remove(textureEntity);
  }

  public void handleTouchEvent(MotionEvent event) {
    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
      // Here we might check if the coordinates are over a button or something, otherwise implement
      // the controller
      player.onTouch(event);
    }
  }
}
