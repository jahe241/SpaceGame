package com.spacegame.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

abstract class Quad {
  float width;
  float height;
  int FLOATS_PER_VERTEX = 6;
  int VERTEX_PER_QUAD = 4;
  int BYTES_PER_FLOAT = 4;
  float x; // current x position
  float y; // current y position
  FloatBuffer vertexData;
  short[] indices;
  ShortBuffer indexBuffer;
  float rotationRad = 0f; // storing it in radians reduces the need for frequent conversion

  Quad(float x, float y, float width, float height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;

    // Allocate buffer for vertex data (4 vertices per quad * 6 floats per vertex * 4 bytes per
    // float
    this.vertexData =
        ByteBuffer.allocateDirect(VERTEX_PER_QUAD * FLOATS_PER_VERTEX * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    // Set vertex data
    this.updateVertexData();

    // set the indices array
    this.indices =
        new short[] {
          0, 1, 2, // First triangle (bottom-left, bottom-right, top-left)
          2, 1, 3 // Second triangle (top-left, bottom-right, top-right)
        };

    // Allocate buffer for indices
    ByteBuffer ibb = ByteBuffer.allocateDirect(this.indices.length * 2); // short is 2 bytes
    ibb.order(ByteOrder.nativeOrder());
    this.indexBuffer = ibb.asShortBuffer();
    this.indexBuffer.put(this.indices);
    this.indexBuffer.position(0);
  }

  /**
   * Updates the entity's vertex data to reflect its current position, orientation, and size. This
   * includes recalculating the vertices for the two triangles that make up the entity's rectangular
   * shape based on its current position (x, y), size (width, height), and rotation. The method also
   * sets texture coordinates for each vertex. These are static and do not change with the entity's
   * transformation.
   */
  void updateVertexData() {
    // Calculate the sine and cosine of the rotation angle for efficient use in vertex rotation
    float cosTheta = (float) Math.cos(rotationRad);
    float sinTheta = (float) Math.sin(rotationRad);

    // Adjust vertex data based on current position, size, and rotation
    float[] adjustedVertexData = {
      // Bottom-left vertex
      x - cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X
      y + sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y
      0f,
      1f,
      0f,
      0f, // 0 Since we don't use a texture

      // Bottom-right vertex
      x + cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X
      y - sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y
      0f,
      1f,
      1f,
      0f, // 0

      // Top-left vertex
      x - cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X
      y + sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y
      0f,
      1f,
      0f,
      0f, // 0

      // Top-right vertex
      x + cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X
      y - sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y
      0f,
      1f,
      1f,
      0f, // 0
    };

    // Reset the buffer to write the new vertex data
    vertexData.clear();
    vertexData.put(adjustedVertexData);
    vertexData.position(0);
  }

  public void draw() {}

  public float getWidth() {
    return width;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  public float getHeight() {
    return height;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getRotationRad() {
    return rotationRad;
  }

  public void setRotationRad(float rotationRad) {
    this.rotationRad = rotationRad;
  }
}
