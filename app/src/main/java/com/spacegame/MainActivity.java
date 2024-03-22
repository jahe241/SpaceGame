package com.spacegame;

import static com.spacegame.utils.WindowManager.setFullscreen;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.spacegame.sound.SoundEngine;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

  MediaPlayer mainMenu;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    mainMenu = MediaPlayer.create(this, R.raw.observingthestar);
    setFullscreen(getWindow());
    mainMenu.start();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // Registering Buttons
    findViewById(R.id.btnStart)
        .setOnClickListener(
            v -> {
              if (mainMenu.isPlaying() && mainMenu != null) {
                mainMenu.stop();
                try {
                  mainMenu.prepare();
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }
              // Start the game
              startActivity(new Intent(this, GameActivity.class));
            });
  }

  @Override
  protected void onResume() {
    super.onResume();
    mainMenu.start();
    setFullscreen(getWindow());
  }

}
