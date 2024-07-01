package com.spacegame.core.ui;

import com.spacegame.entities.Entity;

public class ItemPickupToast implements SpriteContainer {

  /** The height of the toast relative to the screen height */
  public static final float HEIGHT_RELATIVE = 0.2f;

  /** The height of the toast relative to the screen height */
  public static final float WIDTH_RELATIVE = 0.8f;

  @Override
  public Entity[] getElements() {
    return new Entity[0];
  }

  @Override
  public void setVisible(boolean visible) {}
}
