package com.wxf.mobilesafe.activity;

import java.util.List;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.db.dao.CommonNumberDAO;
import com.wxf.mobilesafe.db.dao.CommonNumberDAO.ChildList;
import com.wxf.mobilesafe.db.dao.CommonNumberDAO.Group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberActivity extends Activity {
	private ExpandableListView elv_common_number;
	private String tag = "CommonNumberActivity";
	private List<Group> group;
	private MyAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		initUI();
		initData();
	}

	private void initData() {
		CommonNumberDAO commonNumberDAO = new CommonNumberDAO();
		group = commonNumberDAO.getGroup();
		myAdapter = new MyAdapter();
		elv_common_number.setAdapter(myAdapter);
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				startCall(myAdapter.getChild(groupPosition, childPosition).number);
				return false;
			}
		});
	}

	protected void startCall(String number) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + number));
		startActivity(intent);
	}

	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);

	}

	class MyAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return group.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return group.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			return group.get(groupPosition);
		}

		@Override
		public ChildList getChild(int groupPosition, int childPosition) {
			return group.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {

			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText("       " + getGroup(groupPosition).name);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			textView.setTextColor(Color.RED);
			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.common_number_child_item, null);
			TextView tv_child_name = (TextView) view
					.findViewById(R.id.tv_child_name);
			TextView tv_child_number = (TextView) view
					.findViewById(R.id.tv_child_number);
			tv_child_name.setText(getChild(groupPosition, childPosition).name);
			tv_child_number
					.setText(getChild(groupPosition, childPosition).number);

			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
}
