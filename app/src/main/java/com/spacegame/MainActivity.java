package com.spacegame;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setFullscreen();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Registering Buttons
    findViewById(R.id.btnStart).setOnClickListener(v -> {
      // Start the game
      startActivity(new Intent(this, GameActivity.class));
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    setFullscreen();
  }


  private void setFullscreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // For API level 30 and above
      WindowInsetsController controller = getWindow().getDecorView().getWindowInsetsController();
      if (controller != null) {
        controller.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
        controller.setSystemBarsBehavior(
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
      }
    } else {
      // For API level 16 to 29 Not tested, but should work.
      getWindow().getDecorView().setSystemUiVisibility(
          View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_FULLSCREEN
              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
  }
}