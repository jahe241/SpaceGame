package com.spacegame.sound;

import android.content.Context;
import android.media.*;

import com.spacegame.R;

public class SoundEngine {

  private static SoundEngine instance = null;
  private MediaPlayer mainMenu;
  private MediaPlayer inGame;
  private MediaPlayer explosion;
  private MediaPlayer shoot;

  public SoundEngine(Context context) {
    mainMenu = MediaPlayer.create(context, R.raw.themesong);
    inGame = MediaPlayer.create(context, R.raw.orbitalcolossus);
    explosion = MediaPlayer.create(context, R.raw.rlaunch);
    shoot = MediaPlayer.create(context, R.raw.shoot1);
  }

  public void prepare() {
    mainMenu.prepareAsync();
    inGame.prepareAsync();
    explosion.prepareAsync();
  }

  public MediaPlayer getGameMusic() {
    return inGame;
  }

  public MediaPlayer getMainMenu() {
    return mainMenu;
  }

  public MediaPlayer getExplosion() {
    return explosion;
  }

  public MediaPlayer getSound(SoundType soundType) {
      return switch (soundType) {
          case mainMenu -> mainMenu;
          case inGame -> inGame;
          case shoot -> shoot;
          default -> throw new IllegalArgumentException("Sound Not Found");
      };
  }

  public void play(SoundType soundType) {
    MediaPlayer music = getSound(soundType);
    if (!music.isPlaying()) {
      music.start();
    }
  }

  public boolean isPlaying(MediaPlayer music) {
    return music.isPlaying();
  }




  public void stop(SoundType soundType) {
    MediaPlayer music = getSound(soundType);
    if (music.isPlaying()) {
      music.stop();
      music.prepareAsync();
    }
  }

  public void pause(SoundType soundType) {
    MediaPlayer music = getSound(soundType);
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
