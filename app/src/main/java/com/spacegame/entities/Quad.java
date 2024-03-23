package com.spacegame.entities;

import com.spacegame.utils.Vector2D;
import java.nio.ShortBuffer;

/** Abstract class representing a Quad entity. (A Quad is a 2D rectangle with a texture.) */
abstract class Quad {
  float width;
  float height;
  int FLOATS_PER_VERTEX = 6;
  int VERTEX_PER_QUAD = 4;
  int BYTES_PER_FLOAT = 4;
  public static final int AUX_DATA_STRIDE = 7;

  Vector2D position = new Vector2D(0, 0);
  float z_index; // current z position
  short[] indices;
  ShortBuffer indexBuffer;
  float rotationRad = 0f; // storing it in radians reduces the need for frequent conversion

  // REWRITE
  float[] vertexPositionData = { // Quad corners just kept here for reference
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

    // set the indices array FIXME: Move this into the renderer
    this.indices =
        new short[] {
          0, 1, 2, // First triangle (bottom-left, bottom-right, top-left)
          2, 1, 3 // Second triangle (top-left, bottom-right, top-right)
        };
  }

  /**
   * Updates the entity's positionData Array to reflect its current position, orientation, and size.
   */
  public void updateVertexPositionData() {
    // Calculate the sine and cosine of the rotation angle for efficient use in vertex rotation
    float cosTheta = (float) Math.cos(rotationRad);
    float sinTheta = (float) Math.sin(rotationRad);
    float x = position.getX();
    float y = position.getY();
    // Check if the array is null
    if (vertexPositionData == null) {
      vertexPositionData = new float[12]; // Initialize with size 12 as there are 12 elements
    }

    // Bottom-left corner
    vertexPositionData[0] = x - cosTheta * width / 2 - sinTheta * height / 2; // Adjusted X
    vertexPositionData[1] = y + sinTheta * width / 2 - cosTheta * height / 2; // Adjusted Y
    vertexPositionData[2] = z_index;

    // Bottom-right corner
    vertexPositionData[3] = x + cosTheta * width / 2 - sinTheta * height / 2; // Adjusted X
    vertexPositionData[4] = y - sinTheta * width / 2 - cosTheta * height / 2; // Adjusted Y
    vertexPositionData[5] = z_index;

    // Top-left corner
    vertexPositionData[6] = x - cosTheta * width / 2 + sinTheta * height / 2; // Adjusted X
    vertexPositionData[7] = y + sinTheta * width / 2 + cosTheta * height / 2; // Adjusted Y
    vertexPositionData[8] = z_index;

    // Top-right corner
    vertexPositionData[9] = x + cosTheta * width / 2 + sinTheta * height / 2; // Adjusted X
    vertexPositionData[10] = y - sinTheta * width / 2 + cosTheta * height / 2; // Adjusted Y
    vertexPositionData[11] = z_index;
  }

  /**
   * Returns the current position of the Quad.
   *
   * @return The current position as a Vector2D object.
   */
  public Vector2D getPosition() {
    return position;
  }

  /**
   * Sets the z-index of the Quad.
   *
   * @param z_index The new z-index value.
   */
  public void setZ(float z_index) {
    this.z_index = z_index;
  }

  /**
   * Returns the current z-index of the Quad.
   *
   * @return The current z-index as a float.
   */
  public float getZ() {
    return z_index;
  }

  /**
   * Returns the current width of the Quad.
   *
   * @return The current width as a float.
   */
  public float getWidth() {
    return width;
  }

  /**
   * Sets the width of the Quad.
   *
   * @param width The new width value.
   */
  public void setWidth(float width) {
    this.width = width;
  }

  /**
   * Returns the current height of the Quad.
   *
   * @return The current height as a float.
   */
  public float getHeight() {
    return height;
  }

  /**
   * Sets the height of the Quad.
   *
   * @param height The new height value.
   */
  public void setHeight(float height) {
    this.height = height;
  }

  /**
   * Returns the current rotation of the Quad in radians.
   *
   * @return The current rotation as a float in radians.
   */
  public float getRotationRad() {
    return rotationRad;
  }

  /**
   * Returns the current rotation of the Quad in degrees.
   *
   * @return The current rotation as a float in degrees.
   */
  public float getRotationDeg() {
    return (float) Math.toDegrees(rotationRad);
  }

  /**
   * Sets the rotation of the Quad in radians.
   *
   * @param rotationRad The new rotation value in radians.
   */
  public void setRotationRad(float rotationRad) {
    this.rotationRad = rotationRad;
  }

  /**
   * Sets the rotation of the Quad in degrees.
   *
   * @param rotationDeg The new rotation value in degrees.
   */
  public void setRotationDeg(float rotationDeg) {
    this.rotationRad = (float) Math.toRadians(rotationDeg);
  }

  /**
   * Returns the current vertex position data of the Quad.
   *
   * @return The current vertex position data as a float array.
   */
  public float[] getVertexPositionData() {
    return vertexPositionData;
  }

  /**
   * Returns the current indices of the Quad.
   *
   * @return The current indices as a short array.
   */
  public short[] getIndices() {
    return indices;
  }

  /** Abstract method to update auxiliary data. Implementation should be provided by subclasses. */
  protected abstract void updateauxData();

  /**
   * Abstract method to update the Quad. Implementation should be provided by subclasses.
   *
   * @param deltaTime The time difference since the last update.
   */
  abstract void update(float deltaTime);

  /**
   * Sets the position of the Quad. (center)
   *
   * @param newStickPosition The new position as a Vector2D object.
   */
  public void setPosition(Vector2D newStickPosition) {
    this.position = newStickPosition;
  }
}
