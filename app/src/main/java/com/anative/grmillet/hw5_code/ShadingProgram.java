package com.anative.grmillet.hw5_code;

import android.opengl.GLES30;
import android.opengl.Matrix;

/**
 * Created by grmillet on 2018-06-21.
 */

class LightParameters {
    int light_on;
    float position[] = new float[4];
    float ambient_color[] = new float[4];
    float diffuse_color[] = new float[4];
    float specular_color[] = new float[4];
    float spot_direction[] = new float[3];
    float spot_exponent;
    float spot_cutoff_angle;
};

class MaterialParameters {
    float ambient_color[] = new float[4];
    float diffuse_color[] = new float[4];
    float specular_color[] = new float[4];
    float emissive_color[] = new float[4];
    float specular_exponent;
}

class LocLightParameter {
    int light_on;
    int position;
    int ambient_color, diffuse_color, specular_color;
    int spot_direction;
    int spot_exponent;
    int spot_cutoff_angle;
    int light_attenuation_factors;
}

class LocMaterialParameter {
    int ambient_color, diffuse_color, specular_color, emissive_color;
    int specular_exponent;
}

public class ShadingProgram extends GLES30Program{

    final static int NUMBER_OF_LIGHT_SUPPORTED = 4;

    int locModelViewProjectionMatrix;
    int locModelViewMatrix;
    int locModelViewMatrixInvTrans;

    int locGlobalAmbientColor;
    LocLightParameter locLight[];
    LocMaterialParameter locMaterial = new LocMaterialParameter();
    int locTexture;
    int locFlagTextureMapping;

    int mFlagTextureMapping;

    LightParameters light[];

    MaterialParameters materialMario = new MaterialParameters();

    public ShadingProgram(String vertexShaderCode, String fragmentShaderCode){
        super(vertexShaderCode, fragmentShaderCode);
    }

    /**
     * GLProgram에 결합 된 Shader 내 변수들의 location 인덱스를 설정하는 함수.
     */
    public void prepare() {
        locLight = new LocLightParameter[NUMBER_OF_LIGHT_SUPPORTED];
        for(int i=0 ; i<NUMBER_OF_LIGHT_SUPPORTED ; i++)
            locLight[i] = new LocLightParameter();

        locModelViewProjectionMatrix = GLES30.glGetUniformLocation(mId, "ModelViewProjectionMatrix");
        locModelViewMatrix = GLES30.glGetUniformLocation(mId, "ModelViewMatrix");
        locModelViewMatrixInvTrans = GLES30.glGetUniformLocation(mId, "ModelViewMatrixInvTrans");

        locTexture = GLES30.glGetUniformLocation(mId, "base_texture");

        locFlagTextureMapping = GLES30.glGetUniformLocation(mId, "flag_texture_mapping");

        locGlobalAmbientColor = GLES30.glGetUniformLocation(mId, "global_ambient_color");
        for (int i = 0; i < NUMBER_OF_LIGHT_SUPPORTED; i++) {
            String lightNumStr = "light[" + i + "]";
            locLight[i].light_on = GLES30.glGetUniformLocation(mId, lightNumStr + ".light_on");
            locLight[i].position = GLES30.glGetUniformLocation(mId, lightNumStr + ".position");
            locLight[i].ambient_color = GLES30.glGetUniformLocation(mId, lightNumStr + ".ambient_color");
            locLight[i].diffuse_color = GLES30.glGetUniformLocation(mId, lightNumStr + ".diffuse_color");
            locLight[i].specular_color = GLES30.glGetUniformLocation(mId, lightNumStr + ".specular_color");
            locLight[i].spot_direction = GLES30.glGetUniformLocation(mId, lightNumStr + ".spot_direction");
            locLight[i].spot_exponent = GLES30.glGetUniformLocation(mId, lightNumStr + ".spot_exponent");
            locLight[i].spot_cutoff_angle = GLES30.glGetUniformLocation(mId, lightNumStr + ".spot_cutoff_angle");
            locLight[i].light_attenuation_factors = GLES30.glGetUniformLocation(mId, lightNumStr + ".light_attenuation_factors");
        }

        locMaterial.ambient_color = GLES30.glGetUniformLocation(mId, "material.ambient_color");
        locMaterial.diffuse_color = GLES30.glGetUniformLocation(mId, "material.diffuse_color");
        locMaterial.specular_color = GLES30.glGetUniformLocation(mId, "material.specular_color");
        locMaterial.emissive_color = GLES30.glGetUniformLocation(mId, "material.emissive_color");
        locMaterial.specular_exponent = GLES30.glGetUniformLocation(mId, "material.specular_exponent");
    }

    /**
     * Light와 Material의 값을 설정하는 함수.
     */
    public void initLightsAndMaterial() {
        GLES30.glUseProgram(mId);

        GLES30.glUniform4f(locGlobalAmbientColor, 0.115f, 0.115f, 0.115f, 1.0f);
        for (int i = 0; i < NUMBER_OF_LIGHT_SUPPORTED; i++) {
            GLES30.glUniform1i(locLight[i].light_on, 0); // turn off all lights initially
            GLES30.glUniform4f(locLight[i].position, 0.0f, 0.0f, 1.0f, 0.0f);
            GLES30.glUniform4f(locLight[i].ambient_color, 0.0f, 0.0f, 0.0f, 1.0f);
            if (i == 0) {
                GLES30.glUniform4f(locLight[i].diffuse_color, 1.0f, 1.0f, 1.0f, 1.0f);
                GLES30.glUniform4f(locLight[i].specular_color, 1.0f, 1.0f, 1.0f, 1.0f);
            }
            else {
                GLES30.glUniform4f(locLight[i].diffuse_color, 0.0f, 0.0f, 0.0f, 1.0f);
                GLES30.glUniform4f(locLight[i].specular_color, 0.0f, 0.0f, 0.0f, 1.0f);
            }
            GLES30.glUniform3f(locLight[i].spot_direction, 0.0f, 0.0f, -1.0f);
            GLES30.glUniform1f(locLight[i].spot_exponent, 0.0f); // [0.0, 128.0]
            GLES30.glUniform1f(locLight[i].spot_cutoff_angle, 180.0f); // [0.0, 90.0] or 180.0 (180.0 for no spot light effect)
            GLES30.glUniform4f(locLight[i].light_attenuation_factors, 1.0f, 0.0f, 0.0f, 0.0f); // .w != 0.0f for no ligth attenuation
        }

        GLES30.glUniform4f(locMaterial.ambient_color, 0.2f, 0.2f, 0.2f, 1.0f);
        GLES30.glUniform4f(locMaterial.diffuse_color, 0.8f, 0.8f, 0.8f, 1.0f);
        GLES30.glUniform4f(locMaterial.specular_color, 0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glUniform4f(locMaterial.emissive_color, 0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glUniform1f(locMaterial.specular_exponent, 0.0f); // [0.0, 128.0]

        GLES30.glUseProgram(0);


        // Material 설정.
        materialMario.ambient_color[0] = 0.24725f;
        materialMario.ambient_color[1] = 0.1995f;
        materialMario.ambient_color[2] = 0.0745f;
        materialMario.ambient_color[3] = 1.0f;

        materialMario.diffuse_color[0] = 0.75164f;
        materialMario.diffuse_color[1] = 0.60648f;
        materialMario.diffuse_color[2] = 0.22648f;
        materialMario.diffuse_color[3] = 1.0f;

        materialMario.specular_color[0] = 0.728281f;
        materialMario.specular_color[1] = 0.655802f;
        materialMario.specular_color[2] = 0.466065f;
        materialMario.specular_color[3] = 1.0f;

        materialMario.specular_exponent = 51.2f;

        materialMario.emissive_color[0] = 0.1f;
        materialMario.emissive_color[1] = 0.1f;
        materialMario.emissive_color[2] = 0.0f;
        materialMario.emissive_color[3] = 1.0f;
    }

    public void initFlags() {


        mFlagTextureMapping = 1;

        GLES30.glUseProgram(mId);
        GLES30.glUniform1i(locFlagTextureMapping, mFlagTextureMapping);
        GLES30.glUseProgram(0);
    }

    /**
     * 쉐이딩을 위한 각종 light 관련 값을 그래픽 메모리에 전달하는 함수.
     * @param viewMatrix 현재 상태의 view matrix.
     */

    public void set_up_scene_lights(float[] viewMatrix) {
        light = new LightParameters[NUMBER_OF_LIGHT_SUPPORTED];
        for(int i=0 ; i<NUMBER_OF_LIGHT_SUPPORTED ; i++)
            light[i] = new LightParameters();

        // point_light_EC: use light 0
        light[0].light_on = 1;
        light[0].position[0] = 0.0f; light[0].position[1] = 100.0f; 	// point light position in EC
        light[0].position[2] = 0.0f; light[0].position[3] = 1.0f;

        light[0].ambient_color[0] = 0.13f; light[0].ambient_color[1] = 0.13f;
        light[0].ambient_color[2] = 0.13f; light[0].ambient_color[3] = 1.0f;

        light[0].diffuse_color[0] = 0.5f; light[0].diffuse_color[1] = 0.5f;
        light[0].diffuse_color[2] = 0.5f; light[0].diffuse_color[3] = 1.5f;

        light[0].specular_color[0] = 0.8f; light[0].specular_color[1] = 0.8f;
        light[0].specular_color[2] = 0.8f; light[0].specular_color[3] = 1.0f;

        // spot_light_WC: use light 1
        light[1].light_on = 1;
        light[1].position[0] = -200.0f; light[1].position[1] = 500.0f; // spot light position in WC
        light[1].position[2] = -200.0f; light[1].position[3] = 1.0f;

        light[1].ambient_color[0] = 0.152f; light[1].ambient_color[1] = 0.152f;
        light[1].ambient_color[2] = 0.152f; light[1].ambient_color[3] = 1.0f;

        light[1].diffuse_color[0] = 0.572f; light[1].diffuse_color[1] = 0.572f;
        light[1].diffuse_color[2] = 0.572f; light[1].diffuse_color[3] = 1.0f;

        light[1].specular_color[0] = 0.772f; light[1].specular_color[1] = 0.772f;
        light[1].specular_color[2] = 0.772f; light[1].specular_color[3] = 1.0f;

        light[1].spot_direction[0] = 0.0f; light[1].spot_direction[1] = -1.0f; // spot light direction in WC
        light[1].spot_direction[2] = 0.0f;
        light[1].spot_cutoff_angle = 20.0f;
        light[1].spot_exponent = 8.0f;


        GLES30.glUseProgram(mId);
        GLES30.glUniform1i(locLight[0].light_on, light[0].light_on);
        GLES30.glUniform4fv(locLight[0].position, 1, BufferConverter.floatArrayToBuffer(light[0].position));
        GLES30.glUniform4fv(locLight[0].ambient_color, 1, BufferConverter.floatArrayToBuffer(light[0].ambient_color));
        GLES30.glUniform4fv(locLight[0].diffuse_color, 1, BufferConverter.floatArrayToBuffer(light[0].diffuse_color));
        GLES30.glUniform4fv(locLight[0].specular_color, 1, BufferConverter.floatArrayToBuffer(light[0].specular_color));

        GLES30.glUniform1i(locLight[1].light_on, light[1].light_on);
        // need to supply position in EC for shading
        float[] positionEC = new float[4];
        Matrix.multiplyMV(positionEC, 0, viewMatrix, 0, light[1].position, 0);

        GLES30.glUniform4fv(locLight[1].position, 1, BufferConverter.floatArrayToBuffer(positionEC));
        GLES30.glUniform4fv(locLight[1].ambient_color, 1, BufferConverter.floatArrayToBuffer(light[1].ambient_color));
        GLES30.glUniform4fv(locLight[1].diffuse_color, 1, BufferConverter.floatArrayToBuffer(light[1].diffuse_color));
        GLES30.glUniform4fv(locLight[1].specular_color, 1, BufferConverter.floatArrayToBuffer(light[1].specular_color));


        float[] spot_direction = {
                light[1].spot_direction[0], light[1].spot_direction[1], light[1].spot_direction[2], 0.0f
        };

        float[] directionEC = new float[4];
        Matrix.multiplyMV(directionEC, 0, viewMatrix, 0, spot_direction, 0);

        GLES30.glUniform3fv(locLight[1].spot_direction, 1, BufferConverter.floatArrayToBuffer(directionEC));
        GLES30.glUniform1f(locLight[1].spot_cutoff_angle, light[1].spot_cutoff_angle);
        GLES30.glUniform1f(locLight[1].spot_exponent, light[1].spot_exponent);
        GLES30.glUseProgram(0);
    }

    /*
                Setup For Material.
     */
    public void setUpMaterialMario() {
        GLES30.glUniform4fv(locMaterial.ambient_color, 1, BufferConverter.floatArrayToBuffer(materialMario.ambient_color));
        GLES30.glUniform4fv(locMaterial.diffuse_color, 1, BufferConverter.floatArrayToBuffer(materialMario.diffuse_color));
        GLES30.glUniform4fv(locMaterial.specular_color, 1, BufferConverter.floatArrayToBuffer(materialMario.specular_color));
        GLES30.glUniform1f(locMaterial.specular_exponent, materialMario.specular_exponent);
        GLES30.glUniform4fv(locMaterial.emissive_color, 1, BufferConverter.floatArrayToBuffer(materialMario.emissive_color));
    }

    /*
                Setup For Light.
     */

    public void set_lights1() {
        GLES30.glUseProgram(mId);
        GLES30.glUniform1i(locLight[1].light_on, light[1].light_on);
        GLES30.glUseProgram(0);
    }

}
