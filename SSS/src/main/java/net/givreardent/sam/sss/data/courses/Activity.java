package net.givreardent.sam.sss.data.courses;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Activity implements Comparable<Activity> {
	public static final int stray = -1;
	public static final int generic = 0;
	public static final int lecture = 1;
	public static final int lab = 2;
	public static final int tutorial = 3;
	public static final int exam = 4;

	public static final int noRecurrence = 10;
	public static final int weeklyRecurrence = 11;
	public static final int monthlyRecurrence = 12;
	public static final int annuallyRecurrence = 13;
	public static final int intervalRecurrence = 14;
	
	public static final int defaultReminder = -10;
	public static final int noReminder = -11;

	private static final String JSONID = "ID";
	public static final String JSONType = "Type";
	private static final String JSONCourse = "Course";
	private static final String JSONName = "Name";
	private static final String JSONLocation = "Location";
	private static final String JSONWeekday = "Weekday";
	private static final String JSONStartTime = "Start time";
	private static final String JSONEndTime = "End time";
	private static final String JSONRecurrence = "Recurrence";
	private static final String JSONReminder = "Reminder";

	private final UUID ActivityID;
	private int type;
	private final Course course;
	private String name;
	private String location;
	private boolean[] weekdays;
	private Date startTime;
	private Date endTime;
	private int recurrence;
	private int reminder;

	public Activity(Course course) {
		ActivityID = UUID.randomUUID();
		weekdays = new boolean[7];
		this.course = course;
		reminder = defaultReminder;
		if (course == null)
			type = stray;
		else
			type = generic;
	}

	public Activity(JSONObject json, Course course) throws JSONException {
		ActivityID = UUID.fromString(json.getString(JSONID));
		type = json.getInt(JSONType);
		this.course = course;
		name = json.getString(JSONName);
		location = json.getString(JSONLocation);
		String days = json.getString(JSONWeekday);
		weekdays = new boolean[7];
		for (int i = 0; i < days.length(); i++) {
			if (days.charAt(i) == '0')
				weekdays[i] = false;
			else
				weekdays[i] = true;
		}
		startTime = new Date(json.getLong(JSONStartTime));
		endTime = new Date(json.getLong(JSONEndTime));
		recurrence = json.getInt(JSONRecurrence);
		try {
			reminder = json.getInt(JSONReminder);
		} catch (JSONException e) {
			Log.e("tag", "Unable to read reminder. Setting to default.", e);
			reminder = defaultReminder;
		}
	}

	public void setTitle(String title) {
		this.name = title;
	}

	public void setLocation(String loc) {
		location = loc;
	}

	public void setWeekday(int w, boolean newValue) {
		weekdays[w] = newValue;
	}

	public void setStartTime(Date start) {
		startTime = start;
	}

	public void setEndTime(Date end) {
		endTime = end;
	}

	public void setRecurrence(int recur) {
		recurrence = recur;
	}

	protected void setType(int newType) {
		type = newType;
	}
	
	public void setReminder(int newReminder) {
		reminder = newReminder;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public boolean getWeekday(int w) {
		return weekdays[w];
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public int getRecurrence() {
		return recurrence;
	}

	public Course getCourse() {
		return course;
	}

	public int getType() {
		return type;
	}

	public UUID getID() {
		return ActivityID;
	}
	
	public int getReminder() {
		return reminder;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSONID, ActivityID.toString());
		json.put(JSONType, type);
		if (type != Activity.stray)
			json.put(JSONCourse, course.getID().toString());
		json.put(JSONName, name);
		json.put(JSONLocation, location);
		String days = "";
		for (boolean b : weekdays) {
			if (b)
				days += "1";
			else
				days += "0";
		}
		json.put(JSONWeekday, days);
		json.put(JSONStartTime, startTime.getTime());
		json.put(JSONEndTime, endTime.getTime());
		json.put(JSONRecurrence, recurrence);
		json.put(JSONReminder, reminder);
		return json;
	}

	public boolean hasPassed() {
		Calendar currentTime = Calendar.getInstance();
		Calendar activityEndTime = Calendar.getInstance();
		activityEndTime.setTime(getEndTime());
		if (getType() == Activity.stray) {
			return currentTime.compareTo(activityEndTime) > 0;
		}
		return currentTime.get(Calendar.HOUR_OF_DAY) > activityEndTime.get(Calendar.HOUR_OF_DAY)
				|| (currentTime.get(Calendar.HOUR_OF_DAY) == activityEndTime.get(Calendar.HOUR_OF_DAY) && currentTime
						.get(Calendar.MINUTE) > activityEndTime.get(Calendar.MINUTE));
	}

	public boolean isCurrent() {
		Calendar currentTime = Calendar.getInstance();
		Calendar startTime = Calendar.getInstance();
		startTime.setTime(getStartTime());
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(getEndTime());
		if (getType() == Activity.stray) {
			return currentTime.compareTo(startTime) >= 0 && currentTime.compareTo(endTime) < 0;
		}
		int diffStart = (startTime.get(Calendar.HOUR_OF_DAY) - currentTime.get(Calendar.HOUR_OF_DAY)) * 60
				+ (startTime.get(Calendar.MINUTE) - currentTime.get(Calendar.MINUTE));
		int diffEnd = (endTime.get(Calendar.HOUR_OF_DAY) - currentTime.get(Calendar.HOUR_OF_DAY)) * 60
				+ (endTime.get(Calendar.MINUTE) - currentTime.get(Calendar.MINUTE));
		return diffStart < 0 && diffEnd >= 0;
	}

	public String getRemainingTime(String minF, String hF) {
		Calendar currentTime = Calendar.getInstance();
		Calendar startTime = Calendar.getInstance();
		startTime.setTime(getStartTime());
		int diff = (startTime.get(Calendar.HOUR_OF_DAY) - currentTime.get(Calendar.HOUR_OF_DAY)) * 60
				+ (startTime.get(Calendar.MINUTE) - currentTime.get(Calendar.MINUTE));
		if (diff / 60 >= 1) {
			return String.format(hF, diff / 60) + " " + String.format(minF, diff % 60);
		} else
			return String.format(minF, diff);
	}

	public int getDuration() {
		Calendar calendar = Calendar.getInstance(), calendar2 = Calendar.getInstance();
		calendar.setTime(endTime);
		calendar2.setTime(startTime);
		return ((calendar.get(Calendar.HOUR_OF_DAY) - calendar2.get(Calendar.HOUR_OF_DAY)) * 60
				+ calendar.get(Calendar.MINUTE) - calendar2.get(Calendar.MINUTE));
	}

	@Override
	public int compareTo(Activity otherActivity) {
		if (getRecurrence() == Activity.noRecurrence && otherActivity.getRecurrence() == Activity.noRecurrence) {
			return getStartTime().compareTo(otherActivity.getStartTime());
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startTime);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(otherActivity.getStartTime());
			if (calendar.get(Calendar.HOUR_OF_DAY) == calendar2.get(Calendar.HOUR_OF_DAY)
					&& calendar.get(Calendar.MINUTE) == calendar2.get(Calendar.MINUTE))
				return 0;
			else if (calendar.get(Calendar.HOUR_OF_DAY) < calendar2.get(Calendar.HOUR_OF_DAY)
					|| (calendar.get(Calendar.HOUR_OF_DAY) == calendar2.get(Calendar.HOUR_OF_DAY) && calendar
							.get(Calendar.MINUTE) < calendar2.get(Calendar.MINUTE)))
				return -1;
			else
				return 1;
		}
	}
}
