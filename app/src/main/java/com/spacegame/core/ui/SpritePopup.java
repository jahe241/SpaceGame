package com.spacegame.core.ui;

import com.spacegame.entities.ColorEntity;
import com.spacegame.entities.Entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpritePopup implements SpriteContainer {
  private final ColorEntity background;
  private final List<SpriteButton> buttons;
  private final List<SpriteLabel> labels;

  public SpritePopup(ColorEntity background) {
    this.background = background;
    this.background.setZ(15);
    this.buttons = new ArrayList<>();
    this.labels = new ArrayList<>();
    this.background.setVisible(false);
  }

  public void addButton(SpriteButton button) {
    button.setZ(16);
    this.buttons.add(button);
  }

  public void addLabel(SpriteLabel label) {
    label.setZ(16);
    this.labels.add(label);
  }

  public void show() {
    this.setVisible(true);
  }

  public void hide() {
    this.setVisible(false);
  }

  public boolean isVisible() {
    return this.background.isVisible();
  }

  @Override
  public Entity[] getElements() {
    List<Entity> elements = new ArrayList<>();
    elements.add(this.background);
    elements.addAll(buttons);
    for (SpriteLabel label : labels) {
      elements.addAll(Arrays.asList(label.getElements()));
    }
    return elements.toArray(new Entity[0]);
  }

  @Override
  public void setVisible(boolean visible) {
    this.background.setVisible(visible);
    for (SpriteButton button : buttons) {
      button.setVisible(visible);
    }
    for (SpriteLabel label : labels) {
      label.setVisible(visible);
    }
  }
}
