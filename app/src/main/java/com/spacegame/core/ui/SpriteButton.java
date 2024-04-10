package com.spacegame.core.ui;

import com.spacegame.core.ButtonType;
import com.spacegame.core.InterfaceState;
import com.spacegame.entities.ColorEntity;
import com.spacegame.entities.Entity;
import com.spacegame.graphics.Sprite;
import com.spacegame.graphics.TextureAtlas;

/**
 * The SpriteButton class extends the Entity class and represents a button in the game. It contains
 * information about the button's position, size, state, and type.
 */
public class SpriteButton extends Entity implements SpriteContainer {
  /** The sprite to display when the button is pressed. */
  Sprite spriteDown;

  /** The sprite to display when the button is not pressed. */
  Sprite spriteUp;

  ColorEntity background;

  /** The x-coordinate of the button's center. */
  float x;

  /** The y-coordinate of the button's center. */
  float y;

  /** The height of the button. */
  float height;

  /** The width of the button. */
  float width;

  /** The state of the button. True if the button is pressed, false otherwise. */
  boolean isDown = false;

  /** The active state of the button. True if the button is active, false otherwise. */
  boolean isActive;

  /**
   * The state during which the button is active. This variable is used to control the active state
   * of the button based on the current interface state. For example, if activeDuringState is set to
   * InterfaceState.PLAYING, the button will be active only when the interface state is PLAYING.
   */
  InterfaceState activeDuringState;

  /** The type of the button. */
  ButtonType buttonType;

  /**
   * Constructor for the SpriteButton class. This constructor initializes a new SpriteButton object
   * by setting its position, size, type, and active state.
   *
   * @param textureAtlas The texture atlas that contains the button's sprites.
   * @param name The name of the sprite to display when the button is not pressed.
   * @param nameDown The name of the sprite to display when the button is pressed.
   * @param x The x-coordinate of the button's center.
   * @param y The y-coordinate of the button's center.
   * @param width The width of the button.
   * @param height The height of the button.
   * @param buttonType The type of the button.
   * @param isActive The active state of the button.
   */
  public SpriteButton(
      TextureAtlas textureAtlas,
      String name,
      String nameDown,
      float x,
      float y,
      float width,
      float height,
      ButtonType buttonType,
      boolean isActive,
      float[] backgroundColor) {
    super(textureAtlas, name, x, y, width, height);
    this.background = new ColorEntity(x, y, width, height, backgroundColor);
    this.spriteUp = textureAtlas.getSprite(name);
    this.spriteDown = textureAtlas.getSprite(nameDown);
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.buttonType = buttonType;
    this.isActive = isActive;
    this.setZ(10);
    this.background.setZ(9);
  }

  /**
   * Checks if a touch event is within the button's bounds.
   *
   * @param x The x-coordinate of the touch event.
   * @param y The y-coordinate of the touch event.
   * @return True if the touch event is within the button's bounds, false otherwise.
   */
  public boolean isTouchWithinButton(float x, float y) {
    float[] coords = this.getButtonCoords();
    return x > coords[0] && x < coords[2] && y > coords[3] && y < coords[1];
  }

  /**
   * Returns the top right and bottom left coordinates of the button.
   *
   * @return A float array containing the top right and bottom left coordinates of the button.
   */
  public float[] getButtonCoords() {
    // could be optimized by reading values from the vertex data
    float[] coords = new float[4];
    coords[0] = this.x - this.background.getWidth() / 2;
    coords[1] = this.y + this.background.getHeight() / 2;
    coords[2] = this.x + this.background.getWidth() / 2;
    coords[3] = this.y - this.background.getHeight() / 2;
    return coords;
  }

  /**
   * Toggles the button's state and updates its sprite.
   *
   * @return The type of the button.
   * @see ButtonType
   */
  public ButtonType click() {
    this.isDown = !this.isDown;
    this.setSprite(this.isDown ? this.spriteDown : this.spriteUp);
    return this.buttonType;
  }

  /**
   * Returns the active state of the button.
   *
   * @return True if the button is active, false otherwise.
   */
  public boolean isActive() {
    return this.isActive;
  }

  @Override
  public Entity[] getElements() {
    return new Entity[] {this, this.background};
  }

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
    this.isActive = visible;
    this.background.setVisible(visible);
  }

  public SpriteContainer setActiveDuringState(InterfaceState state) {
    this.activeDuringState = state;
    return this;
  }

  public InterfaceState getActiveState() {
    return this.activeDuringState;
  }
}
