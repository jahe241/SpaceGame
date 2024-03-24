#version 100
attribute vec3 a_Position; // Contains x, y, z
attribute vec2 a_TexCoord; // Contains u, v
attribute float a_Flag; // Contains flag
attribute vec4 a_Color; // Contains R, G, B, A

varying vec2 v_TexCoord;
varying vec4 v_Color;
varying float v_Flag;

uniform mat4 u_ProjectionMatrix;

void main() {
    v_TexCoord = a_TexCoord;
    v_Flag = a_Flag;
    v_Color = a_Color;
    gl_Position = u_ProjectionMatrix * vec4(a_Position, 1.0);
}