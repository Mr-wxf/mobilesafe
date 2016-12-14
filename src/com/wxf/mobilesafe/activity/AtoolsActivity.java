package com.wxf.mobilesafe.activity;

import java.io.File;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.engine.AddressDAO;
import com.wxf.mobilesafe.engine.SmsBackupEngine;
import com.wxf.mobilesafe.engine.SmsBackupEngine.CallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AtoolsActivity extends Activity {

	private TextView tv_query_phone_number;
	private TextView tv_sms_backup;
	private ProgressBar pb;
	private TextView tv_common_number;
	private TextView tv_app_lock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_altool);
		initUI();
		initSmsBackup();
		initCommonNumber();
		initAppLock();
	}

	private void initAppLock() {
		tv_app_lock = (TextView) findViewById(R.id.tv_app_lock);
		tv_app_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
             startActivity(new Intent(getApplicationContext(),AppLockActivity.class));				
			}
		});
	}

	private void initCommonNumber() {
          tv_common_number = (TextView) findViewById(R.id.tv_common_number);
          tv_common_number.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(), CommonNumberActivity.class);
               startActivity(intent);
			}
		});
	}

	private void initSmsBackup() {
		tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		pb = (ProgressBar) findViewById(R.id.pb);
		tv_sms_backup.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				showSmsDialog();

			}
		});
	}

	protected void showSmsDialog() {
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("短信备份");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
		new Thread() {
			public void run() {
				String path = Environment.getExternalStorageDirectory()
						.getAbsoluteFile() + File.separator + "sms.xml";
				SmsBackupEngine.backupSms(getApplicationContext(), path, new CallBack() {
					
					@Override
					public void setProgress(int index) {
						pb.setProgress(index);
						progressDialog.setProgress(index);
					}
					
					@Override
					public void setMax(int max) {
 						pb.setMax(max);
 						progressDialog.setMax(max);
					}
				});
				
				progressDialog.dismiss();
			};
		}.start();

	}

	private void initUI() {
		tv_query_phone_number = (TextView) findViewById(R.id.tv_query_phone_number);
		tv_query_phone_number.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						QueryPhoneNumberActivity.class);
				startActivity(intent);

			}
		});
	}
}
