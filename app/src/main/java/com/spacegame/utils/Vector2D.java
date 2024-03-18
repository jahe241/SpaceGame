package com.spacegame.utils;

/* Helper Class for frequently used Vector Oprations in 2D-Space */
public class Vector2D {
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
  public static float calculateAngle(float x1, float y1, float x2, float y2) {
    return (float) Math.atan2(y2 - y1, x2 - x1);
  }
}
