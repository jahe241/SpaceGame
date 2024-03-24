#version 100
precision mediump float;

varying vec2 v_TexCoord; // Texture coordinates from vertex shader
varying vec4 v_Color; // RGBA color from vertex shader
varying float v_Flag; // We will use this to decide rendering mode

uniform sampler2D u_Texture;

void main() {
    vec4 texColor = texture2D(u_Texture, v_TexCoord);
    if (texColor.a < 0.1) {
        discard; // Discard transparent pixels (alpha < 0.1)
    } else if (v_Flag == 0.0) {
        gl_FragColor = texColor;
    } else if (v_Flag == 1.0) {
        // Texture + color, multiply texture color by vertex color
        gl_FragColor = texColor * v_Color;
    } else if (v_Flag == 2.0) {
        // solid color, use vertex color directly
        gl_FragColor = v_Color;
    } else {
        // this should never happen
        gl_FragColor = texColor;
    }
}