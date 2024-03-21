package com.spacegame.core;

import android.content.Context;
import android.view.MotionEvent;

/**
 * The GameInterface class is responsible for handling user interactions with the game. It contains
 * a reference to the game instance and the application context.
 */
public class GameInterface {
  /** The game instance that this interface interacts with. */
  private final Game game;

  private final Context context;

  /**
   * Constructor for the GameInterface class. This constructor initializes a new GameInterface
   * object by setting its game instance and application context.
   *
   * @param context The application context.
   * @param game The game instance that this interface interacts with.
   */
  public GameInterface(Context context, Game game) {
    this.game = game;
    this.context = context;
  }

  /**
   * Handles touch events from the user. If the touch event is a down action and the coordinates are
   * within a certain range, it toggles the pause state of the game. Otherwise, it passes the touch
   * event to the game instance for further processing.
   *
   * @param event The touch event to handle.
   */
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
