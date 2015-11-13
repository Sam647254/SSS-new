package net.givreardent.sam.sss.data.courses;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class Course {
	private static final String JSONID = "ID";
	private static final String JSONName = "Name";
	private static final String JSONCode = "Code";
	private static final String JSONColour = "Colour";
	private static final String JSONActivities = "Activities";
	private static final String JSONTasks = "Tasks";
	private static final String JSONTerm = "Term";
	private static final String JSONSections = "Sections";
	private static final String JSONGradable = "Gradable?";

	public static final int[] blue = { 0xFF0B3861, 0xFF0174DF, 0xFF2E9AFE, 0xFF81BEF7 };
	public static final int[] red = { 0xFF8A0808, 0xFFDF0101, 0xFFFE2E2E, 0xFFF78181 };
	public static final int[] orange = { 0xFFDF7401, 0xFFFF8000, 0xFFFE9A2E, 0xFFFAAC58 };
	public static final int[] green = { 0xFF088A08, 0xFF01DF01, 0xFF2EFE2E, 0xFF81F781 };
	public static final int[] purple = { 0xFF380B61, 0xFF5F04B4, 0xFF8000FF, 0xFFAC58FA };

	private UUID CourseID;
	private String name;
	private String code;
	private int colour;
	private ArrayList<Activity> activities;
	private ArrayList<Task> tasks;
	private ArrayList<GradeSection> sections;
	private final Term term;
	private boolean gradable;

	public Course(Term term) {
		CourseID = UUID.randomUUID();
		tasks = new ArrayList<Task>();
		activities = new ArrayList<Activity>();
		sections = new ArrayList<GradeSection>();
		gradable = false;
		this.term = term;
	}

	public Course(JSONObject json, Term term, Context context) throws JSONException {
		CourseID = UUID.fromString(json.getString(JSONID));
		try {
			name = json.getString(JSONName);
			code = json.getString(JSONCode);
		} catch (JSONException e) {
			name = "";
			code = "";
		}
		colour = json.getInt(JSONColour);
		activities = new ArrayList<Activity>();
		JSONArray activityArray = json.getJSONArray(JSONActivities);
		for (int i = 0; i < activityArray.length(); i++) {
			Activity activity = new Activity(activityArray.getJSONObject(i), this);
			switch (activity.getType()) {
			case Activity.lecture:
				activities.add(new Lecture(activityArray.getJSONObject(i), this));
				break;
			case Activity.lab:
				activities.add(new Lab(activityArray.getJSONObject(i), this));
				break;
			case Activity.tutorial:
				activities.add(new Tutorial(activityArray.getJSONObject(i), this));
				break;
			case Activity.exam:
				activities.add(new Exam(activityArray.getJSONObject(i), this, context));
				break;
			default:
				activities.add(activity);
				break;
			}
		}
		this.term = term;
		Log.d("tag", "Set term as: " + term.getIdentifier());
		// To be implemented in detail
		try {
			tasks = new ArrayList<Task>();
			JSONArray taskArray = json.getJSONArray(JSONTasks);
			for (int i = 0; i < taskArray.length(); i++) {
				tasks.add(new Task(taskArray.getJSONObject(i), this));
			}
			JSONArray sectionArray = json.getJSONArray(JSONSections);
			sections = new ArrayList<GradeSection>();
			for (int i = 0; i < sectionArray.length(); i++) {
				try {
					sections.add(new GradeSection(sectionArray.getJSONObject(i), this, context));
				} catch (JSONException e) {
					continue;
				}
			}
		} catch (JSONException e) {
			Log.e("tag", "Error loading tasks.", e);
			tasks = new ArrayList<>();
			sections = new ArrayList<>();
		}
		try {
			gradable = json.getBoolean(JSONGradable);
		} catch (JSONException e) {
			gradable = true;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void addTask(Task task) {
		tasks.add(task);
	}

	public boolean addAssessment(UUID SectionID, Assessment a) {
		for (GradeSection s : sections) {
			if (s.getID().equals(SectionID)) {
				s.addAssessment(a);
				return true;
			}
		}
		return false;
	}

	public ArrayList<Task> getUndueTasksAndAssessments() {
		ArrayList<Task> list = new ArrayList<Task>();
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		for (Task t : tasks) {
			if (t.getDueDate().compareTo(now) > 0)
				list.add(t);
		}
		ArrayList<GradeSection> sections = new ArrayList<GradeSection>();
		for (GradeSection s : this.sections) {
			if (!s.isExam()) {
				sections.add(s);
			}
		}
		for (GradeSection s : sections) {
			for (Assessment a : s.getAssessments()) {
				if (a.getDueDate().compareTo(now) > 0)
					list.add(a);
			}
		}
		return list;
	}

	public ArrayList<Task> getAllTasks() {
		return tasks;
	}

	public ArrayList<Assessment> getAllAssessments() {
		ArrayList<Assessment> assessments = new ArrayList<Assessment>();
		ArrayList<GradeSection> sections = new ArrayList<GradeSection>();
		for (GradeSection s : this.sections) {
			if (!s.isExam()) {
				sections.add(s);
			}
		}
		for (GradeSection s : sections) {
			for (Assessment a : s.getAssessments()) {
				assessments.add(a);
			}
		}
		return assessments;
	}

	public void addActivity(Activity act) {
		activities.add(act);
	}

	@Deprecated
	public ArrayList<Activity> getTodayActivities() {
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		return getActivitiesOnDate(today);
	}

	@Deprecated
	public ArrayList<Activity> getTomorrowActivities() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(5, 1);
		Date tomorrow = calendar.getTime();
		return getActivitiesOnDate(tomorrow);
	}

	public ArrayList<Activity> getAllActivities() {
		return activities;
	}

	public ArrayList<Activity> getActivitiesOnDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		ArrayList<Activity> list = new ArrayList<Activity>();
		for (Activity a : activities) {
			if (a.getRecurrence() == Activity.weeklyRecurrence && a.getWeekday(calendar.get(Calendar.DAY_OF_WEEK) - 1)
					&& date.compareTo(term.getEndDate()) < 0 && date.compareTo(term.getStartDate()) >= 0
					&& isOutsideBreaks(date)) {
				list.add(a);
				continue;
			}
			if (a.getRecurrence() == Activity.noRecurrence) {
				Calendar aCalendar = Calendar.getInstance();
				aCalendar.setTime(a.getStartTime());
				if (calendar.get(1) == aCalendar.get(1) && calendar.get(2) == aCalendar.get(2)
						&& calendar.get(5) == aCalendar.get(5) && date.compareTo(term.getEndDate()) < 0
						&& date.compareTo(term.getStartDate()) >= 0) {
					list.add(a);
					continue;
				}
			}
		}
		return list;
	}

	public void setColour(int newColour) {
		colour = newColour;
	}

	public void addSection(GradeSection s) {
		sections.add(s);
	}

	public void setGradable(boolean gradable) {
		this.gradable = gradable;
	}

	public boolean isGradable() {
		return gradable;
	}

	public int getColour() {
		return colour;
	}

	public Term getTerm() {
		return term;
	}

	public UUID getID() {
		return CourseID;
	}

	public ArrayList<GradeSection> getSections() {
		return sections;
	}

	public ArrayList<Exam> getExams() {
		ArrayList<Exam> exams = new ArrayList<Exam>();
		for (Activity a : activities) {
			if (a.getType() == Activity.exam)
				exams.add((Exam) a);
		}
		return exams;
	}

	public double getPercentage() {
		if (sections.size() == 0)
			return -1;
		double total = 0;
		double totalWeight = 0;
		ArrayList<GradeSection> counts = new ArrayList<>();
		for (GradeSection s : sections) {
			if ((s.isExam() && s.getExam().isRecorded())
					|| (s.getAssessments() != null && s.getAssessments().size() > 0 && s.getSectionPercentage() >= 0)) {
				totalWeight += s.getWeight();
				counts.add(s);
			}
		}
		if (totalWeight == 0) {
			return -1;
		}
		for (GradeSection s : counts) {
			if (s.getSectionPercentage() == -1)
				continue;
			total += s.getSectionPercentage() * s.getWeight();
		}
		return total / totalWeight;
	}

	public double getSectionsTotal() {
		double sum = 0;
		for (GradeSection s : sections) {
			sum += s.getWeight();
		}
		return sum;
	}

	public ArrayList<GradeSection> getNonExamSections() {
		ArrayList<GradeSection> sections = new ArrayList<>();
		for (GradeSection s : this.sections) {
			if (!s.isExam())
				sections.add(s);
		}
		return sections;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSONID, CourseID.toString());
		json.put(JSONName, name);
		json.put(JSONCode, code);
		json.put(JSONColour, colour);
		JSONArray activities = new JSONArray();
		for (Activity a : this.activities) {
			switch (a.getType()) {
			case Activity.lecture:
				activities.put(((Lecture) a).toJSON());
				continue;
			case Activity.lab:
				activities.put(((Lab) a).toJSON());
				continue;
			case Activity.tutorial:
				activities.put(((Tutorial) a).toJSON());
				continue;
			default:
				activities.put(a.toJSON());
				continue;
			}
		}
		json.put(JSONActivities, activities);
		JSONArray tasks = new JSONArray();
		for (Task t : this.tasks) {
			tasks.put(t.toJSON());
		}
		JSONArray sections = new JSONArray();
		for (GradeSection s : this.sections) {
			sections.put(s.toJSON());
		}
		json.put(JSONSections, sections);
		json.put(JSONTasks, tasks);
		json.put(JSONTerm, term.getID().toString());
		json.put(JSONGradable, gradable);
		return json;
	}

	private boolean isOutsideBreaks(Date date) {
		ArrayList<Break> breaks = term.getBreaks();
		for (Break b : breaks) {
			if (date.compareTo(b.getStartDate()) >= 0 && date.compareTo(b.getEndDate()) <= 0)
				return false;
		}
		return true;
	}
}
