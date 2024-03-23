package com.spacegame.entities;

/**
 * The ColorEntity class extends the Entity class and represents an entity with a solid color. This
 * class is used to create entities that do not have a texture, but instead are rendered with a
 * solid color.
 */
public class ColorEntity extends Entity {

  /**
   * Constructor for the ColorEntity class. This constructor initializes a new ColorEntity object by
   * calling the superclass constructor with the provided parameters. It also sets the color overlay
   * for the entity and updates the auxiliary data.
   *
   * @param x The initial x-coordinate of the entity.
   * @param y The initial y-coordinate of the entity.
   * @param width The width of the entity.
   * @param height The height of the entity.
   * @param colorOverlay The color overlay to apply to the entity's texture. This is an array of
   *     four floats representing the RGBA color values.
   */
  public ColorEntity(float x, float y, float width, float height, float[] colorOverlay) {
    super(null, null, x, y, width, height);
    this.colorOverlay = colorOverlay;
    this.hasColorOverlay = true;
    this.updateauxData();
  }

  @Override
  protected void updateauxData() {
    // Update the color overlay data
    this.auxData =
        new float[] {
          // Flag = 2 for solid color
          // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A
          0.0f, 0.0f, 2.0f, this.colorOverlay[0], this.colorOverlay[1], this.colorOverlay[2],
              this.colorOverlay[3],
          1.0f, 0.0f, 2.0f, this.colorOverlay[0], this.colorOverlay[1], this.colorOverlay[2],
              this.colorOverlay[3],
          0.0f, 1.0f, 2.0f, this.colorOverlay[0], this.colorOverlay[1], this.colorOverlay[2],
              this.colorOverlay[3],
          1.0f, 1.0f, 2.0f, this.colorOverlay[0], this.colorOverlay[1], this.colorOverlay[2],
              this.colorOverlay[3]
        };
  }
}
