package net.givreardent.sam.sss.data.courses;

import org.json.JSONException;
import org.json.JSONObject;

public class Lab extends Activity {
	private static final String JSONSupervisor = "Supervisor";
	private String supervisorName;

	public Lab(Course course) {
		super(course);
		setType(lab);
	}
	
	public Lab(JSONObject json, Course course) throws JSONException {
		super(json, course);
		setSupervisorName(json.getString(JSONSupervisor));
	}

	public String getSupervisorName() {
		return supervisorName;
	}

	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = super.toJSON();
		json.put(JSONSupervisor, supervisorName);
		return json;
	}
}
