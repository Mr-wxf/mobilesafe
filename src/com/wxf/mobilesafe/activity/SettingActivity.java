package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.service.AddressService;
import com.wxf.mobilesafe.service.AppLockService;
import com.wxf.mobilesafe.service.BlackNumberService;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.ServiceUtil;
import com.wxf.mobilesafe.utils.SpUtil;
import com.wxf.mobilesafe.view.SettingClickView;
import com.wxf.mobilesafe.view.SettingView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	private SettingView settingview;
	private SettingView addressview;
	private SettingClickView toast_style_view;
	private String[] toastStyles;
	private int toast_style_id;
	private SettingClickView scv_location;
	private SettingView sv_blacknumber;
	private SettingView siv_app_lock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		settingview = (SettingView) findViewById(R.id.settingview);

		initAddressDate();
		boolean boolean1 = SpUtil.getBoolean(getApplicationContext(),
				ConstaxtValuse.CHECK_UPDATE, false);
		settingview.setCheck(boolean1);
		settingview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initUpdate();

			}
		});
		initToastStyle();
		initLocationToast();
		initBlackNumber();
		initAppLock();
	}

	private void initAppLock() {
		siv_app_lock = (SettingView) findViewById(R.id.siv_app_lock);
		boolean running = ServiceUtil.isRunning(getApplicationContext(),
				"com.wxf.mobilesafe.service.AppLockService");
		siv_app_lock.setCheck(running);
		siv_app_lock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isCheck = siv_app_lock.isCheck();
				siv_app_lock.setCheck(!isCheck);
				if (!isCheck) {
					startService(new Intent(getApplicationContext(),
							AppLockService.class));
				} else {
					stopService(new Intent(getApplicationContext(),
							AppLockService.class));
				}
			}
		});

	}

	private void initBlackNumber() {
		sv_blacknumber = (SettingView) findViewById(R.id.sv_blacknumber);
		boolean running = ServiceUtil.isRunning(getApplicationContext(),
				"com.wxf.mobilesafe.service.BlackNumberService");
		sv_blacknumber.setCheck(running);
		sv_blacknumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isCheck = sv_blacknumber.isCheck();
				sv_blacknumber.setCheck(!isCheck);
				if (!isCheck) {
					startService(new Intent(getApplicationContext(),
							BlackNumberService.class));
				} else {
					stopService(new Intent(getApplicationContext(),
							BlackNumberService.class));
				}
			}
		});

	}

	private void initLocationToast() {
		scv_location = (SettingClickView) findViewById(R.id.scv_location);
		scv_location.setTitle("归属地提示框位置");
		scv_location.setDes("设置归属地提示框位置");
		scv_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ToastLocationActivity.class));
			}
		});
	}

	private void initToastStyle() {
		toast_style_view = (SettingClickView) findViewById(R.id.toast_style_view);
		toast_style_view.setTitle("更改归属地显示样式");
		toastStyles = new String[] { "透明", "橙色", "蓝色", "灰色", "绿色" };
		toast_style_id = SpUtil.getInt(getApplicationContext(),
				ConstaxtValuse.TOAST_STYLE, 0);
		toast_style_view.setDes(toastStyles[toast_style_id]);
		toast_style_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToastColor();
			}
		});

	}

	protected void showToastColor() {
		Builder builder = new AlertDialog.Builder(this);

		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请选择归属地显示颜色");
		builder.setSingleChoiceItems(toastStyles, toast_style_id,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SpUtil.setInt(getApplicationContext(),
								ConstaxtValuse.TOAST_STYLE, which);
						toast_style_view.setDes(toastStyles[which]);
						dialog.dismiss();
					}
				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		});
		builder.show();
	}

	protected void initAddressDate() {
		addressview = (SettingView) findViewById(R.id.addressview);
		boolean running = ServiceUtil.isRunning(getApplicationContext(),
				"com.wxf.mobilesafe.service.AddressService");

		addressview.setCheck(running);
		addressview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean check = addressview.isCheck();
				addressview.setCheck(!check);
				if (!check) {
					// 开启归属地服务
					startService(new Intent(getApplicationContext(),
							AddressService.class));
				} else {
					// 关闭归属地服务
					stopService(new Intent(getApplicationContext(),
							AddressService.class));
				}
			}
		});
	}

	private void initUpdate() {

		boolean ischeck = settingview.isCheck();
		settingview.setCheck(!ischeck);

		SpUtil.setBoolean(getApplicationContext(), ConstaxtValuse.CHECK_UPDATE,
				!ischeck);
	}
}
