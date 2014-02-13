package jp.co.yoh.primitive;

public class Particle {
	public Vector t = new Vector();		// Translation ���s�ړ�
	public Vector r = new Vector();		// Rotation ��]
	public Vector cr = new Vector();	// Rotation Center ��]��
	public Vector s = new Vector();		// Scaling �g��k����
	public Vector bt = new Vector();	// Translation ���s�ړ�(�O��̍��W)
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
