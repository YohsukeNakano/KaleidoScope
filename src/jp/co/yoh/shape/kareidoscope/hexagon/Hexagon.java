package jp.co.yoh.shape.kareidoscope.hexagon;

import javax.microedition.khronos.opengles.GL10;
import jp.co.yoh.kareidoscope.renderer.KareidoScopeRenderer;
import jp.co.yoh.primitive.UV;
import jp.co.yoh.primitive.Vector;
import jp.co.yoh.shape.Shape;

public class Hexagon extends Shape {
    protected final static int NUM_VERTEX = 36;
    private static final float MAGNIFY = KareidoScopeRenderer.MAGNIFY;

    private final static Vector[] coordinate = {
		//0
		new Vector(0,			0,		0),
		new Vector(1.0f,		0,		0),
		new Vector(0.75f,  -0.5f,		0),
    	//0
		new Vector(0,			0,		0),
		new Vector(0.75f,	-0.5f,		0),
		new Vector(0.5f,	-1.0f,		0),
		//1
		new Vector(0,			0,		0),
		new Vector(0.5f,	 1.0f,		0),
		new Vector(0.75f,	 0.5f,		0),
		//1
		new Vector(0,			0,		0),
		new Vector(0.75f,	 0.5f,		0),
		new Vector(1.0f,		0,		0),
		//2
		new Vector(0,			0,		0),
		new Vector(-0.5f,	 1.0f,		0),
		new Vector(0,		 1.0f,		0),
		//2
		new Vector(0,			0,		0),
		new Vector(0,		 1.0f,		0),
		new Vector(0.5f,	 1.0f,		0),
		//3
		new Vector(0,			0,		0),
		new Vector(-1.0f,	    0,		0),
		new Vector(-0.75f,	 0.5f,		0),
		//3
		new Vector(0,			0,		0),
		new Vector(-0.75f,	 0.5f,		0),
		new Vector(-0.5f,	 1.0f,		0),
		//4
		new Vector(0,			0,		0),
		new Vector(-0.5f,	-1.0f,		0),
		new Vector(-0.75f,	 -0.5f,		0),
		//4
		new Vector(0,			0,		0),
		new Vector(-0.75f,	 -0.5f,		0),
		new Vector(-1.0f,	    0,		0),
		//5
		new Vector(0,			0,		0),
		new Vector(0.5f,	-1.0f,		0),
		new Vector(0,		-1.0f,		0),
		//5
		new Vector(0,			0,		0),
		new Vector(0,		-1.0f,		0),
		new Vector(-0.5f,	-1.0f,		0),
    };

    private final static UV[] uv = {
    		new UV(0.5f,0.5f),
    		new UV(-0.5f,-1.5f),
    		new UV(1.5f,-1.5f),
    		new UV(0.5f,0.5f),
    		new UV(1.5f,-1.5f),
    		new UV(-0.5f,-1.5f),
    };

    public Hexagon() {

    	super(NUM_VERTEX);

        for (int i = 0; i < numVertex; i++) {
            mFVertexBuffer.put(coordinate[i].x * MAGNIFY);
            mFVertexBuffer.put(coordinate[i].y * MAGNIFY);
            mFVertexBuffer.put(coordinate[i].z * MAGNIFY);
        }

        for (int i = 0; i < numVertex; i++) {
       		mTexBuffer.put(uv[(i % 6)].u);
       		mTexBuffer.put(uv[(i % 6)].v);
        }

        for(int i = 0; i < numVertex; i++) {
            mIndexBuffer.put((short) i);
        }

        mFVertexBuffer.position(0);
        mTexBuffer.position(0);
        mIndexBuffer.position(0);
    }

    public void draw(GL10 gl) {
    	if (lm != null) gl.glMultMatrixf(lm.m, 0);
    	gl.glDrawElements(GL10.GL_TRIANGLE_FAN, numVertex,
        		GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
    }
}

