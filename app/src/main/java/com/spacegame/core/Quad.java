package com.spacegame.core;

public class Quad {
  float width;
  float height;
  int FLOATS_PER_VERTEX = 6;
  int VERTEX_PER_QUAD = 4;
  float x; // current x position
  float y; // current y position

  Quad(float x, float y, float width, float height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
}
