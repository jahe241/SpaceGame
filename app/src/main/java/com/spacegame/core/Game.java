package com.spacegame.core;

import android.view.MotionEvent;
import java.util.ArrayList;

public class Game {
  public ArrayList<Entity> entities = new ArrayList<>();
  public Entity player;

  public void update(float deltaTime) {
    for (Entity entity : entities) {
      entity.update(deltaTime);
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
