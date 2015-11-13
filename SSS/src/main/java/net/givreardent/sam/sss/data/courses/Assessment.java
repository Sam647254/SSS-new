package net.givreardent.sam.sss.data.courses;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import net.givreardent.sam.sss.util.Gradable;

public class Assessment extends Task implements Gradable {
	private static final String JSONOutOf = "Out of", JSONScore = "Score", JSONSection = "Section ID",
			JSONRecorded = "Recorded";
	private double outof;
	private double score;
	private boolean recorded;
	private GradeSection section;

	public Assessment(GradeSection s) {
		super(s.getCourse());
		setForMarks(true);
		score = 0;
		section = s;
		recorded = false;
	}
	
	public Assessment(JSONObject json, Course course, GradeSection s) throws JSONException {
		super(json, course);
		outof = json.getDouble(JSONOutOf);
		score = json.getDouble(JSONScore);
		section = s;
		try {
			recorded = json.getBoolean(JSONRecorded);
		} catch (JSONException e) {
			recorded = false;
		}
	}
	
	public boolean setScore(double score) {
		if (score > outof) return false;
		this.score = score;
		recorded = true;
		return true;
	}
	
	public boolean setOutOf(double outof) {
		if (outof < score) return false;
		this.outof = outof;
		recorded = true;
		return true;
	}
	
	public double getOutOf() {
		return outof;
	}
	
	public double getScore() {
		return score;
	}
	
	public double getPercentage() {
		return score/outof * 100;
	}

	public GradeSection getSection() {
		return section;
	}
	
	public boolean isRecorded() {
		return recorded;
	}
	
	public void removeRecord() {
		recorded = false;
	}
	
	public String getScoreString() {
		DecimalFormat df = new DecimalFormat("0.#");
		return df.format(score) + "/" + df.format(outof) + " (" + df.format(getPercentage()) + "%)";
	}
	
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = super.toJSON();
		json.put(JSONSection, section.getID().toString());
		json.put(JSONOutOf, outof);
		json.put(JSONScore, score);
		json.put(JSONRecorded, recorded);
		return json;
	}
}
