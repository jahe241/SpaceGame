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
    // Check if the array is null
    if (this.auxData == null) {
      this.auxData = new float[28]; // Initialize with size 28 as there are 28 elements
    }

    // Set auxData values
    for (int i = 0; i < 4; i++) {
      this.auxData[i * AUX_DATA_STRIDE] = i % 2 == 0 ? 0.0f : 1.0f; // Tex U
      this.auxData[i * AUX_DATA_STRIDE + 1] = i < 2 ? 0.0f : 1.0f; // Tex V

      this.auxData[i * AUX_DATA_STRIDE + 2] = 2.0f; // Flag for solid color

      this.auxData[i * AUX_DATA_STRIDE + 3] = this.colorOverlay[0]; // Color R
      this.auxData[i * AUX_DATA_STRIDE + 4] = this.colorOverlay[1]; // Color G
      this.auxData[i * AUX_DATA_STRIDE + 5] = this.colorOverlay[2]; // Color B
      this.auxData[i * AUX_DATA_STRIDE + 6] = this.colorOverlay[3]; // Color A
    }
  }
}
