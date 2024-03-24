#version 300 es
in vec3 a_Position; // Contains x, y, z
in vec2 a_TexCoord; // Contains u, v
in float a_Flag; // Contains flag
in vec4 a_Color; // Contains R, G, B, A

out vec2 v_TexCoord;
out vec4 v_Color;
out float v_Flag;

uniform mat4 u_ProjectionMatrix;
uniform int u_ActiveVertices; // Uniform to pass the number of active vertices

void main() {
    if (gl_VertexID >= u_ActiveVertices) {
        // Skip processing this vertex, or place it off-screen
        gl_Position = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }
    // Regular vertex processing
    v_TexCoord = a_TexCoord;
    v_Flag = a_Flag;
    v_Color = a_Color;
    gl_Position = u_ProjectionMatrix * vec4(a_Position, 1.0);
}