package com.spacegame.graphics;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.spacegame.R;
import com.spacegame.utils.TextResourceReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class EngineRenderer implements GLSurfaceView.Renderer {
    Context context;

    int program = 0;

    public EngineRenderer(Context context) {
        super();
        this.context = context;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color to black
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Compile vertex shader code
        String vertexShaderSource =
                TextResourceReader.readTextFileFromResource(this.context, R.raw.simple_vertex_shader);
        int compiledVertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        // Compile fragment shader code
        String fragmentShaderSource =
                TextResourceReader.readTextFileFromResource(this.context, R.raw.simple_fragment_shader);
        int compiledFragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        // Link the shaders into a program
        this.program = ShaderHelper.linkProgram(compiledVertexShader, compiledFragmentShader);
    }

    private int loadTexture(int resourceId) {
        // Generate a new texture object and save id in textureObjectIds
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        // If the texture object was not created, return 0
        if (textureObjectIds[0] == 0) {
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Create Bitmap from resource return 0 if failed
        final Bitmap bitmap = BitmapFactory.decodeResource(this.context.getResources(), resourceId, options);
        if (bitmap == null) {
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // Generate Mipmap for the texture
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Load the bitmap into the texture
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glGenerateMipmap(GL_TEXTURE_2D);

        // Free the bitmap and unbind the texture
        glBindTexture(GL_TEXTURE_2D, 0);
        bitmap.recycle();

        return textureObjectIds[0];
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
