package net.givreardent.sam.sss.dialogs;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

import net.givreardent.sam.sss.R;
import net.givreardent.sam.sss.data.courses.Assessment;
import net.givreardent.sam.sss.data.courses.AssessmentList;
import net.givreardent.sam.sss.data.courses.CourseList;
import net.givreardent.sam.sss.data.courses.Task;
import net.givreardent.sam.sss.data.courses.TaskList;
import net.givreardent.sam.sss.data.events.StrayTaskList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskInfoFragment extends DialogFragment {
	public static final String extraForMarks = "For marks", extraTask = "Task", extraCourse = "Course",
			extraTerm = "Term", extraSection = "Section";
	private Task task;
	private CheckBox complete;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		boolean forMarks = getArguments().getBoolean(extraForMarks);
		UUID taskID = (UUID) getArguments().getSerializable(extraTask);
		UUID courseID = (UUID) getArguments().getSerializable(extraCourse);
		UUID termID = (UUID) getArguments().getSerializable(extraTerm);
		if (forMarks) {
			UUID sectionID = (UUID) getArguments().getSerializable(extraSection);
			task = AssessmentList.get(getActivity(), sectionID,
					CourseList.get(getActivity(), termID).getCourse(courseID)).getAssessment(taskID);
		} else {
			if (courseID == null)
				task = StrayTaskList.get(getActivity()).getTask(taskID);
			else {
				Log.i("tag", "Searching in task list...");
				task = TaskList.get(getActivity(), CourseList.get(getActivity(), termID).getCourse(courseID)).getTask(
						taskID);
			}
		}
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_task_info, null);
		builder.setTitle(R.string.task_info_title);
		TextView title = (TextView) v.findViewById(R.id.task_info_name);
		title.setText(task.getName());
		complete = (CheckBox) v.findViewById(R.id.task_complete_CheckBox);
		complete.setChecked(task.isDone());
		if (task.getCourse() != null) {
			LinearLayout courseLayout = (LinearLayout) v.findViewById(R.id.task_info_course_LinearLayout);
			courseLayout.setVisibility(View.VISIBLE);
			TextView course = (TextView) v.findViewById(R.id.task_info_course);
			course.setText(task.getCourse().getCode());
			if (task.isForMarks()) {
				LinearLayout sectionLayout = (LinearLayout) v.findViewById(R.id.task_info_section_LinearLayout);
				sectionLayout.setVisibility(View.VISIBLE);
				TextView section = (TextView) v.findViewById(R.id.task_info_section);
				section.setText(((Assessment) task).getSection().getName());
				if (((Assessment) task).isRecorded()) {
					LinearLayout scoreLayout = (LinearLayout) v.findViewById(R.id.task_info_score_LinearLayout);
					scoreLayout.setVisibility(View.VISIBLE);
					TextView score = (TextView) v.findViewById(R.id.task_info_score);
					NumberFormat nf = new DecimalFormat("0.#");
					score.setText(nf.format(((Assessment) task).getScore()) + "/"
							+ nf.format(((Assessment) task).getOutOf()) + " ("
							+ nf.format(((Assessment) task).getPercentage()) + "%)");
					complete.setEnabled(false);
				}
			}
		}
		TextView time = (TextView) v.findViewById(R.id.task_info_time);
		SimpleDateFormat df = new SimpleDateFormat("H:mm");
		time.setText(df.format(task.getDueDate()));
		TextView date = (TextView) v.findViewById(R.id.task_info_date);
		DateFormat df2 = DateFormat.getDateInstance();
		date.setText(df2.format(task.getDueDate()));
		builder.setNeutralButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				update();
			}
		});
		builder.setView(v);
		return builder.create();
	}

	private void update() {
		task.setIsDone(complete.isChecked());
		if (getTargetFragment() == null)
			return;
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
	}

	public static TaskInfoFragment newInstance(Task task) {
		Bundle arguments = new Bundle();
		arguments.putBoolean(extraForMarks, task.isForMarks());
		arguments.putSerializable(extraTask, task.getID());
		if (task.getCourse() != null) {
			arguments.putSerializable(extraCourse, task.getCourse().getID());
			arguments.putSerializable(extraTerm, task.getCourse().getTerm().getID());
		}
		if (task.isForMarks())
			arguments.putSerializable(extraSection, ((Assessment) task).getSection().getID());
		TaskInfoFragment fragment = new TaskInfoFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
}
