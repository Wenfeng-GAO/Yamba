package com.wenfeng.yamba;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusFragment extends Fragment implements OnClickListener {
	private static final String TAG = "StatusActivity";
	private EditText editStatus;
	private Button buttonTweet;
	private TextView textCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_status, container, false);
		
		editStatus = (EditText) view.findViewById(R.id.editStatus);
		buttonTweet = (Button) view.findViewById(R.id.buttonTweet);
		textCount = (TextView) view.findViewById(R.id.textCount);
		
		buttonTweet.setOnClickListener(this);
		
		editStatus.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				int count = 140 - s.length();
				Log.d(TAG, "count:" + count);
				textCount.setText(Integer.toString(count));
				Log.d(TAG, "textCount:" + textCount.getText());
				textCount.setTextColor(Color.GREEN);
				if(count < 0)
					textCount.setTextColor(Color.RED);
				else if(count < 10)
					textCount.setTextColor(Color.YELLOW);
			}
		});
		
		Log.d(TAG, "onCreated");
		return view;
	}

	@Override
	public void onClick(View v) {
		String status = editStatus.getText().toString();
		Log.d(TAG, "onClicked with status:" + status);
		new PostTask().execute(status);		
	}
	
	private final class PostTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			YambaClient yambaCloud = new YambaClient("student", "password");
			try {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				String username = prefs.getString("username", "");
				String password = prefs.getString("password", "");
				
				if(TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
					getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
					return "Please update your username and password";
				}
				yambaCloud.postStatus(params[0]);
				return "Successfully posted";
			} catch (YambaClientException e) {
				e.printStackTrace();
				return "Failed to post to Yamba service";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(StatusFragment.this.getActivity(), result, Toast.LENGTH_LONG).show();
		}
		
	}

}
