package com.spacegame.core.ui;

import com.spacegame.entities.ColorEntity;
import com.spacegame.entities.Entity;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.utils.ColorHelper;
import com.spacegame.utils.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteLabel implements SpriteContainer {

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

  private final List<Entity> characters = new ArrayList<>();
  private final Entity background;
  private float ADDITIONAL_CHAR_SPACE = 0;
  private float x; // top left, starting position
  private float y;
  private float fontSize;
  private int charCount;
  private int length;
  private boolean isVisible = true;
  private float z = 10f;
  private TextureAtlas textureAtlas;
  private boolean needsUpdate = false;

  public SpriteLabel(
      String text,
      float x,
      float y,
      float fontSize,
      float[] backgroundColor,
      TextureAtlas textureAtlas) {
    this.ADDITIONAL_CHAR_SPACE = -(fontSize * .25f);
    this.textureAtlas = textureAtlas;
    this.x = x;
    this.y = y;
    this.fontSize = fontSize;
    this.charCount = text.length();
    // Calculate the offset since the text is centered, and the x, y is the top left corner
    var baseOffset = fontSize / 2;
    var backgroundWidth = (fontSize - ADDITIONAL_CHAR_SPACE) * charCount + ADDITIONAL_CHAR_SPACE;
    background =
        new ColorEntity(
            x + backgroundWidth / 2, y + baseOffset, backgroundWidth, fontSize, backgroundColor);
    background.setZ(9f);
    setText(text);
    this.length = text.length();
  }

  public void setText(String text) {
    updateCharacters(text);
    this.length = text.length();
  }

  private void updateCharacters(String text) {
    //    DebugLogger.log("Textrender", "Updating characters for text: " + text);
    float baseOffset = fontSize / 2;
    float baseX = x + baseOffset;

    if (text.length() > characters.size()) {
      for (int i = characters.size(); i < text.length(); i++) {
        characters.add(
            new Entity(
                this.textureAtlas,
                Constants.FONT_PREFIX + "0",
                baseX,
                y + baseOffset - 16,
                fontSize,
                fontSize));
      }
    } else if (text.length() < characters.size()) {
      // Hide excess entities
      for (int i = text.length(); i < characters.size(); i++) {
        var character = characters.get(i);
        character.setVisible(false);
        character.setColorOverlay(ColorHelper.TRANSPARENT);
      }
    }

    for (int i = 0; i < text.length(); i++) {
      char currentChar = text.charAt(i);
      Entity character = characters.get(i);

      if (currentChar != ' ') {
        character.setSprite(parseCharacterName(text.toLowerCase().charAt(i)));
        character.setVisible(this.isVisible);
        character.disableColorOverlay();
      } else {
        character.setVisible(false);
        character.setColorOverlay(ColorHelper.TRANSPARENT);
      }

      character.setX(baseX);
      character.setY(y + baseOffset - 16);
      character.setZ(this.z);

      baseX += (fontSize + ADDITIONAL_CHAR_SPACE);
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
    this.isVisible = visible;
    for (var character : characters) {
      character.setVisible(visible);
    }
    background.setVisible(visible);
  }

  public void setZ(float z) {
    this.z = z;
    for (var character : characters) {
      character.setZ(z);
    }
    background.setZ(z - 1);
  }

  public boolean isNeedsUpdate() {
    return needsUpdate;
  }
}
