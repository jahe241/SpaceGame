package com.spacegame.core;

import com.spacegame.utils.Vector2D;
import java.nio.ShortBuffer;

abstract class Quad {
  float width;
  float height;
  int FLOATS_PER_VERTEX = 6;
  int VERTEX_PER_QUAD = 4;
  int BYTES_PER_FLOAT = 4;
  Vector2D position = new Vector2D(0, 0);
  float z_index; // current z position
  short[] indices;
  ShortBuffer indexBuffer;
  float rotationRad = 0f; // storing it in radians reduces the need for frequent conversion

  // REWRITE
  float[] vertexPositionData = { // Quad corners, dummy data - ignore for now, just for allocating
    // X    Y     Z
    -0.5f, -0.5f, 0.0f, // Bottom-left
    0.5f, -0.5f, 0.0f, // Bottom-right
    -0.5f, 0.5f, 0.0f, // Top-left
    0.5f, 0.5f, 0.0f // Top-right
  };

  Quad(float x, float y, float width, float height) {
    this.position = new Vector2D(x, y);
    this.width = width;
    this.height = height;

    // Set vertex data
    this.updateVertexPositionData();

    // set the indices array FIXME: most likely not needed anymore
    this.indices =
        new short[] {
          0, 1, 2, // First triangle (bottom-left, bottom-right, top-left)
          2, 1, 3 // Second triangle (top-left, bottom-right, top-right)
        };
  }

  /**
   * Updates the entity's positionData Array to reflect its current position, orientation, and size.
   */
  void updateVertexPositionData() {
    // Calculate the sine and cosine of the rotation angle for efficient use in vertex rotation
    float cosTheta = (float) Math.cos(rotationRad);
    float sinTheta = (float) Math.sin(rotationRad);
    float x = position.getX();
    float y = position.getY();
    vertexPositionData =
        new float[] {

          // Bottom-left corner
          x - cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X
          y + sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y
          z_index,

          // Bottom-right corner
          x + cosTheta * width / 2 - sinTheta * height / 2, // Adjusted X
          y - sinTheta * width / 2 - cosTheta * height / 2, // Adjusted Y
          z_index,

          // Top-left corner
          x - cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X
          y + sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y
          z_index,

          // Top-right corner
          x + cosTheta * width / 2 + sinTheta * height / 2, // Adjusted X
          y - sinTheta * width / 2 + cosTheta * height / 2, // Adjusted Y
          z_index,
        };
  }

  public Vector2D getPosition() {
    return position;
  }

  public void setZ(float z_index) {
    this.z_index = z_index;
  }

  public float getZ() {
    return z_index;
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

  public float[] getVertexPositionData() {
    return vertexPositionData;
  }

  public short[] getIndices() {
    return indices;
  }

  protected abstract void updateauxData();

  abstract void update(float deltaTime);

  public void setPosition(Vector2D newStickPosition) {
    this.position = newStickPosition;
  }
}
