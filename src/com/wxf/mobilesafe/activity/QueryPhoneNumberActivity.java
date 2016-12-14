package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.engine.AddressDAO;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryPhoneNumberActivity extends Activity {

	protected static final String tag = "QueryPhoneNumberActivity";
	private TextView tv_phone_address;
	private EditText et_phone;
	private Button bt_query;
	private static String address = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv_phone_address.setText(address);

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_queryphonenumber);
		initUI();
	}

	private void initUI() {

		et_phone = (EditText) findViewById(R.id.et_phone);
		bt_query = (Button) findViewById(R.id.bt_query);
		tv_phone_address = (TextView) findViewById(R.id.tv_phone_address);

		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String number = et_phone.getText().toString();
				queryAddress(number);

			}
		});
		bt_query.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String number = et_phone.getText().toString();
				if (!TextUtils.isEmpty(number)) {
					queryAddress(number);
				} else {
					Animation shake = AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.shake);
					et_phone.startAnimation(shake);
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(1000);
				}
			}
		});

	}

	protected void queryAddress(final String number) {

		new Thread() {

			public void run() {
				address = AddressDAO.queryAddress(number);
				Log.i(tag, address);
				handler.sendEmptyMessage(0);
			};

		}.start();

	}

}
