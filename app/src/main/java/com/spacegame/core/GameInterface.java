package com.spacegame.core;

import android.content.Context;
import android.view.MotionEvent;

public class GameInterface {
  private final Game game;
  private final Context context;

  public GameInterface(Context context, Game game) {
    this.game = game;
    this.context = context;
  }

  public void handleTouchEvent(MotionEvent event) {
    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
      // Here we might check if the coordinates are over a button or something, otherwise implement
      // the controller
      if (event.getX() < 200 && event.getY() < 200) {
        // Pause the game
        if (game.paused) {
          game.resumeGame();
        } else {
          game.pauseGame();
        }
      } else {
        game.handleTouchEvent(event);
      }
    }
  }
}
