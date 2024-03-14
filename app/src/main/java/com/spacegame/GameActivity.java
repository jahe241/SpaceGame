package com.spacegame;

import static com.spacegame.utils.WindowManager.setFullscreen;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.spacegame.core.SpaceRenderer;


public class GameActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setFullscreen(getWindow());
    super.onCreate(savedInstanceState);
    GLSurfaceView glSurfaceView = new GLSurfaceView(this);
    glSurfaceView.setEGLContextClientVersion(2);
    glSurfaceView.setRenderer(new SpaceRenderer(this));
    setContentView(glSurfaceView);
  }

  @Override
  protected void onResume() {
    super.onResume();
    setFullscreen(getWindow());
  }


}