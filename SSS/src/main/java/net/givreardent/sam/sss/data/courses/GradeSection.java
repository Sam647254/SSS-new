package net.givreardent.sam.sss.data.courses;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class GradeSection {
	private static final String JSONID = "ID", JSONCourse = "Course", JSONWeight = "Weight", JSONName = "Name",
			JSONExam = "Is exam", JSONAssessments = "Assessments", JSONExamID = "Exam";

	private final UUID sectionID;
	private Course course;
	private double weight;
	private String name;
	private boolean isExam;
	private UUID examID;
	private Exam exam;
	private ArrayList<Assessment> assessments;

	public GradeSection(String n, double w, Course course) {
		sectionID = UUID.randomUUID();
		name = n;
		weight = w;
		assessments = new ArrayList<Assessment>();
		isExam = false;
		this.course = course;
	}

	public GradeSection(JSONObject json, Course course, Context context) throws JSONException {
		sectionID = UUID.fromString(json.getString(JSONID));
		this.course = course;
		weight = json.getDouble(JSONWeight);
		name = json.getString(JSONName);
		isExam = json.getBoolean(JSONExam);
		if (!isExam) {
			JSONArray array = json.getJSONArray(JSONAssessments);
			assessments = new ArrayList<Assessment>();
			for (int i = 0; i < array.length(); i++) {
				assessments.add(new Assessment(array.getJSONObject(i), course, this));
			}
		} else {
			examID = UUID.fromString(json.getString(JSONExamID));
		}
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void addAssessment(Assessment a) {
		assessments.add(a);
	}

	public void setName(String n) {
		name = n;
	}

	public void setIsExam(boolean isExam) {
		this.isExam = isExam;
	}

	public double getWeight() {
		return weight;
	}

	public String getName() {
		return name;
	}

	public UUID getID() {
		return sectionID;
	}

	public boolean isExam() {
		return isExam;
	}

	public Course getCourse() {
		return course;
	}

	public ArrayList<Assessment> getAssessments() {
		return assessments;
	}

	public Exam getExam() {
		if (exam == null)
			exam = (Exam) ActivityList.get(null, course.getID(), course.getTerm().getID()).getActivity(examID);
		return exam;
	}

	public void setExam(Exam e) {
		examID = e.getID();
		exam = e;
		e.setSection(this);
		Log.d("tag", "in GradeSection, setting section of exam " + e.getName());
	}

	public double getSectionPercentage() {
		if (assessments != null) {
			if (assessments.size() == 0)
				return 0;
			double total = 0;
			int size = 0;
			for (Assessment a : assessments) {
				if (a.isRecorded()) {
					total += a.getPercentage();
					size++;
				}
			}
			if (size == 0)
				return -1;
			return total / size;
		} else {
			return exam.getScore() * 100 / exam.getOutOf();
		}
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSONID, sectionID.toString());
		json.put(JSONCourse, course.getID().toString());
		json.put(JSONWeight, weight);
		json.put(JSONName, name);
		json.put(JSONExam, isExam);
		if (!isExam) {
			JSONArray assessments = new JSONArray();
			for (Assessment a : this.assessments) {
				assessments.put(a.toJSON());
			}
			json.put(JSONAssessments, assessments);
		} else {
			if (getExam() != null)
				json.put(JSONExamID, getExam().getID());
		}
		return json;
	}

	@Override
	public String toString() {
		return name;
	}
}
