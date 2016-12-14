package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.SpUtil;
import com.wxf.mobilesafe.view.SettingView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Setup2Activity extends BaseSetupActivity {
	private SettingView setup_bount;
	private String number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup2_activity);
		setup_bount = (SettingView) findViewById(R.id.setup_bount);
		number = SpUtil.getPsd(getApplicationContext(), ConstaxtValuse.NUMBER,
				"");
		if (TextUtils.isEmpty(number)) {
			setup_bount.setCheck(false);
		} else {
			setup_bount.setCheck(true);
		}
		setup_bount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean check = setup_bount.isCheck();
				if (!check) {
					setup_bount.setCheck(!check);
					TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String simNumber = manager.getSimSerialNumber();
					SpUtil.setPsd(getApplicationContext(),
							ConstaxtValuse.NUMBER, simNumber);
				} else {
					setup_bount.setCheck(!check);
					SpUtil.romove(getApplicationContext(),
							ConstaxtValuse.NUMBER);
				}

			}
		});
	}

	@Override
	public void showNext() {
		String number2 = SpUtil.getPsd(getApplicationContext(),
				ConstaxtValuse.NUMBER, "");

		if (TextUtils.isEmpty(number2)) {
			Toast.makeText(getApplicationContext(), "请绑定SIM卡", 0).show();
		} else {
			Intent intent = new Intent(this, Setup3Activity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}
}
