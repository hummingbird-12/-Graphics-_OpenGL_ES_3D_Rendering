package com.anative.grmillet.hw5_code;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by grmillet on 2018-06-21.
 */

public class GLES30Program {

    protected int mId;
    public int getProgramID(){ return mId; }

    /**
     * Vertex Shader와 Fragment Shader만을 사용하는 기초적인 glProgram.
     * @param vertexShaderCode String 타입의 vertex shader 코드.
     * @param fragmentShaderCode String 타입의 fragment shader 코드.
     */
    public GLES30Program(String vertexShaderCode, String fragmentShaderCode) {

        int vsHandle = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fsHandle = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mId = GLES30.glCreateProgram();
        GLES30.glAttachShader(mId, vsHandle);
        GLES30.glAttachShader(mId, fsHandle);
        GLES30.glLinkProgram(mId);

        int linked[] = new int[1];
        GLES30.glGetProgramiv(mId, GLES30.GL_LINK_STATUS, linked, 0);
        if(linked[0] == 0) {
            String infoLog = GLES30.glGetProgramInfoLog(mId);
            Log.e("Program", infoLog);
            GLES30.glDeleteProgram(mId);
        }
    }

    public int use() {
        GLES30.glUseProgram(mId);
        return mId;
    }

    private int loadShader(int type, String shaderCode) {
        // 빈 쉐이더를 생성하고 그 인덱스를 할당.
        int shader = GLES30.glCreateShader(type);

        // 컴파일 결과를 받을 공간을 생성.
        IntBuffer compiled = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        String shaderType;

        // 컴파일 결과를 출력하기 위해 쉐이더를 구분.
        if (type == GLES30.GL_VERTEX_SHADER)
            shaderType = "Vertex";
        else if (type == GLES30.GL_FRAGMENT_SHADER)
            shaderType = "Fragment";
        else
            shaderType = "Unknown";

        // 빈 쉐이더에 소스코드를 할당.
        GLES30.glShaderSource(shader, shaderCode);
        // 쉐이더에 저장 된 소스코드를 컴파일
        GLES30.glCompileShader(shader);

        // 컴파일 결과 오류가 발생했는지를 확인.
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled);
        // 컴파일 에러가 발생했을 경우 이를 출력.
        if (compiled.get(0) == 0) {
            GLES30.glGetShaderiv(shader, GLES30.GL_INFO_LOG_LENGTH, compiled);
            if (compiled.get(0) > 1) {
                Log.e("Shader", shaderType + " shader: " + GLES30.glGetShaderInfoLog(shader));
            }
            GLES30.glDeleteShader(shader);
            Log.e("Shader", shaderType + " shader compile error.");
        }

        // 완성된 쉐이더의 인덱스를 리턴.
        return shader;
    }
}
