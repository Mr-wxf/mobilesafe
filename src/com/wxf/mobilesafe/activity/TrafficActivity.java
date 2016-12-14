package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;

public class TrafficActivity extends Activity {
   @Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

   setContentView(R.layout.activity_traffic);
//   手机下载的流量
   long mobileRxBytes = TrafficStats.getMobileRxBytes();
//     手机总流量 下载+上传
   long mobileTxBytes = TrafficStats.getMobileTxBytes();
//   总下载流量  wifi+2g，3g，4g 
   long totalRxBytes = TrafficStats.getTotalRxBytes();
   
   }
}
