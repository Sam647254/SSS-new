package net.givreardent.sam.sss.JSONIO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import net.givreardent.sam.sss.data.courses.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class ActivityJSONSerializer {
	private Context mContext;
	private String mFilename;

	public ActivityJSONSerializer(Context c, String f) {
		mContext = c;
		mFilename = f;
	}

	public ArrayList<Activity> loadActivities() throws IOException, JSONException {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		BufferedReader reader = null;
		try {
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder JSONString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) { 
				JSONString.append(line);
			}
			JSONArray array = (JSONArray) new JSONTokener(JSONString.toString()).nextValue();
			for (int i = 0; i < array.length(); i++) {
				activities.add(new Activity(array.getJSONObject(i), null));
			}
		} catch (FileNotFoundException e) {
			
		} finally {
			if (reader!= null)
				reader.close();
		}
		return activities;
	}

	public void saveActivities(ArrayList<Activity> activities)
			throws JSONException, IOException {
		JSONArray array = new JSONArray();
		for (Activity a : activities) {
			array.put(a.toJSON());
		}
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(mFilename,
					Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
			Log.i("tag", "Stray activity list saved.");
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
