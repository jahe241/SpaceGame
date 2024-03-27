package com.spacegame;

import static com.spacegame.utils.WindowManager.setFullscreen;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import com.spacegame.sound.SoundEngine;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

  MediaPlayer mainMenu;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    mainMenu = MediaPlayer.create(this, R.raw.themesong);
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
                mainMenu.prepareAsync();
              }
              // Start the game
              startActivity(new Intent(this, GameActivity.class));
            });
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mainMenu != null && !mainMenu.isPlaying()) {
      mainMenu.start();
    }
    setFullscreen(getWindow());
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mainMenu != null && mainMenu.isPlaying()) {
      mainMenu.pause();
    }
  }
}
