package com.anative.grmillet.hw5_code;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    SurfaceView mGLSurfaceView;

    private RadioGroup radio;

    public static RadioButton leftButton;
    public static RadioButton middleButton1;
    public static RadioButton middleButton2;
    public static RadioButton rightButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, 0, 0, "HeadLight");
        menu.add(0, 1, 1, "LampLight");
        menu.add(0, 2, 2, "PointLight");
        menu.add(0, 3, 3, "CowLight");
        menu.add(0, 4, 4, "TextureOnOff");

        return true;
    }

    int onoff(int flag) {
        if (flag == 1)
            flag = 0;
        else
            flag = 1;
        return flag;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                mGLSurfaceView.mRenderer.headLightFlag = onoff(mGLSurfaceView.mRenderer.headLightFlag);
                break;
            case 1:
                mGLSurfaceView.mRenderer.lampLightFlag = onoff(mGLSurfaceView.mRenderer.lampLightFlag);
                break;
            case 2:
                mGLSurfaceView.mRenderer.pointLightFlag = onoff(mGLSurfaceView.mRenderer.pointLightFlag);
                break;
            case 3:
                mGLSurfaceView.mRenderer.cowLightFlag = onoff(mGLSurfaceView.mRenderer.cowLightFlag);
                break;
            case 4:
                mGLSurfaceView.mRenderer.textureFlag = onoff(mGLSurfaceView.mRenderer.textureFlag);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout1);
        if(mGLSurfaceView == null){
            mGLSurfaceView = new SurfaceView(this);
            frameLayout.addView(mGLSurfaceView);
        }

        // 버튼 그룹
        radio = (RadioGroup) findViewById(R.id.radioGroup1);
        //버튼 4개를 각각의 변수에 allocate.
        leftButton = (RadioButton) findViewById(R.id.radioButton1);
        middleButton1 = (RadioButton) findViewById(R.id.radioButton2);
        middleButton2 = (RadioButton) findViewById(R.id.radioButton3);
        rightButton = (RadioButton) findViewById(R.id.radioButton4);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

}

///////////////////////////////////////////////////////////////////////////////////
/*
        SurfaceViewClass
 */

class SurfaceView extends GLSurfaceView{

    // member variables
    public GLES30Renderer mRenderer = null;

    // 포인트점 두개 이용 : 포인트점0,포인트점1
    // 포인트0,1 각각의 이전포인트x,y 현재 포인트 x,y
    // delta : 이전포인트와 현재 포인트간의 차이
    public float previousX[] = new float[2], currentX[] = new float[2], deltaX;
    public float previousY[] = new float[2], currentY[] = new float[2], deltaY;
    int animIndex = 0;
    float angle = 0;

    public SurfaceView(Context context){
        super(context);
        //Create an OpenGL ES 3.0 context.
        setEGLContextClientVersion(3);

        //Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new GLES30Renderer(context);
        setRenderer(mRenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        final int SENSITIVITY = 5;// 이벤트를 발생시킬지 기준이 되는 값

        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < e.getPointerCount(); i++) {
                    currentX[i] = e.getX(i);
                    currentY[i] = e.getY(i);
                    previousX[i] = currentX[i];
                    previousY[i] = currentY[i];
                }
                break;
            case MotionEvent.ACTION_MOVE: // touch move
                for (int i = 0; i < e.getPointerCount(); i++) {
                    previousX[i] = currentX[i];
                    previousY[i] = currentY[i];

                    currentX[i] = e.getX(i);
                    currentY[i] = e.getY(i);
                }

                // delta calculation of each coord
                deltaX = currentX[0] - previousX[0];
                deltaY = currentY[0] - previousY[0];

                if (e.getPointerCount() == 1) {

                    previousX[1] = previousX[0];
                    currentX[1] = currentX[0];

                    if (MainActivity.leftButton.isChecked() == true) {
                        if (Math.abs(deltaY) > SENSITIVITY)
                            mRenderer.mCamera.MoveUpward(deltaY);
                        if (Math.abs(deltaX) > SENSITIVITY)
                            mRenderer.mCamera.MoveSideward(-deltaX);
                    }

                    else if (MainActivity.middleButton1.isChecked() == true) {
                        if (Math.abs(deltaY) > SENSITIVITY)
                            mRenderer.mCamera.Pitch(deltaY);
                        if (Math.abs(deltaX) > SENSITIVITY)
                            mRenderer.mCamera.Yaw(deltaX);

                    } else if (MainActivity.middleButton2.isChecked() == true) {
                        if (Math.abs(deltaY) > SENSITIVITY)
                            mRenderer.mCamera.MoveForward(deltaY);

                    } else if (MainActivity.rightButton.isChecked() == true) {
                        if (Math.abs(deltaX) > SENSITIVITY)
                            mRenderer.mCamera.Roll(deltaX);
                    }

                } else if (e.getPointerCount() == 2) {
                    float pre = Math.abs(previousX[0] - previousX[1]);
                    float cur = Math.abs(currentX[0] - currentX[1]);

                    if (pre - cur > SENSITIVITY)
                        mRenderer.mCamera.Zoom(20);
                    else if (pre - cur < -1 * SENSITIVITY)
                        mRenderer.mCamera.Zoom(-20);
                }

                requestRender();
                break;
        }
        return true;
    }

}