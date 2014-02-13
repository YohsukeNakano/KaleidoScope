package jp.co.yoh.primitive;

public class Matrix extends android.opengl.Matrix {

	public float[] m = new float[16];

	public Matrix()
	{
		setIdentityM(this.m, 0);
	}

	public Matrix(Matrix matrix)
	{
		for (int i = 0; i < m.length; i++) m[i] = matrix.m[i];
	}

	public Matrix(float matrix[])
	{
		for (int i = 0; i < m.length; i++) m[i] = matrix[i];
	}

	public void setIdentity()
	{
		setIdentityM(this.m, 0);
	}

	public void rotate(Vector r)
	{
		rotate(r.x,r.y,r.z);
	}

	public void rotate(float rx, float ry, float rz)
	{
        rotateM(this.m,0,rz,0,0,1);
        rotateM(this.m,0,ry,0,1,0);
        rotateM(this.m,0,rx,1,0,0);
	}

	public void scale(float x, float y, float z)
	{
		scaleM(this.m, 0, x, y, z);
	}

	public void transpose(float mTrans[], int mTransOffset, float m[], int mOffset)
    {
    	transpose(this.m,mTransOffset,m,mOffset);
    }

    public void trans(float x, float y, float z)
    {
/*
    	Matrix m1 = new Matrix();

        m1.m[3 * 4 + 0] = x;
        m1.m[3 * 4 + 1] = y;
        m1.m[3 * 4 + 2] = z;

        multiplyMM(this.m, 0, m1.m, 0, this.m, 0);
*/
    	translateM(this.m, 0, x, y, z);
        return;
    }

    public String toString()
    {
    	String result = "";
    	result  = m[0] + " , " + m[1] + " , "  + m[2] + " , "  + m[3] + " \n";
    	result += m[4] + " , " + m[5] + " , "  + m[6] + " , "  + m[7] + " \n";
    	result += m[8] + " , " + m[9] + " , "  + m[10] + " , " + m[11]+ " \n";
    	result += m[12]+ " , " + m[13]+ " , "  + m[14]+ " , "  + m[15] + "";
    	return result;
    }
}
