package com.wenfeng.yamba;

import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class RefreshService extends IntentService {	
	static final String TAG = "RefreshService";

	public RefreshService() {
		super(TAG);
	}


//	@Override
//	public IBinder onBind(Intent arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println(11111);
		Log.d(TAG, "onCreated");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println(333);
		Log.d(TAG, "onDestroyed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "onStarted");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final String username = prefs.getString("username", "");
		final String password = prefs.getString("password", "");
		
		if(TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
			Toast.makeText(this, "Please update your username and password.", Toast.LENGTH_LONG).show();
			return;
		}
		
		Log.d(TAG, "onStarted");
		DBHelper dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		YambaClient cloud = new YambaClient(username, password);
		try {
//			int count = 0;
			Log.d(TAG, "before list");
			List<Status> timeline = cloud.getTimeline(10);
			Log.d(TAG, "after list");
			for(Status status : timeline) {
				Log.d(TAG, String.format("%s: %s", status.getUser(), status.getMessage()));
				values.clear();
				values.put(StatusContract.Column.ID, status.getId());
				values.put(StatusContract.Column.USER, status.getUser());
				values.put(StatusContract.Column.MESSAGE, status.getMessage());
				values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());
				db.insertWithOnConflict(StatusContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
				
//				Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);
//				if(uri != null) {
//					count++;
//					Log.d(TAG, String.format("%d. %s: %s", count, status.getUser(), status.getMessage()));
//				}
			}
			} catch (YambaClientException e) {
			Log.d(TAG, "Failed to fetch the timeline");
			e.printStackTrace();
		}	
		
	}


}
