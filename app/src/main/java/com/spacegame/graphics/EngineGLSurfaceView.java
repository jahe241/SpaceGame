package com.spacegame.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import com.spacegame.core.Game;

public class EngineGLSurfaceView extends GLSurfaceView {

  private final Game game = new Game();

  private EngineRenderer renderer;

  public EngineGLSurfaceView(Context context) {
    super(context);
    this.setFocusableInTouchMode(true);
    this.requestFocus();

    // Initialize the renderer and set the OpenGL version to 2.0
    this.renderer = new EngineRenderer(context, game);
    this.setEGLContextClientVersion(2);
    this.setRenderer(renderer);
    // Enable touch events
    this.setFocusableInTouchMode(true);
    this.requestFocus();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    game.handleTouchEvent(event);
    return true;
  }
}
