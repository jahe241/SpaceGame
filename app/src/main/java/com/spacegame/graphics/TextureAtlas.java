package com.spacegame.graphics;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * The TextureAtlas class represents a texture atlas for a game. A texture atlas is a large image
 * containing many smaller sub-images, each of which is a texture for some part of a game object.
 * The class maintains a map of sprite names to their respective Sprite objects, and another map for
 * animation sprite sequences. It also provides methods to retrieve a sprite or an animation
 * sequence by name, and to calculate the UV coordinates for a sprite. The class is initialized by
 * parsing an XML resource that describes the texture atlas.
 */
public class TextureAtlas {
  private final Context context;

  /** OpenGL ptr to the loaded Texture atlas. */
  private final int textureId;

  /** Internal Map of sprite names to their respective Sprite objects. */
  private final Map<String, Sprite> sprites = new HashMap<>();

  /** Internal Map of animation sprite sequences names to their respective Sprite objects. */
  private final Map<String, List<Sprite>> animationSpriteCache = new HashMap<>();

  /** Width of the texture atlas in pixels. */
  private int atlasWidth;

  /** Height of the texture atlas in pixels. */
  private int atlasHeight;

  /**
   * Constructor for the TextureAtlas class. This constructor initializes a new TextureAtlas object
   * by parsing the provided XML resource.
   *
   * @param context The application context.
   * @param xmlResourceId The resource ID of the XML file that describes the texture atlas.
   * @param textureId The OpenGL texture ID of the texture atlas.
   * @throws IOException If an error occurs while reading the XML file.
   * @throws XmlPullParserException If an error occurs while parsing the XML file.
   */
  public TextureAtlas(Context context, int xmlResourceId, int textureId)
      throws IOException, XmlPullParserException {
    this.context = context;
    this.textureId = textureId;
    InputStream xmlStream = this.context.getResources().openRawResource(xmlResourceId);
    XmlPullParser parser = Xml.newPullParser();
    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
    parser.setInput(xmlStream, null);
    parser.nextTag();
    readTextureAtlas(parser);
    Log.d("TextureAtlas", "TextureAtlas loaded: " + sprites.size() + " sprites");
    Log.d("TextureAtlas", "TextureAtlas loaded: " + sprites);
  }

  /**
   * Reads the texture atlas from the provided XML parser.
   *
   * @param parser The XML parser.
   * @throws IOException If an error occurs while reading the XML file.
   * @throws XmlPullParserException If an error occurs while parsing the XML file.
   */
  private void readTextureAtlas(XmlPullParser parser) throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, null, "TextureAtlas");

    atlasWidth = Integer.parseInt(parser.getAttributeValue(null, "width"));
    atlasHeight = Integer.parseInt(parser.getAttributeValue(null, "height"));

    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        continue;
      }

      String name = parser.getName();
      if (name.equals("sprite")) {
        Sprite sprite = readSprite(parser);
        sprites.put(sprite.name(), sprite);
      } else {
        skip(parser);
      }
    }
  }

  /**
   * Reads a sprite from the provided XML parser.
   *
   * @param parser The XML parser.
   * @return The sprite.
   * @throws IOException If an error occurs while reading the XML file.
   * @throws XmlPullParserException If an error occurs while parsing the XML file.
   */
  private Sprite readSprite(XmlPullParser parser) throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, null, "sprite");

    String spriteName = parser.getAttributeValue(null, "n");
    int x = Integer.parseInt(parser.getAttributeValue(null, "x"));
    int y = Integer.parseInt(parser.getAttributeValue(null, "y"));
    int w = Integer.parseInt(parser.getAttributeValue(null, "w"));
    int h = Integer.parseInt(parser.getAttributeValue(null, "h"));

    // Calculate the UVs for the sprite
    float[] uvs = calcUVs(x, y, w, h);

    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        continue;
      }
      skip(parser);
    }
    return new Sprite(spriteName, x, y, w, h, uvs);
  }

  /**
   * Skips the current XML tag in the provided XML parser.
   *
   * @param parser The XML parser.
   * @throws XmlPullParserException If an error occurs while parsing the XML file.
   * @throws IOException If an error occurs while reading the XML file.
   */
  private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
    if (parser.getEventType() != XmlPullParser.START_TAG) {
      throw new IllegalStateException();
    }
    int depth = 1;
    while (depth != 0) {
      switch (parser.next()) {
        case XmlPullParser.END_TAG:
          depth--;
          break;
        case XmlPullParser.START_TAG:
          depth++;
          break;
      }
    }
  }

  /**
   * Calculates the UV coordinates for a sprite. Intended for internal use within the TextureAtlas
   * class.
   *
   * @param spriteX The x-coordinate of the sprite in the texture atlas.
   * @param spriteY The y-coordinate of the sprite in the texture atlas.
   * @param spriteWidth The width of the sprite in the texture atlas.
   * @param spriteHeight The height of the sprite in the texture atlas.
   * @return The UV coordinates of the sprite in the order: left, top, right, bottom
   */
  private float[] calcUVs(int spriteX, int spriteY, int spriteWidth, int spriteHeight) {
    // Calculate the U coordinate for the left edge of the sprite
    float u1 = (float) spriteX / atlasWidth;
    // Calculate the V coordinate for the top edge of the sprite
    float v1 = (float) spriteY / atlasHeight;
    // Calculate the U coordinate for the right edge of the sprite
    float u2 = u1 + (float) spriteWidth / atlasWidth;
    // Calculate the V coordinate for the bottom edge of the sprite
    float v2 = v1 + (float) spriteHeight / atlasHeight;

    // Return the UV coordinates in the order: left, top, right, bottom
    return new float[] {u1, v1, u2, v2};
  }

  /**
   * Retrieves a sprite from the texture atlas by its name.
   *
   * @param name The name of the sprite.
   * @return The sprite, or null if no sprite with the given name exists in the texture atlas.
   */
  public Sprite getSprite(String name) {
    return sprites.get(name);
  }

  /**
   * Retrieves a list of sprites for an animation from the texture atlas by the animation's name.
   * The sprites are sorted in the order they should appear in the animation.
   *
   * @param name The name of the animation.
   * @return The list of sprites for the animation.
   */
  public List<Sprite> getAnimationSprites(String name) {
    // Check if the animation is already in the cache
    if (animationSpriteCache.containsKey(name)) {
      return animationSpriteCache.get(name);
    }
    var animationFrames = new ArrayList<Sprite>();
    // get all sprites that match the name in the animation
    for (Map.Entry<String, Sprite> entry : sprites.entrySet()) {
      if (entry.getKey().startsWith(name)) {
        animationFrames.add(entry.getValue());
      }
    }
    assert !animationFrames.isEmpty();
    // sort the sprites based on their names
    animationFrames.sort(Comparator.comparing(Sprite::name));
    // add the animation to the cache
    animationSpriteCache.put(name, animationFrames);
    return animationFrames;
  }

  /**
   * Returns the OpenGL texture ID of the texture atlas.
   *
   * @return The OpenGL texture ID of the texture atlas.
   */
  public int getTexturePtr() {
    return textureId;
  }
}
