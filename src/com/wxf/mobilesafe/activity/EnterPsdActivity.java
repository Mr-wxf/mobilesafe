package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EnterPsdActivity extends Activity {
	private TextView tv_app_name;
	private ImageView iv_app_icon;
	private EditText et_psd;
	private Button bt_submit;
	private String mPackageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_psd);
		mPackageName = getIntent().getStringExtra("packageName");
		initUI();
		initData();

	}

	private void initData() {
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(
					mPackageName, 0);
			Drawable icon = applicationInfo.loadIcon(pm);
			iv_app_icon.setBackgroundDrawable(icon);
			tv_app_name.setText(applicationInfo.loadLabel(pm).toString());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String psd = et_psd.getText().toString().trim();
				if (!TextUtils.isEmpty(psd)) {
					if (psd.equals("123")) {
						Intent intent = new Intent("android.intent.action.SKIP");
						intent.putExtra("packageName", mPackageName);
						sendBroadcast(intent);
						finish();
					} else {
						ToastUtil.toast(getApplicationContext(), "密码错误");
					}
				} else {
					ToastUtil.toast(getApplicationContext(), "请输入密码");
				}
			}
		});
	}

	private void initUI() {
		tv_app_name = (TextView) findViewById(R.id.tv_app_name);
		iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
		et_psd = (EditText) findViewById(R.id.et_psd);
		bt_submit = (Button) findViewById(R.id.bt_submit);
	}

	@Override
	public void onBackPressed() {

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		super.onBackPressed();
	}
}
