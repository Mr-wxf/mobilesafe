package com.wxf.mobilesafe.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.wxf.mobilesafe.db.dao.BlcakNumberDAO;
import com.wxf.mobilesafe.service.AddressService.MyPhoneStateListener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.view.WindowManager;

public class BlackNumberService extends Service {
	private InnerSmsReceiver innerSmsReceiver;
	private TelephonyManager mTM;
	private MyPhoneStateListener myPhoneStateListener;
	private String tag = "BlackNumberService";
	private MyContentObserver myContentObserver;

	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);
		innerSmsReceiver = new InnerSmsReceiver();
		registerReceiver(innerSmsReceiver, filter);
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			// 空闲
			case TelephonyManager.CALL_STATE_IDLE:

				break;
			// 通话
			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;
			// 响铃
			case TelephonyManager.CALL_STATE_RINGING:
				endCall(incomingNumber);

				break;
			}
		}

	}

	class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] object = (Object[]) intent.getExtras().get("pdus");
			for (Object object2 : object) {
				SmsMessage sm = SmsMessage.createFromPdu((byte[]) object2);
				String body = sm.getMessageBody();
				String address = sm.getOriginatingAddress();
				BlcakNumberDAO db = BlcakNumberDAO
						.getInstance(getApplicationContext());
				int mode = db.getMode(address);
				if (mode == 1 || mode == 3) {
					abortBroadcast();
				}
			}

		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void endCall(String phone) {

		// return
		// ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));

		BlcakNumberDAO db = BlcakNumberDAO.getInstance(getApplicationContext());
		int mode = db.getMode(phone);
		Log.i(tag, mode + "");
		if (mode == 2 || mode == 3) {
			try {
				Class<?> clazz = Class.forName("android.os.ServiceManager");
				Method method = clazz.getMethod("getService", String.class);
				IBinder binder = (IBinder) method.invoke(null,
						Context.TELEPHONY_SERVICE);
				ITelephony asInterface = ITelephony.Stub.asInterface(binder);
				asInterface.endCall();
			} catch (Exception e) {

				e.printStackTrace();
			}
			myContentObserver = new MyContentObserver(new Handler(),phone);

			getContentResolver().registerContentObserver(
					Uri.parse("content://call_log/calls"), true, myContentObserver);
		}
	}

	class MyContentObserver extends ContentObserver {

		private String phone;

		public MyContentObserver(Handler handler, String phone) {
			super(handler);
			this.phone = phone;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			getContentResolver().delete(Uri.parse("content://call_log/calls"),
					"number=?",new String[] { phone });
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (innerSmsReceiver != null) {
			unregisterReceiver(innerSmsReceiver);
		}
		if(myContentObserver!=null){
			getContentResolver().unregisterContentObserver(myContentObserver);
		}
		if(myPhoneStateListener!=null){
			mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
	}
}
