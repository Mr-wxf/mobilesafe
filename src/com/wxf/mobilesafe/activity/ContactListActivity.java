package com.wxf.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wxf.mobilesafe.R;

public class ContactListActivity extends Activity {
	protected static final String tag = "ContactListActivity";
	private ListView lv_contact;
	private List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
	  private MyAdapter myAdapter;
	private Handler handler = new Handler() {
  

		public void handleMessage(android.os.Message msg) {

			myAdapter = new MyAdapter();
			lv_contact.setAdapter(myAdapter);
		};
	};

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return contactList.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			return contactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = View.inflate(getApplicationContext(),
					R.layout.contact_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);

			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			tv_name.setText(getItem(position).get("name"));
			tv_phone.setText(getItem(position).get("phone"));
			return view;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contactlist);
		initUI();
		initData();
	}

	private void initData() {
		new Thread() {

			public void run() {

				ContentResolver contentResolver = getContentResolver();
				Cursor query = contentResolver.query(Uri
						.parse("content://com.android.contacts/raw_contacts"),
						new String[] { "contact_id" }, null, null, null);
				contactList.clear();
				while (query.moveToNext()) {
					String id = query.getString(0);
					Log.i(tag, id);
					Cursor cursor = contentResolver.query(
							Uri.parse("content://com.android.contacts/data"),
							new String[] { "data1", "mimetype" },
							"raw_contact_id=?", new String[] { id }, null);
					HashMap<String, String> hashMap = new HashMap<String, String>();
					while (cursor.moveToNext()) {
						String data = cursor.getString(0);
						String mimetype = cursor.getString(1);
//						Log.i(tag, data);
//						Log.i(tag, mimetype);
						if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
							hashMap.put("phone", data);
						} else if (mimetype
								.equals("vnd.android.cursor.item/name")) {
							hashMap.put("name", data);
						}

					}

					cursor.close();
					contactList.add(hashMap);
					handler.sendEmptyMessage(0);
				}

				query.close();
			};
		}.start();
	}

	private void initUI() {
		lv_contact = (ListView) findViewById(R.id.lv_contact);

		lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
                       if(myAdapter!=null){
                    	  String phone = myAdapter.getItem(position).get("phone");
                    	  Intent intent = new Intent();
                    	  intent.putExtra("phone", phone);
                    	  setResult(0, intent); 
                    	  finish();
                       }
			}
		});
	}
}
