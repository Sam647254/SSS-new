package net.givreardent.sam.sss.data.courses;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Task implements Comparable<Task> {
	private static final String JSONID = "ID";
	private static final String JSONName = "Name";
	private static final String JSONDescr = "Description";
	private static final String JSONDate = "Due date";
	private static final String JSONTime = "Has time";
	private static final String JSONCourse = "Course";
	private static final String JSONMarks = "For marks";
	private static final String JSONDone = "Done";

	private final UUID taskID;
	private String name;
	private String description;
	private Course course;
	private Date dueDate;
	private boolean dueTime;
	private boolean forMarks;
	private boolean isDone;

	public Task(Course course) {
		taskID = UUID.randomUUID();
		this.course = course;
		forMarks = false;
		isDone = false;
	}

	public Task(JSONObject json, Course course) throws JSONException {
		taskID = UUID.fromString(json.getString(JSONID));
		name = json.getString(JSONName);
		// description = json.getString(JSONDescr);
		this.course = course;
		dueDate = new Date(json.getLong(JSONDate));
		dueTime = json.getBoolean(JSONTime);
		forMarks = json.getBoolean(JSONMarks);
		try {
			isDone = json.getBoolean(JSONDone);
		} catch (JSONException e) {
			isDone = false;
		}
	}

	public void setName(String newName) {
		name = newName;
	}

	public void setDescription(String d) {
		description = d;
	}

	public void setDueDate(Date date) {
		dueTime = false;
		dueDate = date;
	}

	public void setDueTime(Date date) {
		dueTime = true;
		dueDate = date;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Course getCourse() {
		return course;
	}

	public boolean hasTime() {
		return dueTime;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public UUID getID() {
		return taskID;
	}

	public void setForMarks(boolean f) {
		forMarks = f;
	}

	public boolean isForMarks() {
		return forMarks;
	}

	public void setIsDone(boolean done) {
		isDone = done;
	}

	public boolean isDone() {
		return isDone;
	}

	public boolean isOverdue() {
		return Calendar.getInstance().getTime().compareTo(dueDate) > 0;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSONID, taskID.toString());
		json.put(JSONName, name);
		json.put(JSONDescr, description);
		json.put(JSONDate, dueDate.getTime());
		json.put(JSONTime, dueTime);
		json.put(JSONMarks, forMarks);
		if (course != null)
			json.put(JSONCourse, course.getID().toString());
		json.put(JSONDone, isDone);
		return json;
	}

	@Override
	public int compareTo(Task another) {
		if (isDone && !another.isDone())
			return -1;
		if (!isDone && another.isDone())
			return 1;
		return dueDate.compareTo(another.getDueDate());
	}
}
