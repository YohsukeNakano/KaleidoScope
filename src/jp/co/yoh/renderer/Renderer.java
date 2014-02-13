package jp.co.yoh.renderer;

import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import jp.co.yoh.primitive.Vector;
import jp.co.yoh.renderer.utility.NMath;
import jp.co.yoh.renderer.utility.RendererUtility;
import jp.co.yoh.shape.Shape;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class Renderer implements GLSurfaceView.Renderer{
	private static final String TAG = "Renderer";

	protected Context mContext;
    protected LinkedList<Shape> mOrderingTable = null;
    protected int[] mTextureID;
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    protected GL10 mGL = null;
    protected int mFrameCount = 0;

    public Renderer(Context context) {
        mContext = context;
        mOrderingTable = new LinkedList<Shape>();
    }

    public void setEGLConfigChooser(GLSurfaceView glView)
    {
    	glView.setEGLConfigChooser(true);
    	glView.setRenderer(this);
    }

    protected final static int OFFSCREEN_WIDTH = 512;
    protected final static int OFFSCREEN_HEIGHT = 512;
    protected int[] mOffscreenTextureID = new int[1];
    protected int mOffscreenFrameBuffer = 0;
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	mGL = gl;
    	Display disp = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

    	if (mOffScreenRendering == false) {
    		mScreenWidth = disp.getWidth();
    		mScreenHeight = disp.getHeight();
    	} else
    	{
            mScreenWidth = OFFSCREEN_WIDTH;
        	mScreenHeight = OFFSCREEN_HEIGHT;
       		//オフスクリーンバッファの初期化
       		mOffscreenFrameBuffer = RendererUtility.createFrameBuffer(mGL, mScreenWidth, mScreenHeight,mOffscreenTextureID);
    	}

    	//スクリーンバッファの初期化
    	gl.glDisable(GL10.GL_DITHER);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_FASTEST);

        gl.glClearColor(.5f, .5f, .5f, 1);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);

        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_REPLACE);
    	return;
    }

    protected boolean mOffScreenRendering = false;
    public void setOffScreenRendering() {
    	mOffScreenRendering = true;
    	return;
    }

    public void drawOffscreenBuffer(GL10 gl)
    {
    	GL11ExtensionPack gl11e = (GL11ExtensionPack)mGL;
   		gl11e.glBindFramebufferOES( GL11ExtensionPack.GL_FRAMEBUFFER_OES,mOffscreenFrameBuffer);
   		storeFramebufferStatus();

   		if (onExecFrame() == false) {
    		clearOrderingTable();
       		restoreFramebufferStatus();
       		gl11e.glBindFramebufferOES( GL11ExtensionPack.GL_FRAMEBUFFER_OES,0);
        	mFrameCount++;
    		return;
    	}

        gl.glDisable(GL10.GL_DITHER);

        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_MODULATE);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glFrontFace(GL10.GL_CCW);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        Shape shape = null;
        int currentTextureID = 0;
        for (int i = 0; i < mOrderingTable.size(); i++) {
        	shape = mOrderingTable.get(i);
        	if (shape == null) continue;

        	if (shape.mTextureID > 0) {
            	if (currentTextureID != shape.mTextureID) {
                   	RendererUtility.texture2DChange(gl,shape.mTextureID);
                   	currentTextureID = shape.mTextureID;
            	}
        	}
        	shape.drawBegin(gl);
        	shape.draw(gl);
        	shape.drawEnd(gl);
        }

        clearOrderingTable();
        gl.glFlush();

   		Bitmap bitmap = RendererUtility.getBackBuffer(gl,mScreenWidth,mScreenHeight);
   		GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, bitmap, 0);
		RendererUtility.saveBitmapToJpgFile("test.jpg",bitmap);
   		bitmap.recycle();
   		restoreFramebufferStatus();
   		gl11e.glBindFramebufferOES( GL11ExtensionPack.GL_FRAMEBUFFER_OES,0);

   		return;
    }

    public void onDrawFrame(GL10 gl) {

    	if (onExecFrame() == false) {
    		clearOrderingTable();
        	mFrameCount++;
    		return;
    	}
        gl.glDisable(GL10.GL_DITHER);

        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_MODULATE);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glFrontFace(GL10.GL_CCW);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        Shape shape = null;
        int currentTextureID = 0;
        for (int i = 0; i < mOrderingTable.size(); i++) {
        	shape = mOrderingTable.get(i);
        	if (shape == null) continue;

        	if (shape.mTextureID > 0) {
            	if (currentTextureID != shape.mTextureID) {
                   	RendererUtility.texture2DChange(gl,shape.mTextureID);
                   	currentTextureID = shape.mTextureID;
            	}
        	}
        	shape.drawBegin(gl);
        	shape.draw(gl);
        	shape.drawEnd(gl);
        }
        clearOrderingTable();
        gl.glFlush();
    	mFrameCount++;
    }

    private boolean mExecOnce = false;
    protected boolean onExecFrame() {
    	if (mExecOnce == false){
    		initOnExecFrame();
    		mExecOnce = true;
    	}
    	return true;
    }

    protected void initOnExecFrame()
    {
    	return;
    }

    private void clearOrderingTable()
    {
    	if (mOrderingTable != null) {
    		mOrderingTable.clear();
    	} else
    	{
    		mOrderingTable = new LinkedList<Shape>();
    	}
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
        float ratio = (float) w / h;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        float zNear = 0.01f;
        float zFar = 100.0f;
        float size = 0.01f / (float) Math.tan(45.0f / 2.0);
        gl.glFrustumf(-size, size, -size / ratio, size / ratio, zNear, zFar);
        gl.glOrthof(-1, 1, -1.0f/ratio, 1.0f/ratio, zNear, zFar);
    }

   public boolean addOrderingTable(Shape shape)
   {
	   if (shape == null) return false;
	   return mOrderingTable.add(shape);
   }

   protected MotionEvent mEvent = null;
   public boolean  onTouchEvent(MotionEvent event) {
	    String action = "";
	    switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
	       action = "ACTION_DOWN";
	       float x = event.getX();
	       float y = event.getY();
	 	   Log.d("MotionEvent",
			        "action = " + action + ", " +
			        "x = " + String.valueOf(x) + ", " +
			        "y = " + String.valueOf(y));
	 	   break;
	    case MotionEvent.ACTION_UP:
	        action = "ACTION_UP";
	        break;
	    case MotionEvent.ACTION_MOVE:
	        action = "ACTION_MOVE";
	        break;
	    case MotionEvent.ACTION_CANCEL:
	        action = "ACTION_CANCEL";
	        break;
	    }
		mEvent = event;
	    return true;
   }

   protected Vector mAttitude = new Vector();
   protected Vector mBeforeAttitude = new Vector();
	public void onSensorChanged(Vector attitude) {

		Vector v = new Vector();
		v.x = (int)NMath.radian2Degree(attitude.x);
		v.y = (int)NMath.radian2Degree(attitude.y);
		v.z = (int)NMath.radian2Degree(attitude.z);
		Vector bv = new Vector();
		bv.x = (int)NMath.radian2Degree(mBeforeAttitude.x);
		bv.y = (int)NMath.radian2Degree(mBeforeAttitude.y);
		bv.z = (int)NMath.radian2Degree(mBeforeAttitude.z);
		if (Math.abs((int)v.x - (int)bv.x) <= 10 && Math.abs((int)v.y - (int)bv.y) <= 10 && (Math.abs((int)v.z - (int)bv.z) <= 10)) {
		} else
		{
			mAttitude.copy(mBeforeAttitude);
//			Log.d(TAG,mAttitude.toString());
		}
		attitude.copy(mAttitude);
	}

    private int[] backupViewPort = new int[4];
	public void storeFramebufferStatus()
	{
    	GL11 gl11 = (GL11)mGL;
    	gl11.glGetIntegerv(GL11.GL_VIEWPORT, backupViewPort,0);
    	gl11.glMatrixMode( GL11.GL_PROJECTION_MATRIX);
    	gl11.glPushMatrix();
    	gl11.glMatrixMode( GL11.GL_MODELVIEW_MATRIX );
    	gl11.glPushMatrix();
	}

	public void restoreFramebufferStatus()
	{
    	GL11 gl11 = (GL11)mGL;
    	gl11.glViewport( backupViewPort[0], backupViewPort[1], backupViewPort[2], backupViewPort[3] );
    	gl11.glMatrixMode( GL11.GL_MODELVIEW_MATRIX );
    	gl11.glPopMatrix();
    	gl11.glMatrixMode( GL11.GL_PROJECTION_MATRIX );
    	gl11.glPopMatrix();
	}

}
