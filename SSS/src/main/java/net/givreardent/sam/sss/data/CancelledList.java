package net.givreardent.sam.sss.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import net.givreardent.sam.sss.data.courses.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class CancelledList {
	private static final String JSONID = "ID", JSONDate = "Date";
	private Context context;
	private static CancelledList sList;
	private ArrayList<AbstractMap.SimpleEntry<UUID, Date>> cancelledActivities;

	private CancelledList(Context c) {
		context = c;
		cancelledActivities = loadActivities();
	}

	public static CancelledList get(Context c) {
		if (sList == null)
			sList = new CancelledList(c);
		return sList;
	}

	public void addCancelledActivity(Activity a, boolean isToday) {
		if (isToday)
			cancelledActivities
					.add(new AbstractMap.SimpleEntry<UUID, Date>(a.getID(), Calendar.getInstance().getTime()));
		else {
			Calendar calendar = Calendar.getInstance();
			calendar.add(5, 1);
			cancelledActivities.add(new AbstractMap.SimpleEntry<UUID, Date>(a.getID(), calendar.getTime()));
		}
		try {
			saveActivities();
		} catch (IOException e) {
			Log.e("tag", "Error saving cancelled list", e);
		}
		Log.d("tag", "Activity cancelled.");
	}

	public void addCancelledActivity(Activity a, Date date) {
		cancelledActivities.add(new AbstractMap.SimpleEntry<UUID, Date>(a.getID(), date));
		try {
			saveActivities();
		} catch (IOException e) {
			Log.e("tag", "Error saving cancelled list", e);
		}
		Log.d("tag", "Activity cancelled.");
	}

	public void removeActivity(Activity a, boolean isToday) {
		Calendar calendar = Calendar.getInstance(), calendar2 = Calendar.getInstance();
		if (!isToday) {
			calendar.add(5, 1);
		}
		for (AbstractMap.SimpleEntry<UUID, Date> s : cancelledActivities) {
			calendar2.setTime(s.getValue());
			if (a.getID().equals(s.getKey()) && calendar.get(1) == calendar2.get(1)
					&& calendar.get(2) == calendar2.get(2) && calendar.get(5) == calendar2.get(5)) {
				cancelledActivities.remove(s);
				try {
					saveActivities();
				} catch (IOException e) {
					Log.e("tag", "Error saving cancelled list", e);
				}
				return;
			}
		}
	}
	
	public void removeActivity(Activity a, Date date) {
		Calendar calendar = Calendar.getInstance(), calendar2 = Calendar.getInstance();
		calendar.setTime(date);
		for (AbstractMap.SimpleEntry<UUID, Date> s : cancelledActivities) {
			calendar2.setTime(s.getValue());
			if (a.getID().equals(s.getKey()) && calendar.get(1) == calendar2.get(1)
					&& calendar.get(2) == calendar2.get(2) && calendar.get(5) == calendar2.get(5)) {
				cancelledActivities.remove(s);
				try {
					saveActivities();
				} catch (IOException e) {
					Log.e("tag", "Error saving cancelled list", e);
				}
				return;
			}
		}
	}

	public boolean isInList(Activity a, boolean isToday) {
		Calendar calendar = Calendar.getInstance(), calendar2 = Calendar.getInstance();
		if (!isToday)
			calendar.add(5, 1);
		for (AbstractMap.SimpleEntry<UUID, Date> s : cancelledActivities) {
			calendar2.setTime(s.getValue());
			if (a.getID().equals(s.getKey()) && calendar.get(1) == calendar2.get(1)
					&& calendar.get(2) == calendar2.get(2) && calendar.get(5) == calendar2.get(5))
				return true;
		}
		return false;
	}
	
	public boolean isInList(Activity a, Date date) {
		Calendar calendar = Calendar.getInstance(), calendar2 = Calendar.getInstance();
		calendar.setTime(date);
		for (AbstractMap.SimpleEntry<UUID, Date> s : cancelledActivities) {
			calendar2.setTime(s.getValue());
			if (a.getID().equals(s.getKey()) && calendar.get(1) == calendar2.get(1)
					&& calendar.get(2) == calendar2.get(2) && calendar.get(5) == calendar2.get(5))
				return true;
		}
		return false;
	}

	private ArrayList<AbstractMap.SimpleEntry<UUID, Date>> loadActivities() {
		ArrayList<AbstractMap.SimpleEntry<UUID, Date>> list = new ArrayList<AbstractMap.SimpleEntry<UUID, Date>>();
		BufferedReader reader = null;
		try {
			InputStream in = context.openFileInput("Cancelled list.json");
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder JSONString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
				JSONString.append(line);
			JSONArray array = (JSONArray) new JSONTokener(JSONString.toString()).nextValue();
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				UUID ID = UUID.fromString(object.getString(JSONID));
				Date date = new Date(object.getLong(JSONDate));
				AbstractMap.SimpleEntry<UUID, Date> entry = new AbstractMap.SimpleEntry<UUID, Date>(ID, date);
				list.add(entry);
			}
		} catch (Exception e) {
			Log.e("tag", "Error loading crimes", e);
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					Log.e("tag", "Error loading crimes", e);
				}
		}
		return list;
	}

	private void saveActivities() throws IOException {
		JSONArray array = new JSONArray();
		for (AbstractMap.SimpleEntry<UUID, Date> entry : cancelledActivities) {
			JSONObject object = new JSONObject();
			try {
				object.put(JSONID, entry.getKey().toString());
				object.put(JSONDate, entry.getValue().getTime());
				array.put(object);
			} catch (JSONException e) {
				Log.e("tag", "Error saving cancelled list", e);
				return;
			}
		}

		Writer writer = null;
		try {
			OutputStream out = context.openFileOutput("Cancelled list.json", Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
