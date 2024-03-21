package com.spacegame.core;

import com.spacegame.graphics.TextureAtlas;

public class ColorEntity extends Entity {

  public ColorEntity(float x, float y, float width, float height, float[] colorOverlay) {
    super(null, null, x, y, width, height);
    this.colorOverlay = colorOverlay;
    this.hasColorOverlay = true;
    this.updateauxData();
  }

  @Override
  protected void updateauxData() {
    // Update the color overlay data
    this.auxData =
        new float[] {
          // Flag = 2 for solid color
          // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A
          0.0f, 0.0f, 2.0f, this.colorOverlay[0], this.colorOverlay[1], this.colorOverlay[2],
              this.colorOverlay[3],
          1.0f, 0.0f, 2.0f, this.colorOverlay[0], this.colorOverlay[1], this.colorOverlay[2],
              this.colorOverlay[3],
          0.0f, 1.0f, 2.0f, this.colorOverlay[0], this.colorOverlay[1], this.colorOverlay[2],
              this.colorOverlay[3],
          1.0f, 1.0f, 2.0f, this.colorOverlay[0], this.colorOverlay[1], this.colorOverlay[2],
              this.colorOverlay[3]
        };
  }
}
