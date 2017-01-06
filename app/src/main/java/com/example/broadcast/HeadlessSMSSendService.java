package com.example.broadcast;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service that delivers message from the phone "quick response"
 * 
 * @author SOL
 * 
 */
public class HeadlessSMSSendService extends Service implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
