package net.speleomaniac.mapit;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Sencer Coltu on 08.02.2014.
 */
class SurveyGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private float mBearing=0, mInclination=0, mDepth=0, mPinchDist=0, mAngleX=0, mAngleY=0;

    public SurveyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {

        //float light_position[] = { 0, 0, 0, 0 };
        float light_color[] = { 1, 1, 1, 1 };
        float light_direction[]  = {0, 0, 1};

        gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
        gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
        gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
        gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
        gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        //gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light_position, 0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION, light_direction, 0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, light_color, 0);
        gl.glEnable(GL10.GL_COLOR_MATERIAL);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height <= 0) return;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 1000.0f);
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        Log.d("OPENGL", "Resized");
        requestRender();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glRotatef(mInclination, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(mBearing, 0.0f, 1.0f, 0.0f);
        gl.glTranslatef(0.0f, 0.0f, mDepth);
        gl.glRotatef(mAngleX, 0, 1, 0);
        gl.glRotatef(mAngleY, 1, 0, 0);
        MapItActivity.mStorage.CaveModel.draw(gl);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        switch(e.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (e.getPointerCount() == 2) {
                    float x = e.getX(0) - e.getX(1);
                    float y = e.getY(0) - e.getY(1);
                    mPinchDist = (float) Math.sqrt(x * x + y * y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (e.getPointerCount() == 1) {
                    float px;
                    float py;

                    float x = e.getX(0);
                    float y = e.getY(0);

                    if (e.getHistorySize() > 0) {
                        px = e.getHistoricalX(0, 0);
                        py = e.getHistoricalY(0, 0);
                    } else {
                        px = x;
                        py = y;
                    }

                    mBearing -= (x - px) / 5.0f;
                    mInclination -= (y - py) / 5.0f;
                    requestRender();
                } else if (e.getPointerCount() == 2) {
                    //pinch zoom
                    float x = e.getX(0) - e.getX(1);
                    float y = e.getY(0) - e.getY(1);
                    float dist = (float) Math.sqrt(x * x + y * y);
                    float diff = dist - mPinchDist;
                    mPinchDist = dist;

                    mDepth += diff / 100.0f;
                    requestRender();
                } else if (e.getPointerCount() == 3) {
                    float px;
                    float py;

                    float x = e.getX(0);
                    float y = e.getY(0);

                    if (e.getHistorySize() > 0) {
                        px = e.getHistoricalX(0, 0);
                        py = e.getHistoricalY(0, 0);
                    } else {
                        px = x;
                        py = y;
                    }

                    mAngleX -= (x - px) / 5.0f;
                    mAngleY -= (y - py) / 5.0f;
                    requestRender();
                }
                break;
        }
        return true;
    }
}
