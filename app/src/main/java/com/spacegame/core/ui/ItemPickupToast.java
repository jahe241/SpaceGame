package com.spacegame.core.ui;

import com.spacegame.core.Game;
import com.spacegame.core.GameInterface;
import com.spacegame.entities.ColorEntity;
import com.spacegame.entities.Entity;
import com.spacegame.entities.inventory.items.Item;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.DebugLogger;
import com.spacegame.utils.Vector2D;

public class ItemPickupToast extends Entity {

  public static class PositionAndDimension {
    float x;
    float y;
    float width;
    float height;

    public PositionAndDimension(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }
  }

  ColorEntity background;

  SpriteLabel itemName = null;

  SpriteLabel itemDescription = null;

  Entity itemSprite = null;

  /** The height relative to the screen height */
  public static final float HEIGHT = 0.2f;

  /** The width relative to the screen width */
  public static final float WIDTH = 0.8f;

  /** Time to live, is 5 seconds */
  static final float TIME_TO_LIVE = 5;

  float timeLived = 0;

  /** Name of the item to display */
  String name;

  /** Description of the item to display */
  String description;

  public ItemPickupToast(Item item, PositionAndDimension pos) {
    super((TextureAtlas) null, (String) null, pos.x, pos.y, pos.width, pos.height);
    Vector2D screenDimensions = Game.game.getScreenDimensions();
    float halfScreenX = screenDimensions.getX() / 2f;
    float halfScreenY = screenDimensions.getY() / 2f;
    this.setX(halfScreenX);
    this.setY(halfScreenY);
    this.name = item.name;
    this.description = item.description;
    this.background =
        new ColorEntity(
            halfScreenX, halfScreenY, pos.width, pos.height, new float[] {0.5f, 0.5f, 0.5f});
    this.background.setZ(20);
    this.setVisible(true);
  }

  public static PositionAndDimension calcPositionAndDimensions() {
    Vector2D dimensions = Game.game.getScreenDimensions();
    float height = dimensions.getX() * ItemPickupToast.HEIGHT;
    float width = dimensions.getY() * ItemPickupToast.WIDTH;
    float x = dimensions.getX() / 2f;
    float y = dimensions.getY() - height / 2f;
    return new PositionAndDimension(x, y, width, height);
  }

  public void setItemName(String name) {
    float x = this.getX() - this.getWidth() / 2;
    float y = this.getY() - this.getHeight() + 20f;
    float fontSize = Math.min(this.getWidth(), this.getHeight()) * 0.05f;
    SpriteLabel itemName =
        new SpriteLabel(name, x, y, fontSize * 2, new float[] {1, 1, 1}, Game.game.textureAtlas);
    itemName.setZ(21);
    itemName.setVisible(true);
    this.itemName = itemName;
  }

  public void setItemDescription(String description) {
    float x = this.getX() - this.getWidth() / 2;
    float y = this.getY() + this.getHeight() - 20f;
    float fontSize = Math.min(this.getWidth(), this.getHeight()) * 0.05f;
    SpriteLabel itemDescription =
        new SpriteLabel(
            description, x, y, fontSize, new float[] {1f, 1f, 1f}, Game.game.textureAtlas);
    itemDescription.setZ(21);
    itemDescription.setVisible(true);
    this.itemDescription = itemDescription;
  }

  public void setItemSprite(Item item) {
    float x = this.getX() - this.getWidth() / 3;
    float y = this.getY();
    Entity itemSprite = new Entity(Game.game.textureAtlas, item.getSpriteName(), x, y, 100, 100);
    itemSprite.setZ(21);
    itemSprite.setVisible(true);
    this.itemSprite = itemSprite;
  }

  public static void create(Item item) {
    PositionAndDimension pos = calcPositionAndDimensions();
    ItemPickupToast toast = new ItemPickupToast(item, pos);
    toast.setItemName(item.name);
    toast.setItemDescription(item.description);
    toast.setItemSprite(item);
    synchronized (GameInterface.gameInterface) {
      GameInterface.gameInterface.addInterfaceElement(toast);
      GameInterface.gameInterface.addInterfaceElement(toast.itemSprite);
      GameInterface.gameInterface.addInterfaceContainer(toast.itemDescription);
      GameInterface.gameInterface.addInterfaceContainer(toast.itemName);
    }
  }

  @Override
  public void update(float delta) {

    DebugLogger.log("Item", "Is visible: " + this.isVisible());
    DebugLogger.log("Item", "X: " + this.getX() + "\tY: " + this.getY());
    DebugLogger.log("Item", "Screen Height: " + this.getX() + "\tY: " + this.getY());
    this.timeLived += delta;
    if (this.timeLived >= TIME_TO_LIVE) {
      this.setDiscard(true);
      return;
    }
    /*
    this.itemSprite.update(delta);
    for (Entity e : this.itemName.getElements()) {
      e.update(delta);
    }
    for (Entity e : this.itemDescription.getElements()) {
      e.update(delta);
    }

     */
  }
}
