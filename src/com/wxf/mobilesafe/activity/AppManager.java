package com.wxf.mobilesafe.activity;

import java.util.ArrayList;

import javax.crypto.spec.PSource;

import org.w3c.dom.Text;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.db.bean.AppInfo;
import com.wxf.mobilesafe.engine.AppInfoEngine;
import com.wxf.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.LiveFolders;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManager extends Activity implements OnClickListener {
	private TextView tv_memory;
	private TextView tv_sd_card;
	private ArrayList<AppInfo> appInfoList;
	private AppInfo mAppInfo;

	private ArrayList<AppInfo> isSystemApp;
	private ArrayList<AppInfo> isUserApp;
	private Handler handler = new Handler() {
		private MyAdapter myAdapter;

		public void handleMessage(android.os.Message msg) {

			myAdapter = new MyAdapter();
			lv_app_manager.setAdapter(myAdapter);
			tv_des.setText("用户应用(" + isUserApp.size() + ")");
		};
	};

	private ListView lv_app_manager;
	private TextView tv_des;
	private TextView tv_uninstall;
	private TextView tv_start;
	private TextView tv_share;
	private PopupWindow popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanager);
		initMemory();

		initAppInfo();
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
			if (position == 0 || position == isUserApp.size() + 1) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getCount() {
			return isSystemApp.size() + isUserApp.size()+2;
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == isUserApp.size() + 1) {
				return null;
			} else {
				if (position < isUserApp.size() + 1) {
					return isUserApp.get(position - 1);
				} else {
					return isSystemApp.get(position - isUserApp.size() - 2);
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
					viewTitleHolder.tv_title.setText("用户应用" + "("
							+ isUserApp.size() + ")");
				} else {
					viewTitleHolder.tv_title.setText("系统应用" + "("
							+ isSystemApp.size() + ")");
				}

				return convertView;
			} else {
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.appinfolist_item, null);
					viewHolder = new ViewHolder();
					viewHolder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					viewHolder.tv_appname = (TextView) convertView
							.findViewById(R.id.tv_appname);
					viewHolder.tv_path = (TextView) convertView
							.findViewById(R.id.tv_path);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				viewHolder.iv_icon
						.setBackgroundDrawable(getItem(position).icon);
				viewHolder.tv_appname.setText(getItem(position).name);
				if (getItem(position).isSDCard) {
					viewHolder.tv_path.setText("SD卡应用");
				} else {
					viewHolder.tv_path.setText("手机应用");
				}
				return convertView;
			}

		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_appname;
		TextView tv_path;
	}

	static class ViewTitleHolder {

		TextView tv_title;
	}

	private void initAppInfo() {
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		tv_des = (TextView) findViewById(R.id.tv_des);
		
		lv_app_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (isSystemApp != null && isUserApp != null) {
					if (firstVisibleItem > isUserApp.size() + 1) {
						tv_des.setText("系统应用(" + isSystemApp.size() + ")");
					} else {
						tv_des.setText("用户应用(" + isUserApp.size() + ")");
					}
				}

			}
		});
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == isUserApp.size() + 1) {
					return;
				} else {
					if (position < isUserApp.size() + 1) {
						mAppInfo = isUserApp.get(position - 1);
					} else {
						mAppInfo = isSystemApp.get(position - isUserApp.size()
								- 2);
					}
					showPopuWindon(view);
				}
			}
		});
	}

	/**
	 * 显示popuwido
	 * 
	 */
	protected void showPopuWindon(View view) {
		View popuView = View.inflate(getApplicationContext(),
				R.layout.popuwindon_layout, null);
		tv_uninstall = (TextView) popuView.findViewById(R.id.tv_uninstall);
		tv_start = (TextView) popuView.findViewById(R.id.tv_start);
		tv_share = (TextView) popuView.findViewById(R.id.tv_share);

		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);

		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(500);
		alphaAnimation.setFillAfter(true);

		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnimation.setDuration(500);
		scaleAnimation.setFillAfter(true);

		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);

		popupWindow = new PopupWindow(popuView,
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new ColorDrawable());
		popupWindow.showAsDropDown(view, 90, -view.getHeight());
		popuView.setAnimation(animationSet);
	}

	private void initMemory() {
		tv_memory = (TextView) findViewById(R.id.tv_memory);
		tv_sd_card = (TextView) findViewById(R.id.tv_sd_card);
		String path = Environment.getDataDirectory().getAbsolutePath();
		String sdpath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String memory = Formatter.formatFileSize(getApplicationContext(),
				getAvailableSpace(path));
		String sdCard = Formatter.formatFileSize(getApplicationContext(),
				getAvailableSpace(sdpath));
		tv_memory.setText("内存可用大小：" + memory);
		tv_sd_card.setText("sd卡可用大小：" + sdCard);
	}

	private long getAvailableSpace(String path) {
		StatFs statFs = new StatFs(path);
		// 获取区块大小
		long size = statFs.getBlockSize();
		// 获取区块数
		long count = statFs.getAvailableBlocks();
		return size * count;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_uninstall:
			if (mAppInfo.isSystem) {
				ToastUtil.toast(getApplicationContext(), "此应用不能卸载");
			} else {
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:" + mAppInfo.packageName));
				startActivity(intent);
				
			}
			break;
		case R.id.tv_start:
			PackageManager pm = getPackageManager();
			Intent launchIntentForPackage = pm
					.getLaunchIntentForPackage(mAppInfo.packageName);
			if (launchIntentForPackage != null) {
				startActivity(launchIntentForPackage);
			} else {
				ToastUtil.toast(getApplicationContext(), "此应用不能被开启");
			}
			break;
		case R.id.tv_share:
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用" + mAppInfo.name);
			intent.setType("text/plain");
			startActivity(intent);
			break;

		}
		if(popupWindow!=null){
			popupWindow.dismiss();
		}
	}
    @Override
    protected void onResume() {
    	super.onResume();
    	new Thread() {

			public void run() {
				isSystemApp = new ArrayList<AppInfo>();
				isUserApp = new ArrayList<AppInfo>();
				appInfoList = AppInfoEngine.getAppInfo(getApplicationContext());
				for (AppInfo appInfo : appInfoList) {
					if (appInfo.isSystem) {
						isSystemApp.add(appInfo);
					} else {
						isUserApp.add(appInfo);
					}
				}
				handler.sendEmptyMessage(0);
			};
		}.start();
    }
}
