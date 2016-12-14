package com.wxf.mobilesafe.activity;

import java.util.List;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.R.id;
import com.wxf.mobilesafe.db.BlackNumberHelper;
import com.wxf.mobilesafe.db.bean.BlackNumberBean;
import com.wxf.mobilesafe.db.dao.BlcakNumberDAO;
import com.wxf.mobilesafe.utils.ToastUtil;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DialerFilter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class BlackNumberActivity extends Activity {
	private ListView lv_blacknumber;
	private Button bt_add;
	private List<BlackNumberBean> mBlackNumberList;
	private BlcakNumberDAO db;
	private int count;
	private BlcakNumberAdapter blcakNumberAdapter;
	private boolean isload = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (blcakNumberAdapter == null) {
				blcakNumberAdapter = new BlcakNumberAdapter();
				lv_blacknumber.setAdapter(blcakNumberAdapter);
			} else {
				blcakNumberAdapter.notifyDataSetChanged();
			}
		};
	};
	private EditText et_phone;
	private RadioGroup rg_group;
	private Button bt_submit;
	private Button bt_cencal;
	protected int mode = 1;
	protected String tag="BlackNumberActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);
		initUI();
		initData();
	}

	private void initData() {
		queryAllBlackNumebr();

	}

	private void queryAllBlackNumebr() {
		new Thread() {

			public void run() {
				db = BlcakNumberDAO.getInstance(getApplicationContext());
				mBlackNumberList = db.find(0);
				count = db.getCount();
				Log.i(tag, count+"");
				handler.sendEmptyMessage(0);
			};

		}.start();
	}

	class BlcakNumberAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mBlackNumberList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBlackNumberList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.blacknumbet_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				viewHolder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				viewHolder.iv_del = (ImageView) convertView
						.findViewById(R.id.iv_del);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.tv_phone.setText(mBlackNumberList.get(position).phone);
			String mode = mBlackNumberList.get(position).mode;
			switch (Integer.parseInt(mode)) {
			case 1:
				viewHolder.tv_mode.setText("拦截短信");
				break;
			case 2:
				viewHolder.tv_mode.setText("拦截电话");
				break;
			case 3:
				viewHolder.tv_mode.setText("拦截所有");
				break;

			}
			viewHolder.iv_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					db.delete(mBlackNumberList.get(position).phone);
					mBlackNumberList.remove(position);
					if (blcakNumberAdapter != null) {
						blcakNumberAdapter.notifyDataSetChanged();
					}
				}
			});
			return convertView;
		}

	}

	static class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_del;
	}

	private void initUI() {
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		bt_add = (Button) findViewById(R.id.bt_add);
		bt_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (mBlackNumberList != null) {
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& lv_blacknumber.getLastVisiblePosition() >= mBlackNumberList
									.size() - 1 && !isload) {
						if (count > mBlackNumberList.size()) {
							new Thread() {

								public void run() {
									db = BlcakNumberDAO
											.getInstance(getApplicationContext());
									  List<BlackNumberBean> find = db.find(mBlackNumberList
											.size());
									mBlackNumberList.addAll(find);
									handler.sendEmptyMessage(0);
								};

							}.start();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	protected void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final View view = View.inflate(getApplicationContext(),
				R.layout.dialog_blacknumber, null);
		final AlertDialog dialog = builder.create();
		et_phone = (EditText) view.findViewById(R.id.et_phone);
		rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		bt_submit = (Button) view.findViewById(R.id.bt_submit);
		bt_cencal = (Button) view.findViewById(R.id.bt_cencal);
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					mode = 1;
					break;
				case R.id.rb_phone:
					mode = 2;
					break;
				case R.id.rb_all:
					mode = 3;
					break;

				}

			}

		});

		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString();
				if (TextUtils.isEmpty(phone)) {
					ToastUtil.toast(getApplicationContext(), "请输入号码");
				} else {
					BlackNumberBean numberBean = new BlackNumberBean();
					numberBean.phone = phone;
					numberBean.mode = mode + "";
					db.insert(phone, mode + "");
					mBlackNumberList.add(0, numberBean);
					if (blcakNumberAdapter != null) {
						blcakNumberAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();
				}

			}
		});

		bt_cencal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setView(view);
		dialog.show();

	}

}
