package com.spacegame.graphics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import com.spacegame.core.Game;
import com.spacegame.core.GameInterface;
import android.graphics.Point;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class EngineGLSurfaceView extends GLSurfaceView {

  private final Game game;
  private final GameInterface gameInterface;
  private final EngineRenderer renderer;

  // The size.x and size.y fields now contain the width and height of the display in pixels
  int width;
  int height;

  public EngineGLSurfaceView(Context context) {
    super(context);
    this.setFocusableInTouchMode(true);
    this.requestFocus();
    // Get the screen width and height
    //    int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
    //    int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

    // Initialize the game and start it
    getScreenSize(context);
    this.game = new Game(this.height, this.width);

    // Initialize the renderer and set the OpenGL version to 3.0
    this.gameInterface = new GameInterface(context, game);
    this.renderer = new EngineRenderer(context, game, gameInterface);
    this.setEGLContextClientVersion(3);
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

  private void getScreenSize(Context context) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    display.getRealSize(size); // getSize() does not include the menu bar
    width = size.x;
    height = size.y;
    size = null;
  }

  @Override
  public void onPause() {
    super.onPause();
    gameInterface.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    gameInterface.onResume();
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    super.surfaceDestroyed(holder);
    gameInterface.onDestroy();
    Log.d("EngineGLSurfaceView", "Surface destroyed");
  }


}
