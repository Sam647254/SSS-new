package net.givreardent.sam.sss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.givreardent.sam.sss.data.courses.Assessment;
import net.givreardent.sam.sss.data.courses.AssessmentList;
import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.Task;
import net.givreardent.sam.sss.data.courses.TaskList;
import net.givreardent.sam.sss.data.events.StrayTaskList;
import net.givreardent.sam.sss.dialogs.DeleteTaskFragment;
import net.givreardent.sam.sss.dialogs.TaskInfoFragment;
import net.givreardent.sam.sss.util.SSS;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class TaskListFragment11 extends ListFragment {
	private ArrayList<Task> tasks;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.task_list_title);
		tasks = SSS.getAllTasks(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, parent, false);
		TaskAdapter adapter = new TaskAdapter(tasks);
		setListAdapter(adapter);
		ListView list = (ListView) v.findViewById(android.R.id.list);
		registerForContextMenu(list);
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Task task = (Task) getListAdapter().getItem(position);
		TaskInfoFragment fragment = TaskInfoFragment.newInstance(task);
		fragment.setTargetFragment(this, 0);
		fragment.show(getActivity().getSupportFragmentManager(), "Task info");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0)
			resetAdapter();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.tasks_context, menu);
	}

	public void resetAdapter() {
		tasks = SSS.getAllTasks(getActivity());
		setListAdapter(new TaskAdapter(tasks));
	}

	private class TaskAdapter extends ArrayAdapter<Task> {
		public TaskAdapter(ArrayList<Task> tasks) {
			super(getActivity(), 0, tasks);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_task_all, null);
			Task task = getItem(position);
			TextView title = (TextView) convertView.findViewById(R.id.task_name_list);
			if (task.isOverdue() && !task.isDone())
				title.setTextColor(Course.red[2]);
			title.setText(task.getName());
			SimpleDateFormat dFormat = new SimpleDateFormat("H:mm");
			DateFormat dateFormat = DateFormat.getDateInstance();
			TextView dueDate = (TextView) convertView.findViewById(R.id.task_due_time_list);
			dueDate.setText(dateFormat.format(task.getDueDate()) + " - " + dFormat.format(task.getDueDate()));
			View colourTile = convertView.findViewById(R.id.task_colour_list);
			if (Build.VERSION.SDK_INT < 11) {
				colourTile.setVisibility(View.GONE);
			}
			CheckBox isComplete = (CheckBox) convertView.findViewById(R.id.task_is_finished_checkBox);
			isComplete.setChecked(task.isDone());
			if (task.isDone()) {
				title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			} else {
				title.setPaintFlags(0);
			}
			TextView course = (TextView) convertView.findViewById(R.id.task_course_list);
			if (task.getCourse() != null) {
				colourTile.setBackgroundColor(task.getCourse().getColour());
				course.setText(task.getCourse().getCode());
				course.setVisibility(View.VISIBLE);
			}
			return convertView;
		}
	}
}
