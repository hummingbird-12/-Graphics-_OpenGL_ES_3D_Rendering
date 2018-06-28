package com.anative.grmillet.hw5_code;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

public class GLES30Renderer implements GLSurfaceView.Renderer {

    private Context mContext;


    Camera mCamera;

    private Axes mAxes;
    private Mario mMario;
    private Building mBuilding;
    private IronMan mIronMan;
    private Bike mBike;

    public float ratio = 1.0f;
    public int headLightFlag = 1;
    public int lampLightFlag = 1;
    public int pointLightFlag = 1;
    public int cowLightFlag = 1;
    public int textureFlag = 1;

    public float[] mMVPMatrix = new float[16];
    public float[] mProjectionMatrix = new float[16];
    public float[] mModelViewMatrix = new float[16];
    public float[] mModelMatrix = new float[16];
    public float[] mViewMatrix = new float[16];
    public float[] mModelViewInvTrans = new float[16];

    private float[] mMarioModelMatrix = new float[16];

    final static int TEXTURE_ID_MARIO = 0;
    final static int TEXTURE_ID_BUILDING = 1;
    final static int TEXTURE_ID_IRONMAN = 2;
    final static int TEXTURE_ID_BIKE = 3;

    private ShadingProgram mPhongShaderProgram;

    public GLES30Renderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.8f, 1.0f);


        //GLES30.glEnable(GLES30.GL_CULL_FACE);
        //GLES30.glCullFace(GLES30.GL_BACK);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        //GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        // 초기 뷰 매트릭스를 설정.
        mCamera = new Camera();

        //vertex 정보를 할당할 때 사용할 변수.
        int nBytesPerVertex = 8 * 4;        // 3 for vertex, 3 for normal, 2 for texcoord, 4 is sizeof(float)
        int nBytesPerTriangles = nBytesPerVertex * 3;

        /*
            우리가 만든 ShadingProgram을 실제로 생성하는 부분
         */

        mPhongShaderProgram = new ShadingProgram(
                AssetReader.readFromFile("Phong.vert", mContext),
                AssetReader.readFromFile("Phong.frag", mContext)
        );
        mPhongShaderProgram.prepare();
        mPhongShaderProgram.initLightsAndMaterial();
        mPhongShaderProgram.initFlags();
        mPhongShaderProgram.set_up_scene_lights(mViewMatrix);

        /*
                우리가 만든 Object들을 로드.
         */
        mAxes = new Axes();
        mAxes.addGeometry();
        mAxes.prepare();

        mMario = new Mario();
        mMario.addGeometry(AssetReader.readGeometry("Mario_Triangle.geom", nBytesPerTriangles, mContext));
        mMario.prepare();
        mMario.setTexture(AssetReader.getBitmapFromFile("mario.jpg", mContext), TEXTURE_ID_MARIO);

        mBuilding = new Building();
        mBuilding.addGeometry(AssetReader.readGeometry("Building1_vnt.geom", nBytesPerTriangles, mContext));
        mBuilding.prepare();
        mBuilding.setTexture(AssetReader.getBitmapFromFile("building_texture.jpeg", mContext), TEXTURE_ID_BUILDING);

        mIronMan = new IronMan();
        mIronMan.addGeometry(AssetReader.readGeometry("IronMan.geom", nBytesPerTriangles, mContext));
        mIronMan.prepare();
        mIronMan.setTexture(AssetReader.getBitmapFromFile("building_texture.jpeg", mContext), TEXTURE_ID_IRONMAN);

        mBike = new Bike();
        mBike.addGeometry(AssetReader.readGeometry("Bike.geom", nBytesPerTriangles, mContext));
        mBike.prepare();
        mBike.setTexture(AssetReader.getBitmapFromFile("building_texture.jpeg", mContext), TEXTURE_ID_BIKE);
    }

    @Override
    public void onDrawFrame(GL10 gl){ // 그리기 함수 ( = display )
        int pid;
        int timestamp = getTimeStamp();

        /*
             실시간으로 바뀌는 ViewMatrix의 정보를 가져온다.
             MVP 중 V 매트릭스.
         */
        mViewMatrix = mCamera.GetViewMatrix();
        /*
             fovy 변화를 감지하기 위해 PerspectiveMatrix의 정보를 가져온다.
             MVP 중 P
             mat, offset, fovy, ratio, near, far
         */
        Matrix.perspectiveM(mProjectionMatrix, 0, mCamera.getFovy(), ratio, 0.1f, 2000.0f);

        /*
              행렬 계산을 위해 이제 M만 계산하면 된다.
         */

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        mPhongShaderProgram.set_lights1();


        /*
         그리기 영역.
         */
        mPhongShaderProgram.initLightsAndMaterial();
        mPhongShaderProgram.initFlags();
        mPhongShaderProgram.set_up_scene_lights(mViewMatrix);
        mPhongShaderProgram.use(); // 이 프로그램을 사용해 그림을 그릴 것입니다.

        // Axes
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);
        Matrix.transposeM(mModelViewInvTrans, 0, mModelViewMatrix, 0);
        Matrix.invertM(mModelViewInvTrans, 0, mModelViewInvTrans, 0);

        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewProjectionMatrix, 1, false, mMVPMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrix, 1, false, mModelViewMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrixInvTrans, 1, false, mModelViewInvTrans, 0);
        mPhongShaderProgram.setUpMaterial("Axes");
        mAxes.draw();

        // Building
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, 1.0f / 10.0f, 1.0f / 10.0f, 1.0f / 10.0f);

        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);
        Matrix.transposeM(mModelViewInvTrans, 0, mModelViewMatrix, 0);
        Matrix.invertM(mModelViewInvTrans, 0, mModelViewInvTrans, 0);

        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewProjectionMatrix, 1, false, mMVPMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrix, 1, false, mModelViewMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrixInvTrans, 1, false, mModelViewInvTrans, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mBuilding.mTexId[0]);
        GLES30.glUniform1i(mPhongShaderProgram.locTexture, TEXTURE_ID_BUILDING);

        mPhongShaderProgram.setUpMaterial("Building");
        mBuilding.draw();

        // Bike
        Matrix.setIdentityM(mModelMatrix, 0);
        //Matrix.scaleM(mModelMatrix, 0, 1.0f / 10.0f, 1.0f / 10.0f, 1.0f / 10.0f);
        Matrix.translateM(mModelMatrix, 0, 9.5f, 11.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, 90.0f, 0.0f, 0.0f, -1.0f);
        Matrix.rotateM(mModelMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);

        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);
        Matrix.transposeM(mModelViewInvTrans, 0, mModelViewMatrix, 0);
        Matrix.invertM(mModelViewInvTrans, 0, mModelViewInvTrans, 0);

        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewProjectionMatrix, 1, false, mMVPMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrix, 1, false, mModelViewMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrixInvTrans, 1, false, mModelViewInvTrans, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mBike.mTexId[0]);
        GLES30.glUniform1i(mPhongShaderProgram.locTexture, TEXTURE_ID_BIKE);

        mPhongShaderProgram.setUpMaterial("Bike");
        mBike.draw();

        /***** Hierarchical objects: Mario and IronMan *****/
        // Mario
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 7.5f, 7.5f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, getTimeStamp() * 6.0f % 360.0f, 0.0f, 0.0f, -1.0f);
        Matrix.translateM(mModelMatrix, 0, 0.5f, 0.0f, 0.0f);
        Matrix.scaleM(mModelMatrix, 0, 0.8f, 0.9f, 0.8f);
        Matrix.rotateM(mModelMatrix, 0, 180.0f, 0.0f, 0.0f, 1.0f);

        mMarioModelMatrix = mModelMatrix.clone();

        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);
        Matrix.transposeM(mModelViewInvTrans, 0, mModelViewMatrix, 0);
        Matrix.invertM(mModelViewInvTrans, 0, mModelViewInvTrans, 0);

        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewProjectionMatrix, 1, false, mMVPMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrix, 1, false, mModelViewMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrixInvTrans, 1, false, mModelViewInvTrans, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mMario.mTexId[0]);
        GLES30.glUniform1i(mPhongShaderProgram.locTexture, TEXTURE_ID_MARIO);

        mPhongShaderProgram.setUpMaterial("Mario");
        mMario.draw();

        // IronMan (left)
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 1.0f, 0.0f, 1.5f);
        Matrix.rotateM(mModelMatrix, 0, getTimeStamp() * 20.0f % 360.0f, 0.0f, 0.0f, 1.0f);
        Matrix.scaleM(mModelMatrix, 0, 1.0f / 2.0f, 4.0f / 5.0f, 3.0f / 5.0f);
        Matrix.rotateM(mModelMatrix, 0, 180.0f, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(mModelMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);

        Matrix.multiplyMM(mModelMatrix, 0, mMarioModelMatrix, 0, mModelMatrix, 0);

        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);
        Matrix.transposeM(mModelViewInvTrans, 0, mModelViewMatrix, 0);
        Matrix.invertM(mModelViewInvTrans, 0, mModelViewInvTrans, 0);

        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewProjectionMatrix, 1, false, mMVPMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrix, 1, false, mModelViewMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrixInvTrans, 1, false, mModelViewInvTrans, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mIronMan.mTexId[0]);
        GLES30.glUniform1i(mPhongShaderProgram.locTexture, TEXTURE_ID_IRONMAN);

        mPhongShaderProgram.setUpMaterial("IronMan");
        mIronMan.draw();

        // IronMan (right)
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, -1.0f, 0.0f, 1.5f);
        Matrix.rotateM(mModelMatrix, 0, (getTimeStamp() * 20.0f % 360.0f) + 90.0f, 0.0f, 0.0f, 1.0f);
        Matrix.scaleM(mModelMatrix, 0, 1.0f / 2.0f, 4.0f / 5.0f, 3.0f / 5.0f);
        Matrix.rotateM(mModelMatrix, 0, 180.0f, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(mModelMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);

        Matrix.multiplyMM(mModelMatrix, 0, mMarioModelMatrix, 0, mModelMatrix, 0);

        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);
        Matrix.transposeM(mModelViewInvTrans, 0, mModelViewMatrix, 0);
        Matrix.invertM(mModelViewInvTrans, 0, mModelViewInvTrans, 0);

        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewProjectionMatrix, 1, false, mMVPMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrix, 1, false, mModelViewMatrix, 0);
        GLES30.glUniformMatrix4fv(mPhongShaderProgram.locModelViewMatrixInvTrans, 1, false, mModelViewInvTrans, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mIronMan.mTexId[0]);
        GLES30.glUniform1i(mPhongShaderProgram.locTexture, TEXTURE_ID_IRONMAN);

        mPhongShaderProgram.setUpMaterial("IronMan");
        mIronMan.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height){
        GLES30.glViewport(0, 0, width, height);

        ratio = (float)width / height;

        Matrix.perspectiveM(mProjectionMatrix, 0, mCamera.getFovy(), ratio, 0.1f, 2000.0f);
    }

    static int prevTimeStamp = 0;
    static int currTimeStamp = 0;
    static int totalTimeStamp = 0;

    private int getTimeStamp(){
        Long tsLong = System.currentTimeMillis() / 100;

        currTimeStamp = tsLong.intValue();
        if(prevTimeStamp != 0){
            totalTimeStamp += (currTimeStamp - prevTimeStamp);
        }
        prevTimeStamp = currTimeStamp;

        return totalTimeStamp;
    }

    public void setLight1(){
        //mGouraudShaderProgram.light[1].light_on = 1 - mGouraudShaderProgram.light[1].light_on;
    }

}