package com.spacegame.entities;

import com.spacegame.graphics.VertexBufferObject;
import com.spacegame.utils.Vector2D;

/** Abstract class representing a Quad entity. (A Quad is a 2D rectangle with a texture.) */
abstract class Quad {
  float width;
  float height;
  Vector2D position;
  float z_index; // current z position
  float rotationRad = 0f; // storing it in radians reduces the need for frequent conversion
  VertexBufferObject vbo;

  Quad(float x, float y, float width, float height) {
    this.position = new Vector2D(x, y);
    this.width = width;
    this.height = height;

    this.vbo = new VertexBufferObject(x, y, 0, width, height, 0);
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
   * Sets the position of the Quad. (center)
   *
   * @param pos The new position as a Vector2D object.
   */
  public void setPosition(Vector2D pos) {
    this.position = pos;
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
   * Sets the z-index of the Quad.
   *
   * @param z_index The new z-index value.
   */
  public void setZ(float z_index) {
    this.z_index = z_index;
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
   * Scales the Quad to the specified width and height.
   *
   * <p>This method updates the width and height of the Quad and also scales the associated
   * VertexBufferObject. The method returns the Quad object itself, allowing for method chaining.
   *
   * @param width The new width for the Quad.
   * @param height The new height for the Quad.
   * @return The Quad object itself.
   */
  public Quad scale(float width, float height) {
    this.width = width;
    this.height = height;
    vbo.scale(width, height);
    return this;
  }

  public Quad scale(float factor) {
    this.width *= factor;
    this.height *= factor;
    vbo.scale(this.width, this.height);
    return this;
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
   * Returns the current rotation of the Quad in radians.
   *
   * @return The current rotation as a float in radians.
   */
  public float getRotationRad() {
    return rotationRad;
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
   * Returns the current rotation of the Quad in degrees.
   *
   * @return The current rotation as a float in degrees.
   */
  public float getRotationDeg() {
    return (float) Math.toDegrees(rotationRad);
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
  public VertexBufferObject vbo() {
    return this.vbo;
  }

  /**
   * Abstract method to update the Quad. Implementation should be provided by subclasses.
   *
   * @param deltaTime The time difference since the last update.
   */
  abstract void update(float deltaTime);

  public void setX(float x) {
    this.position.setX(x);
  }

  public void setY(float y) {
    this.position.setY(y);
  }
}
