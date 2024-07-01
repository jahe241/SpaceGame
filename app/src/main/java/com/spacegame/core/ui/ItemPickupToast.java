package com.spacegame.core.ui;

import com.spacegame.core.Game;
import com.spacegame.core.GameInterface;
import com.spacegame.entities.ColorEntity;
import com.spacegame.entities.Entity;
import com.spacegame.entities.inventory.items.Item;
import com.spacegame.utils.Vector2D;

public class ItemPickupToast extends ColorEntity {

  public static final float TIME_TO_LIVE = 5f;

  private float timeLived = 0f;

  private float fontSize;

  /** The height of the toast relative to the screen height */
  public static final float HEIGHT_RELATIVE = 0.2f;

  /** The height of the toast relative to the screen height */
  public static final float WIDTH_RELATIVE = 0.8f;

  public SpriteLabel itemNameLabel;

  public SpriteLabel itemDescriptionLabel;

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
    this.setZ(10);
    this.itemNameLabel.setZ(12);
    this.itemDescriptionLabel.setZ(12);
    this.itemSprite.setZ(12);
  }

  public static void create(Item item) {
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

  public void setItemNameLabel(String name) {
    float x = this.getX() - this.getWidth() / 4;
    float y = this.getY() - 100f;
    this.itemNameLabel =
        new SpriteLabel(
            name, x, y, this.fontSize, new float[] {0f, 0f, 0f, 0f}, Game.game.textureAtlas);
  }

  public void setItemDescriptionLabel(String description) {
    float x = this.getX() - this.getWidth() / 4;
    float y = this.getY() + 100f;
    this.itemDescriptionLabel =
        new SpriteLabel(
            description,
            x,
            y,
            this.fontSize * 0.6f,
            new float[] {0f, 0f, 0f, 0f},
            Game.game.textureAtlas);
  }

  public void setItemSprite(Item item) {
    float x = this.getX() / 3;
    float y = this.getY();
    this.itemSprite =
        new Entity(
            Game.game.textureAtlas,
            item.getSpriteName(),
            x,
            y,
            this.getHeight() / 2,
            this.getHeight() / 2);
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
    /*
    if (this.timeLived < 1) {
      this.setOpacity(timeLived);
    } else if (this.timeLived >= 1 && this.timeLived < TIME_TO_LIVE - 1) {
      this.setOpacity(1f);
    } else if (this.timeLived > TIME_TO_LIVE - 1) {
      this.setOpacity(Math.max(TIME_TO_LIVE - this.timeLived, 0));
    }

     */
    if (timeLived >= TIME_TO_LIVE) {
      this.setDiscard(true);
    }
  }

  public void setOpacity(float opacity) {
    this.vbo().setOpacity(opacity);
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
