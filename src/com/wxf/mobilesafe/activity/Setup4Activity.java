package com.wxf.mobilesafe.activity;

import java.io.Console;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.SpUtil;
import com.wxf.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_success;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup4_activity);
		initUI();

	}

	private void initUI() {
		cb_success = (CheckBox) findViewById(R.id.cb_success);
		boolean open_success = SpUtil.getBoolean(getApplicationContext(),
				ConstaxtValuse.OPEN_SUCCESS, false);
		cb_success.setChecked(open_success);
		cb_success.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				cb_success.setChecked(isChecked);
				SpUtil.setBoolean(getApplicationContext(),
						ConstaxtValuse.OPEN_SUCCESS, isChecked);

			}
		});

	}

	@Override
	public void showNext() {
		boolean open_success = SpUtil.getBoolean(getApplicationContext(),
				ConstaxtValuse.OPEN_SUCCESS, false);
		if (open_success) {
			Intent intent = new Intent(this, SetupOver.class);
			startActivity(intent);
			SpUtil.setBoolean(getApplicationContext(),
					ConstaxtValuse.SETUP_OVER, open_success);
			finish();
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			ToastUtil.toast(getApplicationContext(), "请开启防盗保护");
		}
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
	}

}
