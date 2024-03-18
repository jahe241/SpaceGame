package com.spacegame.core;

import android.view.MotionEvent;
import java.util.ArrayList;

public class Game {
  public ArrayList<TextureEntity> entities = new ArrayList<>();
  public TextureEntity player;

  public void update(float deltaTime) {
    // Calls the update method for each entity: Updates Position and adjusts the vertex data based
    // on the new position
    for (TextureEntity textureEntity : entities) {
      textureEntity.update(deltaTime);
    }
    // TODO: Physics / Interaction-Checks here

    // Draw the entities
    for (TextureEntity textureEntity : entities) {
      textureEntity.draw();
    }
  }

  public void setPlayer(TextureEntity player) {
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
