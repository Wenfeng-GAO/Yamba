package com.wenfeng.yamba;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

public class YambaWidget extends AppWidgetProvider {
	public static final String TAG = YambaWidget.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		this.onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context,YambaWidget.class)));
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		Log.d(TAG, "onUpdate");
		
		// get the latest tweet
		Cursor cursor = context.getContentResolver().query(StatusContract.CONTENT_URI, null, null, null, StatusContract.DEFAULT_SORT);
		if(!cursor.moveToFirst())
			return;
		String user = cursor.getString(cursor.getColumnIndex(StatusContract.Column.USER));
		String message = cursor.getString(cursor.getColumnIndex(StatusContract.Column.MESSAGE));
		long createAt = cursor.getLong(cursor.getColumnIndex(StatusContract.Column.CREATED_AT));
		
		PendingIntent operation = PendingIntent.getActivity(context, -1, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		
		// loop through all the instances of YambaWidget
		for(int appWidgetId : appWidgetIds) {
			RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget);
			
			// update the view
			view.setTextViewText(R.id.list_item_text_user_widget, user);
			view.setTextViewText(R.id.list_item_text_message_widget, message);
			view.setTextViewText(R.id.list_item_text_created_at, DateUtils.getRelativeTimeSpanString(createAt));
			view.setOnClickPendingIntent(R.id.list_item_text_user_widget, operation);
			view.setOnClickPendingIntent(R.id.list_item_text_message_widget, operation);
			
			// update the widget
			appWidgetManager.updateAppWidget(appWidgetId, view);
		}
	}
	
	
}
