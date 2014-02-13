package jp.co.yoh.shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import jp.co.yoh.primitive.Matrix;
import jp.co.yoh.primitive.Particle;
import jp.co.yoh.primitive.Vector;

public class Shape {

    protected FloatBuffer mFVertexBuffer;
    protected FloatBuffer mTexBuffer;
    protected ShortBuffer mIndexBuffer;
    protected int numVertex = 0;
    public int mTextureID = -1;
    public Matrix lm;	//ローカルマトリックス

    public Shape(int numVertex)
    {
    	this.numVertex = numVertex;
        ByteBuffer vbb = ByteBuffer.allocateDirect(this.numVertex * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();

        ByteBuffer tbb = ByteBuffer.allocateDirect(this.numVertex * 2 * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTexBuffer = tbb.asFloatBuffer();

        ByteBuffer ibb = ByteBuffer.allocateDirect(this.numVertex * 2);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();
    	return;
    }

    public void setTextureID(int textureID)
    {
    	mTextureID = textureID;
    	return;
    }

    public int getTextureID()
    {
    	return mTextureID;
    }

    public void setTransMatrix(float x,float y,float z)
    {
    	if (lm == null) lm = new Matrix();
    	lm.trans(x, y, z);
//    	Log.d("Shape",lm.toString());
    	return;
    }

    public void setTransMatrix(Vector trans)
    {
    	if (lm == null) {
    		lm = new Matrix();
    	} else
    	{
    		lm.setIdentity();
    	}
    	lm.trans(trans.x, trans.y, trans.z);
    	return;
    }

    public void setRotMatrix(Vector r)
    {
    	if (lm == null) {
    		lm = new Matrix();
    	} else
    	{
    		lm.setIdentity();
    	}
    	lm.rotate(r.x,r.y,r.z);
    	return;
    }

    public void setTransRotScaleMatrix(Particle p)
    {
    	setTransRotScaleMatrix(p.t,p.r,p.s);
    }

    public void setTransRotScaleMatrix(Vector t,Vector r,Vector s)
    {
    	setTransRotScaleMatrix(t.x, t.y, t.z,r.x,r.y,r.z,s.x, s.y, s.z);
    }

    public void setTransRotScaleMatrix(float x,float y,float z,float rx,float ry,float rz,float sx,float sy,float sz)
    {
    	if (lm == null) {
    		lm = new Matrix();
    	} else
    	{
    		lm.setIdentity();
    	}

        lm.trans(x,y,z);
        lm.rotate(rx,ry,rz);
        lm.scale(sx,sy,sz);
        return;
    }

    public void setTransRotMatrix(Particle p)
    {
    	setTransRotMatrix(p.t,p.r);
    }

    public void setTransRotMatrix(Vector t,Vector r)
    {
    	setTransRotMatrix(t.x, t.y, t.z,r.x, r.y, r.z);
    }

    public void setTransRotMatrix(float x,float y,float z,float rx,float ry,float rz)
    {
    	if (lm == null) {
    		lm = new Matrix();
    	} else
    	{
    		lm.setIdentity();
    	}
        lm.trans(x,y,z);
    	lm.rotate(rx,ry,rz);
        return;
    }


    public void draw(GL10 gl) {
    	drawBegin(gl);
    	if (mIndexBuffer != null) {
        	if (lm != null) gl.glMultMatrixf(lm.m, 0);
        	gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, numVertex,
        		GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
        }
    	drawEnd(gl);
    }

    public void drawBegin(GL10 gl) {

    	if (lm != null) gl.glPushMatrix();
    	if (mFVertexBuffer != null) {
        	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
        }
        if (mTexBuffer != null) {
        	gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
        }
    }
    public void drawEnd(GL10 gl) {
    	if (lm != null) gl.glPopMatrix();
    }
}
