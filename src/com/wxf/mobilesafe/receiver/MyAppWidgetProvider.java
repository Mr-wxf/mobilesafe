package com.wxf.mobilesafe.receiver;

import com.wxf.mobilesafe.service.UpdateAppWidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MyAppWidgetProvider extends AppWidgetProvider {
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, UpdateAppWidget.class));
 		super.onReceive(context, intent);
 		
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		context.startService(new Intent(context, UpdateAppWidget.class));
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		context.startService(new Intent(context, UpdateAppWidget.class));
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		context.stopService(new Intent(context, UpdateAppWidget.class));
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		context.stopService(new Intent(context, UpdateAppWidget.class));
	}
}
