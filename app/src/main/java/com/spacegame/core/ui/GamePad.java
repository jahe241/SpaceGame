package com.spacegame.core.ui;

import android.util.Log;
import com.spacegame.core.ui.SpriteContainer;
import com.spacegame.entities.Entity;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.Constants;
import com.spacegame.utils.Vector2D;

/**
 * The GamePad class represents the gamepad in the game. It contains methods to handle the gamepad's
 * visibility, position, and direction.
 */
public class GamePad implements SpriteContainer {
  // The pad entity of the gamepad
  Entity pad;
  // The stick entity of the gamepad
  Entity stick;
  // The texture atlas used for the gamepad's entities
  TextureAtlas textureAtlas;
  // The screen size
  Vector2D screenSize;
  // The visibility status of the gamepad
  boolean visible = true;
  // The radius used to limit the sticks movement
  float radius;

  /**
   * Constructor for the GamePad class. This constructor initializes a new GamePad object by setting
   * its texture atlas and screen size. The textures for the pad and stick are loaded from the
   * texture atlas using the set names in Constants.GAMEPAD.
   *
   * @param textureAtlas The texture atlas used for the gamepad's entities.
   * @param screenWidth The width of the screen.
   * @param screenHeight The height of the screen.
   */
  public GamePad(TextureAtlas textureAtlas, float screenWidth, float screenHeight) {
    this.textureAtlas = textureAtlas;
    this.screenSize = new Vector2D(screenWidth, screenHeight);
    this.pad = new Entity(textureAtlas, Constants.GAMEPAD[0], 500, 500, 256f, 256f);
    this.stick = new Entity(textureAtlas, Constants.GAMEPAD[1], 500, 500, 64f, 64f);
    this.radius = this.pad.getWidth() / 2; // we might want to tweak this later
    this.pad.setZ(9);
    this.stick.setZ(10);
    this.pad.hide();
    this.stick.hide();
  }

  /**
   * Returns the pad and stick entities of the gamepad.
   *
   * @return An array containing the pad and stick entities of the gamepad.
   */
  @Override
  public Entity[] getElements() {
    return new Entity[] {pad, stick};
  }

  /**
   * Sets the visibility status of the gamepad.
   *
   * @param visible The new visibility status of the gamepad.
   */
  @Override
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Returns the visibility status of the gamepad.
   *
   * @return The visibility status of the gamepad.
   */
  public boolean isVisible() {
    return this.visible;
  }

  /**
   * Updates the position of the gamepad's stick based on the touch position.
   *
   * @param touchX The x-coordinate of the touch position.
   * @param touchY The y-coordinate of the touch position.
   */
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

  /**
   * Returns the direction of the gamepad's stick.
   *
   * @return The direction of the gamepad's stick.
   */
  public Vector2D getStickDirection() {
    Vector2D padPosition = pad.getPosition();
    Vector2D stickPosition = stick.getPosition();
    Vector2D direction = stickPosition.sub(padPosition);
    return direction.normalized();
  }

  /** Resets the position of the gamepad's stick to the position of the pad. */
  public void resetStickPosition() {
    stick.setPosition(pad.getPosition());
  }

  /**
   * Shows the gamepad at the specified position.
   *
   * @param x The x-coordinate of the position.
   * @param y The y-coordinate of the position.
   */
  public void showGamePad(float x, float y) {
    this.visible = true;
    var vec = new Vector2D(x, y);
    pad.setPosition(vec);
    stick.setPosition(vec);
    this.pad.show();
    this.stick.show();
    this.pad.updatePositionVertex(); // technically it can be updated twice per frame
    this.stick.updatePositionVertex(); // but otherwise might ghost a bit
    Log.d("GamePad", "Showing GamePad at: " + vec);
  }

  /** Hides the gamepad and resets the position of the stick. */
  public void hideGamePad() {
    this.visible = false;
    this.pad.hide();
    this.stick.hide();
    this.resetStickPosition();
  }
}
