package com.wxf.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.engine.VirusDAO;
import com.wxf.mobilesafe.utils.Md5Util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class KillVirusActivity extends Activity {
	protected static final int ISVIRUSAPP = 100;
	protected static final int FINISH = 101;
	private String tag = "KillVirusActivity";
	private ImageView iv_scan_icon;
	private TextView tv_scan_app_name;
	private ProgressBar pb_bar;
	private LinearLayout ll_list;
	private RotateAnimation rotateAnimation;
	private int index = 0;
	private List<ScanAppInfo> mIsVirusAppList;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ISVIRUSAPP:
				ScanAppInfo scanAppInfo = (ScanAppInfo) msg.obj;
				// 是病毒
				TextView textView = new TextView(getApplicationContext());
				if (scanAppInfo.isVirus) {
					textView.setText("有病毒:" + scanAppInfo.name);
					textView.setTextColor(Color.RED);
					tv_scan_app_name.setText(scanAppInfo.name);

				} else {
					// 不是病毒
					textView.setText("扫描安全:" + scanAppInfo.name);
					tv_scan_app_name.setText(scanAppInfo.name);
					textView.setTextColor(Color.BLACK);
				}
				ll_list.addView(textView, 0);
				break;
			// 扫描结束
			case FINISH:
				tv_scan_app_name.setText("扫描完成");
				iv_scan_icon.clearAnimation();
				unInstallVirus();

				break;
			}

		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_killvirus);

		initUI();
		initAnimation();
		initKillVirus();
	}

	protected void unInstallVirus() {
		for (ScanAppInfo scanAppInfo : mIsVirusAppList) {

			Intent intent = new Intent("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + scanAppInfo.packageName));
			startActivity(intent);
		}
	}

	private void initKillVirus() {
		new Thread() {

			public void run() {
				List<String> virusMD5 = VirusDAO.getVirusMD5();
				PackageManager pm = getPackageManager();
				List<PackageInfo> packagesList = pm
						.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
								+ PackageManager.GET_SIGNATURES);
				mIsVirusAppList = new ArrayList<ScanAppInfo>();
				List<ScanAppInfo> allScanAppList = new ArrayList<ScanAppInfo>();
				pb_bar.setMax(packagesList.size());
				for (PackageInfo packageInfo : packagesList) {
					ScanAppInfo scanAppInfo = new ScanAppInfo();
					Signature[] signatures = packageInfo.signatures;
					Signature signature = signatures[0];
					String string = signature.toCharsString();
					String encoder = Md5Util.encoder(string);
//					Log.i(tag, encoder);
					if (virusMD5.contains(encoder)) {
						// 是病毒
						scanAppInfo.isVirus = true;
						mIsVirusAppList.add(scanAppInfo);
					} else {
						scanAppInfo.isVirus = false;
					}
					scanAppInfo.packageName = packageInfo.packageName;
					scanAppInfo.name = packageInfo.applicationInfo
							.loadLabel(pm).toString();
					allScanAppList.add(scanAppInfo);
					try {
						Thread.sleep(50 + new Random().nextInt(100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					index++;
					pb_bar.setProgress(index);
					Message msg = Message.obtain();
					msg.what = ISVIRUSAPP;
					msg.obj = scanAppInfo;
					mHandler.sendMessage(msg);
				}

				Message msg = Message.obtain();
				msg.what = FINISH;
				mHandler.sendMessage(msg);
			};

		}.start();
	}

	class ScanAppInfo {
		boolean isVirus;
		String packageName;
		String name;
	}

	private void initAnimation() {
		rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
		rotateAnimation.setFillAfter(true);
		iv_scan_icon.startAnimation(rotateAnimation);
	}

	private void initUI() {
		iv_scan_icon = (ImageView) findViewById(R.id.iv_scan_icon);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_scan_app_name = (TextView) findViewById(R.id.tv_scan_app_name);
		ll_list = (LinearLayout) findViewById(R.id.ll_list);

	}
}
