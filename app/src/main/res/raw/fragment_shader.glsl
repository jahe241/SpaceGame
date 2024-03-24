#version 300 es
precision mediump float;

in vec2 v_TexCoord;
in vec4 v_Color;
in float v_Flag;

uniform sampler2D u_Texture;

out vec4 outColor;

void main() {
    vec4 texColor = texture(u_Texture, v_TexCoord);
    if (texColor.a < 0.1) {
        discard;
    } else if (v_Flag == 0.0) {
        outColor = texColor;
    } else if (v_Flag == 1.0) {
        outColor = texColor * v_Color;
    } else if (v_Flag == 2.0) {
        outColor = v_Color;
    } else {
        outColor = texColor;
    }
}