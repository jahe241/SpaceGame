package com.spacegame.entities;

import android.view.MotionEvent;
import androidx.annotation.NonNull;
import com.spacegame.core.Game;
import com.spacegame.entities.inventory.items.Items;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.ColorHelper;
import com.spacegame.utils.DebugLogger;
import com.spacegame.utils.Vector2D;
import java.util.ArrayList;
import java.util.Arrays;

public class Player extends Actor {
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
    this.setZ(1);

    // Collision stuff
    this.collidable = true;
    this.collisionMask = CollisionMask.PLAYER;
    this.collidesWith =
        new ArrayList<>(
            Arrays.asList(CollisionMask.ENEMY, CollisionMask.ENEMY_PROJECTILE, CollisionMask.ITEM));
    this.inventory.addItem(Items.createItem(Items.AllItems.LaserCanon, this.inventory));
    this.inventory.addItem(Items.createItem(Items.AllItems.RocketLauncher, this.inventory));
    this.inventory.addItem(Items.createItem(Items.AllItems.LaserBeam, this.inventory));
    this.setMaxHealth(3);
    this.baseSpeed = 300;
  }

  public void onTouch(MotionEvent event) {
    float touchX = event.getX();
    float touchY = event.getY();
    /*
    // Log.d("Entity", "Setting Destination to touch Event: (" + touchX + ", " + touchY + ')');
    DebugLogger.log("Movement", "Velocity: " + this.velocity.getX() + ", " + this.velocity.getY());
    DebugLogger.log(
        "Movement", "Direction: " + this.direction.getX() + ", " + this.direction.getY());
    DebugLogger.log(
        "Movement", "Current Position: " + this.position.getX() + ", " + this.position.getY());
     */
    Vector2D destination = new Vector2D(touchX, touchY);
    Vector2D direction = this.position.to(destination).normalized();

    this.setDirection(direction.mult(this.getBaseSpeed()));
  }

  @Override
  public void update(float delta) {
    // TODO: Uncomment this when the game over game state is handled
    if (this.getCurrentHealth() <= 0) {
      Game.game.onPlayerDeath();
      return;
    }
    super.update(delta);
    // DebugLogger.log("GameOver", "Player  Health: " + this.getCurrentHealth());
  }

  @Override
  public void updatePosition(float delta) {
    Vector2D oldPosition = new Vector2D(this.position);
    super.updatePosition(delta);
    this.position = oldPosition;
  }

  @Override
  public void onCollision(Actor other) {
    this.takeDamage(other);
  }

  @Override
  public void onCollisionEnd() {
    this.disableColorOverlay();
  }

  // this function dynamically changes the color of the player based on time delta
  public void updateColor() {
    float[] increment = {0.005f, 0.01f, 0.015f}; // Change these values as needed
    float[] rainbowColor = ColorHelper.getRainbowColor(colorOverlay, increment);
    colorOverlay[0] = rainbowColor[0];
    colorOverlay[1] = rainbowColor[1];
    colorOverlay[2] = rainbowColor[2];
    this.setColorOverlay(colorOverlay);
  }

  @Override
  public Vector2D getVelocity() {
    return new Vector2D(this.velocity);
  }

  @NonNull
  @Override
  public String toString() {
    return "Player{"
        + "Pos="
        + this.getPosition()
        + ", Vel="
        + this.getVelocity()
        + ", Dir="
        + this.getDirection()
        + ", Acc="
        + this.getAcceleration()
        + ", Speed="
        + this.getBaseSpeed()
        + "}";
  }

  @Override
  public void takeDamage(Actor from) {
    DebugLogger.log("GameOver", "TakeDamage: " + from.getCollisionDamage());
    DebugLogger.log("GameOver", "Before CurrentHealth: " + from.getCollisionDamage());
    super.takeDamage(from);
    DebugLogger.log("GameOver", "After CurrentHealth: " + from.getCollisionDamage());
  }
}
