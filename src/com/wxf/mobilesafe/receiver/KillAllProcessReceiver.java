package com.wxf.mobilesafe.receiver;

import com.wxf.mobilesafe.engine.ProcessinfoEngine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillAllProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ProcessinfoEngine.killAll(context);
	}

}
