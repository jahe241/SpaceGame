package com.spacegame.entities;

import android.util.Log;
import android.view.MotionEvent;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.ColorHelper;
import com.spacegame.utils.Vector2D;

public class Player extends Entity {

  /**
   * Constructor for the Player class. This constructor initializes a new Player object by calling
   * the superclass constructor with the provided parameters.
   *
   * @param textureAtlas The TextureAtlas object that contains the sprite for this player.
   * @param spriteName The name of the sprite in the texture atlas to use for this player.
   * @param x The initial x-coordinate of the player.
   * @param y The initial y-coordinate of the player.
   * @param width The width of the player.
   * @param height The height of the player.
   */
  public Player(
      TextureAtlas textureAtlas, String spriteName, float x, float y, float width, float height) {
    super(textureAtlas, spriteName, x, y, width, height);
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
    this.updateRotation(delta);
    // Update the entity's vertex data
    this.updateVertexPositionData();
  }

  @Override
  void updatePosition(float delta) {
    Vector2D oldPosition = new Vector2D(this.position);
    super.updatePosition(delta);
    this.setPosition(oldPosition);
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
