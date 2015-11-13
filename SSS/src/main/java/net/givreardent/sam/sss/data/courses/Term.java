package net.givreardent.sam.sss.data.courses;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Term {
	private static final String JSONID = "ID";
	private static final String JSONIdentifier = "Identifier";
	private static final String JSONStartDate = "Start date";
	private static final String JSONEndDate = "End date";
	private static final String JSONCourses = "Courses";
	private static final String JSONBreaks = "Breaks";

	private UUID TermID;
	private String identifier;
	private Date startDate;
	private Date endDate;
	private ArrayList<Course> courses;
	private ArrayList<Break> breaks;

	public Term() {
		courses = new ArrayList<Course>();
		TermID = UUID.randomUUID();
		breaks = new ArrayList<Break>();
	}

	public Term(JSONObject json, Context context) throws JSONException {
		TermID = UUID.fromString(json.getString(JSONID));
		identifier = json.getString(JSONIdentifier);
		startDate = new Date(json.getLong(JSONStartDate));
		endDate = new Date(json.getLong(JSONEndDate));
		courses = new ArrayList<Course>();
		JSONArray courseList = json.getJSONArray(JSONCourses);
		for (int i = 0; i < courseList.length(); i++) {
			courses.add(new Course(courseList.getJSONObject(i), this, context));
		}
		JSONArray breakList = json.getJSONArray(JSONBreaks);
		breaks = new ArrayList<Break>();
		if (breakList != null)
			for (int i = 0; i < breakList.length(); i++) {
				breaks.add(new Break(breakList.getJSONObject(i), this));
			}
	}

	public void setIdentifier(String name) {
		identifier = name;
	}

	public void setStartDate(Date date) {
		startDate = date;
	}

	public void setEndDate(Date date) {
		endDate = date;
	}

	public void addCourse(Course course) {
		courses.add(course);
	}

	public void removeCourse(UUID ID) {
		for (Course c : courses) {
			if (c.getID().equals(ID))
				courses.remove(c);
		}
	}

	public ArrayList<Course> getCourses() {
		return courses;
	}

	public Course getCourse(UUID ID) {
		for (Course c : courses) {
			if (c.getID().equals(ID))
				return c;
		}
		return null;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void addBreak(Break b) {
		breaks.add(b);
	}

	public ArrayList<Break> getBreaks() {
		return breaks;
	}

	public UUID getID() {
		return TermID;
	}

	public String getIdentifier() {
		return identifier;
	}

	public boolean isCurrent() {
		Date today = Calendar.getInstance().getTime();
		return (today.compareTo(startDate) > 0 && today.compareTo(endDate) < 0);
	}

	public ArrayList<Activity> getActivitiesOnDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		ArrayList<Activity> activities = new ArrayList<Activity>();
		for (Course c : courses) {
			for (Activity a : c.getActivitiesOnDate(date)) {
				activities.add(a);
			}
		}
		return activities;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSONID, TermID.toString());
		json.put(JSONIdentifier, identifier);
		json.put(JSONStartDate, startDate.getTime());
		json.put(JSONEndDate, endDate.getTime());
		JSONArray courses = new JSONArray();
		for (Course c : this.courses)
			courses.put(c.toJSON());
		json.put(JSONCourses, courses);
		JSONArray breaks = new JSONArray();
		for (Break b : this.breaks) {
			breaks.put(b.toJSON());
		}
		json.put(JSONBreaks, breaks);
		return json;
	}
}
