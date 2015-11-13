package net.givreardent.sam.sss.util;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import net.givreardent.sam.sss.FrontActivity;
import net.givreardent.sam.sss.R;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class NotifierService extends IntentService {
	private static final String tag = "Notifier Service";
	
	public static final String extraType = "Type";
	public static final String extraCourse = "Course";
	public static final String extraActivityName = "Activity name";
	public static final String extraTime = "Activity Time";
	public static final String extraTaskName = "Task name";
	
	public static final int activityType = 0;
	public static final int taskType = 1;
	
	public NotifierService() {
		super(tag);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("NotifierService", "Received intent: " + intent);
		Resources res = getResources();
		Intent i = new Intent(this, FrontActivity.class);
		int type = intent.getIntExtra(extraType, 0);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
		NotificationCompat.Builder builder = new Builder(this);
		builder.setContentIntent(pi);
		String course = intent.getStringExtra(extraCourse);
		if (course == null) course = "";
		String time = intent.getStringExtra(extraTime);
		builder.setContentIntent(pi).setAutoCancel(true);
		builder.setSmallIcon(android.R.drawable.ic_menu_report_image);
		if (type == activityType) {
			String activityName = intent.getStringExtra(extraActivityName);
			builder.setTicker(res.getString(R.string.activity_notification_ticker, activityName, time));
			builder.setContentTitle(res.getString(R.string.activity_notification_title, course, activityName, time));
		} else {
			String taskName = intent.getStringExtra(extraTaskName);
			builder.setTicker(res.getString(R.string.task_notification_ticker, taskName, time));
			builder.setContentTitle(res.getString(R.string.task_notification_title, course, taskName, time));
		}
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, builder.build());
		Log.d("tag", "Notification sent. Scheduling next.");
		SSS.setNotification(this);
	}

}
