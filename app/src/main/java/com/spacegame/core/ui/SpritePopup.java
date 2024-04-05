package com.spacegame.core.ui;

import com.spacegame.entities.ColorEntity;
import java.util.ArrayList;
import java.util.List;

public class SpritePopup {
  private final ColorEntity background;
  private final List<SpriteButton> buttons;
  private final List<SpriteLabel> labels;

  public SpritePopup(ColorEntity background) {
    this.background = background;
    this.buttons = new ArrayList<>();
    this.labels = new ArrayList<>();
  }

  public void addButton(SpriteButton button) {
    this.buttons.add(button);
  }

  public void addLabel(SpriteLabel label) {
    this.labels.add(label);
  }

  public void show() {
    this.background.setVisible(true);
    for (SpriteButton button : buttons) {
      button.setVisible(true);
    }
    for (SpriteLabel label : labels) {
      label.setVisible(true);
    }
  }

  public void hide() {
    this.background.setVisible(false);
    for (SpriteButton button : buttons) {
      button.setVisible(false);
    }
    for (SpriteLabel label : labels) {
      label.setVisible(false);
    }
  }

  public boolean isVisible() {
    return this.background.isVisible();
  }
}
