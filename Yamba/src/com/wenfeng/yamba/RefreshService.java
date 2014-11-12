package com.wenfeng.yamba;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
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


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

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
		
		YambaClient cloud = new YambaClient(username, password);
		try {
			List<Status> timeline = cloud.getTimeline(20);
			for(Status status : timeline) {
				Log.d(TAG, String.format("%s: %s", status.getUser(), status.getMessage()));
			}
		} catch (YambaClientException e) {
			Log.d(TAG, "Failed to fetch the timeline");
			e.printStackTrace();
		}	
		
	}


}
