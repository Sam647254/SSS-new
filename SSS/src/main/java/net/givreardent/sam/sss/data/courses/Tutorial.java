package net.givreardent.sam.sss.data.courses;

import org.json.JSONException;
import org.json.JSONObject;

public class Tutorial extends Activity {
	private static final String JSONTutor = "Tutor";
	private String tutorName;

	public Tutorial(Course course) {
		super(course);
		setType(tutorial);
	}
	
	public Tutorial(JSONObject json, Course course) throws JSONException {
		super(json, course);
		setTutorName(json.getString(JSONTutor));
	}

	public String getTutorName() {
		return tutorName;
	}

	public void setTutorName(String tutorName) {
		this.tutorName = tutorName;
	}
	
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = super.toJSON();
		json.put(JSONTutor, tutorName);
		return json;
	}

}
