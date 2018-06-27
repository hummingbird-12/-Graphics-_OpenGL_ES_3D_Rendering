#version 300 es
precision highp float;

struct LIGHT {
	vec4 position; // assume point or direction in EC in this example shader
	vec4 ambient_color, diffuse_color, specular_color;
	vec4 light_attenuation_factors; // compute this effect only if .w != 0.0f
	vec3 spot_direction;
	float spot_exponent;
	float spot_cutoff_angle;
	int light_on;
};

struct MATERIAL {
	vec4 ambient_color;
	vec4 diffuse_color;
	vec4 specular_color;
	vec4 emissive_color;
	float specular_exponent;
};

#define NUMBER_OF_LIGHTS_SUPPORTED 4

uniform vec4 global_ambient_color;
uniform LIGHT light[NUMBER_OF_LIGHTS_SUPPORTED];
uniform MATERIAL material;

uniform int flag_texture_mapping;

uniform sampler2D base_texture;

// shader extension
/*
uniform bool u_screen_effect = false;
uniform float u_screen_width = 0.125f;

uniform bool u_blind_effect = false;
uniform float u_blind_intensity = 90.0f;

uniform bool u_cartoon_effect = false;
uniform float u_cartoon_level = 3.0f;

uniform bool u_negative_effect = false;

uniform bool u_wave_effect = false;
uniform int u_wave_position = 0;
*/

const float zero_f = 0.0f;
const float one_f = 1.0f;

in vec3 v_position_EC;
in vec3 v_normal_EC;
in vec2 v_position_sc;
in vec2 tex_coord;

layout (location = 0) out vec4 final_color;

vec4 lighting_equation(in vec3 P_EC, in vec3 N_EC, in vec4 base_color) {
	vec4 color_sum;
	float local_scale_factor, tmp_float; 
	vec3 L_EC;

    color_sum = material.emissive_color + global_ambient_color * base_color;
	//color_sum = material.emissive_color + global_ambient_color * material.ambient_color;
 
	for (int i = 0; i < NUMBER_OF_LIGHTS_SUPPORTED; i++) {
		if (light[i].light_on == 0) continue;

		local_scale_factor = one_f;
		if (light[i].position.w != zero_f) { // point light source
			L_EC = light[i].position.xyz - P_EC.xyz;

			if (light[i].light_attenuation_factors.w  != zero_f) {
				vec4 tmp_vec4;

				tmp_vec4.x = one_f;
				tmp_vec4.z = dot(L_EC, L_EC);
				tmp_vec4.y = sqrt(tmp_vec4.z);
				tmp_vec4.w = zero_f;
				local_scale_factor = one_f/dot(tmp_vec4, light[i].light_attenuation_factors);
			}

			L_EC = normalize(L_EC);

			if (light[i].spot_cutoff_angle < 180.0f) { // [0.0f, 90.0f] or 180.0f
				float spot_cutoff_angle = clamp(light[i].spot_cutoff_angle, zero_f, 90.0f);
				vec3 spot_dir = normalize(light[i].spot_direction);

				tmp_float = dot(-L_EC, spot_dir);
				if (tmp_float >= cos(radians(spot_cutoff_angle))) {
				    tmp_float = pow(tmp_float, light[i].spot_exponent);
				    /*
					if((i > 0) && u_blind_effect) {
						tmp_float = pow(tmp_float, u_light[i].spot_exponent) * cos(u_blind_intensity * acos(tmp_float));
					}
					else
						tmp_float = pow(tmp_float, u_light[i].spot_exponent);
					if(tmp_float < zero_f) tmp_float = zero_f;
					*/
				}
				else 
					tmp_float = zero_f;
				local_scale_factor *= tmp_float;
			}
		}
		else {  // directional light source
			L_EC = normalize(light[i].position.xyz);
		}	

		if (local_scale_factor > zero_f) {				
			vec4 local_color_sum = light[i].ambient_color * material.ambient_color;

			tmp_float = dot(N_EC, L_EC);
			if (tmp_float > zero_f) {
			    local_color_sum += light[i].diffuse_color*base_color*tmp_float;

			    vec3 H_EC = normalize(L_EC - normalize(P_EC));
                tmp_float = dot(N_EC, H_EC);
                if (tmp_float > zero_f) {
                    local_color_sum += light[i].specular_color
                        *material.specular_color*pow(tmp_float, material.specular_exponent);
                }
			    /*
				if(u_cartoon_effect)
					local_color_sum += u_light[i].diffuse_color*u_material.diffuse_color*floor(tmp_float*u_cartoon_level)/u_cartoon_level;
				else {
					local_color_sum += u_light[i].diffuse_color*u_material.diffuse_color*tmp_float;
			
					vec3 H_EC = normalize(L_EC - normalize(P_EC));
					tmp_float = dot(N_EC, H_EC); 
					if (tmp_float > zero_f) {
						local_color_sum += u_light[i].specular_color
										   *u_material.specular_color*pow(tmp_float, u_material.specular_exponent);
					}
				}
				*/
			}
			color_sum += local_scale_factor*local_color_sum;
		}
	}
 	return color_sum;
}

void main(void) {

	/*
	if(u_screen_effect) {
		float x_mod, y_mod;
		x_mod = mod(v_position_sc.x*5.0f, 1.0f);
		y_mod = mod(v_position_sc.y*3.0f, 1.0f);

		if( (x_mod > u_screen_width) && (x_mod < 1.0f - u_screen_width) && (y_mod > u_screen_width) && (y_mod < 1.0f - u_screen_width) )
			discard;
	}

	if(u_wave_effect)
		if( (v_position_sc.x >= u_wave_position - 10) && (v_position_sc.x <= u_wave_position + 10) )
			discard;
	*/

	vec4 base_color, shaded_color;
    if (flag_texture_mapping == 1)
      	base_color = texture(base_texture, tex_coord);
    else
       	base_color = material.diffuse_color;

	final_color = lighting_equation(v_position_EC, normalize(v_normal_EC), base_color); // for normal rendering
	/*
	if(u_negative_effect)
		final_color = vec4(1.0f) - final_color;
	*/
}
