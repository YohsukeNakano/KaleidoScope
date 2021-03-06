package jp.co.yoh.primitive;

public class Particle {
	public Vector t = new Vector();		// Translation 平行移動
	public Vector r = new Vector();		// Rotation 回転
	public Vector cr = new Vector();	// Rotation Center 回転軸
	public Vector s = new Vector();		// Scaling 拡大縮小率
	public Vector bt = new Vector();	// Translation 平行移動(前回の座標)
	public int mTextureID = 0;
	public int timer = 0;
	public int mAction = FL_ACTION_NORMAL;
	public int mActionTimer = 0;
	public static final int FL_ACTION_NORMAL = 0;
	public static final int FL_ACTION_JUMP_DOWN = 1;
	public static final int FL_ACTION_JUMP_UP = 2;
	public static final int FL_ACTION_LANDING = 3;
	public Particle()
	{
		s.x = s.y = s.y = s.z = 1.0f;
	}
}
