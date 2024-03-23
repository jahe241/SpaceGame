package com.spacegame.core;

import android.util.Log;
import com.spacegame.entities.Entity;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.Constants;
import com.spacegame.utils.Vector2D;

public class GamePad {
  Entity pad;
  Entity stick;
  TextureAtlas textureAtlas;
  Vector2D screenSize;

  boolean visible = true;
  float radius;

  public GamePad(TextureAtlas textureAtlas, float screenWidth, float screenHeight) {
    this.textureAtlas = textureAtlas;
    this.screenSize = new Vector2D(screenWidth, screenHeight);
    this.pad = new Entity(textureAtlas, Constants.GAMEPAD[0], 500, 500, 400f, 400f);
    this.stick = new Entity(textureAtlas, Constants.GAMEPAD[1], 500, 500, 200f, 200f);
    this.radius = this.pad.getWidth() / 2; // we might want to tweak this later
    this.pad.hide();
    this.stick.hide();
  }

  public Entity[] getPadElements() {
    return new Entity[] {pad, stick};
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void updateStickPosition(float touchX, float touchY) {
    Vector2D touchPosition = new Vector2D(touchX, touchY);
    Vector2D padPosition = pad.getPosition();
    Log.d("GamePad", "Touch Position: " + touchPosition + " Pad Position: " + padPosition);
    Vector2D direction = touchPosition.sub(padPosition);
    float distance = direction.length();

    if (distance < radius) {
      stick.setPosition(touchPosition);
    } else {
      Vector2D normalizedDirection = direction.normalized();
      Vector2D stickPosition = padPosition.add(normalizedDirection.mult(radius));
      stick.setPosition(stickPosition);
    }
  }

  public Vector2D getStickDirection() {
    Vector2D padPosition = pad.getPosition();
    Vector2D stickPosition = stick.getPosition();
    Vector2D direction = stickPosition.sub(padPosition);
    return direction.normalized();
  }

  public void resetStickPosition() {
    stick.setPosition(pad.getPosition());
  }

  public void showGamePad(float x, float y) {
    this.visible = true;
    this.pad.show();
    this.stick.show();
    var vec = new Vector2D(x, y);
    pad.setPosition(vec);
    stick.setPosition(vec);
    pad.disableColorOverlay();
    stick.disableColorOverlay();
    Log.d("GamePad", "Showing GamePad at: " + vec);
  }

  public void hideGamePad() {
    this.visible = false;
    this.pad.hide();
    this.stick.hide();
    this.resetStickPosition();
  }
}
