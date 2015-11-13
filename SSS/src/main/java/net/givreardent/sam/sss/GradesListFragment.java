package net.givreardent.sam.sss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

import net.givreardent.sam.sss.data.courses.Assessment;
import net.givreardent.sam.sss.data.courses.AssessmentList;
import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.CourseList;
import net.givreardent.sam.sss.data.courses.GradeSection;
import net.givreardent.sam.sss.data.courses.SectionList;
import net.givreardent.sam.sss.dialogs.TaskInfoFragment;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class GradesListFragment extends ListFragment {
	public static final String extraCourse = "Course", extraTerm = "Term";
	private Course course;
	private ArrayList<Assessment> assessments;
	private UUID courseID, termID;

	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		courseID = (UUID) getArguments().getSerializable(extraCourse);
		termID = (UUID) getArguments().getSerializable(extraTerm);
		course = CourseList.get(getActivity(), termID).getCourse(courseID);
		getActivity().setTitle(course.getCode());
		if (Build.VERSION.SDK_INT > 11)
			getActivity().getActionBar().setSubtitle(R.string.grade_list_subtitle);
		resetAdapter();
		setHasOptionsMenu(true);
	}

	private void resetAdapter() {
		assessments = new ArrayList<>();
		for (GradeSection s : SectionList.get(getActivity(), courseID, termID).getSections()) {
			if (!s.isExam())
				for (Assessment a : AssessmentList.get(getActivity(), s.getID(), course).getAssessments()) {
					assessments.add(a);
				}
		}
		AssessmentAdapter adapter = new AssessmentAdapter(assessments);
		setListAdapter(adapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.gradetracker_menu, menu);
		menu.getItem(0).setTitle(R.string.section_editor_title);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Assessment assessment = ((AssessmentAdapter) getListAdapter()).getItem(position);
		TaskInfoFragment fragment = TaskInfoFragment.newInstance(assessment);
		fragment.show(getActivity().getSupportFragmentManager(), "Task info");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.record_mark:
			Intent i = new Intent(getActivity(), SectionEditorActivity.class);
			i.putExtra(SectionEditorFragment.extraCourse, course.getID());
			i.putExtra(SectionEditorFragment.extraTerm, course.getTerm().getID());
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup paraent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, paraent, false);
		return v;
	}

	public static GradesListFragment newInstance(UUID courseID, UUID termID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraCourse, courseID);
		arguments.putSerializable(extraTerm, termID);
		GradesListFragment fragment = new GradesListFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	private class AssessmentAdapter extends ArrayAdapter<Assessment> {
		public AssessmentAdapter(ArrayList<Assessment> assessments) {
			super(getActivity(), 0, assessments);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_assessment, null);
			Assessment assessment = getItem(position);
			TextView name = (TextView) convertView.findViewById(R.id.assessment_name);
			name.setText(assessment.getName());
			TextView section = (TextView) convertView.findViewById(R.id.section_name);
			section.setText(assessment.getSection().getName());
			SimpleDateFormat dFormat = new SimpleDateFormat("H:mm");
			DateFormat dateFormat = DateFormat.getDateInstance();
			TextView dueDate = (TextView) convertView.findViewById(R.id.assessment_due_time);
			dueDate.setText(dateFormat.format(assessment.getDueDate()) + " - "
					+ dFormat.format(assessment.getDueDate()));
			CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.task_is_finished_checkBox);
			checkBox.setChecked(assessment.isDone());
			if (assessment.isRecorded()) {
				checkBox.setVisibility(View.GONE);
				TextView grade = (TextView) convertView.findViewById(R.id.task_score);
				grade.setVisibility(View.VISIBLE);
				grade.setText(assessment.getScoreString());
			}
			View colourTile = convertView.findViewById(R.id.task_colour_list);
			colourTile.setBackgroundColor(assessment.getCourse().getColour());
			return convertView;
		}
	}
}
