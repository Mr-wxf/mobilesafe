package com.wxf.mobilesafe.service;

import java.util.List;

import com.wxf.mobilesafe.activity.EnterPsdActivity;
import com.wxf.mobilesafe.db.dao.AppLockrDAO;
import com.wxf.mobilesafe.engine.AppInfoEngine;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class AppLockService extends Service {
	boolean isWatch = true;
	private AppLockrDAO lockrDAO;
	private InnerReceiver mInnerReceiver;
	private String mPackageName;
	private List<String> findAll;
	private MyContentObserver myContentObserver;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		lockrDAO = AppLockrDAO.getInstance(getApplicationContext());
		watch();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.SKIP");
		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, intentFilter);
		myContentObserver = new MyContentObserver(
				new Handler());
		getContentResolver().registerContentObserver(
				Uri.parse("contant://applock/skip"), true, myContentObserver);
	}

	class MyContentObserver extends ContentObserver {

	
		
		public MyContentObserver(Handler handler) {
			super(handler);
		}
		@Override
		public void onChange(boolean selfChange) {
			findAll = lockrDAO.findAll();
			super.onChange(selfChange);

		}

	}

	private void watch() {
		new Thread() {
			

			public void run() {
			 findAll = lockrDAO.findAll();
				while (isWatch) {
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					String packageName = runningTaskInfo.topActivity
							.getPackageName();

					if (findAll.contains(packageName)) {
						if (!packageName.equals(mPackageName)) {
							Intent intent = new Intent(getApplicationContext(),
									EnterPsdActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packageName", packageName);
							startActivity(intent);
						}

					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

			};

		}.start();

	}

	class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mPackageName = intent.getStringExtra("packageName");
		}

	}

	@Override
	public void onDestroy() {
		isWatch=false;
		if(mInnerReceiver!=null){
			unregisterReceiver(mInnerReceiver);
		}
		if(myContentObserver!=null){
			getContentResolver().unregisterContentObserver(myContentObserver);
		}
		super.onDestroy();
	}
}
