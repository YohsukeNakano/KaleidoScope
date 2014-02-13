package jp.co.yoh.kareidoscope.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.co.yoh.kareidscope.R;
import jp.co.yoh.primitive.Vector;
import jp.co.yoh.renderer.Renderer;
import jp.co.yoh.renderer.utility.NMath;
import jp.co.yoh.renderer.utility.RendererUtility;
import jp.co.yoh.shape.Square;
import jp.co.yoh.shape.kareidoscope.hexagon.Hexagon;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.MotionEvent;

public class KareidoScopeRenderer extends Renderer implements GLSurfaceView.Renderer{

    public static final float MAGNIFY = 2.0f;

    private final static Vector[] coordinate = {
		new Vector(0*MAGNIFY,			0*MAGNIFY,		0),
		new Vector(0.0f*MAGNIFY,	 2.0f*MAGNIFY,		0),
		new Vector(0.0f*MAGNIFY,	-2.0f*MAGNIFY,		0),
		new Vector(1.5f*MAGNIFY,	 1.0f*MAGNIFY,		0),
		new Vector(-1.5f*MAGNIFY,	 1.0f*MAGNIFY,		0),
		new Vector(1.5f*MAGNIFY,	 -1.0f*MAGNIFY,		0),
		new Vector(-1.5f*MAGNIFY,	 -1.0f*MAGNIFY,		0),
    };

    private FreeFallRenderer mFreeFallRenderer = null;

    public KareidoScopeRenderer(Context context) {
    	super(context);
    	mFreeFallRenderer = new FreeFallRenderer(context);
    }

    private final static int loadResourceList[] = {
    	R.drawable.robot,
    	R.drawable.images
    };

    public void setEGLConfigChooser(GLSurfaceView glView)
    {
    	super.setEGLConfigChooser(glView);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	super.onSurfaceCreated(gl, config);
    	mFreeFallRenderer.setOffScreenRendering();
    	mFreeFallRenderer.onSurfaceCreated(gl, config);
    	mTextureID = new int[loadResourceList.length];
    	for (int i = 0; i < loadResourceList.length; i++) {
    		mTextureID[i] = RendererUtility.loadTexture(gl,mContext,loadResourceList[i]);
    	}
    }

    private Hexagon[] hex = new Hexagon[coordinate.length];
    private Square square = new Square();
    private int textureId = 0;
    protected void initOnExecFrame()
    {
    	textureId = RendererUtility.initTexture(mGL);
    	for (int i = 0; i < coordinate.length; i++) {
        	hex[i] = new Hexagon();
        	Vector v = coordinate[i];
        	hex[i].setTransMatrix(v);
        	hex[i].mTextureID = mTextureID[0];
        	hex[i].mTextureID = textureId;
        }
    }

    protected boolean onExecFrame()
    {
    	super.onExecFrame();

    	RendererUtility.texture2DChange(mGL,textureId);
        mFreeFallRenderer.drawOffscreenBuffer(mGL);
        textureId = RendererUtility.loadTexture(mGL,"test.jpg");
    	for (int i = 0; i < coordinate.length; i++) {
        	hex[i].mTextureID = textureId;
        }

    	for (int i = 0; i < coordinate.length; i++) {
        	addOrderingTable(hex[i]);
        }
    	square.mTextureID = textureId;
//       	addOrderingTable(square);
    	return true;
    }

    public boolean  onTouchEvent(MotionEvent event) {
    	mFreeFallRenderer.onTouchEvent(event);
    	return super.onTouchEvent(event);
   }
	public void onSensorChanged(Vector attitude) {
		mFreeFallRenderer.onSensorChanged(attitude);
		super.onSensorChanged(attitude);
	}
}
