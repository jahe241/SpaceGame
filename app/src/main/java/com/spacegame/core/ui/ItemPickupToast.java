package com.spacegame.core.ui;

import com.spacegame.core.Game;
import com.spacegame.core.GameInterface;
import com.spacegame.entities.ColorEntity;
import com.spacegame.entities.Entity;
import com.spacegame.entities.inventory.items.Item;
import com.spacegame.utils.Vector2D;
import java.util.LinkedList;
import java.util.Queue;

// TODO: Clean up
// TODO: Adjust centering of text

/**
 * The class handling the toast, when a item was picked up
 */
public class ItemPickupToast extends ColorEntity {

  /**
   * The queue for displaying the items picked up.
   * This makes sure all items that were picked up are being displayed.
   */
  public static Queue<Item> queue = new LinkedList<>();

  /**
   * If an item toast is currently displaying
   */
  public static boolean isDisplaying = false;

  /**
   * How long the toast should be visible in seconds.
   */
  public static final float TIME_TO_LIVE = 3f;

  /**
   * The current time lived of the current toast
   */
  private float timeLived = 0f;

  /**
   * The font size used for the toasts
   */
  private float fontSize;

  /** The height of the toast relative to the screen height */
  public static final float HEIGHT_RELATIVE = 0.1f;

  /** The height of the toast relative to the screen height */
  public static final float WIDTH_RELATIVE = 0.8f;

  /** The Sprite Label that hold the item name text */
  public SpriteLabel itemNameLabel;

  /** The Sprite Label that hold the item description text */
  public SpriteLabel itemDescriptionLabel;

  /** The Entity that hold the item sprite */
  public Entity itemSprite;

  public ItemPickupToast(Item item, Vector2D position, Vector2D dimensions) {
    super(
        position.getX(),
        position.getY(),
        dimensions.getX(),
        dimensions.getY(),
        new float[] {0.5f, 0.5f, 0.5f, 1f});
    Vector2D screenDimensions = Game.game.getScreenDimensions();
    this.fontSize = Math.min(screenDimensions.getX(), screenDimensions.getY()) * 0.05f;
    this.setItemNameLabel(item.name);
    this.setItemDescriptionLabel(item.description);
    this.setItemSprite(item);
    this.setZ(17);
    this.itemNameLabel.setZ(18);
    this.itemDescriptionLabel.setZ(18);
    this.itemSprite.setZ(18);
  }

  /**
   * Creates a toast for the given item
   * @param item
   */
  public static void create(Item item) {
    if (ItemPickupToast.isDisplaying) {
      ItemPickupToast.queue.add(item);
      return;
    }
    ItemPickupToast.isDisplaying = true;
    Vector2D screenDimension = Game.game.getScreenDimensions();
    float width = screenDimension.getX() * WIDTH_RELATIVE;
    float height = screenDimension.getY() * HEIGHT_RELATIVE;
    float x = screenDimension.getX() / 2;
    float y = screenDimension.getY() - height / 2 - 100f;
    ItemPickupToast ret =
        new ItemPickupToast(item, new Vector2D(x, y), new Vector2D(width, height));
    synchronized (GameInterface.gameInterface) {
      GameInterface.gameInterface.addInterfaceElement(ret);
      GameInterface.gameInterface.addInterfaceElement(ret.itemSprite);
      GameInterface.gameInterface.addInterfaceContainer(ret.itemNameLabel);
      GameInterface.gameInterface.addInterfaceContainer(ret.itemDescriptionLabel);
    }
  }

  /**
   * Setter for the itemNameLabel
   * @param name
   */
  public void setItemNameLabel(String name) {
    this.itemNameLabel =
        new SpriteLabel(
            name,
            this.getX(),
            this.getY(),
            this.fontSize,
            new float[] {0f, 0f, 0f, 0f},
            Game.game.textureAtlas);
    // Item name in the top quarter of the box
    float y = this.getY() - this.getHeight() / 2 + (this.getHeight() * 0.25f);
    // Center the text
    float x = this.getX() - this.itemNameLabel.getWidth() / 2;
    this.itemNameLabel.setPosition(new Vector2D(x, y));
  }

  /**
   * Setter for itemDescriptionLabel
   * @param description
   */
  public void setItemDescriptionLabel(String description) {
    this.itemDescriptionLabel =
        new SpriteLabel(
            description,
            this.getX(),
            this.getY(),
            this.fontSize * 0.4f,
            new float[] {0f, 0f, 0f, 0f},
            Game.game.textureAtlas);
    // Item name in the top quarter of the box
    float y = this.getY() + this.getHeight() / 2 - (this.getHeight() * 0.25f);
    // Center the text
    float x = this.getX() - this.itemDescriptionLabel.getWidth() / 2;
    this.itemDescriptionLabel.setPosition(new Vector2D(x, y));
  }

  public void setItemSprite(Item item) {
    float x = this.getX() / 3;
    float y = this.getY();
    this.itemSprite =
        new Entity(
            Game.game.textureAtlas, item.getSpriteName(), x, y, this.getHeight(), this.getHeight());
  }

  @Override
  public void setDiscard(boolean discard) {
    super.setDiscard(discard);
    this.itemSprite.setDiscard(true);
    for (Entity e : this.itemNameLabel.getElements()) {
      e.setDiscard(discard);
    }
    for (Entity e : this.itemDescriptionLabel.getElements()) {
      e.setDiscard(discard);
    }
  }

  @Override
  public void update(float delta) {
    super.update(delta);
    this.timeLived += delta;
    if (this.timeLived < 1) {
      this.setOpacity(timeLived);
    } else if (this.timeLived >= 1 && this.timeLived < TIME_TO_LIVE - 1) {
      this.setOpacity(1f);
    } else if (this.timeLived > TIME_TO_LIVE - 1) {
      this.setOpacity(Math.max(TIME_TO_LIVE - this.timeLived, 0));
    }

    if (timeLived >= TIME_TO_LIVE) {
      this.setDiscard(true);
      ItemPickupToast.isDisplaying = false;
      if (!ItemPickupToast.queue.isEmpty()) {
        ItemPickupToast.create(ItemPickupToast.queue.poll());
      }
    }
  }

  /**
   * Sets the opacity for the whole toast
   * @param opacity
   */
  public void setOpacity(float opacity) {
    // this.vbo().setOpacity(opacity);
    this.vbo().setColor(new float[] {0.5f, 0.5f, 0.5f, opacity});
    this.vbo().setFlagSolidColor();
    this.itemSprite.vbo().setOpacity(opacity);
    for (Entity e : this.itemNameLabel.getElements()) {
      e.vbo().setOpacity(opacity);
    }
    for (Entity e : this.itemDescriptionLabel.getElements()) {
      e.vbo().setOpacity(opacity);
    }
  }
}
