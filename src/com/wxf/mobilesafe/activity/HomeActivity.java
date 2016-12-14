package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.engine.AddressDAO;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.Md5Util;
import com.wxf.mobilesafe.utils.SpUtil;
import com.wxf.mobilesafe.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class HomeActivity extends Activity {
	private GridView gv_home;
	private String[] mTitleStrs;
	private int[] mConInts;
	private EditText et_confirm_psd;
	private EditText et_input_psd;
	public static DevicePolicyManager mDPM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		initUI();
		initDate();

	}

	private void initDate() {
		mTitleStrs = new String[] { "手机防盗", "通信卫士", "软件管理", "进程保护", "流量统计",
				"手机杀毒", "缓存清理", "高级工具", "设置中心" };
		mConInts = new int[] { R.drawable.home_safe,
				R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager,
				R.drawable.home_trojan, R.drawable.home_sysoptimize,
				R.drawable.home_tools, R.drawable.home_settings };

		gv_home.setAdapter(new Myadpter());
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					showDialog();
					break;
				case 1:

					startActivity(new Intent(getApplicationContext(),
							BlackNumberActivity.class));
					break;
				case 2:

					startActivity(new Intent(getApplicationContext(),
							AppManager.class));
					break;
				case 3:
					
					startActivity(new Intent(getApplicationContext(),
						ProcessManager.class));
					break;
				case 4:
					
					startActivity(new Intent(getApplicationContext(),
							TrafficActivity.class));
					break;
	             case 5:
					
					startActivity(new Intent(getApplicationContext(),
					KillVirusActivity.class));
					break;
	             case 6:
	            	 
	            	/* startActivity(new Intent(getApplicationContext(),
	            			CacheClearActivity.class));*/
	            	 startActivity(new Intent(getApplicationContext(),
	            			BaseCacheClearActivity.class));
	            	 break;
				case 7:
					
					queryPhoneAddress();
					break;
				case 8:
					Intent intent = new Intent(getApplicationContext(),
							SettingActivity.class);
					startActivity(intent);
					break;

				}

			}
		});
	}

	protected void queryPhoneAddress() {
		Intent intent = new Intent(this, AtoolsActivity.class);
		startActivity(intent);
	}

	protected void showDialog() {
		// AlertDialog.Builder builder=new Builder(this);
		View view = View.inflate(getApplicationContext(),
				R.layout.set_psd_view, null);
		et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
		et_input_psd = (EditText) view.findViewById(R.id.et_input_psd);
		Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		String psd = SpUtil.getPsd(getApplicationContext(),
				ConstaxtValuse.MOBILE_SAFE_PSD, "");
		if (TextUtils.isEmpty(psd)) {
			AlertDialog.Builder builder = new Builder(this);
			final AlertDialog dialog = builder.create();
			dialog.setView(view);
			dialog.show();

			bt_confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String inputPsd = et_input_psd.getText().toString();
					String confirmPsd = et_confirm_psd.getText().toString();
					if (!(TextUtils.isEmpty(inputPsd))
							&& !(TextUtils.isEmpty(confirmPsd))) {
						if (inputPsd.equals(confirmPsd)) {
							Intent intent = new Intent(getApplicationContext(),
									SetupOver.class);
							startActivity(intent);
							dialog.dismiss();
							SpUtil.setPsd(getApplicationContext(),
									ConstaxtValuse.MOBILE_SAFE_PSD,
									Md5Util.encoder(inputPsd));
						} else {

							ToastUtil.toast(getApplicationContext(), "密码输入不同");
						}

					} else {

						ToastUtil.toast(getApplicationContext(), "密码不能为空");
					}

				}
			});
			bt_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();

				}
			});
		} else {
			// 显示确认密码框
			showConfirm();
		}

	}

	private void showConfirm() {
		View view = View.inflate(getApplicationContext(),
				R.layout.confirm_psd_view, null);
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		dialog.setView(view);
		dialog.show();

		et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
		Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		bt_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String confirmPsd = et_confirm_psd.getText().toString();
				String psd = SpUtil.getPsd(getApplicationContext(),
						ConstaxtValuse.MOBILE_SAFE_PSD, "");
				if (!(TextUtils.isEmpty(confirmPsd))) {
					if (Md5Util.encoder(confirmPsd).equals(psd)) {
						Intent intent = new Intent(getApplicationContext(),
								SetupOver.class);
						startActivity(intent);
						dialog.dismiss();
					} else {

						ToastUtil.toast(getApplicationContext(), "密码输入错误");
					}

				} else {

					ToastUtil.toast(getApplicationContext(), "密码不能为空");
				}

			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
	}

	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);

	}

	class Myadpter extends BaseAdapter {

		@Override
		public int getCount() {

			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {

			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.gridview_item, null);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

			ImageView iv_con = (ImageView) view.findViewById(R.id.iv_con);
			tv_item.setText(mTitleStrs[position]);
			iv_con.setBackgroundResource(mConInts[position]);
			return view;
		}

	}
}
