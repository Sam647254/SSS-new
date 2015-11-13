package net.givreardent.sam.sss.data.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import net.givreardent.sam.sss.JSONIO.ActivityJSONSerializer;
import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.util.SSS;
import android.content.Context;
import android.util.Log;

public class StrayActivityList {
	private static final String filename = "Stray activities.json";
	private Context mContext;
	private static StrayActivityList sList;
	private ArrayList<Activity> activities;
	private ActivityJSONSerializer mSerializer;

	private StrayActivityList(Context c) {
		mContext = c;
		mSerializer = new ActivityJSONSerializer(mContext, filename);
		try {
			activities = mSerializer.loadActivities();
			Collections.sort(activities);
		} catch (Exception e) {
			activities = new ArrayList<Activity>();
		}
	}

	public static StrayActivityList get(Context c) {
		if (sList == null)
			sList = new StrayActivityList(c);
		return sList;
	}

	public ArrayList<Activity> getActivities() {
		Collections.sort(activities);
		return activities;
	}

	public void addActivity(Activity a) {
		Log.i("tag", "add activity from Stray Activity List called.");
		activities.add(a);
		saveActivities();
		SSS.clearNotifications(mContext);
		SSS.setNotifications(mContext);
	}

	public void removeActivity(Activity a) {
		activities.remove(a);
		saveActivities();
		SSS.clearNotifications(mContext);
		SSS.setNotifications(mContext);
	}

	public Activity getActivity(UUID ID) {
		for (Activity a : activities) {
			if (a.getID().equals(ID))
				return a;
		}
		return null;
	}

	public ArrayList<Activity> getActivitiesOnDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		ArrayList<Activity> list = new ArrayList<Activity>();
		for (Activity a : activities) {
			Calendar aCalendar = Calendar.getInstance();
			aCalendar.setTime(a.getStartTime());
			if (calendar.get(1) == aCalendar.get(1) && calendar.get(2) == aCalendar.get(2)
					&& calendar.get(5) == aCalendar.get(5)) {
				list.add(a);
				continue;
			}
		}
		return list;
	}

	public ArrayList<Activity> getActivitiesFromDate(Date date) {
		int start = 0, end = activities.size() - 1;
		while (start <= end) {
			int mid = start + (end - start) / 2;
			int comparison = date.compareTo(activities.get(mid).getEndTime());
			if (comparison < 0)
				end = mid - 1;
			else if (comparison > 0)
				start = mid + 1;
			else {
				start = mid;
				break;
			}
		}
		if (start > activities.size() - 1)
			return new ArrayList<Activity>();
		return new ArrayList<Activity>(activities.subList(start, activities.size()));
	}

	private boolean saveActivities() {
		try {
			mSerializer.saveActivities(activities);
			Log.d("tag", "save activities called.");
			return true;
		} catch (Exception e) {
			Log.e("tag", "Error saving activities:", e);
			return false;
		}
	}
}
