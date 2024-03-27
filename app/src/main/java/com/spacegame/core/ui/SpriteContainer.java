package com.spacegame.core.ui;

import com.spacegame.entities.Entity;

public interface SpriteContainer {
  /**
   * Returns the sprite entities of the container.
   *
   * @return An array containing the sprite entities of the container.
   */
  Entity[] getElements();

  /**
   * Sets the visibility status of the container.
   *
   * @param visible The new visibility status of the container.
   */
  void setVisible(boolean visible);
}
