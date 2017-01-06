package com.example.new_message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;

import com.example.contents.MessageContent;
import com.example.contents.PhoneNumberContent;
import com.example.ezmessage.R;
import com.example.talk_list.TalkActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 전화번호를 선택할 경우 edittext의 내용을 전화번호로 표시 한 후 추가한다
 * 
 * @author SOL
 *
 */
@SuppressLint("NewApi")
public class NewMessageActivity extends AppCompatActivity{

	private final int[] focusSendTextMessageGoneItemNumbers ={0,1,3,4,5,7};

	private LinkedList<SendPhoneNumberLayout> sendViewList;
	private ArrayList<MenuItem> menuList;

	private PhoneNumberAdapter phoneNumberAdapter = null;


	private LinearLayout sendPhoneNumberListLayout = null;
	private EditText phoneNumberSearchEditText = null;
	private ListView phoneNumberListView = null;

	private Button searchTelephoneDirectory = null;
	private Button addPhoneNumber = null;

	private EditText sendTextMessage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_message);

		sendViewList = new LinkedList<SendPhoneNumberLayout>();

		sendPhoneNumberListLayout = (LinearLayout) findViewById(R.id.sendPhoneNumberListLayout);
		phoneNumberSearchEditText = (EditText) findViewById(R.id.phoneNumberSearchEditText);
		phoneNumberListView = (ListView) findViewById(R.id.phoneNumberListView);

		searchTelephoneDirectory = (Button) findViewById(R.id.searchTelephoneDirectory);
		addPhoneNumber = (Button) findViewById(R.id.addPhoneNumber);

		sendTextMessage = (EditText) findViewById(R.id.sendText);

		phoneNumberAdapter = new PhoneNumberAdapter(this);

		phoneNumberListView.setAdapter(phoneNumberAdapter);
		phoneNumberListView.setTextFilterEnabled(true);


		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("새 메시지");

		phoneNumberListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				addPhoneNumberLayout((PhoneNumberContent)phoneNumberAdapter.getItem(position));
				phoneNumberSearchEditText.setText("");
			}

		});

		//phoneNumberSearchEditText text유무에 따라 searchTelephoneDirectory, phoneNumberListView, addPhoneNumber의 visibility를 바꾼다
		phoneNumberSearchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				phoneNumberAdapter.getFilter().filter(s);

				if(s.toString().equals("")){
					searchTelephoneDirectory.setVisibility(View.VISIBLE);
					phoneNumberListView.setVisibility(View.GONE);
					addPhoneNumber.setVisibility(View.GONE);
				}
				else {
					addPhoneNumber.setVisibility(View.VISIBLE);
					phoneNumberListView.setVisibility(View.VISIBLE);
					searchTelephoneDirectory.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// focus가 바뀔 경우 menu의 item들 동적으로 visible을 바꾼다
		sendTextMessage.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					for (MenuItem item : menuList) {
						item.setVisible(true);
					}
				}
				else{
					for (int i = 0; i < focusSendTextMessageGoneItemNumbers.length; i++) {

						menuList.get(focusSendTextMessageGoneItemNumbers[i]).setVisible(false);
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_message_menu, menu);

		menuList = new ArrayList<MenuItem>();
		menuList.add(menu.findItem(R.id.quickReplyPhrases));
		menuList.add(menu.findItem(R.id.insertEmoticon));
		menuList.add(menu.findItem(R.id.addTitle));
		menuList.add(menu.findItem(R.id.addSlide));
		menuList.add(menu.findItem(R.id.messageDeliverySchedule));
		menuList.add(menu.findItem(R.id.contactInformationDelivery));
		menuList.add(menu.findItem(R.id.saveTemproryMessage));
		menuList.add(menu.findItem(R.id.translate));
		menuList.add(menu.findItem(R.id.notSave));
		menuList.add(menu.findItem(R.id.fontSize));

		for (int i = 0; i < focusSendTextMessageGoneItemNumbers.length; i++) {

			menuList.get(focusSendTextMessageGoneItemNumbers[i]).setVisible(false);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		//선택
		case android.R.id.home :
			onBackPressed();
			break;

		case R.id.saveTemproryMessage :
			saveTemporaryStorageMessage();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	//TODO 전화번호부 만들기
	public void onSearchTelephoneDirectoryButtonClicked(View v){

	}


	/**
	 * 
	 * @author SOL
	 *
	 * @param v
	 * 
	 * text가 숫자이면 추가한다
	 */
	public void onAddPhoneNumberButtonClicked(View v){
		String number = phoneNumberSearchEditText.getText().toString();

		//TODO +, -에 대해서 처이 안됨 이 부분은 전화번호를 처리하는 클래스를 만들어 해결할 것.
		if( !Pattern.matches("[0-9]+", number)){
			/*TODO
			 * 받는 사람이 유효하지 않음
			 * 
			 * 전화번호가 바르지 않아 받는 사람을 추가할 수 없습니다.
			 * --확인--
			 * Dialog를 사용해서 만들면 될 듯
			 */
			Toast.makeText(this, "전화 번호가 바르지 않아 받는 사람을 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
			return ;
		}

		/*	
		 * text를 숫자로 검색하면 밑의 전화번호부도 같이 검색된다
		 * 그것을 이용해 검색된 것 중 첫 값과 번호를 비교해 동일하면 이름으로 추가한다
		 */
		PhoneNumberContent content = phoneNumberAdapter.getFirstItem();

		if( content == null ||  !content.isMine(number))
			content = new PhoneNumberContent("", number, null);

		addPhoneNumberLayout(content);

		phoneNumberSearchEditText.setText("");
	}

	public void onSendButtonClicked(View v){

		String smsText = sendTextMessage.getText().toString();

		if(sendViewList.size() > 0 && smsText.length() > 0){
			sendSMS(sendViewList, smsText);
		}else{
			Toast.makeText(this, "문자를 입력해 주세요", Toast.LENGTH_SHORT).show();
		}
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(sendTextMessage.getWindowToken(), 0);
	}

	public void deleteSendItem(SendPhoneNumberLayout content) {
		sendViewList.remove(content);
		sendPhoneNumberListLayout.removeView(content);
	}


	private void addPhoneNumberLayout(PhoneNumberContent item) {
		for (SendPhoneNumberLayout view : sendViewList) {
			if(view.getPhoneNumberContent().isMine(item.getInformation())){
				Toast.makeText(this, "중복된 번호 입니다", Toast.LENGTH_LONG).show();
				return ;
			}
		}
		sendViewList.add(new SendPhoneNumberLayout(this, item));
		sendPhoneNumberListLayout.addView(sendViewList.getLast());

	}

	private void sendSMS(LinkedList<SendPhoneNumberLayout> viewList, String smsText) {
		PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"), 0);
		PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"), 0);

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch(getResultCode()){
				case Activity.RESULT_OK:
					// 전송 성공
					//TODO 새 대화창으로 넘어가기
					Toast.makeText(NewMessageActivity.this, "전송 완료", Toast.LENGTH_SHORT).show();
					sendTextMessage.setText("");
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					// 전송 실패
					Toast.makeText(NewMessageActivity.this, "전송 실패", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					// 서비스 지역 아님
					Toast.makeText(NewMessageActivity.this, "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					// 무선 꺼짐
					Toast.makeText(NewMessageActivity.this, "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					// PDU 실패
					Toast.makeText(NewMessageActivity.this, "PDU Null", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(NewMessageActivity.this, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					// 도착 안됨
					Toast.makeText(NewMessageActivity.this, "SMS 도착 실패", Toast.LENGTH_SHORT).show();
					break;
				}

			}
		}, new IntentFilter("SMS_DELIVERED_ACTION"));

		SmsManager smsManager = SmsManager.getDefault();
		for (SendPhoneNumberLayout view : viewList) {
			Log.i("send/NewMessageActivity", "number : " + view.getPhoneNumberContent().getInformation().trim());
			smsManager.sendTextMessage(view.getPhoneNumberContent().getInformation().trim(), null, smsText, sentIntent, deliveredIntent);		
		}
	}

	/**TODO
	 * 번호만 이용해 저장할 경우 thread_id를 db에서 알아서 찾아서 넣어준다
	 * 스페이스로 나눌경우 뒷번호만 저장됨
	 * |로 나눌경우 아예 다른 thread로 저장됨
	 * , 나 ; 로 나누어야 한다 중간에 스페이스 있어도 상관없음
	 * 나누고 thread 값을, 알아낸다음 다시지웠다가 번호 순대로 집어넣으면 될듯하다
	 * 다른 여러가지 메시지 전달도 이런 방법을 이용하자
	 * 단체문자메시지인 경우 저장은 한번만하고 전송은 여러번 한다
	 * 
	 * @author SOL
	 *
	 */
	private void saveTemporaryStorageMessage() {

		if(sendTextMessage.getText().toString().length() <= 0){
			Toast.makeText(this, "저장할 내용이 없습니다", Toast.LENGTH_LONG).show();
			return;
		}else if(sendViewList.size() <= 0){
			Toast.makeText(this, "받는 사람을 입력하세요", Toast.LENGTH_LONG).show();
			return;
		}

		Uri allMessage = Uri.parse("content://sms/");
		ContentValues values = new ContentValues();

		String number = "";
		for (SendPhoneNumberLayout view : sendViewList) {
			number += view.getPhoneNumberContent().getInformation().trim();
			number +=";";
		}

		values.put(MessageContent.ADDRESS, number);
		values.put(MessageContent.BODY, sendTextMessage.getText().toString());
		values.put(MessageContent.DATE, System.currentTimeMillis());
		values.put(MessageContent.TYPE, MessageContent.TEMPORARY_STORAGE_SMS_TYPE);

		Uri returnUri = getContentResolver().insert(allMessage, values);

		String zeroUri = allMessage.toString() + "0";
		int _id = -1;
		int thread_id = -1;
		if( !zeroUri.equals(returnUri.toString())){
			Cursor cur = getContentResolver().query(returnUri, 
					new String[]{
							MessageContent._ID,
							MessageContent.THREAD_ID}, 
					null, null, null);
			
			if(cur.getCount() <= 0){
				return;
			}
			
			cur.moveToFirst();
			_id = cur.getInt(cur.getColumnIndex(MessageContent._ID));
			thread_id = cur.getInt(cur.getColumnIndex(MessageContent.THREAD_ID));
			
			int i = getContentResolver().delete(Uri.parse("content://sms/" + _id), null, null);
			if(i <= 0) return;
			
			//임시메시지 저장은 하나만 한다!
			for (String splitNumber : number.split(";")) {
				if(splitNumber == null || splitNumber.equals("")) break;
				values.put(MessageContent.THREAD_ID, thread_id);
				values.put(MessageContent.ADDRESS, splitNumber.trim());
				values.put(MessageContent.BODY, sendTextMessage.getText().toString());
				values.put(MessageContent.DATE, System.currentTimeMillis());
				values.put(MessageContent.TYPE, MessageContent.TEMPORARY_STORAGE_SMS_TYPE);
				returnUri = getContentResolver().insert(allMessage, values);
				break;
			}
			
		}else{
			return;
		}
		


		zeroUri = allMessage.toString() + "0";
		if( !zeroUri.equals(returnUri.toString())){
			Toast.makeText(this, "메시지가 임시 보관되었습니다", Toast.LENGTH_LONG).show();
		}
		
		Intent intent = new Intent(this,TalkActivity.class);

		intent.putExtra(MessageContent.THREAD_ID, thread_id);

		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}

}
