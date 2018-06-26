package com.anative.grmillet.hw5_code;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Android의 OpenGL ES는 배열 데이터를 넘길 때 단순 배열이 아닌 buffer 형태의 데이터를 넘겨줘야 한다.
 * 이를 편하게 수행할 수 있는 Array to Buffer 클래스.
 */
public class BufferConverter {
    static int SIZE_OF_FLOAT = 4;
    static int SIZE_OF_SHORT = 2;

    static FloatBuffer floatArrayToBuffer(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * SIZE_OF_FLOAT);   // 4는 sizeof(float)
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);

        return fb;
    }

    static ShortBuffer shortArrayToBuffer(short[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * SIZE_OF_SHORT);   // 2는 sizeof(int)
        bb.order(ByteOrder.nativeOrder());

        ShortBuffer sb = bb.asShortBuffer();
        sb.put(arr);
        sb.position(0);

        return sb;
    }
}
