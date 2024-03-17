package com.spacegame.utils;

import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

public class WindowManager {

  public static void setFullscreen(android.view.Window window) {
    var view = window.getDecorView();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // For API level 30 and above
      WindowInsetsController controller = view.getWindowInsetsController();
      if (controller != null) {
        controller.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
        controller.setSystemBarsBehavior(
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
      }
      // We need this, for the SurfaceView to be fullscreen too.
      window.setFlags(
          android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
          android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    } else {
      // For API level 16 to 29
      view.setSystemUiVisibility(
          View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_FULLSCREEN
              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
  }
}
