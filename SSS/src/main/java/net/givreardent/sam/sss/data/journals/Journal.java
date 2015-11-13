package net.givreardent.sam.sss.data.journals;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Journal {
	private static final String JSON = "ID";
	private static final String JSONTitle = "Title";
	private static final String JSONWeather = "Weather";
	private static final String JSONMood = "Mood";
	private static final String JSONEntry = "Entry";
	private static final String JSONDate = "Date";
	
	public static final int sunny = -1;
	public static final int cloudy = -2;
	public static final int rain = -3;
	public static final int snow = -4;
	public static final int windy = -5;
	
	private UUID mID;
	private String mName;
	private Date mDate;
	private int mWeather;
	private int mMoodRating;
	private String mEntry;
	
	public Journal() {
		mID = UUID.randomUUID();
		mDate = Calendar.getInstance().getTime();
	}
	
	public Journal(JSONObject json) throws JSONException {
		mID = UUID.fromString(json.getString(JSON));
		mDate = new Date(json.getLong(JSONDate));
		mName = json.getString(JSONTitle);
		mWeather = json.getInt(JSONWeather);
		mMoodRating = json.getInt(JSONMood);
		mEntry = json.getString(JSONEntry);
	}
	
	public UUID getID() {
		return mID;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public void setWeather(int weather) {
		mWeather = weather;
	}

	public int getMoodRating() {
		return mMoodRating;
	}

	public void setMoodRating(int moodRating) {
		mMoodRating = moodRating;
	}

	public String getEntry() {
		return mEntry;
	}

	public void setEntry(String entry) {
		mEntry = entry;
	}
	
	public void setDate(Date d) {
		mDate = d;
	}

	public Date getDate() {
		return mDate;
	}

	public int getWeather() {
		return mWeather;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON, mID);
		json.put(JSONDate, mDate.getTime());
		json.put(JSONTitle, mName);
		json.put(JSONMood, mMoodRating);
		json.put(JSONWeather, mWeather);
		json.put(JSONEntry, mEntry);
		return json;
	}
	
	@Override
	public String toString() {
		return mName;
	}
}
