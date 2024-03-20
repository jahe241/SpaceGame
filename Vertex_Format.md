# Pipeline

## Load Vertex Data:

- First, fill the position and auxiliary vertex data arrays with values for rendering
- These arrays are stored in app memory, easily modifiable by your render code before uploading

## Buffer Setup:

- The float arrays are copied into direct FloatBuffers using createFloatBuffer
- These buffers reside in native memory space where OpenGL can efficiently access them

Vertex Specification:

- In onDrawFrame, the buffer data is bound to the vertex attribute locations
- Using glVertexAttribPointer, we define the data format for position and aux buffers
- For positions, we have a vec3 - (x, y, z)
- For aux data, we pack a vec7 (u, v, flag, r, g, b, a)

## Shader Execution:

- The vertex shader receives position and aux data through its 'a_Position' and 'a_TexCoordAuxData'
  inputs
- It transforms positions to clipspace and passes through texcoords, colors & flags to varyings
- The fragment shader fetches the varyings and samples the texture modulated by vertex color and/or
  rendered solid color based on the 'flag' value

# Aux Data Packing:

```java
    //    float auxData[] = {
//      // Tex U, Tex V, Flag, Color R, Color G, Color B, Color A
//        0.0f, 0.0f, 2.0f, 1.0f, 0.0f, 0.0f, 1.0f, // Solid red
//        1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, // Textured white
//        0.0f, 1.0f, 2.0f, 0.0f, 1.0f, 0.0f, 1.0f, // Solid green
//        1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f  // Textured white
//    }
```

This would render:

- Bottom-left: Solid red
- Bottom-right: Textured white
- Top-left: Solid green
- Top-right: Textured white