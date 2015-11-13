package net.givreardent.sam.sss.data;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class TimePeriod {
	protected static final String JSONName = "Name";
	protected static final String JSONStart = "Start date";
	protected static final String JSONEnd = "End date";
	protected static final String JSONID = "ID";
	
	private final UUID ID;
	private String name;
	private Date startDate;
	private Date endDate;
	
	public TimePeriod() {
		Calendar calendar = Calendar.getInstance();
		startDate = calendar.getTime();
		calendar.add(5, 1);
		endDate = calendar.getTime();
		ID = UUID.randomUUID();
	}
	
	public TimePeriod(JSONObject json) throws JSONException {
		ID = UUID.fromString(json.getString(JSONID));
		name = json.getString(JSONName);
		startDate = new Date(json.getLong(JSONStart));
		endDate = new Date(json.getLong(JSONEnd));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public UUID getID() {
		return ID;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSONID, ID.toString());
		json.put(JSONName, name);
		json.put(JSONStart, startDate.getTime());
		json.put(JSONEnd, endDate.getTime());
		return json;
	}
}
