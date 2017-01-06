package com.example.ezmessage.menu.setting;

import com.example.ezmessage.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


/**
 * TODO 200자 제한 만들기
 * TODO 바로 키보드 창 올라오게 만들기
 * 
 * QuickReplyPhrases 추가, 수정 Activity.
 * 받은 Intent의 QuickReplyPhrasesActivity.COL_PHRASE의 값에 따라 역할이 달라진다
 * 값이 null이면 추가, String 값으로 들어 왔으면 수정이다. 
 * 
 * @author SOL
 *
 */
@SuppressLint("NewApi")
public class QuickReplyPhrasesInsertActivity extends AppCompatActivity{
	public final static String RESULT_KEY = "text";
	public final static String RESULT_ID = "id";
	
	
	private EditText mainTextView = null;;
	private int id = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_main_layout);
		
		mainTextView = (EditText)findViewById(R.id.mainTextView);
		
		ActionBar ab = getSupportActionBar();
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.cancel_save_action_bar_button, null);
		ab.setCustomView(view);
		ab.setDisplayShowCustomEnabled(true);
		
		String text = getIntent().getStringExtra(QuickReplyPhrasesActivity.COL_PHRASE);
		if( text  != null){
			mainTextView.setText(text);
			id =(int) getIntent().getLongExtra(QuickReplyPhrasesActivity.COL_ID, -1);
		}
		
	}
	
	public void onCancelButtonClicked(View v){
		finish();
	}
	
	/**
	 * 입력된 값을 intent에 넣어서 호출한 Activity로 보내준다
	 * 보냈거나, 값이 없으면 Toast를 띄운다
	 * @author SOL
	 *
	 * @param v
	 */
	public void onSaveButtonClicked(View v){
		String text = mainTextView.getText().toString();
		
		if(text.length() <= 0){
			Toast.makeText(this, "입력된 텍스트가 없어서 상용구를 저장할 수 없습니다", Toast.LENGTH_LONG).show();
			finish();
		}
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra(RESULT_KEY, text);
		
		if(id != -1){
			resultIntent.putExtra(RESULT_ID, id);
			Toast.makeText(this, "저장하였습니다.", Toast.LENGTH_LONG).show();
		}
		
		setResult(RESULT_OK, resultIntent);
		finish();
	}
}
