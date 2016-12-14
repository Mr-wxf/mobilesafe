package com.wxf.mobilesafe.receiver;

import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private String tag = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		String number = SpUtil.getPsd(context, ConstaxtValuse.NUMBER, "");

		String contact_number = SpUtil.getPsd(context,
				ConstaxtValuse.CONTACT_NUMBER, "");
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = tm.getSimSerialNumber();
		if (!number.equals(simSerialNumber)) {
			SmsManager sm = SmsManager.getDefault();
			sm.sendTextMessage(contact_number, null, "SIM卡变更", null, null);
		}
	}

}
