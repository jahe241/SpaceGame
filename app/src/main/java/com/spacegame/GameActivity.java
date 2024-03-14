package com.spacegame;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import android.view.WindowManager;
import com.spacegame.core.SpaceRenderer;


public class GameActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setFullscreen(getWindow().getDecorView());
    super.onCreate(savedInstanceState);
    GLSurfaceView glSurfaceView = new GLSurfaceView(this);
    glSurfaceView.setEGLContextClientVersion(2);
    glSurfaceView.setRenderer(new SpaceRenderer(this));
    setContentView(glSurfaceView);
  }

  @Override
  protected void onResume() {
    super.onResume();
    setFullscreen(getWindow().getDecorView());
  }

  private void setFullscreen(View view) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // For API level 30 and above
      WindowInsetsController controller = view.getWindowInsetsController();
      if (controller != null) {
        controller.hide(
            WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
        controller.setSystemBarsBehavior(
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
      }
      getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
      // We need this, for the SurfaceView to be fullscreen too.
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
          WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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