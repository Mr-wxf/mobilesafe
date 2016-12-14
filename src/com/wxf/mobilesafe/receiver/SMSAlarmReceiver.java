package com.wxf.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.gsm.SmsMessage;
import android.widget.Toast;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.activity.HomeActivity;
import com.wxf.mobilesafe.service.LocationService;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.SpUtil;

public class SMSAlarmReceiver extends BootReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		ComponentName mDeviceAdminSample = new ComponentName(context,
				DeviceAdmin.class);

		boolean OPEN_SUCCESS = SpUtil.getBoolean(context,
				ConstaxtValuse.OPEN_SUCCESS, false);
		if (OPEN_SUCCESS) {
			Object[] object = (Object[]) intent.getExtras().get("pdus");
			for (Object object2 : object) {
				SmsMessage sm = SmsMessage.createFromPdu((byte[]) object2);
				String body = sm.getMessageBody();
				String address = sm.getOriginatingAddress();
				if (body.contains("#*alarm*#")) {
					MediaPlayer player = MediaPlayer
							.create(context, R.raw.ylzs);
					player.setLooping(true);
					player.start();
				}
				if (body.contains("#*location*#")) {
					Intent locationService = new Intent(context,
							LocationService.class);
					intent.putExtra("address", address);
					context.startService(locationService);
				}
				if (body.contains("#*lockscreen*#")) {
					if (HomeActivity.mDPM.isAdminActive(mDeviceAdminSample)) {
						HomeActivity.mDPM.lockNow();
						HomeActivity.mDPM.resetPassword("123", 0);
					} else {
						Toast.makeText(context, "请激活", 0).show();
					}
				}
				if (body.contains("#*wipedata*#")) {
					if (HomeActivity.mDPM.isAdminActive(mDeviceAdminSample)) {
						HomeActivity.mDPM.wipeData(0);
					} else {
						Toast.makeText(context, "请激活", 0).show();
					}
				}
	     
			}
		}

	}
}
