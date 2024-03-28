package com.spacegame.core;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import com.spacegame.core.ui.GamePad;
import com.spacegame.core.ui.SpriteContainer;
import com.spacegame.core.ui.SpriteLabel;
import com.spacegame.entities.Entity;
import com.spacegame.core.ui.SpriteButton;
import com.spacegame.sound.SoundEngine;
import com.spacegame.utils.ColorHelper;
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
  private final List<Entity> interfaceElements = Collections.synchronizedList(new ArrayList<>());

  private GamePad gamePad;

  /** The width of the screen. */
  private float screenWidth;

  /** The height of the screen. */
  private float screenHeight;

  private SoundEngine soundEngine;

  private SpriteLabel scoreLabel;

  InterfaceState state = InterfaceState.PLAYING;

  /**
   * Constructor for the GameInterface class. This constructor initializes a new GameInterface
   * object by setting its game instance and application context.
   *
   * @param context The application context.
   * @param game The game instance that this interface interacts with.
   */
  public GameInterface(Context context, Game game, float screenWidth, float screenHeight) {
    this.game = game;
    this.context = context;
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.soundEngine = new SoundEngine(context);
    soundEngine.start(soundEngine.getGameMusic());
  }

  /**
   * The main run method for the GameInterface thread. It sets up the interface and then enters a
   * loop where it updates the interface elements and sleeps for the remainder of the frame time.
   */
  @Override
  public void run() {
    setupInterface();
    Log.d("GameInterface", "Game Thread started on Thread: " + Thread.currentThread().getName());
    long timePerFrame =
        1000 / 120; // Time for each frame in milliseconds we target 120fps for the gamepad

    while (true) {
      long startTime = System.currentTimeMillis();

      long endTime = System.currentTimeMillis();
      long timeSpent = endTime - startTime;
      update(timePerFrame / 1000.0f); // Convert to seconds

      if (timeSpent < timePerFrame) {
        try {
          Thread.sleep(timePerFrame - timeSpent);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
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
    this.scoreLabel.setText("SCORE: " + game.getScore());

    synchronized (interfaceElements) {
      // Remove the entities that are marked for deletion
      interfaceElements.removeIf(Entity::getDiscard);
      for (Entity entity : interfaceElements) {
        entity.update(deltaTime);
      }
    }
  }

  /** Sets up the interface by adding the pause button to the interface elements list. */
  private void setupInterface() {
    // Add the pause button
    addInterfaceContainer(
        new SpriteButton(
            game.textureAtlas,
            "peepo",
            "monk",
            screenWidth - (screenWidth * 0.2f),
            screenHeight - (screenHeight * 0.9f),
            250f,
            250f,
            ButtonType.TOGGLE_PAUSE,
            true,
            ColorHelper.TRANSPARENT));
    // Reset Button
    addInterfaceContainer(
        new SpriteButton(
            game.textureAtlas,
            "joystix_c",
            "joystix_c",
            screenWidth - 360,
            screenHeight - 100,
            200f,
            200f,
            ButtonType.RESET_GAME,
            true,
            ColorHelper.GREEN));
    // Debug Button
    addInterfaceContainer(
        new SpriteButton(
            game.textureAtlas,
            "joystix_b",
            "joystix_b",
            screenWidth - 110,
            screenHeight - 100,
            200f,
            200f,
            ButtonType.DEBUG_BUTTON,
            true,
            ColorHelper.ORANGE));

    // Add the gamepad
    this.gamePad = new GamePad(game.textureAtlas, screenWidth, screenHeight);
    gamePad.setVisible(false);
    addInterfaceContainer(gamePad);

    // Add the score label
    this.scoreLabel =
        new SpriteLabel("SCORE: 9999", 50, 50, 64 * 2, ColorHelper.NAVY, game.textureAtlas);
    addInterfaceContainer(scoreLabel);

    Log.d("GameInterface", "Setup Interface: " + interfaceElements);
  }

  /**
   * Adds a new interface element to the interface elements list.
   *
   * @param element The new interface element to add.
   */
  private void addInterfaceElement(Entity... element) {
    Collections.addAll(interfaceElements, element);
  }

  private void addInterfaceContainer(SpriteContainer... container) {
    for (SpriteContainer c : container) {
      Collections.addAll(interfaceElements, c.getElements());
    }
  }

  /**
   * Handles a touch event. If the touch event is within the bounds of any of the interface
   * elements, it handles the button event. Otherwise, it passes the touch event to the game.
   *
   * @param event The touch event to handle.
   */
  public void receiveTouchEvent(MotionEvent event) {
    Log.d("GameInterface", "Received Touch Event: " + event.getActionMasked());

    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
      // Check if the touch event is within the bounds of any of the interface elements
      for (Entity element : interfaceElements) {
        if (element instanceof SpriteButton button) { // this is so fancy!
          if (button.isActive() && button.isTouchWithinButton(event.getX(), event.getY())) {
            this.handleButtonEvent(button.click());
            return;
          }
        }
      }

      // GamePad
      if (this.state == InterfaceState.PLAYING && gamePad.isVisible()) {
        gamePad.updateStickPosition(event.getX(), event.getY());
        this.game.setPlayerDirection(gamePad.getStickDirection());
      } else if (this.state == InterfaceState.PLAYING && !gamePad.isVisible()) {
        Log.d("GameInterface", "Showing GamePad");
        gamePad.showGamePad(event.getX(), event.getY());
      }
    }

    if (event.getActionMasked() == MotionEvent.ACTION_UP) {
      if (gamePad.isVisible()) {
        Log.d("GameInterface", "Hiding GamePad");
        gamePad.hideGamePad();
        gamePad.resetStickPosition();
        this.game.setPlayerDirection(gamePad.getStickDirection());
      }
    }
    if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
      if (gamePad.isVisible()) {
        gamePad.updateStickPosition(event.getX(), event.getY());
        this.game.setPlayerDirection(gamePad.getStickDirection());
      }
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
        if (game.state == GameState.PAUSED) {
          game.resumeGame();
          this.state = InterfaceState.PLAYING;
          soundEngine.start(soundEngine.getGameMusic());
        } else {
          game.pauseGame();
          this.state = InterfaceState.PAUSE_MENU;
          soundEngine.pause(soundEngine.getGameMusic());
        }
        break;
        // Check other Cases here
      case RESET_GAME:
        Log.d("GameInterface", "Resetting Game");
        this.game.player.vbo().print();
        game.resetGame();
        break;
      case DEBUG_BUTTON:
        this.game.spawnRandomEnemy(64);
        this.game.setScore(this.game.getScore() + 10);
        break;
    }
  }

  /**
   * Returns the list of interface elements.
   *
   * @return The list of interface elements.
   * @see Entity
   */
  public List<Entity> getInterfaceElements() {
    synchronized (interfaceElements) {
      return new ArrayList<>(interfaceElements);
    }
  }

  public List<Entity> getVisibleEntities() {
    synchronized (interfaceElements) {
      List<Entity> visibleEntities = new ArrayList<>(interfaceElements.size());
      for (Entity entity : interfaceElements) {
        if (entity.isVisible()) {
          visibleEntities.add(entity);
        }
      }
      return visibleEntities;
    }
  }

  public void onPause() {
    soundEngine.pause(soundEngine.getGameMusic());
  }

  public void onResume() {
    soundEngine.start(soundEngine.getGameMusic());
  }

  public void onDestroy() {
    soundEngine.stop(soundEngine.getGameMusic());
    soundEngine.release();
  }
}
