package com.spacegame.utils;

import android.util.Log;
import com.spacegame.graphics.TextureAtlas;
import java.util.ArrayList;
import java.util.List;

public class Constants {
  // Might aswell keep them here, will be easier to change them later
  public static String animation_EXPLOSION = "exp2_0-";
  public static String PLAYER = "pixel_ship_pepe";

  public static String[] GAMEPAD = {
    "touch_input_2d_white", "touch_input_cursor_white"
  }; // gamepad, stick

  public static String[] ENEMIES = {
    "ship_red_01",
    "ship_red_02",
    "ship_red_03",
    "ship_spikey_01",
    "ship_grey_01",
    "ship_green_01",
    "ship_blue_01",
    "ship_black_01"
  };
}
