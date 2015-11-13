package net.givreardent.sam.sss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.givreardent.sam.sss.data.courses.Assessment;
import net.givreardent.sam.sss.data.courses.AssessmentList;
import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.Task;
import net.givreardent.sam.sss.data.courses.TaskList;
import net.givreardent.sam.sss.data.courses.TermList;
import net.givreardent.sam.sss.data.events.StrayTaskList;
import net.givreardent.sam.sss.dialogs.DeleteTaskFragment;
import net.givreardent.sam.sss.dialogs.RecordScoreFragment;
import net.givreardent.sam.sss.dialogs.TaskCreateFragment;
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

@TargetApi(11)
public class TaskListFragment extends ListFragment implements MultiChoiceModeListener {
	private ArrayList<Task> tasks;
	private ActionMode mode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.task_list_title);
		tasks = SSS.getCurrentTasks(getActivity());
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, parent, false);
		TaskAdapter adapter = new TaskAdapter(tasks);
		setListAdapter(adapter);
		ListView list = (ListView) v.findViewById(android.R.id.list);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		list.setMultiChoiceModeListener(this);
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Task task = (Task) getListAdapter().getItem(position);
		TaskInfoFragment fragment = TaskInfoFragment.newInstance(task);
		fragment.setTargetFragment(this, 0);
		fragment.show(getActivity().getSupportFragmentManager(), "Task info");
	}

	@TargetApi(11)
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			resetAdapter();
			TermList.get(getActivity()).saveTerms();
		}
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				TaskAdapter adapter = (TaskAdapter) getListAdapter();
				for (int i = adapter.getCount() - 1; i >= 0; i--) {
					if (getListView().isItemChecked(i)) {
						Task task = adapter.getItem(i);
						if (task.getCourse() != null) {
							if (task.isForMarks())
								AssessmentList
										.get(getActivity(), ((Assessment) task).getSection().getID(), task.getCourse())
										.removeAssessment((Assessment) task);
							else
								TaskList.get(getActivity(), task.getCourse()).removeTask(task);
						} else
							StrayTaskList.get(getActivity()).removeTask(task.getID());
					}
				}
				mode.finish();
				resetAdapter();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.tasks_context, menu);
	}

	public void resetAdapter() {
		int index = getListView().getFirstVisiblePosition();
		View topView = getListView().getChildAt(0);
		int top;
		if (topView == null)
			top = 0;
		else
			top = topView.getTop();
		tasks = SSS.getCurrentTasks(getActivity());
		setListAdapter(new TaskAdapter(tasks));
		getListView().setSelectionFromTop(index, top);
	}

	private class TaskViewHolder {
		TextView title;
		TextView dueDate;
		CheckBox isComplete;
		TextView score;
		TextView course;
		View colourTile;
	}

	private class TaskAdapter extends ArrayAdapter<Task> {
		public TaskAdapter(ArrayList<Task> tasks) {
			super(getActivity(), 0, tasks);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskViewHolder holder;
			if (convertView == null) {
				holder = new TaskViewHolder();
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_task_all, null);
				holder.title = (TextView) convertView.findViewById(R.id.task_name_list);
				holder.dueDate = (TextView) convertView.findViewById(R.id.task_due_time_list);
				holder.score = (TextView) convertView.findViewById(R.id.task_grade);
				holder.isComplete = (CheckBox) convertView.findViewById(R.id.task_is_finished_checkBox);
				holder.course = (TextView) convertView.findViewById(R.id.task_course_list);
				holder.colourTile = convertView.findViewById(R.id.task_colour_list);
				convertView.setTag(holder);
			} else
				holder = (TaskViewHolder) convertView.getTag();
			Task task = getItem(position);
			if (task.isOverdue() && !task.isDone())
				holder.title.setTextColor(Course.red[2]);
			holder.title.setText(task.getName());
			DateFormat dFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
			DateFormat dateFormat = DateFormat.getDateInstance();
			holder.dueDate.setText(dateFormat.format(task.getDueDate()) + " - " + dFormat.format(task.getDueDate()));

			if (Build.VERSION.SDK_INT < 11) {
				holder.colourTile.setVisibility(View.GONE);
			}
			holder.isComplete.setChecked(task.isDone());
			if (task.isDone()) {
				holder.title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			} else {
				holder.title.setPaintFlags(0);
			}
			if (task.isForMarks()) {
				if (((Assessment) task).isRecorded()) {
					holder.isComplete.setVisibility(View.GONE);
					holder.score.setVisibility(View.VISIBLE);
					holder.score.setText(((Assessment) task).getScoreString());
				} else {
					holder.isComplete.setVisibility(View.VISIBLE);
					holder.score.setVisibility(View.GONE);
				}
			}

			if (task.getCourse() != null) {
				holder.colourTile.setBackgroundColor(task.getCourse().getColour());
				holder.course.setText(task.getCourse().getCode());
				holder.course.setVisibility(View.VISIBLE);
			} else {
				holder.course.setVisibility(View.GONE);
				holder.colourTile.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
			}
			return convertView;
		}
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {

	}

	@TargetApi(11)
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.tasks_context, menu);
		return true;
	}

	@TargetApi(11)
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		this.mode = mode;
		switch (item.getItemId()) {
		case R.id.edit_entry:
			TaskAdapter adapter = (TaskAdapter) getListAdapter();
			Task task2 = null;
			for (int i = 0; i < adapter.getCount(); i++) {
				if (getListView().isItemChecked(i)) {
					task2 = adapter.getItem(i);
					break;
				}
			}
			TaskCreateFragment dialog2 = null;
			if (task2.getCourse() == null)
				dialog2 = TaskCreateFragment.newInstance(task2.getID(), null, null);
			else if (task2.isForMarks()) {
				if (((Assessment) task2).isRecorded()) {
					RecordScoreFragment dialog = RecordScoreFragment.newInstance((Assessment) task2);
					dialog.setTargetFragment(this, 0);
					dialog.show(getActivity().getSupportFragmentManager(), "Edit score");
					mode.finish();
					return true;
				} else
					dialog2 = TaskCreateFragment.newInstance(task2.getID(), ((Assessment) task2).getSection().getID(),
							task2.getCourse().getID(), task2.getCourse().getTerm().getID());
			} else
				dialog2 = TaskCreateFragment.newInstance(task2.getID(), task2.getCourse().getID(),
						task2.getCourse().getTerm().getID());
			dialog2.show(getActivity().getSupportFragmentManager(), "Edit task");
			mode.finish();
			return true;
		case R.id.delete_entry:
			adapter = (TaskAdapter) getListAdapter();
			boolean hasRecorded = false;
			for (int i = adapter.getCount() - 1; i >= 0; i--) {
				Task task = adapter.getItem(i);
				if (task.isForMarks())
					if (((Assessment) task).isRecorded()) {
						hasRecorded = true;
						break;
					}
			}
			Log.d("tag", "Has recorded: " + hasRecorded);
			if (hasRecorded) {
				DeleteTaskFragment dialog = new DeleteTaskFragment();
				dialog.setTargetFragment(this, 1);
				dialog.show(getActivity().getSupportFragmentManager(), "Confirm deletion");
			} else {
				for (int i = adapter.getCount() - 1; i >= 0; i--) {
					if (getListView().isItemChecked(i)) {
						Task task = adapter.getItem(i);
						Log.d("tag", "Deleting: " + task.getName());
						if (task.getCourse() != null) {
							if (task.isForMarks())
								AssessmentList
										.get(getActivity(), ((Assessment) task).getSection().getID(), task.getCourse())
										.removeAssessment((Assessment) task);
							else
								TaskList.get(getActivity(), task.getCourse()).removeTask(task);
						} else
							StrayTaskList.get(getActivity()).removeTask(task.getID());
					}
				}
				StrayTaskList.get(getActivity()).saveTask();
				mode.finish();
				resetAdapter();
			}
		}
		return true;
	}

	@TargetApi(11)
	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
		if (getListView().getCheckedItemCount() > 1) {
			mode.getMenu().findItem(R.id.edit_entry).setEnabled(false).setVisible(false);
		} else {
			mode.getMenu().findItem(R.id.edit_entry).setEnabled(true).setVisible(true);
		}
	}
}
