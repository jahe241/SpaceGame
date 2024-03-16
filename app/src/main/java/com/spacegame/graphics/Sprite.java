package com.spacegame.graphics;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;

import com.spacegame.interfaces.TextureRenderable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Sprite implements TextureRenderable {
    private float x;
    private float y;
    private float width;
    private float height;
    private int texture;

    private FloatBuffer vertexBuffer;

    public Sprite(int x, int y, int width, int height, int texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;

        this.vertexBuffer = ByteBuffer
                .allocateDirect(30*4) // 30 floats, 4 bytes per float
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        initalizeVertecies();
    }

    private void initalizeVertecies() {
        float[] vertices = {
                x - width / 2, y - height / 2, 0f, 1f, 0f, 0f,
                x + width / 2, y - height / 2, 0f, 1f, 1f, 0f,
                x - width / 2, y + height / 2, 0f, 1f, 0f, 1f,
                // Triangle 2
                x - width / 2, y + height / 2, 0f, 1f, 0f, 1f,
                x + width / 2, y - height / 2, 0f, 1f, 1f, 0f,
                x + width / 2, y + height / 2, 0f, 1f, 1f, 1f
        };
        vertexBuffer.clear();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }


    @Override
    public void draw(int program) {
        // If the shaders are not compiled and there is no program
        if (program ==0) return;

        // Get the gl pointer of the attributes
        int aPositionLocation = glGetAttribLocation(program, "a_Position");
        int aTextureCoordinatesLocation = glGetAttribLocation(program, "a_TextureCoordinates");

        // Load position vertex data into the shader
        this.vertexBuffer.position(0);
        glVertexAttribPointer(aPositionLocation, 4, GL10.GL_FLOAT, false, 24, vertexBuffer);
        glEnableVertexAttribArray(aPositionLocation);

        // Load texture vertex data into the shader
        this.vertexBuffer.position(4);
        glVertexAttribPointer(aTextureCoordinatesLocation, 2, GL10.GL_FLOAT, false, 24, vertexBuffer);
        glEnableVertexAttribArray(aTextureCoordinatesLocation);

        // Bind texture to the shader
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, );
    }
}
