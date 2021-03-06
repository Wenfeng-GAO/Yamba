package com.wenfeng.yamba;

import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class RefreshService extends IntentService {	
	static final String TAG = "RefreshService";
	private Handler handler;
	

	public RefreshService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreated");
		handler = new Handler();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
			Log.d(TAG, "onHandleIntent");
			
			// Can not Toast directly here.
			handler.post(new DisplayToast("Please update your username and password."));
			return;
		}
		ContentValues values = new ContentValues();
		YambaClient cloud = new YambaClient(username, password);

		try {
			int count = 0;

			// This is often disable to connect.
			Log.d(TAG, "1");
			List<Status> timeline = cloud.getTimeline(20);
			Log.d(TAG, "2");

			for(Status status : timeline) {
				Log.d(TAG, String.format("%s: %s", status.getUser(), status.getMessage()));
				values.clear();
				values.put(StatusContract.Column.ID, status.getId());
				values.put(StatusContract.Column.USER, status.getUser());
				values.put(StatusContract.Column.MESSAGE, status.getMessage());
				values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());
				Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);

				if(uri != null) {
					count++;
					Log.d(TAG, String.format("%d. %s: %s", count, status.getUser(), status.getMessage()));
				}
				
				if(count > 0) {
					sendBroadcast(new Intent("com.wenfeng.yamba.action.NEW_STATUSES").putExtra("count", count));
				}
			}
			} catch (YambaClientException e) {
				handler.post(new DisplayToast("Failed to pull the statuses."));
				Log.d(TAG, "Failed to fetch the timeline");
				e.printStackTrace();
		}	
	}

	private class DisplayToast implements Runnable {
		String mText;
		
		public DisplayToast(String text) {
			this.mText = text;
		}

		@Override
		public void run() {
			Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_LONG).show();
		}
		
	}
}
