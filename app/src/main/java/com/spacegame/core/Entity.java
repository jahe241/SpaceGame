package com.spacegame.core;

import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import static javax.microedition.khronos.opengles.GL10.GL_FLOAT;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLES;

import android.util.Log;
import android.view.MotionEvent;

import com.spacegame.graphics.EngineRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class Entity {

    int gl_texture_ptr;
    public static final int FLOATS_PER_VERTEX = 6;
    public static final int VERTEX_PER_QUAD = 6;
    private float x;
    private float y;
    private float width;
    private float height;
    private float[] velocity = {0f,0f};
    private int roationDeg = 0;

    private FloatBuffer vertexData;

    public Entity(float x, float y, float width, float height, int gl_texture_ptr) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gl_texture_ptr = gl_texture_ptr;

        // Initialize the vertex data
        float[] initialVertexData = {
                // Triangle 1
                x - width / 2, y - height / 2, 0f, 1f, 0f, 0f,
                x + width / 2, y - height / 2, 0f, 1f, 1f, 0f,
                x - width / 2, y + height / 2, 0f, 1f, 0f, 1f,
                // Triangle 2
                x - width / 2, y + height / 2, 0f, 1f, 0f, 1f,
                x + width / 2, y - height / 2, 0f, 1f, 1f, 0f,
                x + width / 2, y + height / 2, 0f, 1f, 1f, 1f
        };
        this.vertexData = ByteBuffer
            .allocateDirect(initialVertexData.length * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
        // Set vertex data

        vertexData.clear();
        vertexData.put(initialVertexData);
        vertexData.position(0);
    }

    public void draw() {
        // Static pointer not loaded, skip draw frame
        Log.d("Entity", "GL Pointer: " + EngineRenderer.gl_a_Position_ptr);
        if (EngineRenderer.gl_a_Position_ptr == -1) {
            Log.e("Entity", "GL Pointer not set!!");
            return;
        }
        // Give position vertex data to the shader
        vertexData.position(0);
        glVertexAttribPointer(
                EngineRenderer.gl_a_Position_ptr,
                4,
                GL_FLOAT,
                false,
                FLOATS_PER_VERTEX * 4,
                vertexData
        );
        glEnableVertexAttribArray(EngineRenderer.gl_a_Position_ptr);
        // Give texture vertex data to the shader
        vertexData.position(4);
        glVertexAttribPointer(
               EngineRenderer.gl_a_TexCoordinate_ptr,
                2,
                GL_FLOAT,
                false,
                FLOATS_PER_VERTEX * 4,
                vertexData
        );
        glEnableVertexAttribArray(EngineRenderer.gl_a_TexCoordinate_ptr);

        // Bind the texture
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.gl_texture_ptr);

        // Draw the all triangles
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    public void update(float delta) {
        //Log.d("Entity Coords", "x: " + x + " y:" + y);
        // Update the entity's position
        this.x += this.velocity[0] * delta;
        this.y += this.velocity[1] * delta;

        // Update the entity's vertex data
        float[] adjustedVertexData = {
            // Triangle 1
            x - width / 2, y - height / 2, 0f, 1f, 0f, 0f,
            x + width / 2, y - height / 2, 0f, 1f, 1f, 0f,
            x - width / 2, y + height / 2, 0f, 1f, 0f, 1f,
            // Triangle 2
            x - width / 2, y + height / 2, 0f, 1f, 0f, 1f,
            x + width / 2, y - height / 2, 0f, 1f, 1f, 0f,
            x + width / 2, y + height / 2, 0f, 1f, 1f, 1f
        };
        vertexData.clear();
        vertexData.put(adjustedVertexData);
        vertexData.position(0);

        this.draw();
    }

    public void onTouch(MotionEvent event)  {
        float touchX = event.getX();
        float touchY = event.getY();

        this.x = touchX;
        this.y = touchY;
    }
}
