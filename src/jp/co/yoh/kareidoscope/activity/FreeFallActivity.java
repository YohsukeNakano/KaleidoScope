package jp.co.yoh.kareidoscope.activity;

import jp.co.yoh.kareidoscope.renderer.FreeFallRenderer;
import jp.co.yoh.primitive.Vector;
import jp.co.yoh.renderer.Renderer;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

public class FreeFallActivity extends Activity implements SensorEventListener {
	private Renderer mRenderer = new FreeFallRenderer(this);
    protected GLSurfaceView mGLView = null;
    protected SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mGLView = new GLSurfaceView(this);
        mRenderer.setEGLConfigChooser(mGLView);
        setContentView(mGLView);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }

    public boolean onTouchEvent(MotionEvent event) {
    	if (mRenderer != null) mRenderer.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

	public void onResume(){
		super.onResume();
		mSensorManager.registerListener(
			this,
			mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
			SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(
			this,
			mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
			SensorManager.SENSOR_DELAY_GAME);
	}

	public void onPause(){
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	private float[] mRotationMatrix = new float[9];
	private float[] mGravity = new float[3];
	private float[] mGeomagnetic = new float[3];
	private float[] mAttitude = new float[3];
	private Vector mVectorAttitude = new Vector();
	@Override
	public void onSensorChanged(SensorEvent event) {

		switch(event.sensor.getType()){
		case Sensor.TYPE_MAGNETIC_FIELD:
			mGeomagnetic = event.values.clone();
			break;
		case Sensor.TYPE_ACCELEROMETER:
			mGravity = event.values.clone();
			break;
		}

		if(mGeomagnetic != null && mGravity != null){

			SensorManager.getRotationMatrix(
				mRotationMatrix, null,
				mGravity, mGeomagnetic);

			SensorManager.getOrientation(
				mRotationMatrix,
				mAttitude);

			mVectorAttitude.x = mAttitude[0];	//Yaw
			mVectorAttitude.y = mAttitude[1];	//Pitch
			mVectorAttitude.z = mAttitude[2];	//Roll
			if (mRenderer != null) mRenderer.onSensorChanged(mVectorAttitude);

		}

	}

}
