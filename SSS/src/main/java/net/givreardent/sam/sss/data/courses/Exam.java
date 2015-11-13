package net.givreardent.sam.sss.data.courses;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import net.givreardent.sam.sss.util.Gradable;

public class Exam extends Activity implements Gradable {
	private static final String JSONOutOf = "Out of", JSONScore = "Score", JSONForMarks = "For marks?",
			JSONRecorded = "Recorded?", JSONSection = "Section";
	private double outof;
	private double score;
	private boolean forMarks;
	private boolean recorded;
	private GradeSection section;
	private UUID sectionID;

	public Exam(Course course) {
		super(course);
		setType(exam);
		setRecurrence(noRecurrence);
		outof = score = 0;
		recorded = false;
		section = null;
	}

	public Exam(JSONObject json, Course course, Context context) throws JSONException {
		super(json, course);
		outof = json.getInt(JSONOutOf);
		score = json.getInt(JSONScore);
		forMarks = json.getBoolean(JSONForMarks);
		try {
			recorded = json.getBoolean(JSONRecorded);
			sectionID = UUID.fromString(json.getString(JSONSection));
			Log.d("tag", "Section ID loaded: " + sectionID.toString());
		} catch (JSONException e) {
			Log.e("tag", "Error loading exam parts: ", e);
			recorded = false;
			sectionID = null;
		}
	}

	public boolean setScore(double score) {
		recorded = true;
		this.score = score;
		return true;
	}

	public boolean setOutOf(double outof) {
		this.outof = outof;
		return true;
	}

	public double getOutOf() {
		return outof;
	}
	
	public GradeSection getSection() {
		if (sectionID == null && section == null) return null;
		if (section == null)
			section = SectionList.get(null, getCourse().getID(), getCourse().getTerm().getID()).getSection(sectionID);
		return section;
	}
	
	public void setSection(GradeSection section) {
		this.section = section;
		Log.i("tag", "Section set: " + section.getName());
	}
	
	public void removeSection() {
		section = null;
	}

	public double getScore() {
		return score;
	}
	
	public boolean isRecorded() {
		return recorded;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = super.toJSON();
		json.put(JSONOutOf, outof);
		json.put(JSONScore, score);
		json.put(JSONForMarks, forMarks);
		json.put(JSONRecorded, recorded);
		if (getSection() != null) {
			Log.d("tag", "Saved section ID.");
			json.put(JSONSection, getSection().getID());
		}
		Log.d("tag", "Saved exam: " + getName());
		return json;
	}

	public boolean isForMarks() {
		return forMarks;
	}

	public void setForMarks(boolean forMarks) {
		this.forMarks = forMarks;
	}
	
	public void removeRecord() {
		recorded = false;
	}
}
