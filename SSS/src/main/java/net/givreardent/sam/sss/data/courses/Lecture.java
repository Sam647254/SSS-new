package net.givreardent.sam.sss.data.courses;

import org.json.JSONException;
import org.json.JSONObject;

public class Lecture extends Activity {
	private static final String JSONProf = "Professor";
	private String profName;

	public Lecture(Course course) {
		super(course);
		setType(lecture);
	}
	
	public Lecture(JSONObject json, Course course) throws JSONException {
		super(json, course);
		setProfName(json.getString(JSONProf));
	}

	public String getProfName() {
		return profName;
	}

	public void setProfName(String profName) {
		this.profName = profName;
	}
	
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = super.toJSON();
		json.put(JSONProf, profName);
		return json;
	}
}
