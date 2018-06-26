package com.anative.grmillet.hw5_code;

/**
 * Created by grmillet on 2018-06-22.
 */

import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLUtils;

/**
 * DrawElements()를 기반으로 오브젝트를 그리는 클래스.
 */
abstract public class Object {
    static int VERTEX_POS_INDEX = 0;      // layout(location = 0) vec4 vPosition;
    static int VERTEX_NORM_INDEX = 1;     // layout(location = 1) vec4 vNormal;
    static int VERTEX_TEX_INDEX = 2;      // layout(location = 2) vec4 vTexCoord;

    static int VERTEX_POS_SIZE  = 3;  // x, y, z
    static int VERTEX_NORM_SIZE = 3;  // x, y, z
    static int VERTEX_TEX_SIZE = 2;   // s, t

    int mVBO[] = new int[4];
    int mVAO[] = new int[1];
    int mTexId[] = new int[2];

    private Bitmap mTexture;

    /**
     * 그래픽 메모리에 텍스쳐 데이터를 넘겨주고 mTexId[0]에 해당 텍스쳐의 아이디를 저장하는 함수.
     * @param tex 텍스쳐 이미지에 해당하는 비트맵 데이터.
     * @param textureId 중복되지 않는 텍스쳐 아이디.
     */
    public void setTexture(Bitmap tex, int textureId) {
        mTexture = tex;

        // 텍스쳐 설정.
        GLES30.glGenTextures(1, mTexId, 0);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + textureId);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexId[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, mTexture, 0);

        mTexture.recycle();
        mTexture = null;
    }

    /**
     * Object를 상속받는 클래스는 VBO와 VAO를 생성하는 prepare 함수를 구현해야 한다.
     */
    abstract void prepare();
}
