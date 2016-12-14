package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SetupOver extends Activity {
	private TextView tv_resert;
	private TextView tv_security_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean SETUP_OVER = SpUtil.getBoolean(getApplicationContext(),
				ConstaxtValuse.SETUP_OVER, false);
		if (SETUP_OVER) {
			setContentView(R.layout.setup_over);
			initUI();
		} else {
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}

		
	}

	private void initUI() {
		tv_resert = (TextView) findViewById(R.id.tv_resert);
		tv_security_number = (TextView) findViewById(R.id.tv_security_number);
		String number = SpUtil.getPsd(getApplicationContext(),
				ConstaxtValuse.CONTACT_NUMBER, "");
		tv_security_number.setText(number);
		tv_resert.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						Setup1Activity.class);
				startActivity(intent);

				finish();
			}
		});
	}
}
