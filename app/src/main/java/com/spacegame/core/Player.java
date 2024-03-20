package com.spacegame.core;

import android.view.MotionEvent;
import com.spacegame.utils.Vector2D;

public class Player extends TextureEntity {

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

    Vector2D destination = new Vector2D(touchX, touchY);
    Vector2D direction = this.position.to(destination).normalized();

    this.setVelocity(direction.mult(this.getBaseSpeed()));
  }
}
