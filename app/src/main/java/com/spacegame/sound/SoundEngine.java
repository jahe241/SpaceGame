package com.spacegame.sound;

import android.media.*;
import android.view.View;
import android.os.Bundle;

import com.spacegame.MainActivity;
import com.spacegame.R;

public class SoundEngine extends MediaPlayer {
  MediaPlayer mainMenu;
  MediaPlayer inGame;

  public SoundEngine() {}

  public MediaPlayer getInGame() {
    return inGame;
  }

  public MediaPlayer getMainMenu() {
    return mainMenu;
  }

  public static void start(MediaPlayer music) {
    if (!music.isPlaying()) {
      music.start();
    }
  }

  public static boolean isPlaying(MediaPlayer music) {
    return music.isPlaying();
  }

  public static void stop(MediaPlayer music) {
    if (music.isPlaying()) {
      music.stop();
    }
  }

  public void pause(MediaPlayer music) {
    if (music.isPlaying()) {
      music.pause();
    }
  }

  public void create(MainActivity activity) {
    inGame = MediaPlayer.create(activity, R.raw.megalovania);
  }

  public void release() {
    inGame.release();
    mainMenu.release();
    inGame = null;
    mainMenu = null;
  }
}
