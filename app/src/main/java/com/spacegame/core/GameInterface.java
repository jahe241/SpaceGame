package com.spacegame.core;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The GameInterface class is responsible for handling user interactions with the game. It contains
 * a reference to the game instance and the application context.
 */
public class GameInterface extends Thread {

  /** The game instance that this interface interacts with. */
  private final Game game;

  /** The application context. */
  private final Context context;

  /** A list of SpriteButton objects that represent the interface elements of the game. */
  private final List<SpriteButton> interfaceElements =
      Collections.synchronizedList(new ArrayList<>());

  /** The width of the screen. */
  private float screenWidth;

  /** The height of the screen. */
  private float screenHeight;

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
    this.screenWidth = context.getResources().getDisplayMetrics().widthPixels;
    this.screenHeight = context.getResources().getDisplayMetrics().heightPixels;
  }

  /**
   * The main run method for the GameInterface thread. It sets up the interface and then enters a
   * loop where it updates the interface elements and sleeps for the remainder of the frame time.
   */
  @Override
  public void run() {
    setupInterface();
    Log.d("GameInterface", "Game Thread started on Thread: " + Thread.currentThread().getName());
    long timePerFrame = 1000 / 60; // Time for each frame in milliseconds
    long startTime = System.currentTimeMillis();

    update(timePerFrame / 1000.0f); // Convert to seconds

    long endTime = System.currentTimeMillis();
    long timeSpent = endTime - startTime;

    if (timeSpent < timePerFrame) {
      try {
        Thread.sleep(timePerFrame - timeSpent);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Updates the position and vertex data of each entity in the interface elements list.
   *
   * @param deltaTime The time since the last frame in seconds.
   */
  public void update(float deltaTime) {
    // Calls the update method for each entity: Updates Position and adjusts the vertex data based
    // on the new position
    synchronized (interfaceElements) {
      // Remove the entities that are marked for deletion
      interfaceElements.removeIf(Entity::getDiscard);
      for (Quad entity : interfaceElements) {
        entity.update(deltaTime);
      }
    }
  }

  /** Sets up the interface by adding the pause button to the interface elements list. */
  private void setupInterface() {
    // Add the pause button
    addInterfaceElement(
        new SpriteButton(
            game.textureAtlas,
            "peepo",
            "monk",
            screenWidth - (screenWidth * 0.2f),
            screenHeight - (screenHeight * 0.9f),
            250f,
            250f,
            ButtonType.TOGGLE_PAUSE,
            true));
  }

  /**
   * Adds a new interface element to the interface elements list.
   *
   * @param element The new interface element to add.
   */
  private void addInterfaceElement(SpriteButton element) {
    interfaceElements.add(element);
  }

  /**
   * Handles a touch event. If the touch event is within the bounds of any of the interface
   * elements, it handles the button event. Otherwise, it passes the touch event to the game.
   *
   * @param event The touch event to handle.
   */
  public void receiveTouchEvent(MotionEvent event) {
    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {

      // Check if the touch event is within the bounds of any of the interface elements
      for (SpriteButton element : interfaceElements) {
        if (element.isActive() && element.isTouchWithinButton(event.getX(), event.getY())) {
          this.handleButtonEvent(element.click());
          return;
        }
      }

      // If no interface element was clicked, pass the touch event to the game
      game.handleTouchEvent(event);
    }
  }

  /**
   * Handles a button event. Depending on the type of the button event, it performs the appropriate
   * action.
   *
   * @param type The type of the button event to handle.
   */
  private void handleButtonEvent(ButtonType type) {
    switch (type) {
      case TOGGLE_PAUSE:
        if (game.paused) {
          game.resumeGame();
        } else {
          game.pauseGame();
        }
        break;
        // Check other Cases here
    }
  }

  /**
   * Returns the list of interface elements.
   *
   * @return The list of interface elements.
   */
  public List<SpriteButton> getInterfaceElements() {
    return interfaceElements;
  }
}
