package net.givreardent.sam.sss.data.courses;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class TaskList {
	private Context mContext;
	private static TaskList sTaskList;
	private ArrayList<Task> tasks;
	private static Course mCourse;
	
	private TaskList(Context c, Course course) {
		mContext = c;
		tasks = course.getAllTasks();
		mCourse = course;
	}
	
	public static TaskList get(Context c, Course course) {
		if (sTaskList == null || !course.getID().equals(mCourse.getID())) {
			sTaskList = new TaskList(c, course);
		}
		return sTaskList;
	}
	
	public ArrayList<Task> getTasks() {
		return tasks;
	}

	
	public void addTask(Task t) {
		tasks.add(t);
	}
	
	public void removeTask(Task t) {
		tasks.remove(t);
	}
	
	public Task getTask(UUID ID) {
		for (Task task: tasks) {
			if (task.getID().equals(ID))
				return task;
		}
		return null;
	}
}
