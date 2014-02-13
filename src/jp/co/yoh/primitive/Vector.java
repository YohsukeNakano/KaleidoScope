package jp.co.yoh.primitive;

public class Vector {
	public float x,y,z,w;
	public Vector() {return;}
	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return;
	}
	public Vector unit() {
		this.x = this.y = this.z = this.w = 1.0f;
		return this;
	}
	public Vector init() {
		this.x = this.y = this.z = this.w = 0.0f;
		return this;
	}
	//コンストラクタ
    public Vector(Vector origin) {
        set(origin);
    }

    public Vector(float same) {
		this.x = this.y = this.z = this.w = same;
    }

    public void copy(Vector dst) {
        dst.x = x;
        dst.y = y;
        dst.z = z;
    }

    //値の指定
    public void set(Vector origin) {
        x=origin.x;
        y=origin.y;
        z=origin.z;
    }

    //値の指定
    public void set(float x,float y,float z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }

    //内積の計算
    public float dot(Vector v) {
        return (x*v.x)+(y*v.y)+(z*v.z);
    }

    //内積の計算
    public float dot(float x,float y,float z) {
        return (this.x*x)+(this.y*y)+(this.z*z);
    }

    //外積の計算
    public Vector cross(Vector v,Vector result) {
        result.set((y*v.z)-(z*v.y),(z*v.x)-(x*v.z),(x*v.y)-(y*v.x));
        return result;
    }

    //外積の計算
    public void cross(float x,float y,float z) {
        set((this.y*z)-(this.z*y),(this.z*x)-(this.x*z),(this.x*y)-(this.y*x));
    }

    //和の計算
    public void add(Vector v0,Vector v1) {
        x=v0.x+v1.x;
        y=v0.y+v1.y;
        z=v0.z+v1.z;
    }

    //差の計算
    public void sub(Vector v0,Vector v1) {
        x=v0.x-v1.x;
        y=v0.y-v1.y;
        z=v0.z-v1.z;
    }

    //ベクトルの長さの取得
    public float length() {
        return (float)Math.sqrt((double)((x*x)+(y*y)+(z*z)));
    }

    //ベクトルの長さの正規化
    public void normalize() {
        final float len=length();
        x/=len;
        y/=len;
        z/=len;
    }

    //値の比較
    @Override
    public boolean equals(Object o) {
        Vector v=(Vector)o;
        return v.x==x && v.y==y && v.z==z;
    }
    public String toString() {
    	return "x = " + this.x + " y = " + this.y + " z = " + this.z;
    }

    //3次元座標上での2点間の距離
    public static float calcRange(Vector src,Vector dst)
    {
    	float tx,ty,tz;
    	float range;

    	tx = src.x - dst.x;
    	ty = src.y - dst.y;
    	tz = src.z - dst.z;
    	range = (float)Math.sqrt((tx * tx) + (ty * ty) + (tz * tz));

    	return range;
    }

    //2次元座標上での2点で示される線分の角度(radian)
    public static float calcLineDirection(Vector src,Vector dst)
    {
		float direction = (float)Math.atan2(src.x - dst.x,src.z - dst.z);
    	return direction;
    }

    public static Vector moveRange(Vector v,float radian, float range)
    {
    	v.x += (float)(Math.sin(radian) * range);
    	v.z += (float)(Math.cos(radian) * range);
    	return v;
    }
}
