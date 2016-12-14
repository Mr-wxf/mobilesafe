package com.wxf.mobilesafe.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

public class MyApplication extends Application {
	protected static final String tag = "MyApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				ex.printStackTrace();
				Log.i(tag, "捕获到了异常");
				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + File.separator + "error.log";
				
				File file = new File(path);
				PrintWriter printWriter=null;
				try {
					printWriter = new PrintWriter(file);
					ex.printStackTrace(printWriter);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});

	}
}
