package net.givreardent.sam.sss.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import net.givreardent.sam.sss.data.courses.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public final class CustomReminderList {
	private static CustomReminderList list;
	private ArrayList<CustomReminder> reminderList;
	private Context context;

	private CustomReminderList(Context c) {
		context = c;
		loadList();
	}

	public static CustomReminderList get(Context c) {
		if (list == null)
			list = new CustomReminderList(c);
		return list;
	}

	public void addCustomReminder(Activity activity, Date date, int reminder) {
		CustomReminder newReminder = new CustomReminder();
		newReminder.activityID = activity.getID();
		newReminder.date = date;
		newReminder.reminder = reminder;
		reminderList.add(newReminder);
		saveList();
	}

	public boolean isInList(Activity activity, Date date) {
		for (CustomReminder cr : reminderList) {
			if (cr.activityID.equals(activity.getID())) {
				if (cr.date.equals(date)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int getCustomReminder(Activity activity, Date date) {
		for (CustomReminder cr : reminderList) {
			if (cr.activityID.equals(activity.getID())) {
				if (cr.date.equals(date))
					return cr.reminder;
			}
		}
		return -1;
	}
	
	public void removeCustomReminder(Activity activity) {
		for (CustomReminder cr: reminderList) {
			if (cr.activityID.equals(activity.getID())) {
				reminderList.remove(cr);
				saveList();
			}
		}
	}
	
	public void removeCustomReminder(Activity activity, Date date) {
		for (CustomReminder cr : reminderList) {
			if (cr.activityID.equals(activity.getID())) {
				if (cr.date.equals(date)) {
					reminderList.remove(cr);
					saveList();
					return;
				}
			}
		}
	}
	
	private void saveList() {
		JSONArray array = new JSONArray();
		Writer writer = null;
		try {
			for (CustomReminder cr : reminderList) {
				array.put(cr.toJSON());
			}
			OutputStream out = context.openFileOutput("Reminder list.json", Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} catch (IOException | JSONException ioe) {
			Log.e("CustomReminderList", "Error trying to save:", ioe);
		}
	}
	
	private void loadList() {
		ArrayList<CustomReminder> list = new ArrayList<>();
		BufferedReader reader = null;
		try {
			InputStream in = context.openFileInput("Reminder list.json");
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder JSONString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
				JSONString.append(line);
			JSONArray array = (JSONArray) new JSONTokener(JSONString.toString()).nextValue();
			for (int i = 0; i < array.length(); ++i) {
				CustomReminder newReminder = new CustomReminder(array.getJSONObject(i));
				list.add(newReminder);
			}
		} catch (IOException | JSONException ioe) {
			Log.e("CustomReminderList", "Error loading list:", ioe);
		} finally {
			reminderList = list;
		}
	}

	private class CustomReminder {
		private static final String JSONID = "ID";
		private static final String JSONDate = "Date";
		private static final String JSONReminder = "Reminder";
		
		UUID activityID;
		Date date;
		int reminder;
		
		public CustomReminder() {
			
		}
		
		public CustomReminder(JSONObject json) throws JSONException {
			activityID = UUID.fromString(json.getString(JSONID));
			date = new Date(json.getLong(JSONDate));
			reminder = json.getInt(JSONReminder);
		}
		
		public JSONObject toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(JSONID, activityID.toString());
			json.put(JSONDate, date.getTime());
			json.put(JSONReminder, reminder);
			return json;
		}
	}
}
