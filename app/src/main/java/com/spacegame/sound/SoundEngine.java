package com.spacegame.sound;

import android.content.Context;
import android.media.*;

import com.spacegame.R;

public class SoundEngine {

  private MediaPlayer mainMenu;
  private MediaPlayer inGame;
  private SoundPool soundPool;
  private int shoot;
  private int explosion;
  private int hit;
  private int gameOver;

/**
 * Constructor for the SoundEngine class.
 *
 * @param context The context of the caller. This is used to access application-specific resources.
 */
public SoundEngine(Context context) {
  // Create a MediaPlayer instance for the main menu music, using the themesong resource.
  mainMenu = MediaPlayer.create(context, R.raw.themesong);

  // Create a MediaPlayer instance for the in-game music, using the orbitalcolossus resource.
  inGame = MediaPlayer.create(context, R.raw.orbitalcolossus);

  // Create a new SoundPool instance using the builder pattern.
  soundPool = new SoundPool.Builder().build();

  // Load the explosion sound into the SoundPool, using the rlaunch resource.
  explosion = soundPool.load(context, R.raw.rlaunch,1 );

  // Load the shoot sound into the SoundPool, using the shoot1 resource.
  shoot = soundPool.load(context, R.raw.shoot1,1 );

  // Load the hit sound into the SoundPool, using the hit resource.
  hit = soundPool.load(context, R.raw.hit,1 );

  // Load the game over sound into the SoundPool, using the gameover resource.
  gameOver = soundPool.load(context, R.raw.gameover,1 );
}

  public void prepare() {
    mainMenu.prepareAsync();
    inGame.prepareAsync();
  }


  public MediaPlayer getMusic(SoundType soundType) {
      return switch (soundType) {
        case mainMenu -> mainMenu;
        case inGame -> inGame;
        default -> throw new IllegalArgumentException("Sound Not Found");
      };
  }

  public int getSound(SoundType soundType) {
    return switch (soundType) {
      case explosion -> explosion;
      case shoot -> shoot;
      case hit -> hit;
      case gameOver -> gameOver;
      default -> throw new IllegalArgumentException("Sound Not Found");
    };
  }

  public void playMusic(SoundType soundType) {
    MediaPlayer music = getMusic(soundType);
    if (!music.isPlaying()) {
      music.start();
      music.setLooping(true);
    }
  }

  public void playSound(SoundType soundType) {
    int sound = getSound(soundType);
    soundPool.play(sound, 1,1,0,0,1);
  }

  public boolean isPlaying(MediaPlayer music) {
    return music.isPlaying();
  }

  public void stopMusic(SoundType soundType) {
    MediaPlayer music = getMusic(soundType);
    if (music.isPlaying()) {
      music.stop();
      music.prepareAsync();
    }
  }

  public void pauseMusic(SoundType soundType) {
    MediaPlayer music = getMusic(soundType);
    if (music.isPlaying()) {
      music.pause();
    }
  }

  public void release() {
    inGame.release();
    mainMenu.release();
    inGame = null;
    mainMenu = null;
  }
}
