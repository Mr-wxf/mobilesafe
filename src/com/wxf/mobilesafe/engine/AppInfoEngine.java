package com.wxf.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.wxf.mobilesafe.db.bean.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppInfoEngine {
	private static ArrayList<AppInfo> appInfoList;

	public static  ArrayList<AppInfo> getAppInfo(Context context) {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> installedPackages = packageManager
				.getInstalledPackages(0);
	        appInfoList = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : installedPackages) {
			AppInfo appInfo = new AppInfo();
			appInfo.packageName = packageInfo.packageName;
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			appInfo.icon = applicationInfo.loadIcon(packageManager);
			appInfo.name = applicationInfo.loadLabel(packageManager).toString();
			if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
				appInfo.isSystem=true;
			}else{
				appInfo.isSystem=false;
			}
			if((applicationInfo.flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				appInfo.isSDCard=true;
				
			}else{
				appInfo.isSDCard=false;
			}
			appInfoList.add(appInfo);
		}
		return appInfoList;
	}
}
