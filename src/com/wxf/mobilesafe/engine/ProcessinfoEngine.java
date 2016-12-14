package com.wxf.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.activity.ProcessManager;
import com.wxf.mobilesafe.db.bean.ProcessInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class ProcessinfoEngine {
	private static int subInt = Integer
			.parseInt(android.os.Build.VERSION.RELEASE.substring(0, 1));

	/**
	 * @param context
	 * @return 总进程数
	 */
	public static int getProcessCount(Context context) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		if (subInt > 4) {
			// 5.0后系统获取进程的办法
			List<AndroidAppProcess> runningAppProcesses = com.jaredrummler.android.processes.ProcessManager
					.getRunningAppProcesses();
			return runningAppProcesses.size();
		} else {
			List<RunningAppProcessInfo> runningAppProcesses = am
					.getRunningAppProcesses();
			return runningAppProcesses.size();
		}

		// Log.i("ProcessinfoEngine",substring+"");

	}

	/**
	 * @param context
	 * @return 可用内存大小
	 */
	public static long getAvailableMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		am.getMemoryInfo(memoryInfo);
		return memoryInfo.availMem;
	}

	/**
	 * @return 总内存数
	 */
	public static long getAllMemory() {
		BufferedReader bufferedReader = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader("proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);
			String readLine = bufferedReader.readLine();
			char[] charArray = readLine.toCharArray();
			StringBuffer stringBuffer = new StringBuffer();

			for (char c : charArray) {
				if (c >= '0' && c <= '9') {
					stringBuffer.append(c);
				}
			}
			return Long.parseLong(stringBuffer.toString()) * 1024;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null && fileReader != null) {
				try {
					bufferedReader.close();
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	public static List<ProcessInfo> getRunningProcess(Context context) {
		List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		if (subInt > 4) {
			List<AndroidAppProcess> runningAppProcesses = com.jaredrummler.android.processes.ProcessManager
					.getRunningAppProcesses();
			for (AndroidAppProcess info : runningAppProcesses) {

				ProcessInfo processInfo = new ProcessInfo();
				processInfo.packageName = info.getPackageName();

				android.os.Debug.MemoryInfo[] processMemoryInfo = am
						.getProcessMemoryInfo(new int[] { info.pid });
				android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
				processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;
				try {
					PackageManager packageManager = context.getPackageManager();
					ApplicationInfo applicationInfo = packageManager
							.getApplicationInfo(processInfo.packageName, 0);
					processInfo.name = applicationInfo
							.loadLabel(packageManager).toString();
					processInfo.icon = applicationInfo.loadIcon(packageManager);
					if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
						processInfo.isSystem = true;
					} else {
						processInfo.isSystem = false;
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
					processInfo.icon = context.getResources().getDrawable(
							R.drawable.ic_launcher);
					processInfo.name = info.getPackageName();
					processInfo.isSystem = true;

				}
				processInfoList.add(processInfo);
			}
			return processInfoList;
		} else {
			List<RunningAppProcessInfo> runningAppProcesses = am
					.getRunningAppProcesses();
			for (RunningAppProcessInfo info : runningAppProcesses) {

				ProcessInfo processInfo = new ProcessInfo();
				processInfo.packageName = info.processName;

				android.os.Debug.MemoryInfo[] processMemoryInfo = am
						.getProcessMemoryInfo(new int[] { info.pid });
				android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
				processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;
				try {
					PackageManager packageManager = context.getPackageManager();
					ApplicationInfo applicationInfo = packageManager
							.getApplicationInfo(processInfo.packageName, 0);
					processInfo.name = applicationInfo
							.loadLabel(packageManager).toString();
					processInfo.icon = applicationInfo.loadIcon(packageManager);
					if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
						processInfo.isSystem = true;
					} else {
						processInfo.isSystem = false;
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
					processInfo.icon = context.getResources().getDrawable(
							R.drawable.ic_launcher);
					processInfo.name = info.processName;
					processInfo.isSystem = true;

				}
				processInfoList.add(processInfo);
			}
			return processInfoList;
		}

	}

	public static void killProcess(Context context, ProcessInfo processInfo) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(processInfo.packageName);
	}

	public static void killAll(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		for (RunningAppProcessInfo info : runningAppProcesses) {
			if (info.processName.equals(context.getPackageName())) {
				continue;
			}
			am.killBackgroundProcesses(info.processName);
		}
	}

}
