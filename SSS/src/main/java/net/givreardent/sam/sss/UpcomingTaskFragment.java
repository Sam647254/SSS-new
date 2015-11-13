package net.givreardent.sam.sss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.givreardent.sam.sss.data.courses.Assessment;
import net.givreardent.sam.sss.data.courses.AssessmentList;
import net.givreardent.sam.sss.data.courses.Task;
import net.givreardent.sam.sss.data.courses.TaskList;
import net.givreardent.sam.sss.data.courses.TermList;
import net.givreardent.sam.sss.data.events.StrayTaskList;
import net.givreardent.sam.sss.dialogs.TaskCreateFragment;
import net.givreardent.sam.sss.dialogs.TaskInfoFragment;
import net.givreardent.sam.sss.util.SSS;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class UpcomingTaskFragment extends ListFragment {
	private ArrayList<Task> tasks;
	private ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tasks = SSS.getAllUpcomingTasks(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_upcoming_task, container, false);
		list = (ListView) v.findViewById(android.R.id.list);
		list.setEmptyView(v.findViewById(android.R.id.empty));
		registerForContextMenu(list);
		TaskAdapter adapter = new TaskAdapter(tasks);
		setListAdapter(adapter);
		return v;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(2, R.id.edit_entry, 0, R.string.edit);
		menu.add(2, R.id.delete_entry, 1, R.string.delete_entry);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() != 2)
			return false;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		Task task = ((TaskAdapter) getListAdapter()).getItem(position);
		switch (item.getItemId()) {
		case R.id.edit_entry:
			TaskCreateFragment fragment;
			if (task.isForMarks()) {
				fragment = TaskCreateFragment.newInstance(task.getID(), ((Assessment) task).getSection().getID(), task
						.getCourse().getID(), task.getCourse().getTerm().getID());

			} else if (task.getCourse() == null) {
				fragment = TaskCreateFragment.newInstance(task.getID(), null, null);
			} else {
				fragment = TaskCreateFragment.newInstance(task.getID(), task.getCourse().getID(), task.getCourse()
						.getTerm().getID());
			}
			fragment.setTargetFragment(this, 0);
			fragment.show(getActivity().getSupportFragmentManager(), "Edit task");
			return true;
		case R.id.delete_entry:
			if (task.isForMarks()) {
				AssessmentList.get(getActivity(), ((Assessment) task).getSection().getID(), task.getCourse())
						.removeAssessment((Assessment) task);
			} else if (task.getCourse() == null) {
				StrayTaskList.get(getActivity()).removeTask(task.getID());
			} else {
				TaskList.get(getActivity(), task.getCourse()).removeTask(task);
			}
			resetAdapter();
			return true;
		}
		return super.onContextItemSelected(item);
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
		if (requestCode == 0) {
			TermList.get(getActivity()).saveTerms();
			resetAdapter();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("tag", "on Pause called.");
		TermList.get(getActivity()).saveTerms();
	}

	void resetAdapter() {
		int index = getListView().getFirstVisiblePosition();
		View topView = getListView().getChildAt(0);
		int top;
		if (topView == null)
			top = 0;
		else
			top = topView.getTop();
		tasks = SSS.getAllUpcomingTasks(getActivity());
		setListAdapter(new TaskAdapter(tasks));
		getListView().setSelectionFromTop(index, top);
	}

	private class TaskAdapter extends ArrayAdapter<Task> {
		public TaskAdapter(ArrayList<Task> tasks) {
			super(getActivity(), 0, tasks);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_task, null);
			Task task = getItem(position);
			TextView title = (TextView) convertView.findViewById(R.id.task_name_list);
			title.setText(task.getName());
			DateFormat dFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
			DateFormat dateFormat = DateFormat.getDateInstance();
			TextView dueDate = (TextView) convertView.findViewById(R.id.task_due_time_list);
			dueDate.setText(dateFormat.format(task.getDueDate()) + " - " + dFormat.format(task.getDueDate()));
			View colourTile = convertView.findViewById(R.id.task_colour_list);
			if (Build.VERSION.SDK_INT < 11) {
				colourTile.setVisibility(View.GONE);
			}
			TextView course = (TextView) convertView.findViewById(R.id.task_course_list);
			if (task.getCourse() != null) {
				colourTile.setBackgroundColor(task.getCourse().getColour());
				course.setText(task.getCourse().getCode());
				course.setVisibility(View.VISIBLE);
			} else {
				colourTile.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
				course.setVisibility(View.GONE);
			}
			return convertView;
		}
	}
}
