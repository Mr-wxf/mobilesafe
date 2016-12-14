package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.service.LockScreenClean;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.ServiceUtil;
import com.wxf.mobilesafe.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity {
	private CheckBox cb_show_system_process;
	private CheckBox cb_lock_screen_clean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);

		initSystemProcess();
		initLockScreenClean();
	}

	private void initLockScreenClean() {
		cb_lock_screen_clean = (CheckBox) findViewById(R.id.cb_lock_screen_clean);
		boolean running = ServiceUtil.isRunning(getApplicationContext(),
				"com.wxf.mobilesafe.service.LockScreenClean");
		if (running) {
			cb_lock_screen_clean.setText("锁屏清理已开启");
		} else {
			cb_lock_screen_clean.setText("锁屏清理已关闭");
		}
		cb_lock_screen_clean.setChecked(running);
		cb_lock_screen_clean
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							cb_lock_screen_clean.setText("锁屏清理已开启");
							startService(new Intent(getApplicationContext(),
									LockScreenClean.class));
						} else {
							cb_lock_screen_clean.setText("锁屏清理已关闭");
							stopService(new Intent(getApplicationContext(),
									LockScreenClean.class));
						}

					}
				});
	}

	private void initSystemProcess() {
		cb_show_system_process = (CheckBox) findViewById(R.id.cb_show_system_process);
		boolean systemProcess = SpUtil.getBoolean(getApplicationContext(),
				ConstaxtValuse.SHOW_SYSTEM_PROCESS, false);
		if (systemProcess) {
			cb_show_system_process.setText("隐藏系统进程");
		} else {
			cb_show_system_process.setText("显示系统进程");
		}
		cb_show_system_process.setChecked(systemProcess);
		cb_show_system_process
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							cb_show_system_process.setText("隐藏系统进程");
						} else {
							cb_show_system_process.setText("显示系统进程");
						}
						SpUtil.setBoolean(getApplicationContext(),
								ConstaxtValuse.SHOW_SYSTEM_PROCESS, isChecked);
					}
				});

	}
}
