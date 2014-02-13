package jp.co.yoh.kareidoscope.renderer;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import jp.co.yoh.kareidscope.R;
import jp.co.yoh.primitive.Particle;
import jp.co.yoh.primitive.Vector;
import jp.co.yoh.renderer.Renderer;
import jp.co.yoh.renderer.utility.RendererUtility;
import jp.co.yoh.shape.Square;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;

public class FreeFallRenderer extends Renderer implements GLSurfaceView.Renderer{

    private static final int PARTICLE_NUM = 200;
    private static final int FALL_SPEED = 20;


	private static final String TAG = "FreeFallRenderer";
    public FreeFallRenderer(Context context) {
    	super(context);
    }

	private final static Vector[] coordinate = {
		new Vector(0,			0,		0),
    };

    private final static int loadResourceList[] = {
    	R.drawable.images,
    	R.drawable.robot,
    	R.drawable.image2,
    	R.drawable.image3,
    	R.drawable.image4,
    	R.drawable.image5,
    	R.drawable.image6,
    	R.drawable.image7,
    	R.drawable.image8,
    	R.drawable.image9,
    	R.drawable.image10,
    	R.drawable.image11,

    };

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	super.onSurfaceCreated(gl, config);
    	mTextureID = new int[loadResourceList.length];
    	for (int i = 0; i < loadResourceList.length; i++) {
    		mTextureID[i] = RendererUtility.loadTexture(gl,mContext,loadResourceList[i]);
    	}

    	Vector vector = RendererUtility.calcScreen2ObjectCoord(gl, mScreenHeight, 0, 0);
//    	Log.d("FreeFallRenderer","x = " + vector.x + " y = " + vector.y + " z = " + vector.z);

    }

    private Vector mRect[] = new Vector[4];
    private Square mBG = new Square();
    private Particle[] mParticle;
    private Square mSquare[] = new Square[PARTICLE_NUM];
    protected void initOnExecFrame()
    {
    	mRect[0] = RendererUtility.calcScreen2ObjectCoord(mGL, mScreenHeight, (int)mScreenWidth, (int)0);				//右上
		mRect[1] = RendererUtility.calcScreen2ObjectCoord(mGL, mScreenHeight, (int)mScreenWidth, (int)mScreenHeight);	//右下
		mRect[2] = RendererUtility.calcScreen2ObjectCoord(mGL, mScreenHeight, (int)0, (int)0);							//左上
		mRect[3] = RendererUtility.calcScreen2ObjectCoord(mGL, mScreenHeight, (int)0, (int)mScreenHeight);				//左下
    	mRect[0].z = mRect[1].z = mRect[2].z = mRect[3].z = -0.1f;
//    	for (int i = 0; i < mRect.length; i++) Log.d(TAG,"x = " + mRect[i].x + " y = " + mRect[i].y + " z = " + mRect[i].z);
    	for (int i = 0; i < coordinate.length; i++) {
        	Vector v = coordinate[i];
        	if (mOffScreenRendering == false) {
            	mBG.setVertexCoordinate(0, mRect[0]);
            	mBG.setVertexCoordinate(1, mRect[1]);
            	mBG.setVertexCoordinate(2, mRect[2]);
            	mBG.setVertexCoordinate(3, mRect[3]);
        		mBG.setTransMatrix(v.x,v.y,mRect[0].z);
        	} else
        	{
        		mBG.setTransRotScaleMatrix(0,0,-0.1f,0,0,0,10.0f,10.0f,10.0f);
        	}
        	mBG.mTextureID = mTextureID[0];
        }

    	mParticle = new Particle[PARTICLE_NUM];
    	for (int i = 0; i < PARTICLE_NUM; i++) {
    		mParticle[i] = new Particle();
    		Random random = new Random();
    		mParticle[i].t.x = random.nextInt(mScreenWidth);
    		mParticle[i].t.y = random.nextInt(mScreenHeight);
    		mParticle[i].r.init();
    		mParticle[i].s.unit();
   			mParticle[i].r.z = ((float)random.nextInt()) * 0.090f;
    		mParticle[i].s.x = mParticle[i].s.y = 0.2f;
    		mParticle[i].mAction = Particle.FL_ACTION_JUMP_DOWN;
    		//    		Log.d(TAG,"x = " + mParticle[i].t.x + " y = " + mParticle[i].t.y);
    		mParticle[i].timer = random.nextInt() % 100;
    		mParticle[i].mTextureID = mTextureID[Math.abs((random.nextInt() % loadResourceList.length))];
//    		Log.d("FreeFallRenderer","mParticle[i].mTextureID = " + mParticle[i].mTextureID);
    		mSquare[i] = new Square();
    	}
    	return;
    }

    private static final double PI_HALF = Math.PI / 2;
    private static final int SPEED_UNIT = 100;
    protected boolean onExecFrame()
    {
    	super.onExecFrame();
    	//ロジック書くところ
    	if (mParticle != null) {
        	for (int i = 0; i < mParticle.length; i++) {
        		//加速度に適用しないとダメ
        		int timer = mParticle[i].timer % SPEED_UNIT;
        		double param = (PI_HALF / SPEED_UNIT) * timer;
        		double n = Math.cos(param);
//        		Log.d(TAG,"timer = " + timer + " n = " + n);
        		switch (mParticle[i].mAction) {
        		case Particle.FL_ACTION_NORMAL:
        			break;
        		case Particle.FL_ACTION_JUMP_DOWN:
    				mParticle[i].t.x += mAttitude.z * FALL_SPEED;
    				mParticle[i].t.y -= mAttitude.y * FALL_SPEED;
        			break;
        		case Particle.FL_ACTION_JUMP_UP:
        			break;
        		case Particle.FL_ACTION_LANDING:
        			break;
        		}
        		//リミットチェック
        		if (mParticle[i].t.x < 60) mParticle[i].t.x  = 60;
        		if (mParticle[i].t.y < 60) mParticle[i].t.y  = 60;
        		if (mParticle[i].t.x > (mScreenWidth - 60)) mParticle[i].t.x  = mScreenWidth - 60;
        		if (mParticle[i].t.y > (mScreenHeight - 60)) mParticle[i].t.y  = mScreenHeight - 60;
        		mParticle[i].timer++;
        		mParticle[i].mActionTimer--;
        	}

        	//当たり判定
        	final int OBJECT_SIZE = 16;	//ドット数
        	for (int i = 0; i < mParticle.length; i++) {
        		Vector src = mParticle[i].t;
            	for (int j = 0; j < mParticle.length; j++) {
            		if (i == j) continue;
            		Vector dst = mParticle[j].t;
            		int x = Math.abs((int)src.x - (int)dst.x);
            		int y = Math.abs((int)src.y - (int)dst.y);
            		if (x <= (OBJECT_SIZE * 2) && y <= (OBJECT_SIZE * 2)) {
            			//衝突
                		int srcBeforeTransX = (int)src.x - (int)mParticle[i].bt.x;
                		int srcBeforeTransY = (int)src.y - (int)mParticle[i].bt.y;
                		int dstBeforeTransX = (int)dst.x - (int)mParticle[j].bt.x;
                		int dstBeforeTransY = (int)dst.y - (int)mParticle[j].bt.y;

                		float transX = Math.abs(srcBeforeTransX) + Math.abs(dstBeforeTransX);
                		x = (int)((OBJECT_SIZE * 2) * ((float)srcBeforeTransX / transX));
                		if (Math.abs(x) > Math.abs(srcBeforeTransX)) {
                			src.x = mParticle[i].bt.x;
                		} else
                		{
                			src.x += x;
                		}
                		x = (int)((OBJECT_SIZE * 2) * ((float)dstBeforeTransX / transX));
                		if (Math.abs(x) > Math.abs(dstBeforeTransX)) {
                			dst.x = mParticle[j].bt.x;
                		} else
                		{
                			dst.x += x;
                		}

                		float transY = Math.abs(srcBeforeTransY) + Math.abs(dstBeforeTransY);
                		y = (int)((OBJECT_SIZE * 2) * ((float)srcBeforeTransY / transY));
                		if (Math.abs(y) > Math.abs(srcBeforeTransY)) {
                			src.y = mParticle[i].bt.y;
                		} else
                		{
                			src.y += y;
                		}
                		y = (int)((OBJECT_SIZE * 2) * ((float)dstBeforeTransY / transY));
                		if (Math.abs(y) > Math.abs(dstBeforeTransY)) {
                			dst.y = mParticle[j].bt.y;
                		} else
                		{
                			dst.y += y;
                		}
            		}
            	}
        	}
        	for (int i = 0; i < mParticle.length; i++) {
        		//リミットチェック
        		if (mParticle[i].t.x < 60) mParticle[i].t.x  = 60;
        		if (mParticle[i].t.y < 60) mParticle[i].t.y  = 60;
        		if (mParticle[i].t.x > (mScreenWidth - 60)) mParticle[i].t.x  = mScreenWidth - 60;
        		if (mParticle[i].t.y > (mScreenHeight - 60)) mParticle[i].t.y  = mScreenHeight - 60;
        	}

        	//前の座標を保存
        	for (int i = 0; i < mParticle.length; i++) {
        		mParticle[i].bt.x = mParticle[i].t.x;
        		mParticle[i].bt.y = mParticle[i].t.y;
        	}

    	}

    	if (mParticle != null) {
        	long time = SystemClock.uptimeMillis() % 4000L;
        	float angle = 0.090f * ((int) time);
        	//描画するものを記述
        	for (int i = 0; i < mParticle.length; i++) {
        		Vector v = RendererUtility.calcScreen2ObjectCoord(mGL, mScreenHeight, (int)mParticle[i].t.x, (int)mParticle[i].t.y);
        		v.z =  0.0f;
        		mSquare[i].mTextureID = mParticle[i].mTextureID;
        		mSquare[i].setTransRotScaleMatrix(v,mParticle[i].r,mParticle[i].s);
//        		Log.d("FreeFallRenderer","x = " + v.x + " y = " + v.y + " z = " + v.z);
        		addOrderingTable(mSquare[i]);
        	}
    	}
    	addOrderingTable(mBG);

/*
        float x = 0;
        float y = 0;
        if (mEvent != null) {
        	x = mEvent.getX();
        	y = mEvent.getY();
            Vector vector = RendererUtility.calcScreen2ObjectCoord(mGL, mScreenHeight, (int)x, (int)y);
            mParticle[0].t = vector;
            mParticle[0].t.z = 0.1f;
//    	    Log.d("FreeFallRenderer","x = " + vector.x + " y = " + vector.y + " z = " + vector.z);
    	    if (mOrderingTable != null) {
    	    	//何か適当に動かしてみる
    	    	Shape shape = mOrderingTable.get(0);
    	    	vector.z = 0f;
    	    	if (shape != null) shape.setTransMatrix(vector);
    	    }
    	    mEvent = null;
        }
*/
    	return true;
    }
}
