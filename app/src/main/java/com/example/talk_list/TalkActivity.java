package com.example.talk_list;

import com.example.contents.MessageContent;
import com.example.ezmessage.MainActivity;
import com.example.ezmessage.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 대화 Activity
 * 
 * @author SOL
 *
 */
public class TalkActivity extends Activity {

	public static final String ACTIVITY_NAME = "TalkActivity";

	private static boolean isRun = false;

	private RelativeLayout informationLayout = null;
	private RelativeLayout deleteCheckLayout = null;
	private FrameLayout talkListBottom = null;

	private ListView talkListView = null;
	private TalkListAdapter talkListAdapter = null;

	private TextView printPhoneNumberAtHeader;
	private EditText sendTextMessageEditText;
	private Button callMessageSender;

	private CheckBox allCheckBox;
	private LinearLayout allCheckBoxTexViewstLayout;
	private TextView checkItemCountTextView;

	private int checkCount = 0;
	private boolean isSetListLastItemBottom = false; 

	/**
	 * Activity가 처음 만들어 졌을 때 호출됨
	 * 각 View의 객체를 변수에 저장한다
	 * talkListView에 현재 리스트가 바닥인지의 확인한다
	 * allCheckBoxTexViewstLayout에 text를 눌러도 CheckBox가 반응하게 한다
	 * 현재 대화상대가 여럿일 경우 전화버튼을 없앤다
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk_list);
		int thread_id = getIntent().getIntExtra(MainActivity.KEY_THREAD_ID, -1);

		talkListAdapter = new TalkListAdapter(this, thread_id);
		talkListView = (ListView) findViewById(R.id.talkListView);

		printPhoneNumberAtHeader = (TextView) findViewById(R.id.callingNumber);
		sendTextMessageEditText = (EditText) findViewById(R.id.sendText);

		printPhoneNumberAtHeader.setText(talkListAdapter.getHeader());

		talkListView.setAdapter(talkListAdapter);

		informationLayout = (RelativeLayout) findViewById(R.id.informationLayout);
		deleteCheckLayout = (RelativeLayout) findViewById(R.id.deleteCheckLayout);
		talkListBottom = (FrameLayout) findViewById(R.id.talkListBottom);

		checkItemCountTextView = (TextView) findViewById(R.id.checkItemCountTextView);

		allCheckBox = (CheckBox) findViewById(R.id.allCheckBox);
		callMessageSender = (Button) findViewById(R.id.callMessageSender);
		allCheckBoxTexViewstLayout = (LinearLayout) findViewById(R.id.allCheckBoxTextViewsLayout);

		//리스트의 바닥을 확인하는 리스너
		talkListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				isSetListLastItemBottom = (totalItemCount > 0 ) && (firstVisibleItem + visibleItemCount >= totalItemCount);
			}
		});

		//text를 눌러도 checkbox가 반응한다
		allCheckBoxTexViewstLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				allCheckBox.toggle();
				TalkActivity.this.onAllCheckBoxClicked(v);
			}
		});

		//현재 대화상대가 여럿일 경우 전화버튼을 없앤다
		if(talkListAdapter.getHeader().split(",").length > 1){
			callMessageSender.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		isRun = true;
		talkListAdapter.setItem();
	}

	@Override
	protected void onStop() {
		super.onStop();
		isRun = false;
	}

	/**
	 * 새로운 intent를 받고 현재 talkListAdapter가 
	 * deleteMode가 아니면 talkListAdapter를 최신값으로 만든다
	 * 현재 대화 상대에게 문자 메시지를 받으면 Toast를 띄워서 알려준다
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if( !talkListAdapter.isDeleteMode())
			talkListAdapter.setItem();

		String sender = intent.getStringExtra("sender");

		if(talkListAdapter.getHeader().equals(sender))
			Toast.makeText(this, "↓↓↓새로운 메시지가 전달됨↓↓↓", Toast.LENGTH_LONG).show();
	}

	/**
	 * deleteMode일때 back 버튼을누르면 normalMode로 돌아간다
	 * deleteMode가 아닐때 back 버튼을 누르면 TalkActivity를 끝낸다
	 * 작성중인 문자메시지가 있을때 임시 저장메시지로 저장한다
	 */
	@Override
	public void onBackPressed() {
		if(talkListAdapter.isDeleteMode())

			returnNormaldMode();
		else{
			if(sendTextMessageEditText.getText().toString().length() > 0){
				saveTemporaryStorageMessage();
			}
			super.onBackPressed();
		}

	}

	/**
	 * 
	 * @author SOL
	 *
	 * @param v
	 * 뒤로가기 버튼을 누르면 TalkActivity를 끝낸다 
	 */
	public void onBackButtonClicked(View v){
		onBackPressed();
		//		finish();
	}

	/**
	 * 
	 * @author SOL
	 *
	 * @param v
	 * 현재 대화상대에게 전화를 걸 수 있다
	 */
	public void onCallButtonClicked(View v){
		Intent callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + talkListAdapter.getHeader()));
		startActivity(callIntent);
	}

	public void onDeleteButtonClicked(View v){
		informationLayout.setVisibility(View.GONE);
		deleteCheckLayout.setVisibility(View.VISIBLE);
		talkListBottom.setVisibility(View.GONE);

		//현재의 포커스에서 추가된 만큼의 포커스가 이동
		talkListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

		talkListAdapter.setDeleteMode();
	}
	
	/**
	 * 임시 메시지 저장 버튼
	 * 
	 * @author SOL
	 *
	 * @param v
	 */
	public void onSaveTemporaryStorageMessageButtonClicked(View v){
		if(sendTextMessageEditText.getText().toString().length() > 0){
			saveTemporaryStorageMessage();
			
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(sendTextMessageEditText.getWindowToken(), 0);
			
			sendTextMessageEditText.setText("");
			talkListAdapter.setItem();
			talkListView.setSelection(talkListAdapter.getCount()-1);
		}else{
			Toast.makeText(this, "저장할 내용이 없습니다", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 버리기 후 완료 버튼
	 * 선택된 sms를 지운다
	 * @author SOL
	 *
	 * @param v
	 */
	public void onCompletionButtonClicked(View v){
		talkListAdapter.deleteSMS();

		returnNormaldMode();
	}

	public void onAllCheckBoxClicked(View v){
		if(allCheckBox.isChecked()){
			talkListAdapter.setAllChecked();
		}
		else{
			talkListAdapter.setAllNotChecked();
		}
	}	

	//TODO 2개 이상의 번호에 대해 thread_id를 같게하여 보내기 (현재는 따로따로 보냄)
	public void onSendButtonClicked(View v){
		String messageNum = talkListAdapter.getHeader();

		String smsText = sendTextMessageEditText.getText().toString();

		if(messageNum.length() > 0 && smsText.length() > 0){
			sendSMS(messageNum, smsText);
		}else{
			Toast.makeText(this, "문자를 입력해 주세요", Toast.LENGTH_SHORT).show();
		}
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(sendTextMessageEditText.getWindowToken(), 0);
	}

	public void addtCheckItemCountTextView(){
		checkCount++;
		setCheckItemCountTextView();
	}

	public void subtractCheckItemCountTextView(){
		checkCount--;
		setCheckItemCountTextView();
	}

	private void setCheckItemCountTextView(){
		checkItemCountTextView.setText(Integer.toString(checkCount));
	}

	/**
	 * deleteMode가 아니고 클릭한 대화창이 임시저장메시지 일때 sendTextMessageEditText로 불러오고 메시지 내용을 삭제하고 list update 한다
	 * 
	 * @author SOL
	 *
	 * @param content
	 * @return 대화창을 클릭할 경우 나타나는 행동을 정한 OnClickListener
	 */
	public OnClickListener onTalkContentLayoutClicked(final MessageContent content){
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(content.getType() == 3 &&  !talkListAdapter.isDeleteMode()){
					if(sendTextMessageEditText.getText().toString().length() > 0){
						saveTemporaryStorageMessage();
					}
					sendTextMessageEditText.setText(content.getContext());
					
					content.setChecked(true);
					talkListAdapter.deleteSMS();
					talkListAdapter.setItem();
					talkListAdapter.notifyDataSetChanged();
				}
				
			}
		};
	}

	/**
	 * 공유 메뉴를 띄운다
	 * 
	 * 
	 * @author SOL
	 *
	 * @param content
	 * @return 대화창을 길게 클릭할 경우 나타나는 행동을 정한 OnCreateContextMenuListener
	 */
	public OnCreateContextMenuListener onTalkContentLayoutMunuClickedListener(final MessageContent content){
		return new OnCreateContextMenuListener() {

			private OnMenuItemClickListener menuItemClickListener = new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
					case 0:
						Intent msg = new Intent(Intent.ACTION_SEND);
						msg.addCategory(Intent.CATEGORY_DEFAULT);
						msg.putExtra(Intent.EXTRA_SUBJECT, "");
						msg.putExtra(Intent.EXTRA_TEXT, content.getContext());
						msg.putExtra(Intent.EXTRA_TITLE, "");
						msg.setType("text/plain");
						TalkActivity.this.startActivity(Intent.createChooser(msg, "공유"));
						break;

					default:
						break;
					}
					return false;
				}
			};

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				if( !talkListAdapter.isDeleteMode()){
					menu.setHeaderTitle("메시지 옵션");
					menu.add(0,0,0,"공유").setOnMenuItemClickListener(menuItemClickListener);
				}
			}
		};
	}

	/**
	 * 메시지를 보내는 메서드
	 * 
	 * @author SOL
	 *
	 * @param messageNum
	 * @param smsText
	 */
	private void sendSMS(String messageNum, String smsText) {
		PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"), 0);
		PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"), 0);

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch(getResultCode()){
				case Activity.RESULT_OK:
					// 전송 성공

					Toast.makeText(TalkActivity.this, "전송 완료", Toast.LENGTH_SHORT).show();
					sendTextMessageEditText.setText("");
					talkListAdapter.setItem();
					talkListView.setSelection(talkListAdapter.getCount()-1);
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					// 전송 실패
					Toast.makeText(TalkActivity.this, "전송 실패", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					// 서비스 지역 아님
					Toast.makeText(TalkActivity.this, "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					// 무선 꺼짐
					Toast.makeText(TalkActivity.this, "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					// PDU 실패
					Toast.makeText(TalkActivity.this, "PDU Null", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter("SMS_SENT_ACTION"));

		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()){
				case Activity.RESULT_OK:
					// 도착 완료
					Toast.makeText(TalkActivity.this, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					// 도착 안됨
					Toast.makeText(TalkActivity.this, "SMS 도착 실패", Toast.LENGTH_SHORT).show();
					break;
				}

			}
		}, new IntentFilter("SMS_DELIVERED_ACTION"));

		SmsManager smsManager = SmsManager.getDefault();
		for (String number : messageNum.split(",")) {
			smsManager.sendTextMessage(number.trim(), null, smsText, sentIntent, deliveredIntent);		
		}
	}

	private void returnNormaldMode(){
		informationLayout.setVisibility(View.VISIBLE);
		deleteCheckLayout.setVisibility(View.GONE);
		talkListBottom.setVisibility(View.VISIBLE);

		talkListAdapter.setNormalMode();
		checkCount = 0;
		checkItemCountTextView.setText(Integer.toString(checkCount));
		allCheckBox.setChecked(false);

		//자연 스러운 스크롤 처리.
		if(isSetListLastItemBottom){
			talkListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		}
	}
	
	/**
	 * sendTextMessageEditText의 값을 읽어와 임시메시지로 저장하는 메서드
	 * 
	 * @author SOL
	 *
	 */
	private void saveTemporaryStorageMessage() {
		Uri allMessage = Uri.parse("content://sms/");
		ContentValues values = new ContentValues();

		values.put(MessageContent.THREAD_ID, talkListAdapter.getThread_id());
		values.put(MessageContent.ADDRESS, talkListAdapter.getHeader());
		values.put(MessageContent.BODY, sendTextMessageEditText.getText().toString());
		values.put(MessageContent.DATE, System.currentTimeMillis());
		values.put(MessageContent.TYPE, MessageContent.TEMPORARY_STORAGE_SMS_TYPE);

		Uri returnUri = getContentResolver().insert(allMessage, values);

		String zeroUri = allMessage.toString() + "0";
		if( !zeroUri.equals(returnUri.toString())){
			Toast.makeText(this, "메시지가 임시 보관되었습니다", Toast.LENGTH_LONG).show();
		}
	}

	public static boolean isOnResume() {
		return isRun;
	}

	public void onTestButtonClicked0(View v){
		sendSMS("010********", "button0");
	}
	public void onTestButtonClicked1(View v){
		for (int i = 0; i < 3; i++) {
			sendSMS("010********", "button1 : " + i);
		}
	}
	public void onTestButtonClicked2(View v){
		sendSMS("010********, 1588****", "button2");
	}
	public void onTestButtonClicked3(View v){
		for (int i = 0; i < 3; i++) {
			sendSMS("010********, 1588****", "button3 : " + i);
		}
	}

}
