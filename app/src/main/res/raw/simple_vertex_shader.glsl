uniform mat4 u_ProjectionMatrix;
attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;
varying vec2 v_TexCoordinate;

void main()
{
    // calculate the position of the vertex
    gl_Position = u_ProjectionMatrix * a_Position;
    // Set the point size
    gl_PointSize = 40.0;
    // Pass the texcoord to the fragment shader
    v_TexCoordinate = a_TexCoordinate;
}