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
  float z; // current z position
  short[] indices;
  ShortBuffer indexBuffer;
  float rotationRad = 0f; // storing it in radians reduces the need for frequent conversion

  // REWRITE
  float[] position = {0, 0, 0}; // Quad positions (x, y, z), center of the quad
  float positionData[] = { // Quad corners, dummy data - ignore for now, just for allocating
    // X    Y     Z
    -0.5f, -0.5f, 0.0f, // Bottom-left
    0.5f, -0.5f, 0.0f, // Bottom-right
    -0.5f, 0.5f, 0.0f, // Top-left
    0.5f, 0.5f, 0.0f // Top-right
  };

  Quad(float x, float y, float width, float height) {
    this.positionData[0] = this.x = x;
    this.positionData[1] = this.y = y;
    this.width = width;
    this.height = height;

    // Set vertex data
    this.updatePositionData();

    // set the indices array FIXME: most likely not needed anymore
    this.indices =
        new short[] {
          0, 1, 2, // First triangle (bottom-left, bottom-right, top-left)
          2, 1, 3 // Second triangle (top-left, bottom-right, top-right)
        };
  }

  /**
   * Updates the entity's vertex data to reflect its current position, orientation, and size. This
   * includes recalculating the vertices for the two triangles that make up the entity's rectangular
   * shape based on its current position (x, y), size (width, height), and rotation. The method also
   * sets texture coordinates for each vertex. These are static and do not change with the entity's
   * transformation.
   */
  //  void updateVertexData() {
  //    // Calculate the sine and cosine of the rotation angle for efficient use in vertex rotation
  //    float cosTheta = (float) Math.cos(rotationRad);
  //    float sinTheta = (float) Math.sin(rotationRad);
  //
  //    // Adjust vertex data based on current position, size, and rotation
  //    float[] adjustedVertexData = {
  //      // Bottom-left vertex
  //      x - cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X
  //      y + sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y
  //      0f,
  //      1f,
  //      0f,
  //
  //      // Bottom-right vertex
  //      x + cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X
  //      y - sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y
  //      0f,
  //      1f,
  //      1f,
  //
  //      // Top-left vertex
  //      x - cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X
  //      y + sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y
  //      0f,
  //      1f,
  //      0f,
  //
  //      // Top-right vertex
  //      x + cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X
  //      y - sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y
  //      0f,
  //      1f,
  //      1f,
  //    };
  //
  //    // Reset the buffer to write the new vertex data
  //    vertexData.clear();
  //    vertexData.put(adjustedVertexData);
  //    vertexData.position(0);
  //  }

  /**
   * Updates the entity's positionData Array to reflect its current position, orientation, and size.
   */
  void updatePositionData() {
    // Calculate the sine and cosine of the rotation angle for efficient use in vertex rotation
    float cosTheta = (float) Math.cos(rotationRad);
    float sinTheta = (float) Math.sin(rotationRad);
    float z = this.position[2];
    positionData =
        new float[] {

          // Bottom-left corner
          x - cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X
          y + sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y
          z,

          // Bottom-right corner
          x + cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X
          y - sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y
          z,

          // Top-left corner
          x - cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X
          y + sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y
          z,

          // Top-right corner
          x + cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X
          y - sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y
          z,
        };
  }

  public float[] getPositionArray() {
    return position;
  }

  public void setZ(float z) {
    this.z = z;
  }

  public float getZ() {
    return z;
  }

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

  public float getRotationDeg() {
    return (float) Math.toDegrees(rotationRad);
  }

  public void setRotationRad(float rotationRad) {
    this.rotationRad = rotationRad;
  }

  public void setRotationDeg(float rotationDeg) {
    this.rotationRad = (float) Math.toRadians(rotationDeg);
  }

  public float[] getPositionData() {
    return positionData;
  }

  public short[] getIndices() {
    return indices;
  }

  protected abstract void updateauxData();
}
