package com.anative.grmillet.hw5_code;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Assets 폴더에 있는 데이터를 읽어오는 클래스.
 */
public class AssetReader {
    /**
     * Shader를 읽을 때 사용되는 메서드. assets 폴더에 있는 fileName 파일을 읽어서 문자열로 반환한다.
     * @param fileName 쉐이더 파일 명.
     * @param context AssetManager에 접근할 수 있는 context. 보통 Activity의 객체를 사용한다.
     * @return 쉐이더 코드 문자열.
     */
    public static String readFromFile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_WORLD_READABLE);

            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
                returnString.append("\n");
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }

    /**
     * 텍스쳐를 위한 이미지를 읽을 때 주로 사용되는 메서드. JPG 파일인 filename을 디코딩해서 Bitmap으로 반환한다.
     * @param filename 텍스쳐를 만들고자 하는 파일의 이름.
     * @param context AssetManager에 접근할 수 있는 context. 보통 Activity의 객체를 사용한다.
     * @return 디코딩 된 비트맵 데이터.
     */
    public static Bitmap getBitmapFromFile(String filename, Context context) {
        try {
            InputStream is = context.getAssets().open(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * filename에 해당하는 바이너리 파일을 읽어서 이에 해당하는 float 배열을 리턴하는 함수.
     * @param filename 읽고자 하는 데이터의 이름.
     * @param bytesPerPrimitive 하나의 정점을 구성하는데 필요한 바이트 수.
     * @param context AssetManager에 접근할 수 있는 context. 보통 Activity의 객체를 사용한다.
     * @return 디코딩 된 float 배열.
     */
    public static float[] readGeometry(String filename, int bytesPerPrimitive, Context context) {

        int nTriangles = 0;
        InputStream is = null;

        try {
            is = context.getAssets().open(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // 삼각형의 수를 읽는다.
            byte buffer[] = new byte[4];
            is.read(buffer, 0, 4);
            ByteBuffer bb = ByteBuffer.wrap(buffer);
            bb.order(ByteOrder.LITTLE_ENDIAN);

            nTriangles = bb.getInt();

            // '삼각형의 수 *  삼각형 하나를 구성하는데 필요로 하는 바이트 수' 만큼의 데이터를 읽는다.
            byte verticesBuffer[] = new byte[nTriangles * bytesPerPrimitive];
            is.read(verticesBuffer, 0, nTriangles * bytesPerPrimitive);

            // 버퍼 내의 데이터를 float[]로 변환한다.
            ByteBuffer vb = ByteBuffer.wrap(verticesBuffer);
            vb.order(ByteOrder.LITTLE_ENDIAN);
            FloatBuffer vfb = vb.asFloatBuffer();

            float[] vertArr = new float[vfb.remaining()];
            vfb.get(vertArr);

            return vertArr;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}