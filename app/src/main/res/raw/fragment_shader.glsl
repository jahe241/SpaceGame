#version 300 es
precision mediump float;

in vec2 v_TexCoord;
in vec4 v_Color;
in float v_Flag;

uniform sampler2D u_Texture;

out vec4 outColor;

void main() {
    vec4 texColor = texture(u_Texture, v_TexCoord);

    if (v_Flag != 2.0 && texColor.a < 0.1) {
        discard;

    } else if (v_Flag == 0.0) {
        // texture color
        outColor = texColor;

    } else if (v_Flag == 1.0) {
        // texture color * vertex color (color overlay)
        outColor = texColor * v_Color;

    } else if (v_Flag == 2.0) {
        // solid color
        outColor = vec4(v_Color.rgb * v_Color.a, v_Color.a);

    } else {
        outColor = texColor;
    }
}