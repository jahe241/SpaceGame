package com.spacegame.core.ui;

import com.spacegame.entities.ColorEntity;
import com.spacegame.entities.Entity;
import com.spacegame.graphics.TextureAtlas;
import java.util.ArrayList;
import java.util.List;

public class SpriteLabel implements SpriteContainer {

  public static final int TEXT_SPACING = 30;
  private final List<Entity> characters = new ArrayList<>();
  private final Entity background;
  private float x; // top left, starting position
  private float y;
  private float fontSize;
  private int charCount;

  public SpriteLabel(
      String text,
      float x,
      float y,
      float fontSize,
      float[] backgroundColor,
      TextureAtlas textureAtlas) {
    this.x = x;
    this.y = y;
    this.fontSize = fontSize;
    this.charCount = text.length();
    // Calculate the offset since the text is centered, and the x, y is the top left corner
    var baseOffset = fontSize / 2;
    var backgroundWidth = (fontSize - TEXT_SPACING) * charCount + TEXT_SPACING;
    background =
        new ColorEntity(
            x + backgroundWidth / 2, y + baseOffset, backgroundWidth, fontSize, backgroundColor);
    background.setZ(9f);
    setText(text, textureAtlas);
  }

  public void setText(String text, TextureAtlas textureAtlas) {
    characters.forEach(character -> character.setVisible(false));
    characters.clear();

    float baseOffset = fontSize / 2;
    float baseX = x + baseOffset;

    for (int i = 0; i < text.length(); i++) {
      Entity character =
          new Entity(
              textureAtlas,
              "joystix_c" + text.charAt(i),
              baseX + (fontSize - TEXT_SPACING) * i,
              y + baseOffset - 16,
              fontSize,
              fontSize);
      character.setZ(10f);
      characters.add(character);
    }
  }

  @Override
  public Entity[] getElements() {
    var elements = new Entity[characters.size() + 1];
    elements[0] = background;
    for (int i = 0; i < characters.size(); i++) {
      elements[i + 1] = characters.get(i);
    }
    return elements;
  }

  @Override
  public void setVisible(boolean visible) {
    for (var character : characters) {
      character.setVisible(visible);
    }
    background.setVisible(visible);
  }
}
