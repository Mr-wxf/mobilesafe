package com.wxf.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.engine.ProcessinfoEngine;
import com.wxf.mobilesafe.receiver.MyAppWidgetProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateAppWidget extends Service {

	protected static final String tag = "UpdateAppWidget";
	private Timer mTimer;
	private InnerReceiver mInnerReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
	
		startTime();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, intentFilter);
		super.onCreate();
	}

	class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				startTime();
			} else {
				cancleTaskTimer();
			}
		}

	}

	private void startTime() {

		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				updateWidget();
				Log.i(tag, "5秒更新");
			}
		}, 0, 5000);

	}

	public void cancleTaskTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer=null;
		}

	}

	protected void updateWidget() {
		AppWidgetManager aWM = AppWidgetManager
				.getInstance(getApplicationContext());
		RemoteViews remoteViews = new RemoteViews(getPackageName(),
				R.layout.process_widget);
		remoteViews.setTextViewText(R.id.tv_process_count, "进程总数："
				+ ProcessinfoEngine.getProcessCount(getApplicationContext()));
		long availableMemory = ProcessinfoEngine
				.getAvailableMemory(getApplicationContext());
		String strAvailableMemory = Formatter.formatFileSize(
				getApplicationContext(), availableMemory);
		remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存："
				+ strAvailableMemory);
		ComponentName componentName = new ComponentName(
				getApplicationContext(), MyAppWidgetProvider.class);
		Intent intent = new Intent("android.intent.action.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

		Intent killProcessIntent = new Intent(
				"android.intent.action.KILLALLPROCESSRECEIVER");
		PendingIntent broadcast = PendingIntent.getBroadcast(
				getApplicationContext(), 0, killProcessIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		remoteViews.setOnClickPendingIntent(R.id.bt_clear, broadcast);
		aWM.updateAppWidget(componentName, remoteViews);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mInnerReceiver != null) {
			unregisterReceiver(mInnerReceiver);
		}
		cancleTaskTimer();
	}

}
