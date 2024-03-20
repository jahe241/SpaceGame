package com.spacegame.utils;

/**
 * A helper class for color manipulation handling the OpenGL color format and converting hex colors
 */
public class ColorHelper {

  /**
   * Converts a hex color string to an array of floats in the OpenGL color format
   *
   * @param hex the hex color string
   * @return an array of floats representing the color
   */
  public static float[] hexToRgb(String hex) {
    float[] rgb = new float[3];
    rgb[0] = Integer.valueOf(hex.substring(1, 3), 16) / 255f;
    rgb[1] = Integer.valueOf(hex.substring(3, 5), 16) / 255f;
    rgb[2] = Integer.valueOf(hex.substring(5, 7), 16) / 255f;
    return rgb;
  }

  /**
   * Returns a new color based on the previous color and an increment
   *
   * @param prevColor the previous color
   * @param increment the increment to apply to the previous color
   * @return a new color
   */
  public static float[] getRainbowColor(float[] prevColor, float[] increment) {
    float[] newColor = new float[prevColor.length];

    for (int i = 0; i < 3; i++) {
      newColor[i] = prevColor[i] + increment[i];
      if (newColor[i] >= 1) {
        newColor[i] -= 1;
      }
    }
    // Check if the new color is close to black
    if (newColor[0] < 0.2f && newColor[1] < 0.2f && newColor[2] < 0.2f) {
      // If it is, add an extra increment to skip the black area
      for (int i = 0; i < 3; i++) {
        newColor[i] += increment[i];
        if (newColor[i] >= 1) {
          newColor[i] -= 1;
        }
      }
    }

    // Preserve the alpha channel if it exists
    if (prevColor.length == 4) {
      newColor[3] = prevColor[3];
    }

    return newColor;
  }
}
