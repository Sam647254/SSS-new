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

import net.givreardent.sam.sss.data.courses.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

public class TaskJSONSerializer {
	private Context context;
	private String filename;
	
	public TaskJSONSerializer(Context context, String filename) {
		this.context = context;
		this.filename = filename;
	}
	
	public ArrayList<Task> loadTasks() throws JSONException, IOException {
		ArrayList<Task> tasks = new ArrayList<>();
		BufferedReader reader = null;
		try {
			InputStream in = context.openFileInput(filename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder JSONString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) { 
				JSONString.append(line);
			}
			JSONArray array = (JSONArray) new JSONTokener(JSONString.toString()).nextValue();
			for (int i = 0; i < array.length(); i++) {
				tasks.add(new Task(array.getJSONObject(i), null));
			}
		} catch (FileNotFoundException e) {
			
		} finally {
			if (reader!= null)
				reader.close();
		}
		return tasks;
	}
	
	public void saveTasks(ArrayList<Task> tasks) throws JSONException, IOException {
		JSONArray array = new JSONArray();
		for (Task t: tasks) {
			array.put(t.toJSON());
		}
		Writer writer = null;
		try {
			OutputStream out = context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
