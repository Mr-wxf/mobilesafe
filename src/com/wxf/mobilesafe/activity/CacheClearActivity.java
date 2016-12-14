package com.wxf.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import com.wxf.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CacheClearActivity extends Activity {
	private static final int NEED_CLEAR_CAHCESIZE_APP = 100;
	private static final int SCANNING_CAHCESIZE_APP = 101;
	private static final int SCANNING_FINISH = 102;
	private static final int CLEAR_ALL_CACHE_FINISH = 103;
	private Button bt_cache_clear;
	private ProgressBar pb_cache;
	private TextView tv_scanning_app;
	private LinearLayout ll_cache_app;
	private PackageManager mPm;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case NEED_CLEAR_CAHCESIZE_APP:
				final CacheAppInfo cacheAppInfo = (CacheAppInfo) msg.obj;
				final View view = View.inflate(getApplicationContext(),
						R.layout.lenearlayout_cache_item, null);
				ImageView iv_need_clear_app_icon = (ImageView) view
						.findViewById(R.id.iv_need_clear_app_icon);
				TextView tv_need_clear_app_name = (TextView) view
						.findViewById(R.id.tv_need_clear_app_name);
				TextView tv_cache_size = (TextView) view
						.findViewById(R.id.tv_cache_size);

				iv_need_clear_app_icon.setBackgroundDrawable(cacheAppInfo.icon);
				tv_need_clear_app_name.setText(cacheAppInfo.name);
				String cache = Formatter.formatFileSize(
						getApplicationContext(), cacheAppInfo.cacheSize);
				tv_cache_size.setText(cache);
				ll_cache_app.addView(view, 0);
				ImageView iv_clear = (ImageView) view
						.findViewById(R.id.iv_clear);
				iv_clear.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						/*
						 * public abstract void
						 * deleteApplicationCacheFiles(String packageName,
						 * IPackageDataObserver observer);
						 * 清除App缓存的方法但是只有系统级的应用才能用该方法的权限
						 * 
						 * try { Class<?> clazz =
						 * Class.forName("android.content.pm.PackageManager");
						 * Method method =
						 * clazz.getMethod("deleteApplicationCacheFiles",
						 * String.class, IPackageDataObserver.class);
						 * method.invoke(mPm, cacheAppInfo.packageName, new
						 * IPackageDataObserver.Stub() {
						 * 
						 * @Override public void onRemoveCompleted(String
						 * packageName, boolean succeeded) throws
						 * RemoteException { // 清除缓存后调用的方法
						 * 
						 * <uses-permission
						 * android:name="android.permission.DELETE_CACHE_FILES"
						 * /> 只有系统能用的权限 } }); } catch (Exception e) {
						 * e.printStackTrace(); }
						 */

						// 09-18 17:53:49.230: I/ActivityManager(556):
						// START
						// {act=android.settings.APPLICATION_DETAILS_SETTINGS
						// dat=package:com.qihoo.appstore
						// flg=0x10000000
						// cmp=com.android.settings/.applications.InstalledAppDetails
						// u=0} from pid 757

						Intent intent = new Intent(
								"android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:"
								+ cacheAppInfo.packageName));
						startActivity(intent);
					}
				});
				break;
			case SCANNING_CAHCESIZE_APP:
				PackageInfo packageInfo = (PackageInfo) msg.obj;
				try {
					String name = mPm
							.getApplicationInfo(packageInfo.packageName, 0)
							.loadLabel(mPm).toString();
					tv_scanning_app.setText(name);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				break;
			case SCANNING_FINISH:
				tv_scanning_app.setText("扫描完成");
				break;
			case CLEAR_ALL_CACHE_FINISH:
				ll_cache_app.removeAllViews();
				break;
			}
		};
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clear);
		initUI();
		initData();
	}

	private void initData() {
		new Thread() {

			private int index = 0;

			public void run() {
				mPm = getPackageManager();
				List<PackageInfo> installedPackages = mPm
						.getInstalledPackages(0);
				pb_cache.setMax(installedPackages.size());
				for (PackageInfo packageInfo : installedPackages) {
					String packageName = packageInfo.packageName;
					scanHaveCacheApps(packageName);

					Message msg = Message.obtain();
					msg.what = SCANNING_CAHCESIZE_APP;
					msg.obj = packageInfo;
					mHandler.sendMessage(msg);

					try {
						Thread.sleep(100 + new Random().nextInt(50));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					index++;
					pb_cache.setProgress(index);

				}

				Message msg = Message.obtain();
				msg.what = SCANNING_FINISH;
				mHandler.sendMessage(msg);

			};
		}.start();
	}

	class CacheAppInfo {
		Drawable icon;
		String packageName;
		String name;
		long cacheSize;
	}

	protected void scanHaveCacheApps(String packageName) {

		final android.content.pm.IPackageStatsObserver.Stub mStatsObserver = new android.content.pm.IPackageStatsObserver.Stub() {

			public void onGetStatsCompleted(PackageStats stats,
					boolean succeeded) {
				long cacheSize = stats.cacheSize;
				if (cacheSize > 0) {
					Message msg = Message.obtain();
					msg.what = NEED_CLEAR_CAHCESIZE_APP;
					CacheAppInfo cacheAppInfo = null;
					try {
						cacheAppInfo = new CacheAppInfo();
						cacheAppInfo.packageName = stats.packageName;
						cacheAppInfo.name = mPm
								.getApplicationInfo(stats.packageName, 0)
								.loadLabel(mPm).toString();
						cacheAppInfo.icon = mPm.getApplicationInfo(
								stats.packageName, 0).loadIcon(mPm);
						cacheAppInfo.cacheSize = cacheSize;
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}

					msg.obj = cacheAppInfo;
					mHandler.sendMessage(msg);
				}

			}
		};
		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			Method method = clazz.getMethod("getPackageSizeInfo", String.class,
					android.content.pm.IPackageStatsObserver.class);
			method.invoke(mPm, packageName, mStatsObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initUI() {
		bt_cache_clear = (Button) findViewById(R.id.bt_cache_clear);
		pb_cache = (ProgressBar) findViewById(R.id.pb_cache);
		tv_scanning_app = (TextView) findViewById(R.id.tv_scanning_app);
		ll_cache_app = (LinearLayout) findViewById(R.id.ll_cache_app);

		/*
		 * public void freeStorageAndNotify(long freeStorageSize,
		 * IPackageDataObserver observer) { freeStorageAndNotify(null,
		 * freeStorageSize, observer); } packageManager中清除所有缓存的方法 用反射调用
		 */
		bt_cache_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Class<?> clazz = Class
							.forName("android.content.pm.PackageManager");

					Method method = clazz.getMethod("freeStorageAndNotify",
							long.class, IPackageDataObserver.class);
					method.invoke(mPm, Long.MAX_VALUE,
							new IPackageDataObserver.Stub() {

								@Override
								public void onRemoveCompleted(
										String packageName, boolean succeeded)
										throws RemoteException {
									// 清除缓存后调用的方法

									Message msg = Message.obtain();
									msg.what = CLEAR_ALL_CACHE_FINISH;
									mHandler.sendMessage(msg);

								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
