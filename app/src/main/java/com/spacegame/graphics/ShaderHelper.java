package com.spacegame.graphics;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderHelper {

  private static final String TAG = "ShaderHelper";

  public static int compileVertexShader(String shaderCode) {
    return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
  }

  private static int compileShader(int type, String shaderCode) {
    // Create a new shader object
    final int shaderObjectId = GLES20.glCreateShader(type);
    // Check if the shader was created
    if (shaderObjectId == 0) {
      Log.e(TAG, "Could not create new shader.");
      return 0;
    }
    // Pass the shader source to the shader object
    GLES20.glShaderSource(shaderObjectId, shaderCode);
    // Compile the shader
    GLES20.glCompileShader(shaderObjectId);
    final int[] compileStatus = new int[1];
    // Check if the compilation was successful
    GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
    Log.v(
        TAG,
        "Results of compiling source:"
            + "\n"
            + shaderCode
            + "\n:"
            + GLES20.glGetShaderInfoLog(shaderObjectId));

    if (compileStatus[0] == 0) {
      GLES20.glDeleteShader(shaderObjectId);
      Log.w(TAG, "Compilation of shader failed.");

      return 0;
    }
    return shaderObjectId;
  }

  public static int compileFragmentShader(String shaderCode) {
    return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
  }

  public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
    // Create a new program object
    final int programObjectId = GLES20.glCreateProgram();
    // Check if the program was created
    if (programObjectId == 0) {
      Log.w(TAG, "Could not create new program.");

      return 0;
    }
    // Attach the shaders to the program
    GLES20.glAttachShader(programObjectId, vertexShaderId);
    GLES20.glAttachShader(programObjectId, fragmentShaderId);
    // Link the program
    GLES20.glLinkProgram(programObjectId);
    final int[] linkStatus = new int[1];
    // Check if the linking was successful
    GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);
    Log.v(TAG, "Results of linking program:\n" + GLES20.glGetProgramInfoLog(programObjectId));
    Log.v(TAG, "Status of linking program:\n" + linkStatus[0]);

    if (linkStatus[0] == 0) {
      GLES20.glDeleteProgram(programObjectId);
      Log.w(TAG, "Linking of program failed.");

      return 0;
    }
    return programObjectId;
  }

  public static boolean validateProgram(int programObjectId) {
    GLES20.glValidateProgram(programObjectId);
    final int[] validateStatus = new int[1];
    GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
    Log.v(
        TAG,
        "Results of validating program: "
            + validateStatus[0]
            + "\nLog:"
            + GLES20.glGetProgramInfoLog(programObjectId));
    return validateStatus[0] != 0;
  }
}
