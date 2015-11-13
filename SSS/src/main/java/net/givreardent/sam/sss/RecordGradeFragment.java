package net.givreardent.sam.sss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.givreardent.sam.sss.data.courses.Assessment;
import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.CourseList;
import net.givreardent.sam.sss.data.courses.Exam;
import net.givreardent.sam.sss.data.courses.Term;
import net.givreardent.sam.sss.data.courses.TermList;
import net.givreardent.sam.sss.dialogs.RecordScoreFragment;
import net.givreardent.sam.sss.util.SSS;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecordGradeFragment extends Fragment {
	private ListView tasksList, examList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_record_grades, parent, false);
		TaskAdapter adapter = new TaskAdapter(SSS.getTasksToBeGraded(getActivity()));
		tasksList = (ListView) v.findViewById(R.id.record_tasks);
		tasksList.setEmptyView(v.findViewById(R.id.record_tasks_empty));
		tasksList.setAdapter(adapter);
		tasksList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Assessment a = ((TaskAdapter) parent.getAdapter()).getItem(position);
				RecordScoreFragment dialog = RecordScoreFragment.newInstance(a);
				dialog.setTargetFragment(RecordGradeFragment.this, 1);
				dialog.show(getActivity().getSupportFragmentManager(), "Record score");
			}
		});
		examList = (ListView) v.findViewById(R.id.record_exams);
		examList.setEmptyView(v.findViewById(R.id.record_exams_empty));
		Term term = SSS.getCurrentTerm(getActivity());
		if (term != null) {
			ArrayList<Exam> exams = new ArrayList<>();
			for (Course course : CourseList.get(getActivity(), term.getID()).getCourses())
				for (Exam e : course.getExams()) {
					Date today = Calendar.getInstance().getTime();
					if (e.getEndTime().before(today) && e.isForMarks() && !e.isRecorded())
						exams.add(e);
				}
			ExamAdapter examAdapter = new ExamAdapter(exams);
			examList.setAdapter(examAdapter);
			examList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Exam exam = ((ExamAdapter) parent.getAdapter()).getItem(position);
					if (exam.getSection() == null) {
						Toast.makeText(getActivity(),
								exam.getName() + " has no score section associated.\nPlease make one first.",
								Toast.LENGTH_LONG).show();
					} else {
						RecordScoreFragment dialog = RecordScoreFragment.newInstnace(exam);
						dialog.setTargetFragment(RecordGradeFragment.this, 1);
						dialog.show(getActivity().getSupportFragmentManager(), "Record score");
					}
				}
			});
		}
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			((TaskAdapter) tasksList.getAdapter()).notifyDataSetChanged();
			((ExamAdapter) examList.getAdapter()).notifyDataSetChanged();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		TermList.get(getActivity()).saveTerms();
	}

	private class TaskAdapter extends ArrayAdapter<Assessment> {
		public TaskAdapter(ArrayList<Assessment> tasks) {
			super(getActivity(), 0, tasks);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_task_all, null);
			Assessment task = getItem(position);
			TextView title = (TextView) convertView.findViewById(R.id.task_name_list);
			if (task.isOverdue() && !task.isDone())
				title.setTextColor(Course.red[2]);
			title.setText(task.getName());
			SimpleDateFormat dFormat = new SimpleDateFormat("H:mm");
			DateFormat dateFormat = DateFormat.getDateInstance();
			TextView dueDate = (TextView) convertView.findViewById(R.id.task_due_time_list);
			dueDate.setText(dateFormat.format(task.getDueDate()) + " - " + dFormat.format(task.getDueDate()));
			View colourTile = convertView.findViewById(R.id.task_colour_list);
			CheckBox isComplete = (CheckBox) convertView.findViewById(R.id.task_is_finished_checkBox);
			isComplete.setVisibility(View.GONE);
			if (task.isRecorded()) {
				TextView score = (TextView) convertView.findViewById(R.id.task_score);
				score.setVisibility(View.VISIBLE);
				score.setText(task.getScore() + "/" + task.getOutOf());
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

	private class ExamAdapter extends ArrayAdapter<Exam> {
		public ExamAdapter(ArrayList<Exam> exams) {
			super(getActivity(), 0, exams);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_task_all, null);
			Exam exam = getItem(position);
			TextView title = (TextView) convertView.findViewById(R.id.task_name_list);
			title.setText(exam.getName());
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			TextView dueDate = (TextView) convertView.findViewById(R.id.task_due_time_list);
			dueDate.setText(df.format(exam.getStartTime()));
			View colourTile = convertView.findViewById(R.id.task_colour_list);
			colourTile.setBackgroundColor(exam.getCourse().getColour());
			CheckBox isComplete = (CheckBox) convertView.findViewById(R.id.task_is_finished_checkBox);
			isComplete.setVisibility(View.GONE);
			if (exam.isRecorded()) {
				TextView score = (TextView) convertView.findViewById(R.id.task_score);
				score.setVisibility(View.VISIBLE);
				score.setText(exam.getScore() + "/" + exam.getOutOf());
			}
			TextView course = (TextView) convertView.findViewById(R.id.task_course_list);
			course.setVisibility(View.VISIBLE);
			course.setText(exam.getCourse().getName());
			return convertView;
		}
	}
}
