package net.givreardent.sam.sss.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.givreardent.sam.sss.data.CustomReminderList;
import net.givreardent.sam.sss.data.Settings;
import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.courses.Assessment;
import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.Exam;
import net.givreardent.sam.sss.data.courses.Task;
import net.givreardent.sam.sss.data.courses.Term;
import net.givreardent.sam.sss.data.courses.TermList;
import net.givreardent.sam.sss.data.events.StrayActivityList;
import net.givreardent.sam.sss.data.events.StrayTaskList;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

public final class SSS {
	private static final String prefNotificationsSet = "Notifications set";
	private static final String prefNextNotification = "Next notification";
	public static boolean exitedFromInClass = false;
	public static boolean notificationsSet = false;
	private static int nextNotificationIndex = -1;

	public static ArrayList<Activity> getTodayActivities(Context c) {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		Date today = Calendar.getInstance().getTime();
		for (Term t : TermList.get(c).getTerms()) {
			for (Activity a : t.getActivitiesOnDate(today)) {
				activities.add(a);
			}
		}
		for (Activity a : StrayActivityList.get(c).getActivitiesOnDate(today)) {
			activities.add(a);
		}
		Collections.sort(activities);
		return activities;
	}

	public static ArrayList<Activity> getTomorrowActivities(Context c) {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(5, 1);
		Date tomorrow = calendar.getTime();
		for (Term t : TermList.get(c).getTerms()) {
			for (Activity a : t.getActivitiesOnDate(tomorrow)) {
				activities.add(a);
			}
		}
		for (Activity a : StrayActivityList.get(c).getActivitiesOnDate(tomorrow)) {
			activities.add(a);
		}
		Collections.sort(activities);
		return activities;
	}

	public static ArrayList<Task> getAllTasks(Context c) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		ArrayList<Term> terms = TermList.get(c).getTerms();
		for (Term t : terms) {
			for (Course course : t.getCourses()) {
				for (Task task : course.getAllTasks()) {
					tasks.add(task);
				}
				for (Assessment assessment : course.getAllAssessments())
					tasks.add(assessment);
			}
		}
		for (Task t : StrayTaskList.get(c).getTasks()) {
			tasks.add(t);
		}
		Collections.sort(tasks);
		return tasks;
	}

	public static ArrayList<Task> getCurrentTasks(Context c) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		ArrayList<Term> terms = TermList.get(c).getTerms();
		for (Term term : terms) {
			if (term.isCurrent())
				for (Course course : term.getCourses()) {
					for (Task task : course.getAllTasks()) {
						tasks.add(task);
					}
					for (Assessment assessment : course.getAllAssessments())
						tasks.add(assessment);
				}
		}
		for (Task t : StrayTaskList.get(c).getTasks()) {
			if (!t.isDone())
				tasks.add(t);
		}
		return tasks;
	}

	public static Term getCurrentTerm(Context c) {
		for (Term t : TermList.get(c).getTerms()) {
			if (t.isCurrent())
				return t;
		}
		return null;
	}
	
	public static ArrayList<Term> getHistoricalTerms(Context c) {
		ArrayList<Term> terms = new ArrayList<>();
		Calendar today = Calendar.getInstance();
		for (Term t : TermList.get(c).getTerms()) {
			if (t.getEndDate().compareTo(today.getTime()) < 0)
				terms.add(t);
		}
		return terms;
	}

	public static ArrayList<String> getCurrentCourseCodes(Context c) {
		ArrayList<String> names = new ArrayList<String>();
		if (getCurrentTerm(c) == null)
			return names;
		for (Course course : getCurrentTerm(c).getCourses()) {
			names.add(course.getCode());
		}
		return names;
	}

	public static ArrayList<Task> getAllUpcomingTasks(Context c) {
		ArrayList<Task> tasks = new ArrayList<>();
		for (Term t : TermList.get(c).getTerms()) {
			for (Course course : t.getCourses()) {
				for (Task task : course.getAllTasks()) {
					if (!task.isDone())
						tasks.add(task);
				}
				for (Assessment assessment : course.getAllAssessments()) {
					if (!assessment.isDone()) {
						tasks.add(assessment);
					}
				}
			}
		}
		for (Task t : StrayTaskList.get(c).getTasks()) {
			if (!t.isDone())
				tasks.add(t);
		}
		Collections.sort(tasks);
		return tasks;
	}

	public static ArrayList<Activity> getUpcomingEvents(Context c) {
		ArrayList<Activity> activities = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		calendar.add(5, 14);
		Date end = calendar.getTime();
		for (Activity activity : StrayActivityList.get(c).getActivitiesFromDate(today)) {
			if (activity.getStartTime().compareTo(end) <= 0)
				activities.add(activity);
		}
		Collections.sort(activities);
		return activities;
	}

	public static ArrayList<Activity> getActivitiesOnDate(Context c, Date date) {
		ArrayList<Activity> activities = new ArrayList<>();
		for (Term t : TermList.get(c).getTerms()) {
			for (Activity a : t.getActivitiesOnDate(date)) {
				activities.add(a);
			}
		}
		for (Activity a : StrayActivityList.get(c).getActivitiesOnDate(date)) {
			activities.add(a);
		}
		Collections.sort(activities);
		return activities;
	}

	public static ArrayList<Assessment> getTasksToBeGraded(Context c) {
		ArrayList<Assessment> tasks = new ArrayList<>();
		for (Task t : getAllTasks(c)) {
			if (t.isForMarks() && t.isDone() && !((Assessment) t).isRecorded())
				tasks.add((Assessment) t);
		}
		Collections.sort(tasks);
		return tasks;
	}

	public static ArrayList<Exam> getExamsToBeGraded(Context c) {
		ArrayList<Exam> exams = new ArrayList<>();
		if (getCurrentTerm(c) == null)
			return exams;
		Date today = Calendar.getInstance().getTime();
		for (Course course : getCurrentTerm(c).getCourses()) {
			for (Exam e : course.getExams()) {
				if (e.getEndTime().compareTo(today) < 0)
					exams.add(e);
			}
		}
		return exams;
	}

	public static void setNotifications(Context c) {
		Log.i("tag", "Setting notifications...");
		if (Settings.areNotificationsEnabled(c)) {
			SSS.notificationsSet = PreferenceManager.getDefaultSharedPreferences(c).getBoolean(prefNotificationsSet,
					false);
			SSS.nextNotificationIndex = PreferenceManager.getDefaultSharedPreferences(c).getInt(prefNextNotification,
					-1);
			if (SSS.notificationsSet) {
				Log.d("tag", "Notifications are already set.");
				return;
			}
			ArrayList<Activity> todayActivities = getTodayActivities(c);
			int hour = Calendar.getInstance().get(Calendar.HOUR);
			int min = Calendar.getInstance().get(Calendar.MINUTE);
			for (int i = 0; i < todayActivities.size(); ++i) {
				Activity activity = todayActivities.get(i);
				if (activity.getReminder() == Activity.noReminder)
					continue;
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(activity.getStartTime());
				if (hour < calendar.get(Calendar.HOUR)
						|| (hour == calendar.get(Calendar.HOUR) && min <= calendar.get(Calendar.MINUTE))) {
					Log.i("tag", "Found next reminder at index: " + i);
					PreferenceManager.getDefaultSharedPreferences(c).edit().putInt(prefNextNotification, i).apply();
					SSS.nextNotificationIndex = i;
					break;
				}
			}
			if (SSS.nextNotificationIndex == -1 || SSS.nextNotificationIndex >= todayActivities.size())
				scheduleTomorrowNotifications(c);
			else {
				setNotification(c);
				SSS.nextNotificationIndex++;
				PreferenceManager.getDefaultSharedPreferences(c).edit()
						.putInt(prefNextNotification, nextNotificationIndex).apply();
			}
			PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(prefNotificationsSet, true).apply();
			SSS.notificationsSet = true;
		} else {
			Log.d("tag", "Notifications are not enabled");
		}
	}

	public static void clearNotifications(Context c) {
		PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(prefNotificationsSet, false).apply();
		notificationsSet = false;
		PreferenceManager.getDefaultSharedPreferences(c).edit().putInt(prefNextNotification, -1).apply();
		nextNotificationIndex = -1;
		Intent i = new Intent(c, NotifierService.class);
		AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getService(c, 0, i, 0);
		alarmManager.cancel(pi);
		pi.cancel();
		Log.d("tag", "Notifications are cleared.");
	}

	public static void scheduleTomorrowNotifications(final Context c) {
		Log.d("tag", "No activities today. Scheduling task for tomorrow.");
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				nextNotificationIndex = -1;
				setNotifications(c);
			}
		};
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		timer.schedule(task, calendar.getTime());
	}

	public static void setNotification(Context c) {
		SSS.nextNotificationIndex = PreferenceManager.getDefaultSharedPreferences(c).getInt(prefNextNotification, -1);
		Log.d("tag", "Setting notification for: index " + SSS.nextNotificationIndex);
		ArrayList<Activity> todayActivities = getTodayActivities(c);
		if (nextNotificationIndex == todayActivities.size()) {
			Log.i("tag", "No more reminders to schedule");
			scheduleTomorrowNotifications(c);
			return;
		}
		Activity activity = todayActivities.get(nextNotificationIndex);
		Intent i = new Intent(c, NotifierService.class);
		i.putExtra(NotifierService.extraActivityName, activity.getName());
		if (activity.getCourse() != null)
			i.putExtra(NotifierService.extraCourse, activity.getCourse().getName());
		java.text.DateFormat df = DateFormat.getTimeFormat(c);
		i.putExtra(NotifierService.extraTime, df.format(activity.getStartTime()));
		PendingIntent pi = PendingIntent.getService(c, 0, i, 0);
		AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
		int reminder;
		if (CustomReminderList.get(c).isInList(activity, getStartTimeToday(activity)))
			reminder = CustomReminderList.get(c).getCustomReminder(activity, getStartTimeToday(activity));
		else if (activity.getType() == Activity.exam)
			reminder = (activity.getReminder() == Activity.defaultReminder) ? Settings.getExamReminder(c) : activity
					.getReminder();
		else
			reminder = (activity.getReminder() == Activity.defaultReminder) ? Settings.getReminder(c) : activity
					.getReminder();
		Date reminderDate;
		if (activity.getType() == Activity.stray)
			reminderDate = new Date(activity.getStartTime().getTime() - (reminder * 60000));
		else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(getStartTimeToday(activity));
			calendar.add(Calendar.MINUTE, -reminder);
			reminderDate = calendar.getTime();
		}
		java.text.DateFormat df2 = DateFormat.getDateFormat(c);
		Log.d("tag", "Scheduled notification at: " + df2.format(reminderDate) + " " + df.format(reminderDate));
		alarmManager.set(AlarmManager.RTC_WAKEUP, reminderDate.getTime(), pi);
		PreferenceManager.getDefaultSharedPreferences(c).edit().putInt(prefNextNotification, nextNotificationIndex+1).commit();
	}

	public static Date getStartTimeToday(Activity activity) {
		Calendar calendar = Calendar.getInstance();
		Calendar activityCalendar = Calendar.getInstance();
		activityCalendar.setTime(activity.getStartTime());
		calendar.set(Calendar.HOUR, activityCalendar.get(Calendar.HOUR));
		calendar.set(Calendar.MINUTE, activityCalendar.get(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, activityCalendar.get(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, activityCalendar.get(Calendar.MILLISECOND));
		return calendar.getTime();
	}
}