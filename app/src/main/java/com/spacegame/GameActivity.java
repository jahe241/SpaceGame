package com.spacegame;

import static com.spacegame.utils.WindowManager.setFullscreen;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.spacegame.core.SpaceGLSSurfaceView;
import com.spacegame.graphics.EngineGLSurfaceView;


public class GameActivity extends AppCompatActivity {

  private EngineGLSurfaceView surfaceView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setFullscreen(getWindow());
    super.onCreate(savedInstanceState);

    surfaceView = new EngineGLSurfaceView(this);
    //surfaceView.context = this;
    setContentView(surfaceView);
  }

  @Override
  protected void onResume() {
    super.onResume();
    setFullscreen(getWindow());
  }

  @Override
  public void onPause() {
    super.onPause();  // Always call the superclass method first
  }


}