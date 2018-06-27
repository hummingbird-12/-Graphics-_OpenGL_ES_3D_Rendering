#version 300 es
precision highp float;

uniform mat4 ModelViewProjectionMatrix;
uniform mat4 ModelViewMatrix;
uniform mat4 ModelViewMatrixInvTrans;

layout (location = 0) in vec3 v_position;
layout (location = 1) in vec3 v_normal;
layout (location = 2) in vec2 v_tex_coord;

out vec3 v_position_EC;
out vec3 v_normal_EC;
out vec2 v_position_sc; // 2D coordinates for extension
out vec2 tex_coord;

void main(void) {
	v_position_EC = (ModelViewMatrix*vec4(v_position, 1.0f)).xyz;
	v_normal_EC = normalize(ModelViewMatrixInvTrans* vec4(v_normal, 1.0)).xyz;
	v_position_sc = vec2(v_position);
	tex_coord = v_tex_coord;

	gl_Position = ModelViewProjectionMatrix*vec4(v_position, 1.0f);
}