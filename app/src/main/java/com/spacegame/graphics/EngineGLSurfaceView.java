package com.spacegame.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.spacegame.core.SpaceGLSSurfaceView;

public class EngineGLSurfaceView extends GLSurfaceView {

    public EngineGLSurfaceView(Context context) {
        super(context);
        this.setFocusableInTouchMode(true);
        this.requestFocus();
        //TODO: Add Renderer
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
