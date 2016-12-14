package com.wxf.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.db.bean.AppInfo;
import com.wxf.mobilesafe.db.bean.ProcessInfo;
import com.wxf.mobilesafe.db.dao.AppLockrDAO;
import com.wxf.mobilesafe.engine.AppInfoEngine;
import com.wxf.mobilesafe.engine.ProcessinfoEngine;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AppLockActivity extends Activity {
	private Button bt_unlock;
	private Button bt_lock;
	private LinearLayout ll_unlock;
	private LinearLayout ll_lock;
	private TextView tv_unlock_app;
	private TextView tv_lock_app;
	private ListView lv_unlock;
	private ListView lv_lock;
	private List<AppInfo> mUnLockApp;
	private List<AppInfo> mLockApp;
	private AppLockrDAO appLockrDAO;
	private MyAdapter myAdapterLock;
	private MyAdapter myAdapterUnLock;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			myAdapterUnLock = new MyAdapter(false);
			lv_unlock.setAdapter(myAdapterUnLock);

			myAdapterLock = new MyAdapter(true);
			lv_lock.setAdapter(myAdapterLock);

		};
	};
	private TranslateAnimation mTranslateAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		initUI();
		initData();
		initAnimation();
	}

	private void initAnimation() {
		mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		mTranslateAnimation.setDuration(500);
	}

	class MyAdapter extends BaseAdapter {
		private boolean isLock;
		private ViewHolder holder;

		/**
		 * @param isLock
		 *            为true是已加锁的Adapter false 为 未加锁的Adapter
		 */
		public MyAdapter(boolean isLock) {
			this.isLock = isLock;
		}

		@Override
		public int getCount() {
			if (isLock) {
				tv_lock_app.setText("已加锁的应用:" + mLockApp.size());
				return mLockApp.size();

			} else {
				tv_unlock_app.setText("未加锁的应用:" + mUnLockApp.size());
				return mUnLockApp.size();
			}

		}

		@Override
		public AppInfo getItem(int position) {
			if (isLock) {

				return mLockApp.get(position);

			} else {

				return mUnLockApp.get(position);
			}

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			holder = new ViewHolder();
			if (convertView == null) {
				convertView = convertView.inflate(getApplicationContext(),
						R.layout.app_lock_item, null);
				holder.iv_app_icon = (ImageView) convertView
						.findViewById(R.id.iv_app_icon);
				holder.tv_app_name = (TextView) convertView
						.findViewById(R.id.tv_app_name);
				holder.iv_lock_icon = (ImageView) convertView
						.findViewById(R.id.iv_lock_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final AppInfo appInfo = getItem(position);
			final View animationView = convertView;
			holder.iv_app_icon.setBackgroundDrawable(getItem(position).icon);
			holder.tv_app_name.setText(getItem(position).name);
			if (isLock) {
				holder.iv_lock_icon.setBackgroundResource(R.drawable.lock);

			} else {
				holder.iv_lock_icon.setBackgroundResource(R.drawable.unlock);
			}

			holder.iv_lock_icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					animationView.startAnimation(mTranslateAnimation);
					mTranslateAnimation
							.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {

								}

								@Override
								public void onAnimationRepeat(
										Animation animation) {

								}

								@Override
								public void onAnimationEnd(Animation animation) {
									if (isLock) {
										// 已加锁---》未加锁
										mLockApp.remove(appInfo);
										mUnLockApp.add(appInfo);
										appLockrDAO.delete(appInfo
												.getPackageName());
										myAdapterLock.notifyDataSetChanged();
										myAdapterUnLock.notifyDataSetChanged();
									} else {
										// 未加锁---》已加锁
										mLockApp.add(appInfo);
										mUnLockApp.remove(appInfo);
										appLockrDAO.insert(appInfo.getPackageName());
										myAdapterUnLock.notifyDataSetChanged();
										myAdapterLock.notifyDataSetChanged();
									}
								}
							});

				}
			});

			return convertView;
		}

	}

	static class ViewHolder {
		ImageView iv_app_icon;
		TextView tv_app_name;
		ImageView iv_lock_icon;
	}

	private void initData() {
		new Thread() {

			private List<AppInfo> appInfoList;

			public void run() {
				appInfoList = AppInfoEngine.getAppInfo(getApplicationContext());
				mUnLockApp = new ArrayList<AppInfo>();
				mLockApp = new ArrayList<AppInfo>();
				appLockrDAO = AppLockrDAO.getInstance(getApplicationContext());
				List<String> findAllLockApp = appLockrDAO.findAll();
				for (AppInfo appInfo : appInfoList) {
					if (findAllLockApp.contains(appInfo.getPackageName())) {
						mLockApp.add(appInfo);
					} else {
						mUnLockApp.add(appInfo);
					}

				}
				mHandler.sendEmptyMessage(0);
			};

		}.start();
	}

	private void initUI() {
		bt_unlock = (Button) findViewById(R.id.bt_unlock);
		bt_lock = (Button) findViewById(R.id.bt_lock);

		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_lock = (LinearLayout) findViewById(R.id.ll_lock);

		tv_unlock_app = (TextView) findViewById(R.id.tv_unlock_app);
		tv_lock_app = (TextView) findViewById(R.id.tv_lock_app);

		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		lv_lock = (ListView) findViewById(R.id.lv_lock);

		bt_unlock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bt_lock.setBackgroundResource(R.drawable.tab_right_default);
				bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				ll_unlock.setVisibility(View.VISIBLE);
				ll_lock.setVisibility(View.GONE);
			}
		});

		bt_lock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
				bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
				ll_unlock.setVisibility(View.GONE);
				ll_lock.setVisibility(View.VISIBLE);
			}
		});
	}

}
