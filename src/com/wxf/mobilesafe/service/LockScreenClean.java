package com.wxf.mobilesafe.service;

import com.wxf.mobilesafe.engine.ProcessinfoEngine;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenClean extends Service {

	private InnerReceiver innerReceiver;
	@Override
	public IBinder onBind(Intent intent) {
 		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, intentFilter);
	}
	@Override
	public void onDestroy() {
 		super.onDestroy();
	  if(innerReceiver!=null){
 unregisterReceiver(innerReceiver);
	  }
	}
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
               ProcessinfoEngine.killAll(context);			
		}
		
	}

}
