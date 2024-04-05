package com.spacegame.graphics;

import com.spacegame.utils.Vector2D;

public class VertexBufferObject {
  /**
   * An array representing a single vertex. This is used for reference. The array contains the
   * following values in order: x, y, z, u, v, flag, R, G, B, A
   */
  private float[] oneVertex = { // just for reference
    // x,  y,   z,    u,    v,    flag,  R,    G,    B,     A
    0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
    // 0,  1,   2,    3,    4,    5,    6,    7,    8,     9
  };

  /** The stride, or step from one vertex to the next in the vertex data array. */
  public static final int STRIDE = 10;

  /** The offset of the position data in the vertex data array. */
  public static final int OFFSET_POSITION = 0;

  /** The offset of the texture data in the vertex data array. */
  public static final int OFFSET_TEXTURE = 3;

  /** The offset of the flag data in the vertex data array. */
  public static final int OFFSET_FLAG = 5;

  /** The offset of the color data in the vertex data array. */
  public static final int OFFSET_COLOR = 6;

  /** The vertex data array. This contains the data for all vertices of the object. */
  private final float[] vertexData;

  /** The width of the object. */
  private float width;

  /** The height of the object. */
  private float height;

  /** The x-coordinate of the object. This is kept for ease of use in the resize function. */
  private float x;

  /** The y-coordinate of the object. This is kept for ease of use in the resize function. */
  private float y;

  /** The z-coordinate of the object. This is kept for ease of use in the resize function. */
  private float z;

  /**
   * The rotation of the object, in radians. This is kept for ease of use in the resize function.
   */
  private float rotationRad; // TODO: check if this really is needed

  /**
   * The last rotation of the object, in radians. This is used to check if the rotation has changed.
   */
  private float lastRotationRad = Float.NaN;

  /** The cosine of the rotation. This is calculated when the rotation changes. */
  private float cosTheta;

  /** The sine of the rotation. This is calculated when the rotation changes. */
  private float sinTheta;

  /**
   * Constructor for the VertexBufferObject class. Initializes the vertex data array and sets the
   * initial position, dimensions, and rotation of the object.
   *
   * @param x The initial x-coordinate of the object.
   * @param y The initial y-coordinate of the object.
   * @param z The initial z-coordinate of the object.
   * @param width The initial width of the object.
   * @param height The initial height of the object.
   * @param rotationRad The initial rotation of the object, in radians.
   */
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

  /**
   * Updates the position and rotation of the object and recalculates the position of the vertices.
   *
   * @param x The new x-coordinate of the object.
   * @param y The new y-coordinate of the object.
   * @param z The new z-coordinate of the object.
   * @param rotationRad The new rotation of the object, in radians.
   * @return this VertexBufferObject instance for chaining.
   */
  public VertexBufferObject updateVBOPosition(float x, float y, float z, float rotationRad) {
    // If the rotation has changed, we need to recalculate the cos and sin values
    if (rotationRad != lastRotationRad) {
      cosTheta = (float) Math.cos(rotationRad);
      sinTheta = (float) Math.sin(rotationRad);
      lastRotationRad = rotationRad;
    }

    // Store the new position values, just for the ease of use for the resize function
    this.x = x;
    this.y = y;
    this.z = z;

    float halfWidth = width / 2;
    float halfHeight = height / 2;
    // Bottom-left corner
    vertexData[OFFSET_POSITION] = x - cosTheta * halfWidth - sinTheta * halfHeight;
    vertexData[OFFSET_POSITION + 1] = y + sinTheta * halfWidth - cosTheta * halfHeight;
    vertexData[OFFSET_POSITION + 2] = z;

    // Bottom-right corner
    vertexData[STRIDE + OFFSET_POSITION] = x + cosTheta * halfWidth - sinTheta * halfHeight;
    vertexData[STRIDE + OFFSET_POSITION + 1] = y - sinTheta * halfWidth - cosTheta * halfHeight;
    vertexData[STRIDE + OFFSET_POSITION + 2] = z;

    // Top-left corner
    vertexData[2 * STRIDE + OFFSET_POSITION] = x - cosTheta * halfWidth + sinTheta * halfHeight;
    vertexData[2 * STRIDE + OFFSET_POSITION + 1] = y + sinTheta * halfWidth + cosTheta * halfHeight;
    vertexData[2 * STRIDE + OFFSET_POSITION + 2] = z;

    // Top-right corner
    vertexData[3 * STRIDE + OFFSET_POSITION] = x + cosTheta * halfWidth + sinTheta * halfHeight;
    vertexData[3 * STRIDE + OFFSET_POSITION + 1] = y - sinTheta * halfWidth + cosTheta * halfHeight;
    vertexData[3 * STRIDE + OFFSET_POSITION + 2] = z;

    return this;
  }

  /**
   * Updates the position and rotation of the object using a Vector2D for the position.
   *
   * @param position A Vector2D representing the new position of the object.
   * @param z The new z-coordinate of the object.
   * @param rotationRad The new rotation of the object, in radians.
   * @return this VertexBufferObject instance for chaining.
   */
  public VertexBufferObject updateVBOPosition(Vector2D position, float z, float rotationRad) {
    return updateVBOPosition(position.getX(), position.getY(), z, rotationRad);
  }

  /**
   * Updates the texture coordinates of the object.
   *
   * @param uvs An array of UV coordinates to be set for the object.
   * @return this VertexBufferObject instance for chaining.
   */
  public VertexBufferObject updateTexture(float[] uvs) {
    for (int i = 0; i < 4; i++) {
      // Store the U coordinate for the current vertex
      vertexData[i * STRIDE + OFFSET_TEXTURE] = uvs[i % 2 == 0 ? 0 : 2];
      // Store the V coordinate for the current vertex
      vertexData[i * STRIDE + OFFSET_TEXTURE + 1] = uvs[i < 2 ? 1 : 3];
    }
    return this;
  }

  /**
   * Updates the texture coordinates of the object using a Sprite.
   *
   * @param sprite A Sprite object whose UV coordinates are to be set for the object.
   * @return this VertexBufferObject instance for chaining.
   */
  public VertexBufferObject updateTexture(Sprite sprite) {
    return updateTexture(sprite.uvs());
  }

  /**
   * Sets the flag value in the vertex data to represent a texture. This is done by setting the flag
   * value to 0.0f for each vertex.
   *
   * @return this VertexBufferObject instance for chaining.
   */
  public VertexBufferObject setColor(float[] color) {
    for (int i = 0; i < 4; i++) {
      System.arraycopy(color, 0, vertexData, i * STRIDE + OFFSET_COLOR, color.length);
    }
    return this;
  }

  /**
   * Sets the flag value in the vertex data to represent a color overlay. This is done by setting
   * the flag value to 1.0f for each vertex.
   *
   * @return this VertexBufferObject instance for chaining.
   */
  public VertexBufferObject setFlagTexture() {
    for (int i = 0; i < 4; i++) {
      vertexData[i * STRIDE + OFFSET_FLAG] = 0.0f;
    }
    return this;
  }

  public VertexBufferObject setFlagColorOverlay() {
    for (int i = 0; i < 4; i++) {
      vertexData[i * STRIDE + OFFSET_FLAG] = 1.0f;
    }
    return this;
  }

  /**
   * Sets the flag value in the vertex data to represent a solid color. This is done by setting the
   * flag value to 2.0f for each vertex.
   *
   * @return this VertexBufferObject instance for chaining.
   */
  public VertexBufferObject setFlagSolidColor() {
    for (int i = 0; i < 4; i++) {
      vertexData[i * STRIDE + OFFSET_FLAG] = 2.0f;
    }
    return this;
  }

  /**
   * Scales the VertexBufferObject by updating its width and height. Also updates the position of
   * the vertices based on the new dimensions.
   *
   * @param width The new width.
   * @param height The new height.
   * @return this VertexBufferObject instance for chaining.
   */
  public VertexBufferObject scale(float width, float height) {
    this.width = width;
    this.height = height;
    this.updateVBOPosition(x, y, z, rotationRad);
    return this;
  }

  /**
   * Returns the vertex data array.
   *
   * @return The vertex data array.
   */
  public float[] getVertexArray() {
    return vertexData;
  }

  /**
   * Returns the position of the vertices for this Object
   *
   * @return
   */
  public Vector2D[] getVerticesPositions() {
    Vector2D[] ret = new Vector2D[4];
    for (int i = 0; i < 4; i++) {
      ret[i] = new Vector2D(this.vertexData[i * STRIDE], this.vertexData[i * STRIDE + 1]);
    }
    return ret;
  }

  /**
   * Prints the vertex data array to the console. The data is printed in the format: x, y, z, u, v,
   * flag, R, G, B, A
   */
  public void print() {
    System.out.println("x,  y,   z,    u,    v,    flag,  R,    G,    B,     A");
    for (int i = 0; i < vertexData.length; i++) {
      System.out.print(vertexData[i] + " ");
      if ((i + 1) % STRIDE == 0) {
        System.out.println();
      }
    }
    System.out.println();
  }
}
