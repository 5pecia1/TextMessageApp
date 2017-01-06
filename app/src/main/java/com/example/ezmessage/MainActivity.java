package com.example.ezmessage;

import com.example.contents.MessageContent;
import com.example.ezmessage.menu.MainSettingActivity;
import com.example.new_message.NewMessageActivity;
import com.example.talk_list.TalkActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

//TODO 모든 Activity -> AppCompatAcitity
public class MainActivity extends Activity {

	public static final String KEY_THREAD_ID = "thread_id";

	public static final String ACTIVITY_NAME = "MainActivity";

	private static boolean isRun = false;

	private MainListAdapter mainAdapter;
	private ListView mainListView;
	private EditText searchEditText;

	/**
	 * Activity가 처음 만들어 졌을 때 호출됨
	 * 각 View의 객체를 변수에 저장한다
	 * mainListView에 OnItemClickListener를 붙여서 각 Item을 클릭 할 수 있게 한다
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		searchEditText = (EditText) findViewById(R.id.searchMessageEditText);
		mainListView = (ListView) findViewById(R.id.mainListView);
		
		mainAdapter = new MainListAdapter(this);
		mainListView.setAdapter(mainAdapter);

		mainListView.setTextFilterEnabled(true);
		mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/**
			 * 클릭된 item의 thread_id 값을 받아서 TalkActivity를 실행하면서 넘겨준다 
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int thread_id = ((MessageContent)mainAdapter.getItem(position)).getThread_id();
				Intent intent = new Intent(MainActivity.this,TalkActivity.class);

				intent.putExtra(KEY_THREAD_ID, thread_id);

				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});

		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().equals("")){
					mainAdapter.setItem();
				}
				else{
					mainAdapter.getFilter().filter(s);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		searchEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CharSequence s = ((EditText)v).getText();

				if(s.toString().equals("")){
					mainAdapter.setItem();
				}
				else{
					mainAdapter.getFilter().filter(s);
				}
			}
		});

	}
	
	//TODO api 8 에서는 안됨 Telephony.Sms 문제 -> gsm을 사용하면 어떨까?
	/**
	 * Activity가 사용자와 상호작용하기 바로 전에 호출 됨
	 * mainAdapter를 최신값으로 만든다
	 * 기본 문자앱이 아니면 기본 문자앱 설정 여부를 묻는다
	 * isRun을 true로 만든다
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		isRun = true;
		mainAdapter.setItem();

		//기본 문자앱 설정
		final String myPackageName = getPackageName();
		
		if ( !Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
			// App is not default.
			// Show the "not currently set as the default SMS app" interface
			LinearLayout layout = (LinearLayout) findViewById(R.id.not_default_app);
			layout.setVisibility(View.VISIBLE);
			//            View viewGroup = findViewById(R.id.not_default_app);
			//            viewGroup.setVisibility(View.VISIBLE);

			// Set up a button that allows the user to change the default SMS app
			Button button = (Button) findViewById(R.id.change_default_app);
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent intent =
							new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
					intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, 
							myPackageName);
					startActivity(intent);
				}
			});
		} else {
			// App is the default.
			// Hide the "not currently set as the default SMS app" interface
			LinearLayout layout = (LinearLayout) findViewById(R.id.not_default_app);
			layout.setVisibility(View.GONE);
			//            View viewGroup = findViewById(R.id.not_default_app);
			//            viewGroup.setVisibility(View.GONE);
		}

	}

	/**
	 * 또 다른 Activity를 시작하려고 할 때 호출 됨
	 * isRun을 false로 만든다
	 */
	@Override
	protected void onPause() {
		super.onPause();
		isRun = false;
	}

	/**
	 * 새로운 intent가 넘어올때 호출 됨
	 * mainAdapter를 최신값으로 만든다
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mainAdapter.setItem();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 현재 액티비티의 실행여부를 알려줌
	 * @author SOL
	 *
	 * @return 
	 * 			실행여부
	 */
	public static boolean isOnResume(){
		return isRun;
	}

	public void onWriteButtonClicked(View v){
		Intent intent = new Intent(this, NewMessageActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	public void onSearchButtonClicked(View v){
		
	}
	
	public void onOptionMenuButtonClicked(View v){
		Intent intent = new Intent(this,MainSettingActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
}
