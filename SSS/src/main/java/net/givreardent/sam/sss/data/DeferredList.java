package net.givreardent.sam.sss.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.events.StrayActivityList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class DeferredList {
	private Context context;
	private static DeferredList sList;
	private ArrayList<DeferredActivity> activities;

	private DeferredList(Context c) {
		context = c;
		try {
			activities = loadList();
		} catch (IOException e) {
			Log.e("tag", "Error loading deferred list", e);
		}
	}

	public static DeferredList get(Context c) {
		if (sList == null)
			sList = new DeferredList(c);
		return sList;
	}

	public void addActivity(Activity a, Activity target, boolean isToday) {
		DeferredActivity activity = new DeferredActivity();
		activity.ID = a.getID();
		Calendar calendar = Calendar.getInstance();
		if (!isToday)
			calendar.add(5, 1);
		activity.originalDate = calendar.getTime();
		activity.target = target;
		activities.add(activity);
		saveList();
	}

	public void addActivity(Activity a, Activity target, Date date) {
		DeferredActivity activity = new DeferredActivity();
		activity.ID = a.getID();
		activity.originalDate = date;
		activity.target = target;
		activities.add(activity);
		saveList();
	}

	public boolean isInList(Activity a, boolean isToday) {
		for (DeferredActivity activity : activities) {
			if (activity.ID.equals(a.getID())) {
				Calendar calendar = Calendar.getInstance(), calendar2 = Calendar.getInstance();
				if (!isToday)
					calendar.add(5, 1);
				calendar2.setTime(activity.originalDate);
				if (calendar.get(1) == calendar2.get(1) && calendar.get(2) == calendar2.get(2)
						&& calendar.get(5) == calendar.get(5))
					return true;
			}
		}
		return false;
	}

	public boolean isInList(Activity a, Date date) {
		for (DeferredActivity activity : activities) {
			if (activity.ID.equals(a.getID())) {
				Calendar calendar = Calendar.getInstance(), calendar2 = Calendar.getInstance();
				calendar.setTime(date);
				calendar2.setTime(activity.originalDate);
				if (calendar.get(1) == calendar2.get(1) && calendar.get(2) == calendar2.get(2)
						&& calendar.get(5) == calendar.get(5))
					return true;
			}
		}
		return false;
	}

	public boolean isTargetActivity(Activity activity) {
		for (DeferredActivity a : activities) {
			if (a.target.getID().equals(activity.getID())) {
				Log.i("tag", "is target activity!");
				return true;
			}
		}
		return false;
	}

	public Activity getTargetActivity(Activity activity) {
		for (DeferredActivity a : activities) {
			if (a.ID.equals(activity.getID())) {
				return a.target;
			}
		}
		return null;
	}

	public void removeActivity(Activity targetActivity) {
		for (DeferredActivity a : activities) {
			if (a.target.getID().equals(targetActivity.getID())) {
				StrayActivityList.get(context).removeActivity(targetActivity);
				activities.remove(a);
				saveList();
				Log.i("tag", "Deferred activity removed.");
				return;
			}
		}
	}

	public void removeActivity(UUID ID) {
		for (DeferredActivity a : activities) {
			if (a.ID.equals(ID)) {
				StrayActivityList.get(context).removeActivity(a.target);
				activities.remove(a);
				saveList();
				return;
			}
		}
	}

	private ArrayList<DeferredActivity> loadList() throws IOException {
		ArrayList<DeferredActivity> list = new ArrayList<DeferredActivity>();
		BufferedReader reader = null;
		try {
			InputStream in = context.openFileInput("Deferred List.json");
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder JSONString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				JSONString.append(line);
			}
			JSONArray array = (JSONArray) new JSONTokener(JSONString.toString()).nextValue();
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				DeferredActivity activity = new DeferredActivity();
				activity.ID = UUID.fromString(object.getString(DeferredActivity.JSONID));
				activity.originalDate = new Date(object.getLong(DeferredActivity.JSONDate));
				activity.target = StrayActivityList.get(context).getActivity(
						UUID.fromString(object.getString(DeferredActivity.JSONTargetID)));
				list.add(activity);
			}
		} catch (Exception e) {
			Log.e("tag", "Error loading deferred list", e);
		} finally {
			if (reader != null)
				reader.close();
		}
		return list;
	}

	private void saveList() {
		JSONArray array = new JSONArray();
		Writer writer = null;
		try {
			for (DeferredActivity a : activities) {
				array.put(a.toJSON());
			}
			OutputStream out = context.openFileOutput("Deferred List.json", Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
			Log.i("tag", "Saved: " + array.length());
		} catch (Exception e) {
			Log.e("tag", "Error saving deferred list", e);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {

				}
		}
	}

	private final class DeferredActivity {
		public static final String JSONID = "JSONID", JSONDate = "Date", JSONTargetID = "Target",
				JSONReminder = "Reminder";

		public UUID ID;
		public Date originalDate;
		public Activity target;
		public int reminder = Activity.defaultReminder;

		public JSONObject toJSON() throws JSONException {
			JSONObject object = new JSONObject();
			object.put(JSONID, ID.toString());
			object.put(JSONDate, originalDate.getTime());
			object.put(JSONTargetID, target.getID());
			object.put(JSONReminder, reminder);
			return object;
		}
	}
}
