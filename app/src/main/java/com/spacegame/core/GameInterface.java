package com.spacegame.core;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import com.spacegame.core.ui.GamePad;
import com.spacegame.core.ui.SpriteButton;
import com.spacegame.core.ui.SpriteContainer;
import com.spacegame.core.ui.SpriteLabel;
import com.spacegame.core.ui.SpritePopup;
import com.spacegame.entities.ColorEntity;
import com.spacegame.entities.Entity;
import com.spacegame.sound.SoundEngine;
import com.spacegame.sound.SoundType;
import com.spacegame.utils.ColorHelper;
import com.spacegame.utils.DebugLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The GameInterface class is responsible for handling user interactions with the game. It contains
 * a reference to the game instance and the application context.
 */
public class GameInterface extends Thread {

  public static GameInterface gameInterface;

  /** The game instance that this interface interacts with. */
  private final Game game;

  /** The application context. */
  // private final Context context;

  /** A list of SpriteButton objects that represent the interface elements of the game. */
  private final List<Entity> interfaceElements = Collections.synchronizedList(new ArrayList<>());

  private final List<SpriteContainer> variableContainers = new ArrayList<>();
  InterfaceState state = InterfaceState.PLAYING;
  private GamePad gamePad;

  /** The width of the screen. */
  private float screenWidth;

  /** The height of the screen. */
  private float screenHeight;

  private SoundEngine soundEngine;
  private SpriteLabel scoreLabel;
  private SpriteLabel timeLabel;
  private SpritePopup pauseMenu;
  private SpritePopup gameOverMenu;
  private SpritePopup upgradeMenu;
  private int adaptiveSizeUnit;

  /**
   * Constructor for the GameInterface class. This constructor initializes a new GameInterface
   * object by setting its game instance and application context.
   *
   * @param context The application context.
   * @param game The game instance that this interface interacts with.
   */
  public GameInterface(Context context, Game game, float screenWidth, float screenHeight) {
    this.game = game;
    // this.context = context;
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.adaptiveSizeUnit =
        (int)
            (Math.min(screenWidth, screenHeight)
                * 0.05f); // The font size is 2.5% of the screen height
    DebugLogger.log("Game", "Fontsize set to:" + adaptiveSizeUnit);
    this.soundEngine = new SoundEngine(context);
    soundEngine.playMusic(SoundType.inGame);
    GameInterface.gameInterface = this;
  }

  /**
   * The main run method for the GameInterface thread. It sets up the interface and then enters a
   * loop where it updates the interface elements and sleeps for the remainder of the frame time.
   */
  @Override
  public void run() {
    setupInterface();
    DebugLogger.log("Game", "Game Thread started on Thread: " + Thread.currentThread().getName());
    long timePerFrame = 1000 / 120; // Time for each frame in milliseconds
    long lastFrameTime = System.nanoTime();
    while (true) {
      long startTime = System.nanoTime();
      float elapsed = (startTime - lastFrameTime) / 1_000_000f;

      update(elapsed / 1000.0f); // Convert to seconds

      lastFrameTime = startTime;
      if (elapsed < timePerFrame) {
        try {
          Thread.sleep((long) (timePerFrame - elapsed));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /** Sets up the interface by adding the pause button to the interface elements list. */
  private void setupInterface() {
    // Add the pause button
    addInterfaceContainer(
        new SpriteButton(
            game.textureAtlas,
            "peepo_playing",
            "peepo_paused",
            screenWidth - (screenWidth * 0.2f),
            screenHeight - (screenHeight * 0.9f),
            adaptiveSizeUnit * 3,
            adaptiveSizeUnit * 3,
            ButtonType.TOGGLE_PAUSE,
            true,
            ColorHelper.TRANSPARENT));
    // Reset Button
    addInterfaceContainer(
        new SpriteButton(
                game.textureAtlas,
                "joystix_r",
                "joystix_r",
                (screenWidth * .9f),
                (screenHeight * .95f),
                adaptiveSizeUnit * 3,
                adaptiveSizeUnit * 3,
                ButtonType.RESET_GAME,
                true,
                ColorHelper.RED)
            .setActiveDuringState(InterfaceState.PLAYING));
    // Debug Button
    addInterfaceContainer(
        new SpriteButton(
                game.textureAtlas,
                "joystix_d",
                "joystix_d",
                (screenWidth * .9f),
                (screenHeight * .95f) - adaptiveSizeUnit * 4,
                adaptiveSizeUnit * 3,
                adaptiveSizeUnit * 3,
                ButtonType.DEBUG_BUTTON,
                true,
                ColorHelper.ORANGE)
            .setActiveDuringState(InterfaceState.PLAYING));

    // Add the gamepad
    this.gamePad = new GamePad(game.textureAtlas, screenWidth, screenHeight);
    gamePad.setVisible(false);
    addInterfaceContainer(gamePad);

    // Add the score label
    this.scoreLabel =
        new SpriteLabel(
            "SCORE: 9999",
            screenWidth * .10f, // Center of the screen
            screenHeight * .30f,
            this.adaptiveSizeUnit * 2,
            ColorHelper.TRANSPARENT,
            game.textureAtlas);

    this.timeLabel =
        new SpriteLabel(
            "00:00",
            (screenWidth * .5f)
                - ((adaptiveSizeUnit * 5) * .5f), // +5 characterSize, to center the text
            (screenHeight * .99f) - adaptiveSizeUnit,
            adaptiveSizeUnit,
            ColorHelper.TRANSPARENT,
            game.textureAtlas);
    addInterfaceContainer(timeLabel);
    variableContainers.add(timeLabel);
    Log.d("GameInterface", "Setup Interface: " + interfaceElements);

    this.pauseMenu =
        new SpritePopup(
            new ColorEntity(
                screenWidth / 2, // Center of the screen
                screenHeight / 2,
                screenWidth * .9f,
                screenHeight * .7f,
                ColorHelper.PINK));

    this.pauseMenu.addButton(
        new SpriteButton(
            game.textureAtlas,
            "monk",
            "peepo",
            screenWidth / 2, // Center of the screen
            screenHeight * .7f,
            adaptiveSizeUnit * 3,
            adaptiveSizeUnit * 3,
            ButtonType.CHEAT_BUTTON,
            true,
            ColorHelper.TRANSPARENT));
    this.pauseMenu.addLabel(this.scoreLabel);

    this.pauseMenu.addLabel(
        new SpriteLabel(
            "PAUSED",
            (screenWidth * .5f)
                - (((adaptiveSizeUnit * 2) * 5) * .5f), // +5 characterSize, to center the text
            screenHeight * .18f,
            this.adaptiveSizeUnit * 2,
            ColorHelper.TRANSPARENT,
            game.textureAtlas));
    this.pauseMenu.hide();
    addInterfaceContainer(this.pauseMenu);

    this.gameOverMenu =
        new SpritePopup(
            new ColorEntity(
                screenWidth / 2, // Center of the screen
                screenHeight / 2,
                screenWidth * .9f,
                screenHeight * .7f,
                ColorHelper.PINK));

    this.gameOverMenu.addButton(
        new SpriteButton(
            game.textureAtlas,
            "monk",
            "peepo",
            screenWidth / 2, // Center of the screen
            screenHeight * .7f,
            adaptiveSizeUnit * 3,
            adaptiveSizeUnit * 3,
            ButtonType.RESET_GAME,
            true,
            ColorHelper.TRANSPARENT));
    this.gameOverMenu.addLabel(this.scoreLabel);

    this.gameOverMenu.addLabel(
        new SpriteLabel(
            "GAME OVER",
            (screenWidth * .5f)
                - (((adaptiveSizeUnit * 2) * 5) * .5f), // +5 characterSize, to center the text
            screenHeight * .18f,
            this.adaptiveSizeUnit * 2,
            ColorHelper.TRANSPARENT,
            game.textureAtlas));
    this.gameOverMenu.hide();
    addInterfaceContainer(this.gameOverMenu);
  }

  /**
   * Updates the position and vertex data of each entity in the interface elements list.
   *
   * @param deltaTime The time since the last frame in seconds.
   */
  public void update(float deltaTime) {
    //    this.scoreLabel.setText("SCORE: " + game.getScore());
    this.timeLabel.setText(game.timer.getFormattedElapsedTime());

    synchronized (interfaceElements) {
      // Remove the entities that are marked for deletion
      interfaceElements.removeIf(Entity::getDiscard);
      for (int i = 0; i < interfaceElements.size(); i++) {
        Entity e = interfaceElements.get(i);
        if (e != null) e.update(deltaTime);
      }
    }
  }

  public void addInterfaceContainer(SpriteContainer... container) {
    synchronized (interfaceElements) {
      for (SpriteContainer c : container) {
        Collections.addAll(interfaceElements, c.getElements());
      }
    }
  }

  /**
   * Adds a new interface element to the interface elements list.
   *
   * @param element The new interface element to add.
   */
  public void addInterfaceElement(Entity... element) {
    synchronized (interfaceElements) {
      Collections.addAll(interfaceElements, element);
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

          if (button.isActive()
              && (button.getActiveState() == null || this.state == button.getActiveState())
              && button.isTouchWithinButton(event.getX(), event.getY())) {
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
          this.pauseMenu.hide();
          soundEngine.playMusic(SoundType.inGame);
        } else {
          game.pauseGame();
          this.scoreLabel.setText("SCORE: " + game.getScore());
          this.state = InterfaceState.PAUSE_MENU;
          this.pauseMenu.show();
          soundEngine.pauseMusic(SoundType.inGame);
        }
        break;
        // Check other Cases here
      case RESET_GAME:
        Log.d("GameInterface", "Resetting Game");
        this.game.player.vbo().print();
        this.gameOverMenu.hide();
        synchronized (this.game) {
          game.resetGame();
        }
        break;
      case DEBUG_BUTTON:
        this.game.spawnRandomEnemy(1);
        this.game.setScore(this.game.getScore() + 10);
        break;
      case CHEAT_BUTTON:
        this.game.setScore(this.game.getScore() + 1);
        this.scoreLabel.setText("SCORE: " + game.getScore());
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
    soundEngine.pauseMusic(SoundType.inGame);
  }

  public void onResume() {
    soundEngine.playMusic(SoundType.inGame);
  }

  public void onDestroy() {
    soundEngine.stopMusic(SoundType.inGame);
    soundEngine.release();
  }

  public void onPlayerDeath() {
    this.pauseMenu.hide();
    this.scoreLabel.setText(String.valueOf(game.getScore()));
    this.gameOverMenu.show();
  }
}
