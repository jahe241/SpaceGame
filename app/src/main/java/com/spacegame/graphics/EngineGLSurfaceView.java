package com.spacegame.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.spacegame.core.Game;
import com.spacegame.core.SpaceGLSSurfaceView;

public class EngineGLSurfaceView extends GLSurfaceView {

    private Game game;

    private EngineRenderer renderer;

    public EngineGLSurfaceView(Context context) {
        super(context);
        this.setFocusableInTouchMode(true);
        this.requestFocus();

        // Initialize the renderer and set the OpenGL version to 2.0
        this.renderer = new EngineRenderer(context);
        this.setEGLContextClientVersion(2);
        this.setRenderer(renderer);
        // Enable touch events
        this.setFocusableInTouchMode(true);
        this.requestFocus();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
