precision mediump float;

varying vec2 v_TexCoord;
varying vec4 v_Color;
varying float v_Flag; // We will use this to decide rendering mode

uniform sampler2D u_Texture;

void main() {
    vec4 texColor = texture2D(u_Texture, v_TexCoord);
    if (v_Flag == 1.0) {
        // If flag indicates texture + color, multiply texture color by vertex color
        gl_FragColor = texColor * v_Color;
    } else if (v_Flag == 2.0) {
        // If flag indicates solid color, use vertex color directly
        gl_FragColor = v_Color;
    } else {
        // Default or unspecified flag behavior (e.g., just texture)
        gl_FragColor = texColor;
    }
}