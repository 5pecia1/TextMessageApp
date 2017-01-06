package com.example.broadcast;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

/**
 * 메시지를 전달하길 원하는 다른 앱으로 부터 인텐트를 받는다
 * 
 * @author SOL
 */
public class ComposeSMSActivity extends Activity{
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("broadcast/ComposeSMSActiviy.onResume", "start");
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i("broadcast/ComposeSMSActiviy.onNewIntent", "start");
	}
}
