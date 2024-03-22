package com.spacegame.sound;

import android.content.Context;
import android.media.*;
import android.view.View;
import android.os.Bundle;

import com.spacegame.MainActivity;
import com.spacegame.R;

public class SoundEngine {
  MediaPlayer mainMenu;
  MediaPlayer inGame;
  MediaPlayer explosion;

  public SoundEngine(Context context) {
    mainMenu = MediaPlayer.create(context, R.raw.observingthestar);
    inGame = MediaPlayer.create(context, R.raw.observingthestar);
    explosion = MediaPlayer.create(context, R.raw.rlaunch);
  }

  public MediaPlayer getInGame() {
    return inGame;
  }

  public MediaPlayer getMainMenu() {
    return mainMenu;
  }

  public MediaPlayer getExplosion() {
    return explosion;
  }

  public void start(MediaPlayer music) {
    if (!music.isPlaying()) {
      music.start();
    }
  }

  public static boolean isPlaying(MediaPlayer music) {
    return music.isPlaying();
  }

  public void stop(MediaPlayer music) {
    if (music.isPlaying()) {
      music.stop();
    }
  }

  public void pause(MediaPlayer music) {
    if (music.isPlaying()) {
      music.pause();
    }
  }

  public void release() {
    inGame.release();
    mainMenu.release();
    explosion.release();
    inGame = null;
    mainMenu = null;
    explosion = null;
  }
}
