package com.spacegame.sound;

import android.content.Context;
import android.media.*;
import com.spacegame.R;

public class SoundEngine {
  private static SoundEngine instance = null;
  private MediaPlayer mainMenu;
  private MediaPlayer gameMusic;
  private MediaPlayer explosion;

  public SoundEngine(Context context) {
    mainMenu = MediaPlayer.create(context, R.raw.themesong);
    gameMusic = MediaPlayer.create(context, R.raw.orbitalcolossus);
    explosion = MediaPlayer.create(context, R.raw.rlaunch);
  }

  public static boolean isPlaying(MediaPlayer music) {
    return music.isPlaying();
  }

  public void prepare() {
    mainMenu.prepareAsync();
    gameMusic.prepareAsync();
    explosion.prepareAsync();
  }

  public MediaPlayer getGameMusic() {
    return gameMusic;
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

  public void stop(MediaPlayer music) {
    if (music.isPlaying()) {
      music.stop();
      music.prepareAsync();
    }
  }

  public void pause(MediaPlayer music) {
    if (music.isPlaying()) {
      music.pause();
    }
  }

  public void release() {
    gameMusic.release();
    mainMenu.release();
    explosion.release();
    gameMusic = null;
    mainMenu = null;
    explosion = null;
  }
}
