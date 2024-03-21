package com.spacegame.core;

import android.util.Log;
import android.view.MotionEvent;
import com.spacegame.utils.ColorHelper;
import com.spacegame.utils.TextureAtlas;
import com.spacegame.utils.Vector2D;

public class Player extends Entity {

  public Player(
      TextureAtlas textureAtlas,
      int spriteX,
      int spriteY,
      float x,
      float y,
      float width,
      float height) {
    super(textureAtlas, spriteX, spriteY, x, y, width, height);
  }

  public Player(
      TextureAtlas textureAtlas,
      int spriteX,
      int spriteY,
      float x,
      float y,
      float width,
      float height,
      float[] colorOverlay) {
    super(textureAtlas, spriteX, spriteY, x, y, width, height, colorOverlay);
  }

  public void onTouch(MotionEvent event) {
    float touchX = event.getX();
    float touchY = event.getY();
    // Log.d("Entity", "Setting Destination to touch Event: (" + touchX + ", " + touchY + ')');
    Log.d("Movement", "Velocity: " + this.velocity.getX() + ", " + this.velocity.getY());
    Log.d("Movement", "Direction: " + this.direction.getX() + ", " + this.direction.getY());
    Log.d("Movement", "Current Position: " + this.position.getX() + ", " + this.position.getY());

    Vector2D destination = new Vector2D(touchX, touchY);
    Vector2D direction = this.position.to(destination).normalized();

    this.setDirection(direction.mult(this.getBaseSpeed()));
  }

  @Override
  public void update(float delta) {
    //    this.updateColor(); // Rainbow color effect
    // Update the entity's position
    this.updatePosition(delta);
    // Update the entity's vertex data
    this.updateVertexPositionData();
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
