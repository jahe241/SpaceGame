package com.spacegame.core.ui;

import com.spacegame.entities.ColorEntity;
import com.spacegame.entities.Entity;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteLabel implements SpriteContainer {

  public static final int TEXT_SPACING = 30;
  private final List<Entity> characters = new ArrayList<>();
  private final Entity background;
  private float x; // top left, starting position
  private float y;
  private float fontSize;
  private int charCount;
  private int length;

  private TextureAtlas textureAtlas;

  private static final Map<Character, String> problematicChars = new HashMap<>();

  static {
    problematicChars.put('&', "ampersand");
    problematicChars.put('*', "asterisk");
    problematicChars.put('@', "at");
    problematicChars.put('\\', "backslash");
    problematicChars.put(':', "colon");
    problematicChars.put(',', "comma");
    problematicChars.put('$', "dollar");
    problematicChars.put('=', "equals");
    problematicChars.put('!', "exclamation");
    problematicChars.put('>', "greater_than");
    problematicChars.put('<', "less_than");
    problematicChars.put('-', "minus");
    problematicChars.put('%', "percent");
    problematicChars.put('.', "period");
    problematicChars.put('+', "plus");
    problematicChars.put('?', "question");
    problematicChars.put(';', "semicolon");
    problematicChars.put('/', "slash");
    problematicChars.put('^', "caret");
    problematicChars.put('~', "tilde");
    problematicChars.put('_', "underscore");
    problematicChars.put('|', "vertical_bar");
    problematicChars.put('#', "hash");
    problematicChars.put('{', "left_curly_brace");
    problematicChars.put('}', "right_curly_brace");
    problematicChars.put('[', "left_square_bracket");
    problematicChars.put(']', "right_square_bracket");
    problematicChars.put('(', "left_parenthesis");
    problematicChars.put(')', "right_parenthesis");
    problematicChars.put('"', "quote");
    problematicChars.put('\'', "apostrophe");
    problematicChars.put('`', "backtick");
  }

  public SpriteLabel(
      String text,
      float x,
      float y,
      float fontSize,
      float[] backgroundColor,
      TextureAtlas textureAtlas) {
    this.textureAtlas = textureAtlas;
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
    initializeCharacters(text.length());
    setText(text);
    this.length = text.length();
  }

  private void initializeCharacters(int length) {
    if (this.characters.isEmpty()) {
      for (int i = 0; i < length; i++) {
        characters.add(
            new Entity(
                this.textureAtlas, Constants.FONT_PREFIX + "0", -100, -100, fontSize, fontSize));
      }
    }
  }

  public void setText(String text) {
    float baseOffset = fontSize / 2;
    float baseX = x + baseOffset;

    for (int i = 0; i < text.length() && i < characters.size(); i++) {
      char currentChar = text.charAt(i);
      if (currentChar != ' ') {
        Entity character = characters.get(i);
        character.setSprite(parseCharacterName(text.toLowerCase().charAt(i)));
        character.setX(baseX);
        character.setY(y + baseOffset - 16);
        character.setZ(10f);
        character.setVisible(true);
      }
      baseX += (fontSize - TEXT_SPACING);
    }

    // Hide remaining entities if new text is shorter
    for (int i = text.length(); i < characters.size(); i++) {
      characters.get(i).setVisible(false);
    }
  }

  private String parseCharacterName(char c) {
    String characterName = Constants.FONT_PREFIX;
    if (Character.isLetterOrDigit(c)) {
      characterName += c;
    } else if (problematicChars.containsKey(c)) {
      characterName += problematicChars.get(c);
    }
    return characterName;
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
