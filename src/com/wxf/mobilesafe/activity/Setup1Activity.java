package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup1Activity extends BaseSetupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup1_activity);

	}

	@Override
	public void showNext() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);

		finish();
	}

	@Override
	public void showPre() {

	}
}
