package com.example.broadcast;

import com.example.contents.MessageContent;
import com.example.ezmessage.MainActivity;
import com.example.talk_list.TalkActivity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony.Sms;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * 기본 문자앱이 됐을때 SMS를 직접 받는다
 * 
 * @author SOL
 */
public class SMSBroadcast extends BroadcastReceiver{

	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("broadcast/SMSBroadcast.onReceive",intent.getAction());
		
		makeBadgeCheckService(context);
		
		if(intent.getAction().equals("android.provider.Telephony.SMS_DELIVER")){
			smsDefaultRecived(context,intent);
		}
		else if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			if(MainActivity.isOnResume() || TalkActivity.isOnResume())
				smsRecived(context, intent);
		}
		else {
		}
	}

	/**
	 * TODO 현재는 test 중 나중에 완성할 것
	 * App이 default 상태일 때 받으면 작동하는 기능을 담은 메서드 
	 * 
	 * @author SOL
	 *
	 * @param context
	 * @param intent
	 * 
	 */
	private void smsDefaultRecived(Context context, Intent intent) {
		ContentValues values = new ContentValues();
		
		values.put(Sms.ADDRESS, "010********");
		values.put(Sms.DATE, System.currentTimeMillis()+"");
		values.put(Sms.READ, "1");
		values.put(Sms.TYPE, MessageContent.OTHER_TYPE);
		values.put(Sms.BODY, "Hello");
		Uri uri = Uri.parse("content://sms/");
		context.getContentResolver().insert(uri, values);
		values = new ContentValues();
		
	}

	/**
	 * 리시브 받으면 문자 자체를 넘기지 않고 
	 * 다음 Activity에 대한 정보를 새로운 Activity를 만들어 넘긴다
	 * 
	 * @param context
	 * @param intent
	 * 외부에서 온 context, intent
	 */
	private void smsRecived(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Object[] objs = (Object[])bundle.get("pdus");
		int smsCount = objs.length;
		SmsMessage[] messages = new SmsMessage[smsCount];
		
		//PDU 포맷으로 되어 있는 메시지를 복원함
		for(int i = 0; i < smsCount; i++){
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
				String format = bundle.getString("format");
				messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
			}else{
				messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
			}
		}
		Intent sendIntent = new Intent(context, RecivedMessageSendActivity.class);
		
		sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		if(MainActivity.isOnResume())
			sendIntent.putExtra(RecivedMessageSendActivity.ACTIVTY_NAME, MainActivity.ACTIVITY_NAME);
		else if(TalkActivity.isOnResume())
			sendIntent.putExtra(RecivedMessageSendActivity.ACTIVTY_NAME, TalkActivity.ACTIVITY_NAME);
		
		Log.i("broadcast/SMSBroadcast.smsRecived", messages[0].getOriginatingAddress());
		
		sendIntent.putExtra("sender", messages[0].getOriginatingAddress());
		context.startActivity(sendIntent);
		
	}
	
	private void makeBadgeCheckService(Context context){
		///test
//		Log.i("Test/TalkActivity.makeBadgeCheckService", "start");
//		Uri uri = Uri.parse("content://sms/");
//		String[] projection = new String[]{"_id", "read"};
//		String selection = "read=0";
//		Cursor cur = null;
//		
//		Log.i("Test/TalkActivity.makeBadgeCheckService", "1");
//		cur = context.getContentResolver().query(uri, projection, selection, null,"date ASC");
//		Log.i("Test/TalkActivity.makeBadgeCheckService", "2 cur is? : " + cur.getCount());
//		cur.moveToNext();
//		Log.i("Test/TalkActivity.makeBadgeCheckService", "3");
//		int _id = cur.getInt(cur.getColumnIndex("_id"));
//		
//		Log.i("Test/TalkActivity.makeBadgeCheckService", "4 ID : " + _id);
//		
//		ContentObsever2 contentObserver = null; 
//		contentObserver = new ContentObsever2(null, context);
//		
//		Log.i("Test/TalkActivity.makeBadgeCheckService", "5");
//		context.getContentResolver().registerContentObserver(Uri.parse("content://sms/"+_id+"/read"), false, contentObserver);
//		
		///test
		
		
		
		
		Log.i("Service/SMSBroadcast.makeBadgeCheckService", "Start");
		Intent service = new Intent(context, ReadCheckService.class);
//		service.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startService(service);
	}
	
	/*static class ContentObsever2 extends ContentObserver {
		Context context;
		
		public ContentObsever2(Handler handler, Context context) {
			super(handler);
			Log.i("Test/TalkActivity.creater", "start");
			this.context = context;
		}
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i("Test/TalkActivity.onChange", "is? : " + selfChange);
//			context.getContentResolver().unregisterContentObserver(this);
		}
	}*/
}
