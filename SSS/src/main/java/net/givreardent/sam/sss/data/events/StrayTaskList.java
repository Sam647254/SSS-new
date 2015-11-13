package net.givreardent.sam.sss.data.events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONException;

import net.givreardent.sam.sss.JSONIO.TaskJSONSerializer;
import net.givreardent.sam.sss.data.courses.Task;
import android.content.Context;
import android.util.Log;

public class StrayTaskList {
	private static final String filename = "Stray tasks.json";
	private Context mContext;
	private static StrayTaskList sList;
	private ArrayList<Task> tasks;
	private TaskJSONSerializer serializer;
	
	private StrayTaskList(Context context) {
		mContext = context;
		serializer = new TaskJSONSerializer(context, filename);
		try {
			tasks = serializer.loadTasks();
		} catch (Exception e) {
			tasks = new ArrayList<>();
		}
	}
	
	public static StrayTaskList get(Context c) {
		if (sList == null)
			sList = new StrayTaskList(c);
		return sList;
	}
	
	public void addTask(Task task) {
		tasks.add(task);
		saveTask();
	}
	
	public void saveTask() {
		try {
			serializer.saveTasks(tasks);
		} catch (JSONException | IOException e) {
			Log.e("tag", "Save stray tasks failed.", e);
		}
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}
	
	public void removeTask(UUID ID) {
		for (Task t: tasks) {
			if (t.getID().equals(ID)) {
				tasks.remove(t);
				saveTask();
				return;
			}
		}
	}
	
	public Task getTask(UUID ID) {
		for (Task t: tasks) {
			if (t.getID().equals(ID))
				return t;
		}
		return null;
	}
}
