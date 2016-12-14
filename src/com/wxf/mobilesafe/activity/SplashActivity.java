package com.wxf.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.SpUtil;
import com.wxf.mobilesafe.utils.StreamUtil;
import com.wxf.mobilesafe.utils.ToastUtil;

public class SplashActivity extends Activity {

	protected static final int URL_ERROR = 100;
	protected static final int INTHOME = 101;
	protected static final int UPDATE = 102;
	protected static final int IO_ERROR = 103;
	protected static final int JSON_ERROR = 104;
	private TextView tv_version_name;
	private int mVersionCode;
	private static String tag = "SplashActivity";
	private String versionDes;
	private String downloadUrl;
	private RelativeLayout rl_root;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE:
				// 弹出对话框更新
				updateAlter();
				break;
			case INTHOME:
				// 跳转到HOme界面
				insertHome();
				break;
			case URL_ERROR:
				ToastUtil.toast(getApplicationContext(), "URL异常");
				insertHome();
				break;
			case IO_ERROR:
				ToastUtil.toast(getApplicationContext(), "读取异常");
				insertHome();
				break;
			case JSON_ERROR:
				ToastUtil.toast(getApplicationContext(), "json异常");
				insertHome();
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		initUI();
		initData();
		initAnimation();
		initDB();
	
		if(!SpUtil.getBoolean(getApplicationContext(),ConstaxtValuse.ISBOOLEAN_SHORTCUT, false)){
		initShortCut();
		}
	}

	/**
	 * 生成快捷方式
	 */
	private void initShortCut() {
    Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安全卫士");
    Intent shortCutIntent = new Intent("android.intent.action.HOME");
    shortCutIntent.addCategory("android.intent.category.DEFAULT");
    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
    sendBroadcast(intent);
    SpUtil.setBoolean(getApplicationContext(), ConstaxtValuse.ISBOOLEAN_SHORTCUT, true);
	}

	private void initDB() {
		initAddressDB("address.db");
		initAddressDB("commonnum.db");
	    initAddressDB("antivirus.db");
	}

	private void initAddressDB(String dbName) {
		File file = new File(getFilesDir(), dbName);
		if (file.exists()) {
			return;
		}
		InputStream stream = null;
		FileOutputStream fos = null;
		try {
			stream = getAssets().open(dbName);
			fos = new FileOutputStream(file);
			byte[] bt = new byte[1024];
			int temp = -1;
			while ((temp = stream.read(bt)) != -1) {
				fos.write(bt, 0, temp);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void initAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		rl_root.startAnimation(alphaAnimation);
	}

	protected void updateAlter() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("更新提示");
		builder.setMessage(versionDes);
		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 点击开始下载
				downloadApk();
			}
		});
		builder.setNeutralButton("稍后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				insertHome();

			}
		});
		builder.show();
	}

	protected void downloadApk() {
		// 下载
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + "mobilesafe";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.download(downloadUrl, path, new RequestCallBack<File>() {

				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					Log.i(tag, "下载成功");
					File file = responseInfo.result;
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setDataAndType(Uri.fromFile(file),
							"application/vnd.android.package-archive");
					startActivityForResult(intent, 0);
				}

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Log.i(tag, "下载失败");

				}

				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					Log.i(tag, "正在下载");
					super.onLoading(total, current, isUploading);
				}

				@Override
				public void onStart() {
					Log.i(tag, "开始下载");
					super.onStart();
				}
			});
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		insertHome();
	}

	protected void insertHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);

		finish();
	}

	private void initData() {
		tv_version_name.setText("版本号：" + getVersionName());
		mVersionCode = getVersionCode();
		boolean isCheck = SpUtil.getBoolean(getApplicationContext(),
				ConstaxtValuse.CHECK_UPDATE, false);
		if (isCheck) {
			checkVersion();
		} else {
			handler.sendEmptyMessageDelayed(INTHOME, 4000);
		}

	}

	private void checkVersion() {

		new Thread() {

			public void run() {
				long startTime = System.currentTimeMillis();
				Message msg = Message.obtain();
				try {

					URL url = new URL("http://10.7.62.51:8080/update.json");
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setReadTimeout(2000);
					connection.setConnectTimeout(2000);
					int code = connection.getResponseCode();
					if (code == 200) {
						InputStream is = connection.getInputStream();
						String json = StreamUtil.streamToString(is);
						Log.i(tag, json);
						JSONObject jsonObject = new JSONObject(json);
						String versionName = jsonObject
								.getString("versionName");
						String versionCode = jsonObject
								.getString("versionCode");
						versionDes = jsonObject.getString("versionDes");
						downloadUrl = jsonObject.getString("downloadUrl");
						Log.i(tag, versionName);
						Log.i(tag, versionCode);
						Log.i(tag, versionDes);
						Log.i(tag, downloadUrl);
						if (mVersionCode < Integer.parseInt(versionCode)) {
							// 更新
							msg.what = UPDATE;
						} else {
							// 跳转到home界面
							msg.what = INTHOME;
						}

					}
				} catch (MalformedURLException e) {
					msg.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = IO_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();
					long Time = endTime - startTime;
					if (Time < 2000) {
						try {
							Thread.sleep(2000 - Time);
						} catch (Exception e) {

							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
				}

			};

		}.start();
	}

	private int getVersionCode() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		return 0;

	}

	private String getVersionName() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		return null;
	}

	private void initUI() {
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}

}
