package com.spacegame.utils;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class Constants {
  // This is very tedious. I would use a tool to generate this in the future..
  public static int[] animation_EXPLOSION = {
    0, 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8, 0, 0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1
  };

  public static List<int[]> getAllCoords(TextureAtlas textureAtlas) {
    List<int[]> coordinates = new ArrayList<>();
    int maxX = textureAtlas.getGridWidth();
    int maxY = textureAtlas.getGridHeight();
    for (int x = 0; x < maxX; x++) {
      for (int y = 0; y < maxY; y++) {
        coordinates.add(new int[] {x, y});
      }
    }
    Log.d("Constants", "Debug Animations: " + coordinates.size() + " animations");
    return coordinates;
  }
}
