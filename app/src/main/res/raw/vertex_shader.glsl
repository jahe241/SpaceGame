#version 100
attribute vec4 a_TexCoordFlag; // Contains Tex U, Tex V, Flag, and first color component (usually R)
attribute vec3 a_Color; // Contains remaining Color components (G, B, A)

varying vec2 v_TexCoord;
varying vec4 v_Color;
varying float v_Flag;

uniform mat4 u_ProjectionMatrix;
attribute vec4 a_Position;

void main() {
    v_TexCoord = a_TexCoordFlag.xy; // Tex U and V
    v_Flag = a_TexCoordFlag.z; // Flag
    v_Color = vec4(a_TexCoordFlag.w, a_Color.x, a_Color.y, a_Color.z); // Correctly combines all color components
    gl_Position = u_ProjectionMatrix * a_Position;
}