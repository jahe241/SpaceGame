package com.spacegame.utils;

/* Helper Class for frequently used Vector Oprations in 2D-Space */
public class Vector2D {
  private float x;
  private float y;

  public void setX(float x) {
    this.x = x;
  }

  public void setY(float y) {
    this.y = y;
  }

  public Vector2D(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  /**
   * Add another vector to this vector
   *
   * @param other
   */
  public void add(Vector2D other) {
    this.x = this.getX() + other.getX();
    this.y = this.getY() + other.getY();
  }

  /**
   * Subtract another vector from this vector
   *
   * @param other
   */
  public void sub(Vector2D other) {
    this.x = this.getX() - other.getX();
    this.y = this.getY() - other.getY();
  }

  /**
   * Multiply this vector by a scalar
   *
   * @param scalar
   */
  public void mult(float scalar) {
    this.x = this.getX() * scalar;
    this.y = this.getY() * scalar;
  }

  /**
   * Divide this vector by a scalar
   *
   * @param scalar
   */
  public void div(float scalar) {
    this.x = this.getX() / scalar;
    this.y = this.getY() / scalar;
  }

  /**
   * Calculate the scalar product of this vector and another vector
   *
   * @return
   */
  public float scalarProduct(Vector2D other) {
    return this.getX() * other.getX() + this.getY() * other.getY();
  }

  /**
   * Calc the distance vector from this vector to another vector
   *
   * @param other
   * @return the distance vector
   */
  public Vector2D to(Vector2D other) {
    return new Vector2D(other.getX() - this.getX(), other.getY() - this.getY());
  }

  /**
   * Calculate the length of this vector
   *
   * @return the normalized vector of this vector
   */
  public Vector2D normalized() {
    float length = (float) Math.sqrt(x * x + y * y);
    return new Vector2D(x / length, y / length);
  }

  /** Normalize this vector */
  public void normalize() {
    float length = (float) Math.sqrt(x * x + y * y);
    x = x / length;
    y = y / length;
  }

  /**
   * Calculate the angle from this vector to another vector
   *
   * @return the angle in radians
   */
  public float calcAngle(Vector2D other) {
    return (float) Math.atan2(other.getY() - this.getY(), other.getX() - this.getX());
  }

  /**
   * Calculate the euclidean distance between two points
   *
   * @param x1 x-coordinate of the first point
   * @param y1 y-coordinate of the first point
   * @param x2 x-coordinate of the second point
   * @param y2 y-coordinate of the second point
   * @return the distance between the two points
   */
  public static float calculateDistance(float x1, float y1, float x2, float y2) {
    return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  /**
   * Calculate the angle between two points in Radians
   *
   * @param x1 x-coordinate of the first point
   * @param y1 y-coordinate of the first point
   * @param x2 x-coordinate of the second point
   * @param y2 y-coordinate of the second point
   * @return the angle between the two points
   */
  public static float calcAngle(float x1, float y1, float x2, float y2) {
    return (float) Math.atan2(y2 - y1, x2 - x1);
  }

  /**
   * Calculate the length of a vector
   *
   * @param x x-coordinate of the vector
   * @param y y-coordinate of the vector
   * @return the length of the vector
   */
  public static float calcLength(float x, float y) {
    return (float) Math.sqrt(x * x + y * y);
  }
}
