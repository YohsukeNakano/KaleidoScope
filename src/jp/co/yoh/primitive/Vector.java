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
	//�R���X�g���N�^
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

    //�l�̎w��
    public void set(Vector origin) {
        x=origin.x;
        y=origin.y;
        z=origin.z;
    }

    //�l�̎w��
    public void set(float x,float y,float z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }

    //���ς̌v�Z
    public float dot(Vector v) {
        return (x*v.x)+(y*v.y)+(z*v.z);
    }

    //���ς̌v�Z
    public float dot(float x,float y,float z) {
        return (this.x*x)+(this.y*y)+(this.z*z);
    }

    //�O�ς̌v�Z
    public Vector cross(Vector v,Vector result) {
        result.set((y*v.z)-(z*v.y),(z*v.x)-(x*v.z),(x*v.y)-(y*v.x));
        return result;
    }

    //�O�ς̌v�Z
    public void cross(float x,float y,float z) {
        set((this.y*z)-(this.z*y),(this.z*x)-(this.x*z),(this.x*y)-(this.y*x));
    }

    //�a�̌v�Z
    public void add(Vector v0,Vector v1) {
        x=v0.x+v1.x;
        y=v0.y+v1.y;
        z=v0.z+v1.z;
    }

    //���̌v�Z
    public void sub(Vector v0,Vector v1) {
        x=v0.x-v1.x;
        y=v0.y-v1.y;
        z=v0.z-v1.z;
    }

    //�x�N�g���̒����̎擾
    public float length() {
        return (float)Math.sqrt((double)((x*x)+(y*y)+(z*z)));
    }

    //�x�N�g���̒����̐��K��
    public void normalize() {
        final float len=length();
        x/=len;
        y/=len;
        z/=len;
    }

    //�l�̔�r
    @Override
    public boolean equals(Object o) {
        Vector v=(Vector)o;
        return v.x==x && v.y==y && v.z==z;
    }
    public String toString() {
    	return "x = " + this.x + " y = " + this.y + " z = " + this.z;
    }

    //3�������W��ł�2�_�Ԃ̋���
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

    //2�������W��ł�2�_�Ŏ����������̊p�x(radian)
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
