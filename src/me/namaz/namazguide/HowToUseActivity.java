package me.namaz.namazguide;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HowToUseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_how_to_use);
		
		WebView wv = (WebView) findViewById(R.id.webView);
		wv.loadUrl("file:///android_res/raw/howto.html");
	}
}
