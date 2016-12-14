package com.wxf.mobilesafe.service;

import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.SpUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;
import android.util.Log;

public class LocationService extends Service {
	private String tag = "LocationService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// 地址改变

			double longitude = location.getLongitude();// 经度

			double latitude = location.getLatitude();// 纬度
			Log.i(tag, longitude + "-------" + latitude);
			Intent intent = new Intent();
			String number = SpUtil.getPsd(getApplicationContext(),
					ConstaxtValuse.CONTACT_NUMBER, "");
			String address = intent.getStringExtra("address");
			SmsManager sm = SmsManager.getDefault();

			sm.sendTextMessage("address", null, "经度为" + longitude + "纬度为"
					+ latitude, null, null);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO gps改变

		}

		@Override
		public void onProviderEnabled(String provider) {
			// 开启获取地址

		}

		@Override
		public void onProviderDisabled(String provider) {
			// 关闭获取地址

		}

	}

	@Override
	public void onCreate() {
		super.onCreate();

		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		/*
		 * Criteria criteria = new Criteria();
		 * criteria.setAccuracy(Criteria.ACCURACY_FINE);
		 * criteria.setCostAllowed(true);
		 */

		String bestProvider = lm.getBestProvider(criteria, true);
		if (TextUtils.isEmpty(bestProvider)) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
					new MyLocationListener());
		} else {

			lm.requestLocationUpdates(bestProvider, 0, 0,
					new MyLocationListener());
		}
	}

}
