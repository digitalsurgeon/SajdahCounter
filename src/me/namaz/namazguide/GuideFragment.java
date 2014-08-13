package me.namaz.namazguide;

import me.namaz.namazguide.SajdahCalculator.SajdahListener;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView.FindListener;
import android.widget.TextView;

public class GuideFragment extends Fragment implements SajdahListener, OnClickListener {
	
	private static final float kBrightnessDimValue = 0.01f;
	private static final float kBrightnessNormalValue = -1.0f;
	
	public static String kUseFullScreePrefKey = "PREF_USE_FULL_SCREEN";
	public static String kVibOnSajdahPrefKey = "PREF_VIB_SAJDAH";
	public static String kChangeBrightnessOnSajdahPrefKey = "PREF_BRIGHTNESS_SAJDAH";
	public static String kFirstTimeUsePrefKey = "PREF_FIRST_TIME_USE";
	
	private SajdahCalculator mSajdahCalculator;
	private TextView mTextViewCounter;
	private Vibrator mVibrator;
	
	private SharedPreferences mSharedPreferences;
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			setBrightness(kBrightnessDimValue);
		}
	};
	
	private static Handler mHandler = new Handler();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_main, container,false);
		mTextViewCounter = (TextView) rootView.findViewById(R.id.textViewCount);
		mTextViewCounter.setOnClickListener(this);

		mSajdahCalculator = new SajdahCalculator(getActivity(), this);
		
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

		
		setFullScreen(useFullScreen());
		
		if (getFirstTimeUse()) {
			rootView.findViewById(R.id.buttonWatchTutorial).setVisibility(View.VISIBLE);
		}
		
		return rootView;
	}
	
	public void setFullScreen(boolean f) {
		if (f) {
			getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		else {
//			getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, 
//					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
	}
	
	@Override
	public void onPause() {
	  super.onPause();
	  if (mSajdahCalculator.started()) {
		  mSajdahCalculator.pause();
		  mHandler.removeCallbacks(mRunnable);
		  MainActivity.wakeLock().release();
	  }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mSajdahCalculator.started()) {
			mSajdahCalculator.resume();
			MainActivity.wakeLock().acquire();
		}
	}
	
	private boolean useFullScreen() {
		return mSharedPreferences.getBoolean(kUseFullScreePrefKey, true);
	}
	
	private boolean useVibrator() {
		return mSharedPreferences.getBoolean(kVibOnSajdahPrefKey, true);
	}
	
	private boolean changeBrightness() {
		return mSharedPreferences.getBoolean(kChangeBrightnessOnSajdahPrefKey, true);
	}
	
	
	@Override
	public void sajdahDetected() {
		
		if (useVibrator()){
			mVibrator.vibrate(100);
		}

		if (changeBrightness()) {
			setBrightness(kBrightnessNormalValue);
			mHandler.removeCallbacks(mRunnable);
			mHandler.postDelayed(mRunnable, 1000*3);
		}
		
		
		updateSajdahTextView();
	}

	@Override
	public void onClick(View v) {
		mSajdahCalculator.toggle();
		if (mSajdahCalculator.started()){
			mTextViewCounter.setTextColor( getResources().getColor(R.color.ForegroundColor) );
			setBrightness(kBrightnessDimValue);
			MainActivity.wakeLock().acquire();
		} else {
			MainActivity.wakeLock().release();
			mHandler.removeCallbacks(mRunnable);
			mTextViewCounter.setTextColor( getResources().getColor(R.color.TextViewCounter));
			setBrightness(kBrightnessNormalValue);
		}
		updateSajdahTextView();
		
	}
	
	private void updateSajdahTextView() {
		mTextViewCounter.setText(String.valueOf(mSajdahCalculator.getSajdahCount()));
	}
	
	private void setBrightness(float brightness) {
		if (changeBrightness()) {
			WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
			lp.screenBrightness = brightness;
			getActivity().getWindow().setAttributes(lp);
		}
	}
	
	private boolean getFirstTimeUse() {
		return mSharedPreferences.getBoolean(kFirstTimeUsePrefKey, true);
	}
}
