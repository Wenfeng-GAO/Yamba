package com.wenfeng.yamba;

import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class RefreshService extends IntentService {	
	static final String TAG = "RefreshService";

	public RefreshService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreated");
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
			
			return;
		}
		ContentValues values = new ContentValues();
		YambaClient cloud = new YambaClient(username, password);

		try {
			int count = 0;

			// This is often disable to connect.
			List<Status> timeline = cloud.getTimeline(20);

			for(Status status : timeline) {
				Log.d(TAG, String.format("%s: %s", status.getUser(), status.getMessage()));
				values.clear();
				values.put(StatusContract.Column.ID, status.getId());
				values.put(StatusContract.Column.USER, status.getUser());
				values.put(StatusContract.Column.MESSAGE, status.getMessage());
				values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());
				Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);
				Log.d(TAG, "onHandleIntent3");

				if(uri != null) {
					count++;
					Log.d(TAG, String.format("%d. %s: %s", count, status.getUser(), status.getMessage()));
				}
			}
			} catch (YambaClientException e) {
			Log.d(TAG, "Failed to fetch the timeline");
			e.printStackTrace();
		}	
	}
}
