package me.namaz.namazguide;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


public class SajdahCalculator implements SensorEventListener{
	private SensorManager mSensorManager;
	private Sensor mProximitySensor, mLightSensor;
	private String SENSORS_TAG = "NamazGuide:Sensors";
	private boolean mStarted;
	private SajdahListener mListener;
	
	public int mSajdahCount;
	
	public interface SajdahListener {
		public void sajdahDetected();
	}
	
	public SajdahCalculator(Context context, SajdahListener listener) {
		mListener = listener;
		
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
			Log.d(SENSORS_TAG, "There is a proxmity sensor");
			
			List<Sensor> sensorsList = mSensorManager.getSensorList(Sensor.TYPE_PROXIMITY);
			for (Sensor sensor : sensorsList) {
				Log.d(SENSORS_TAG, "Sensor name:" + sensor.getName());
				mProximitySensor = sensor;
			}
		} else {
			Log.d(SENSORS_TAG, "There is no proximity sensor");
		}
		
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!= null) {
			Log.d(SENSORS_TAG, "There is a light sensor");
			
			List<Sensor> sensorsList = mSensorManager.getSensorList(Sensor.TYPE_LIGHT);
			for(Sensor sensor: sensorsList){
				Log.d(SENSORS_TAG, "Sensor name:" + sensor.getName());
				mLightSensor = sensor;
			}
		} else {
			Log.d(SENSORS_TAG, "There is no light sensor");
		}
		
	}
	
	public void start() {
		mSajdahCount = 0;
		resume();
	}
	
	public void resume() {
		mStarted = true;
		mSensorManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_UI);
	}
	
	public void pause() {
		mSensorManager.unregisterListener(this);
	}
	
	public void stop() {
		mStarted = false;
		pause();
	}
	
	public boolean started() {
		return mStarted;
	}
	
	public void toggle() {
		if (mStarted) 
			stop();  
		else 
			start();
	}
	
	boolean mLightTest = false;
	long mLastDetectionInterval;
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.equals(mProximitySensor)) {
			float value = event.values[0];
			Log.i(SENSORS_TAG, "Proximity Changed:" + value);
			if (value > 5.0f){
				sajdahDetected();
			}
		}
		else if (event.sensor.equals(mLightSensor)) {
			
			if (event.values[0] < 5 && mLightTest != true) {
				mLightTest = true;
			} else if (event.values[0]>5 && mLightTest!= false) {
				mLightTest = false;
				sajdahDetected();
			}
			
			Log.i(SENSORS_TAG, "Light changed:" + event.values[0]);
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
		if (sensor.equals(mProximitySensor))
			Log.i(SENSORS_TAG, "Proximity accuracy:" + accuracy);
		else if (sensor.equals(mLightSensor))
			Log.i(SENSORS_TAG, "Light accuracy:" + accuracy);
	}
	
	private void sajdahDetected() {
		
		if ((System.currentTimeMillis() - mLastDetectionInterval) > 1000) {
			mLastDetectionInterval = System.currentTimeMillis();
			++mSajdahCount;
			if (mListener != null) {
				mListener.sajdahDetected();
			}
		}
	}

	public int getSajdahCount() {
		return mSajdahCount;
	}

}
