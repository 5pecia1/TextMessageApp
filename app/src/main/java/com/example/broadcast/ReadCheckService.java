package com.example.broadcast;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import shortcutbadger.ShortcutBadger;

/**
 * 읽지않은 문자를 확인하여 뱃지를 띄워주는 Service
 * 
 * @author SOL
 *
 */
public class ReadCheckService extends Service implements Runnable{
	Thread thread = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("Service/REadCheckService.onCreate", "Start");
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("Service/REadCheckService.onCreateCommand", "Start");
		
		if( !thread.isAlive()){
			thread = new Thread(this);
			thread.start();
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void run() {
		Log.i("Service/REadCheckService.run", "Start");
		int lastCount = 0;
		int count = 0;
		
		Uri uri = Uri.parse("content://sms/");
		String[] projection = new String[]{"read"};
		String selection = "read=0";
		Cursor cur = null;
		
		//바로 db를 읽을 경우 문제가 생길 수 도 있음.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		while(true){
			cur = getContentResolver().query(uri, projection, selection, null,null);
			

			count = cur.getCount();
			
			if(lastCount  != count){
				lastCount = count;
				ShortcutBadger.applyCount(getApplicationContext(), lastCount);
			}
			
			

			if(count <= 0)	break;
			
			try {//자원 소모를 줄인다
				Thread.sleep(10000);//여름이라는 문자메시지 어플보다 조금 빠르다
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.i("Service/REadCheckService.run", "end");
		
//		stopSelf();
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
}
