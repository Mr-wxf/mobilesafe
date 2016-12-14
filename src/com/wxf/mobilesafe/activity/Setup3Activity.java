package com.wxf.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.SpUtil;
import com.wxf.mobilesafe.utils.ToastUtil;

public class Setup3Activity extends BaseSetupActivity {
	private static final String tag = "Setup3Activity";
	private EditText et_contact_number;
	private Button bt_contact;
	private String number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup3_activity);

		initUI();

	}

	private void initUI() {
		et_contact_number = (EditText) findViewById(R.id.et_contact_number);
		bt_contact = (Button) findViewById(R.id.bt_contact);

		String number = SpUtil.getPsd(getApplicationContext(),
				ConstaxtValuse.CONTACT_NUMBER, "");

		et_contact_number.setText(number);

		bt_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),
						ContactListActivity.class);
				startActivityForResult(intent, 0);

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String phone = data.getStringExtra("phone");
			phone = phone.replace("-", "").replace(" ", "").trim();
			et_contact_number.setText(phone);

			SpUtil.setPsd(getApplicationContext(),
					ConstaxtValuse.CONTACT_NUMBER, phone);

			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void showNext() {
		number = et_contact_number.getText().toString().trim();
		if (!TextUtils.isEmpty(number)) {
			Intent intent = new Intent(this, Setup4Activity.class);
			startActivity(intent);
			SpUtil.setPsd(getApplicationContext(),
					ConstaxtValuse.CONTACT_NUMBER, number);
			finish();
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			ToastUtil.toast(getApplicationContext(), "请输入手机号码");
		}
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
	}
}
