package com.spacegame.core;

import android.view.MotionEvent;
import com.spacegame.utils.ColorHelper;

public class Player extends Entity {

  public Player(float x, float y, float width, float height, int gl_texture_ptr) {
    super(x, y, width, height, gl_texture_ptr);
  }

  public Player(
      float x, float y, float width, float height, int gl_texture_ptr, float[] colorOverlay) {
    super(x, y, width, height, gl_texture_ptr, colorOverlay);
  }

  public void onTouch(MotionEvent event) {
    float touchX = event.getX();
    float touchY = event.getY();
    // Log.d("Entity", "Setting Destination to touch Event: (" + touchX + ", " + touchY + ')');

    this.setDestination(touchX, touchY);
  }

  @Override
  public void update(float delta) {
    this.updateColor();
    // Update the entity's position
    this.updatePosition(delta);
    // Update the entity's vertex data
    this.updatePositionData();
  }

  // this function dynamically changes the color of the player based on time delta
  public void updateColor() {
    float[] increment = {0.005f, 0.01f, 0.015f}; // Change these values as needed
    float[] rainbowColor = ColorHelper.getRainbowColor(colorOverlay, increment);
    colorOverlay[0] = rainbowColor[0];
    colorOverlay[1] = rainbowColor[1];
    colorOverlay[2] = rainbowColor[2];
    this.updateauxData();
  }
}
