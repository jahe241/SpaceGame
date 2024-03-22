package com.spacegame.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Objects;

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

  public Vector2D(Vector2D other) {
    this.x = other.getX();
    this.y = other.getY();
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
  public Vector2D add(Vector2D other) {
    return new Vector2D(this.getX() + other.getX(), this.getY() + other.getY());
  }

  /**
   * Subtract another vector from this vector
   *
   * @param other
   */
  public Vector2D sub(Vector2D other) {
    return new Vector2D(this.getX() - other.getX(), this.getY() - other.getY());
  }

  /**
   * Multiply this vector by a scalar
   *
   * @param scalar
   */
  public Vector2D mult(float scalar) {
    return new Vector2D(this.getX() * scalar, this.getY() * scalar);
  }

  /**
   * Divide this vector by a scalar
   *
   * @param scalar
   */
  public Vector2D div(float scalar) {
    return new Vector2D(this.getX() / scalar, this.getY() / scalar);
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
    if (x == 0 && y == 0) return new Vector2D(0, 0);
    float length = (float) Math.sqrt(x * x + y * y);
    return new Vector2D(x / length, y / length);
  }

  /** Normalize this vector */
  public void normalize() {
    if (x == 0 && y == 0) return;
    float length = (float) Math.sqrt(x * x + y * y);
    x = x / length;
    y = y / length;
  }

  /**
   * Calculate the length of this vector
   *
   * @return the length of this vector
   */
  public float length() {
    return (float) Math.sqrt(x * x + y * y);
  }

  /**
   * Calculate the given Vector to a given size
   *
   * @param size
   * @return
   */
  public Vector2D toSize(float size) {
    return this.normalized().mult(size);
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
   * Calculate the euclidean distance between two vectors
   *
   * @param v1
   * @param v2
   * @return
   */
  public static float calculateDistance(Vector2D v1, Vector2D v2) {
    return calculateDistance(v1.getX(), v1.getY(), v2.getX(), v2.getY());
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Vector2D vector2D = (Vector2D) o;
    return Float.compare(x, vector2D.x) == 0 && Float.compare(y, vector2D.y) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @NonNull
  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
