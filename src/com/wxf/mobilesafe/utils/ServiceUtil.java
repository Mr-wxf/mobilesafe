package com.wxf.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {
   public static boolean  isRunning(Context context ,String serviceName ){
	   ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	  List<RunningServiceInfo> runningServices = am.getRunningServices(100);
	  for (RunningServiceInfo runningServiceInfo : runningServices) {
		if(runningServiceInfo.service.getClassName().equals(serviceName)){
			return true;
		}
	}
	return false;
	   
   }
}
 