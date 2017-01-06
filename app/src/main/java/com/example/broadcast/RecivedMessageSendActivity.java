package com.example.broadcast;

import com.example.ezmessage.MainActivity;
import com.example.talk_list.TalkActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * SMSBroadCast에서 넘어온 값을 처리하는 Activity
 * 넘어가야할 Activity로 값들을 넘겨준다 
 * 
 * @author SOL
 *
 */
public class RecivedMessageSendActivity extends Activity{

	public static final String ACTIVTY_NAME = "RecivedMessageSendActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String nextActivity = intent.getStringExtra(ACTIVTY_NAME);

		if(nextActivity.equals(MainActivity.ACTIVITY_NAME))
			intent.setClass(RecivedMessageSendActivity.this, MainActivity.class);
		else if(nextActivity.equals(TalkActivity.ACTIVITY_NAME))
			intent.setClass(RecivedMessageSendActivity.this, TalkActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(intent);
		finish();
	}
}
