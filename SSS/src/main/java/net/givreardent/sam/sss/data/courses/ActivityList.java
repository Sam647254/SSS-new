package net.givreardent.sam.sss.data.courses;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class ActivityList {
	private static ActivityList sList;
	private Context mContext;
	private static Course sCourse;
	private ArrayList<Activity> activities;
	
	private ActivityList(Context appContext, UUID courseID, UUID termID) {
		mContext = appContext;
		sCourse = CourseList.get(appContext, termID).getCourse(courseID);
		activities = sCourse.getAllActivities();
	}
	
	public static ActivityList get(Context c, UUID courseID, UUID termID) {
		if (sList == null || !sCourse.getID().equals(courseID)) {
			sList = new ActivityList(c, courseID, termID);
		}
		return sList;
	}
	
	public void addActivity(Activity a) {
		activities.add(a);
	}
	
	public void removeActivity(Activity a) {
		activities.remove(a);
	}
	
	public void removeActivity(UUID activityID) {
		for(Activity a : activities) {
			if (a.getID().equals(activityID)) {
				activities.remove(a);
				return;
			}
		}
	}
	
	public Activity getActivity(UUID activityID) {
		for(Activity a : activities) {
			if (a.getID().equals(activityID)) {
				return a;
			}
		}
		return null;
	}
	
	public ArrayList<Activity> getActivities() {
		return activities;
	}
}
