package com.spacegame.graphics;

import com.spacegame.utils.Vector2D;

public class VertexBufferObject {
  // schema
  private float[] oneVertex = {
    // x,  y,   z,    u,    v,    flag,  R,    G,    B,     A
    0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
    // 0,  1,   2,    3,    4,    5,    6,    7,    8,     9
  };
  // old position data was 12 floats in total
  // old aux data was was 28 floats in total
  public static final int STRIDE = 10; // "step" from one vertex to the next
  public static final int OFFSET_POSITION = 0;
  public static final int OFFSET_TEXTURE = 3;
  public static final int OFFSET_FLAG = 5; // not needed?
  public static final int OFFSET_COLOR = 6;

  private final float[] vertexData;

  private float width;
  private float height;

  // We're keeping these just for the ease of use for the resize function
  private float x;
  private float y;
  private float z;
  private float rotationRad;
  private float lastRotationRad = Float.NaN;
  private float cosTheta;
  private float sinTheta;

  public VertexBufferObject(
      float x, float y, float z, float width, float height, float rotationRad) {
    vertexData = new float[STRIDE * 4];
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
    this.z = z;
    this.rotationRad = rotationRad;
    updateVBOPosition(x, y, z, rotationRad);
  }

  public VertexBufferObject updateVBOPosition(float x, float y, float z, float rotationRad) {
    // https://en.wikipedia.org/wiki/Rotation_matrix
    // [ cos(theta)  -sin(theta) ]
    // [ sin(theta)   cos(theta) ]

    // If the rotation has changed, we need to recalculate the cos and sin values
    if (rotationRad != lastRotationRad) {
      cosTheta = (float) Math.cos(rotationRad);
      sinTheta = (float) Math.sin(rotationRad);
      lastRotationRad = rotationRad;
    }

    this.x = x;
    this.y = y;
    this.z = z;

    // Iterate over the 4 corners of the quad
    for (int i = 0; i < 4; i++) {
      // Adjust the X and Y coordinates based on the rotation and the width and height
      vertexData[i * STRIDE + OFFSET_POSITION] =
          x - cosTheta * width / 2 - sinTheta * height / 2; // Adjusted X

      vertexData[i * STRIDE + OFFSET_POSITION + 1] =
          y + sinTheta * width / 2 - cosTheta * height / 2; // Adjusted Y

      vertexData[i * STRIDE + OFFSET_POSITION + 2] = z; // Z index
    }
    return this;
  }

  public VertexBufferObject updateVBOPosition(Vector2D position, float z, float rotationRad) {
    return updateVBOPosition(position.getX(), position.getY(), z, rotationRad);
  }

  public VertexBufferObject updateTexture(float[] uvs) {
    for (int i = 0; i < 4; i++) {
      // Store the U coordinate for the current vertex
      vertexData[i * STRIDE + OFFSET_TEXTURE] = uvs[i * 2];
      // Store the V coordinate for the current vertex
      vertexData[i * STRIDE + OFFSET_TEXTURE + 1] = uvs[i * 2 + 1];
    }
    return this;
  }

  public VertexBufferObject updateTexture(Sprite sprite) {
    return updateTexture(sprite.uvs());
  }

  public VertexBufferObject setColor(float[] color) {
    for (int i = 0; i < 4; i++) {
      System.arraycopy(color, 0, vertexData, i * STRIDE + OFFSET_COLOR, color.length);
    }
    return this;
  }

  public VertexBufferObject setFlagTexture() {
    vertexData[OFFSET_FLAG] = 0.0f;
    return this;
  }

  public VertexBufferObject setFlagColorOverlay() {
    vertexData[OFFSET_FLAG] = 1.0f;
    return this;
  }

  public VertexBufferObject setFlagSolidColor() {
    vertexData[OFFSET_FLAG] = 2.0f;
    return this;
  }

  public VertexBufferObject scale(float width, float height) {
    this.width = width;
    this.height = height;
    this.updateVBOPosition(x, y, z, rotationRad);
    return this;
  }

  public float[] getVertexArray() {
    return vertexData;
  }
}
