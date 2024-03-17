package com.spacegame;

import static com.spacegame.utils.WindowManager.setFullscreen;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setFullscreen(getWindow());
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Registering Buttons
    findViewById(R.id.btnStart)
        .setOnClickListener(
            v -> {
              // Start the game
              startActivity(new Intent(this, GameActivity.class));
            });
  }

  @Override
  protected void onResume() {
    super.onResume();
    setFullscreen(getWindow());
  }
}
