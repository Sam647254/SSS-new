package net.givreardent.sam.sss.dialogs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import net.givreardent.sam.sss.FrontActivity;
import net.givreardent.sam.sss.R;
import net.givreardent.sam.sss.data.courses.Assessment;
import net.givreardent.sam.sss.data.courses.AssessmentList;
import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.CourseList;
import net.givreardent.sam.sss.data.courses.GradeSection;
import net.givreardent.sam.sss.data.courses.Task;
import net.givreardent.sam.sss.data.courses.TaskList;
import net.givreardent.sam.sss.data.events.StrayTaskList;
import net.givreardent.sam.sss.util.SSS;
import net.givreardent.sam.sss.util.ScrollProofDatePicker;
import net.givreardent.sam.sss.util.ScrollProofTimePicker;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class TaskCreateFragment extends DialogFragment {
	private static final String extraCourse = "Course", extraTerm = "Term", extraTask = "Task",
			extraSection = "Section";
	private Task existingTask;
	private EditText title;
	private Spinner courseSelection, sectionSelection;
	private LinearLayout sectionLayout;
	private CheckBox forMarks;
	private ScrollProofDatePicker dueDate;
	private ScrollProofTimePicker dueTime;
	private ArrayList<Course> courses;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_create_task, null);
		title = (EditText) v.findViewById(R.id.task_name);
		courseSelection = (Spinner) v.findViewById(R.id.task_course_spinner);
		sectionSelection = (Spinner) v.findViewById(R.id.task_section_spinner);
		sectionLayout = (LinearLayout) v.findViewById(R.id.task_section_LinearLayout);
		forMarks = (CheckBox) v.findViewById(R.id.task_for_marks);
		forMarks.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					sectionLayout.setVisibility(View.VISIBLE);
				else
					sectionLayout.setVisibility(View.GONE);
			}
		});
		dueDate = (ScrollProofDatePicker) v.findViewById(R.id.task_due_date);
		dueTime = (ScrollProofTimePicker) v.findViewById(R.id.task_due_time);
		dueTime.setIs24HourView(true);
		ArrayList<String> names = SSS.getCurrentCourseCodes(getActivity());
		names.add(0, getResources().getString(R.string.task_course_none));
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, names);
		if (SSS.getCurrentTerm(getActivity()) == null)
			courses = new ArrayList<>();
		else
			courses = SSS.getCurrentTerm(getActivity()).getCourses();
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		if (names.size() == 1) {
			LinearLayout courseLayout = (LinearLayout) v.findViewById(R.id.task_course_LinearLayout);
			courseLayout.setVisibility(View.GONE);
		}
		courseSelection.setAdapter(adapter);
		courseSelection.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Log.i("tag", "on Item Selected Listener triggered");
				if (position > 0) {
					Course course = courses.get(position - 1);
					if (course.getNonExamSections().size() > 0) {
						forMarks.setVisibility(View.VISIBLE);
						ArrayAdapter<GradeSection> adapter = new ArrayAdapter<>(getActivity(),
								android.R.layout.simple_spinner_item, course.getNonExamSections());
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						sectionSelection.setAdapter(adapter);
						if (existingTask != null && existingTask.isForMarks()) {
							forMarks.setChecked(existingTask.isForMarks());
							for (int i = 0; i < sectionSelection.getAdapter().getCount(); i++) {
								if (((GradeSection) sectionSelection.getAdapter().getItem(i)).getID().equals(
										((Assessment) existingTask).getSection().getID())) {
									sectionSelection.setSelection(i, true);
								}
							}
						}
						Log.i("tag", "Sections set!");
					} else {
						forMarks.setChecked(false);
						forMarks.setVisibility(View.GONE);
						sectionLayout.setVisibility(View.GONE);
					}
				} else if (position == 0) {
					forMarks.setChecked(false);
					forMarks.setVisibility(View.GONE);
					sectionLayout.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				parent.setSelection(0);
			}
		});
		if (getArguments() != null) {
			Course course = null;
			if (getArguments().getSerializable(extraTerm) != null)
				course = CourseList.get(getActivity(), (UUID) getArguments().getSerializable(extraTerm)).getCourse(
						(UUID) getArguments().getSerializable(extraCourse));
			if (getArguments().getSerializable(extraTask) != null) {
				if (getArguments().getSerializable(extraSection) != null) {
					existingTask = AssessmentList.get(getActivity(),
							(UUID) getArguments().getSerializable(extraSection), course).getAssessment(
							(UUID) getArguments().getSerializable(extraTask));
				} else {
					if (course == null) {
						existingTask = StrayTaskList.get(getActivity()).getTask(
								(UUID) getArguments().getSerializable(extraTask));
					} else
						existingTask = TaskList.get(getActivity(), course).getTask(
								(UUID) getArguments().getSerializable(extraTask));
				}
				title.setText(existingTask.getName());
				if (course != null)
					for (int i = 0; i < names.size(); i++) {
						if (names.get(i).equals(course.getCode())) {
							Log.i("tag", "Course found! Attempting to set course...");
							courseSelection.setSelection(i, true);
							break;
						}
					}
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(existingTask.getDueDate());
				dueDate.init(calendar.get(1), calendar.get(2), calendar.get(5), null);
				dueTime.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
				dueTime.setCurrentMinute(calendar.get(Calendar.MINUTE));
			} else {
				for (int i = 0; i < names.size(); i++) {
					if (names.get(i).equals(course.getCode())) {
						Log.i("tag", "Course found! Attempting to set course 2...");
						courseSelection.setSelection(i, true);
					}
				}
			}
		}
		builder.setView(v);
		builder.setTitle(R.string.task_dialog_title);
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendResult();
			}
		});
		builder.setNeutralButton(android.R.string.cancel, null);
		return builder.create();
	}

	private void sendResult() {
		Course course = null;
		if (courseSelection.getSelectedItemPosition() > 0)
			course = courses.get(courseSelection.getSelectedItemPosition() - 1);
		Task task = null;
		if (forMarks.isChecked()) {
			task = new Assessment((GradeSection) sectionSelection.getSelectedItem());
		} else if (courseSelection.getSelectedItemPosition() == 0) {
			task = new Task(null);
		} else {
			task = new Task(course);
		}
		task.setName(title.getText().toString());
		task.setDueTime(new GregorianCalendar(dueDate.getYear(), dueDate.getMonth(), dueDate.getDayOfMonth(), dueTime
				.getCurrentHour(), dueTime.getCurrentMinute()).getTime());
		if (existingTask != null) {
			if (existingTask.getCourse() == null) {
				StrayTaskList.get(getActivity()).removeTask(existingTask.getID());
			} else if (existingTask.isForMarks()) {
				AssessmentList.get(getActivity(), ((Assessment) existingTask).getSection().getID(),
						existingTask.getCourse()).removeAssessment((Assessment) existingTask);
			} else {
				Log.d("tag", "Attempting to remove task...");
				TaskList.get(getActivity(), existingTask.getCourse()).removeTask(existingTask);
			}
		}
		if (task.getCourse() == null) {
			StrayTaskList.get(getActivity()).addTask(task);
		} else {
			if (task.isForMarks()) {
				AssessmentList.get(getActivity(), ((GradeSection) sectionSelection.getSelectedItem()).getID(), course)
						.addAssessment((Assessment) task);
			} else
				TaskList.get(getActivity(), course).addTask(task);
		}
		if (getActivity() instanceof FrontActivity) {
			((FrontActivity) getActivity()).onDialogResult2();
			return;
		}
		if (getTargetFragment() == null)
			return;
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
	}

	public static TaskCreateFragment newInstance(UUID courseID, UUID termID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraCourse, courseID);
		arguments.putSerializable(extraTerm, termID);
		TaskCreateFragment fragment = new TaskCreateFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	public static TaskCreateFragment newInstance(UUID taskID, UUID courseID, UUID termID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraTask, taskID);
		arguments.putSerializable(extraCourse, courseID);
		arguments.putSerializable(extraTerm, termID);
		TaskCreateFragment fragment = new TaskCreateFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	public static TaskCreateFragment newInstance(UUID assessmentID, UUID sectionID, UUID courseID, UUID termID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraCourse, courseID);
		arguments.putSerializable(extraTerm, termID);
		arguments.putSerializable(extraTask, assessmentID);
		arguments.putSerializable(extraSection, sectionID);
		TaskCreateFragment fragment = new TaskCreateFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
}
