package jp.co.yoh.shape;

import javax.microedition.khronos.opengles.GL10;

import jp.co.yoh.primitive.UV;
import jp.co.yoh.primitive.Vector;
import jp.co.yoh.shape.Shape;

public class Square extends Shape {
    protected final static int NUM_VERTEX = 5;

    private Vector[] coordinate = {
    	new Vector( 1.0f,	 1.0f,		0),
		new Vector( 1.0f,	-1.0f,		0),
    	new Vector(-1.0f,	 1.0f,		0),
		new Vector(-1.0f,	-1.0f,		0),
    };

    private final static UV[] uv = {
    	new UV(0.0f,0.0f),
		new UV(0.0f,1.0f),
    	new UV(1.0f,0.0f),
    	new UV(1.0f,1.0f),
    };

    private final static int[] vertexIndeces = {
    	0,
    	1,
    	2,
    	3,
    	1
    };

    public void setVertexCoordinate(int vertexIndex,Vector v) {
    	if (this.coordinate.length <= vertexIndex) return;
    	this.coordinate[vertexIndex] = v;
        mFVertexBuffer.position(vertexIndex * 3);
        mFVertexBuffer.put(coordinate[vertexIndex].x);
        mFVertexBuffer.put(coordinate[vertexIndex].y);
        mFVertexBuffer.put(coordinate[vertexIndex].z);
        mFVertexBuffer.position(0);
    }

    public Square() {

    	super(NUM_VERTEX);

        for (int i = 0; i < coordinate.length; i++) {
            mFVertexBuffer.put(coordinate[i].x);
            mFVertexBuffer.put(coordinate[i].y);
            mFVertexBuffer.put(coordinate[i].z);
        }

        for (int i = 0; i < uv.length; i++) {
       		mTexBuffer.put(uv[(i % 6)].u);
       		mTexBuffer.put(uv[(i % 6)].v);
        }

        for(int i = 0; i < vertexIndeces.length; i++) {
            mIndexBuffer.put((short) vertexIndeces[i]);
        }

        mFVertexBuffer.position(0);
        mTexBuffer.position(0);
        mIndexBuffer.position(0);
    }

    public void draw(GL10 gl) {
    	if (lm != null) gl.glMultMatrixf(lm.m, 0);
    	gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, numVertex,
        		GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
    }
}

