package com.spacegame.graphics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import com.spacegame.core.Game;
import com.spacegame.core.GameInterface;

public class EngineGLSurfaceView extends GLSurfaceView {

  private final Game game;
  private final GameInterface gameInterface;

  private final EngineRenderer renderer;

  public EngineGLSurfaceView(Context context) {
    super(context);
    this.setFocusableInTouchMode(true);
    this.requestFocus();

    // Initialize the game and start it
    this.game = new Game();

    // Initialize the renderer and set the OpenGL version to 2.0
    this.gameInterface = new GameInterface(context, game);
    this.renderer = new EngineRenderer(context, game, gameInterface);
    this.setEGLContextClientVersion(2);
    this.setRenderer(renderer);
    // Enable touch events
    this.setFocusableInTouchMode(true);
    this.requestFocus();
  }

  @SuppressLint("ClickableViewAccessibility") // handle later
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    gameInterface.receiveTouchEvent(event);
    return true;
  }
}
