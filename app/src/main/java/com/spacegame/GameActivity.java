package com.spacegame;

import static com.spacegame.utils.WindowManager.setFullscreen;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.spacegame.core.SpaceGLSSurfaceView;


public class GameActivity extends AppCompatActivity {

  private SpaceGLSSurfaceView spaceGLSurfaceView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setFullscreen(getWindow());
    super.onCreate(savedInstanceState);

    spaceGLSurfaceView = new SpaceGLSSurfaceView(this);
    //spaceGLSurfaceView.context = this;
    setContentView(spaceGLSurfaceView);
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