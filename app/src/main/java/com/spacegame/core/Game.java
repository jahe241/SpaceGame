package com.spacegame.core;

import android.view.MotionEvent;
import java.util.ArrayList;

public class Game {
  public ArrayList<Entity> entities = new ArrayList<>();
  public Entity player;

  public void update(float deltaTime) {
    // Calls the update method for each entity: Updates Position and adjusts the vertex data based
    // on the new position
    for (Entity entity : entities) {
      entity.update(deltaTime);
    }
    // TODO: Physics / Interaction-Checks here

    // Draw the entities
    for (Entity entity : entities) {
      entity.draw();
    }
  }

  public void setPlayer(Entity player) {
    this.player = player;
    entities.add(player);
  }

  public Entity getPlayer() {
    return player;
  }

  public void addEntity(Entity entity) {
    entities.add(entity);
  }

  public void removeEntity(Entity entity) {
    entities.remove(entity);
  }

  public void handleTouchEvent(MotionEvent event) {
    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
      // Here we might check if the coordinates are over a button or something, otherwise implement
      // the controller
      player.onTouch(event);
    }
  }
}
