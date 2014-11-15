package com.wenfeng.yamba;

import android.app.Activity;
import android.os.Bundle;

public class DetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// check if this activity has been created before
		if(savedInstanceState == null) {
			// create a fragment
			DetailsFragment fragment = new DetailsFragment();
			getFragmentManager().beginTransaction().add(android.R.id.content, fragment, fragment.getClass().getSimpleName()).commit();
		}
			
	}

}
