#version 300 es
precision highp float;

in vec4 v_shaded_color;
layout (location = 0) out vec4 final_color;

void main(void) {
   final_color = v_shaded_color;
}
