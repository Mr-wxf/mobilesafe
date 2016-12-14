package com.wxf.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.db.bean.ProcessInfo;
import com.wxf.mobilesafe.engine.ProcessinfoEngine;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.SpUtil;
import com.wxf.mobilesafe.utils.ToastUtil;

public class ProcessManager extends Activity implements OnClickListener {
	private TextView tv_process_count;
	private TextView tv_memory;
	private ListView lv_process;
	private Button bt_all_select;
	private Button bt_reverse_select;
	private Button bt_setting;
	private Button bt_remove;

	private ArrayList<ProcessInfo> isSystemProcess;
	private ArrayList<ProcessInfo> isUserProcess;
	private MyAdapter myAdapter;
	private TextView tv_des;

	private ProcessInfo processInfo;
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			myAdapter = new MyAdapter();
			lv_process.setAdapter(myAdapter);
			tv_des.setText("用户进程(" + isUserProcess.size() + ")");
		};
	};
	private int mProcessCount;
	private long mAvailableMemory;
	private String allMemorySize;
	private String strRelase;
	private String fileSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);
		initUI();
		initData();
		initListData();
	}

	class MyAdapter extends BaseAdapter {

		private ViewHolder viewHolder;
		private ViewTitleHolder viewTitleHolder;

		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == isUserProcess.size() + 1) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getCount() {
			boolean systemProcess = SpUtil.getBoolean(getApplicationContext(),
					ConstaxtValuse.SHOW_SYSTEM_PROCESS, false);
			if (systemProcess) {
				return isUserProcess.size()+ 1;
			} else {
				return isSystemProcess.size() + isUserProcess.size() + 2;

			}
		}

		@Override
		public ProcessInfo getItem(int position) {
			if (position == 0 || position == isUserProcess.size() + 1) {
				return null;
			} else {
				if (position < isUserProcess.size() + 1) {
					return isUserProcess.get(position - 1);
				} else {
					return isSystemProcess.get(position - isUserProcess.size()
							- 2);
				}
			}

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (type == 0) {
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.appinfolist_item_title, null);

					viewTitleHolder = new ViewTitleHolder();
					viewTitleHolder.tv_title = (TextView) convertView
							.findViewById(R.id.tv_title);
					convertView.setTag(viewTitleHolder);
				} else {
					viewTitleHolder = (ViewTitleHolder) convertView.getTag();
				}
				if (position == 0) {
					viewTitleHolder.tv_title.setText("用户进程" + "("
							+ isUserProcess.size() + ")");
				} else {
					viewTitleHolder.tv_title.setText("系统进程" + "("
							+ isSystemProcess.size() + ")");
				}

				return convertView;
			} else {
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.processinfolist_item, null);

					viewHolder = new ViewHolder();
					viewHolder.cb_check = (CheckBox) convertView
							.findViewById(R.id.cb_check);
					viewHolder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon2);
					viewHolder.tv_appname = (TextView) convertView
							.findViewById(R.id.tv_appname2);
					viewHolder.tv_memory = (TextView) convertView
							.findViewById(R.id.tv_memory2);
					viewHolder.cb_check = (CheckBox) convertView
							.findViewById(R.id.cb_check);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				viewHolder.iv_icon
						.setBackgroundDrawable(getItem(position).icon);
				viewHolder.tv_appname.setText(getItem(position).name);
				long memSize = getItem(position).memSize;
				String strSize = Formatter.formatFileSize(
						getApplicationContext(), memSize);
				viewHolder.tv_memory.setText(strSize);

				if (getItem(position).packageName.equals(getPackageName())) {
					viewHolder.cb_check.setVisibility(View.GONE);
				} else {
					viewHolder.cb_check.setVisibility(View.VISIBLE);
				}
				viewHolder.cb_check.setChecked(getItem(position).isCheck);
				return convertView;
			}

		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_appname;
		TextView tv_memory;
		CheckBox cb_check;
	}

	static class ViewTitleHolder {

		TextView tv_title;
	}

	private void initListData() {
		new Thread() {

			public void run() {
				isSystemProcess = new ArrayList<ProcessInfo>();
				isUserProcess = new ArrayList<ProcessInfo>();
				List<ProcessInfo> runningProcess = ProcessinfoEngine
						.getRunningProcess(getApplicationContext());
				for (ProcessInfo processInfo : runningProcess) {
					if (processInfo.isSystem) {
						isSystemProcess.add(processInfo);
					} else {
						isUserProcess.add(processInfo);
					}
				}
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initData() {
		mProcessCount = ProcessinfoEngine.getProcessCount(this);
		tv_process_count.setText("进程总数：" + mProcessCount);
		mAvailableMemory = ProcessinfoEngine.getAvailableMemory(this);
		String availableMemorySize = Formatter.formatFileSize(
				getApplicationContext(), mAvailableMemory);

		long allMemory = ProcessinfoEngine.getAllMemory();
		allMemorySize = Formatter.formatFileSize(getApplicationContext(),
				allMemory);

		tv_memory.setText("剩余/总共:" + availableMemorySize + "/" + allMemorySize);
	}

	private void initUI() {

		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory = (TextView) findViewById(R.id.tv_memory);
		lv_process = (ListView) findViewById(R.id.lv_process);

		tv_des = (TextView) findViewById(R.id.tv_des);
		bt_all_select = (Button) findViewById(R.id.bt_all_select);
		bt_reverse_select = (Button) findViewById(R.id.bt_reverse_select);
		bt_remove = (Button) findViewById(R.id.bt_remove);
		bt_setting = (Button) findViewById(R.id.bt_setting);
		lv_process.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (isSystemProcess != null && isUserProcess != null) {
					if (firstVisibleItem > isUserProcess.size() + 1) {
						tv_des.setText("系统进程(" + isSystemProcess.size() + ")");
					} else {
						tv_des.setText("用户进程(" + isUserProcess.size() + ")");
					}
				}

			}
		});
		lv_process.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == isUserProcess.size() + 1) {
					return;
				} else {
					if (position < isUserProcess.size() + 1) {
						processInfo = isUserProcess.get(position - 1);
					} else {
						processInfo = isSystemProcess.get(position
								- isUserProcess.size() - 2);
					}
					if (processInfo != null
							&& !processInfo.packageName
									.equals(getPackageName())) {
						processInfo.isCheck = !processInfo.isCheck;
						CheckBox cb_check = (CheckBox) view
								.findViewById(R.id.cb_check);
						cb_check.setChecked(processInfo.isCheck);

					}
				}
			}
		});

		bt_all_select.setOnClickListener(this);
		bt_reverse_select.setOnClickListener(this);
		bt_remove.setOnClickListener(this);
		bt_setting.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_all_select:
			selectAll();
			break;
		case R.id.bt_reverse_select:
			selectReverse();
			break;
		case R.id.bt_remove:
			removeProcess();
			break;
		case R.id.bt_setting:
			setting();
			break;

		}
	}

	private void setting() {
		Intent intent = new Intent(this, ProcessSettingActivity.class);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (myAdapter != null) {
			myAdapter.notifyDataSetChanged();
		}
	}

	private void removeProcess() {
		long totalRealse = 0;
		List<ProcessInfo> killProcess = new ArrayList<ProcessInfo>();
		for (ProcessInfo processInfo : isUserProcess) {
			if (processInfo.packageName.equals(getPackageName())) {
				continue;
			}
			if (processInfo.isCheck == true) {
				killProcess.add(processInfo);
			}
		}
		for (ProcessInfo processInfo : isSystemProcess) {
			if (processInfo.isCheck == true) {
				killProcess.add(processInfo);
			}
		}
		for (ProcessInfo processInfo : killProcess) {
			if (isUserProcess.contains(processInfo)) {
				isUserProcess.remove(processInfo);
			}

			if (isSystemProcess.contains(processInfo)) {
				isSystemProcess.remove(processInfo);
			}
			ProcessinfoEngine.killProcess(this, processInfo);
			mAvailableMemory = mAvailableMemory + processInfo.memSize;

			totalRealse = totalRealse + processInfo.memSize;
		}
		strRelase = Formatter.formatFileSize(getApplicationContext(),
				mAvailableMemory);

		mProcessCount = mProcessCount - killProcess.size();
		tv_memory.setText("剩余/总共：" + strRelase + "/" + allMemorySize);
		tv_process_count.setText("进程总数：" + mProcessCount);
		String formatFileSize = Formatter.formatFileSize(
				getApplicationContext(), totalRealse);
		ToastUtil.toast(getApplicationContext(), "杀死了" + killProcess.size()
				+ "个进程,释放了" + formatFileSize + "空间");
		if (myAdapter != null) {
			myAdapter.notifyDataSetChanged();

		}
	}

	private void selectReverse() {
		for (ProcessInfo processInfo : isUserProcess) {
			if (processInfo.packageName.equals(getPackageName())) {
				continue;
			}
			processInfo.isCheck = !processInfo.isCheck;
		}
		for (ProcessInfo processInfo : isSystemProcess) {

			processInfo.isCheck = !processInfo.isCheck;
		}
		if (myAdapter != null) {
			myAdapter.notifyDataSetChanged();
		}
	}

	private void selectAll() {
		for (ProcessInfo processInfo : isUserProcess) {
			if (processInfo.packageName.equals(getPackageName())) {
				continue;
			}
			processInfo.isCheck = true;
		}
		for (ProcessInfo processInfo : isSystemProcess) {

			processInfo.isCheck = true;
		}
		if (myAdapter != null) {
			myAdapter.notifyDataSetChanged();
		}
	}
}
