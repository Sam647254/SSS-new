package net.givreardent.sam.sss;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.CourseList;
import net.givreardent.sam.sss.util.SSS;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GradesTrackerFragment extends ListFragment {
	public static final String extraTermID = "Term ID";
	private ArrayList<Course> courses;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		courses = new ArrayList<>();
		if (getArguments() == null) {
			if (SSS.getCurrentTerm(getActivity()) != null)
				for (Course c : SSS.getCurrentTerm(getActivity()).getCourses())
					if (c.isGradable())
						courses.add(c);
		} else {
			UUID termID = (UUID) getArguments().getSerializable(extraTermID);
			for (Course c : CourseList.get(getActivity(), termID).getCourses())
				if (c.isGradable())
					courses.add(c);
		}
		CourseAdapter adapter = new CourseAdapter(courses);
		setHasOptionsMenu(true);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, parent, false);
		TextView emptyMessage = (TextView) v.findViewById(android.R.id.empty);
		emptyMessage.setText(R.string.empty_gradestracker);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.gradetracker_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case R.id.record_mark:
			i = new Intent(getActivity(), RecordGradeActivity.class);
			startActivity(i);
			return true;
		case R.id.view_historical_grades:
			i = new Intent(getActivity(), HistoricalTermsActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static GradesTrackerFragment newInstance(UUID termID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraTermID, termID);
		GradesTrackerFragment fragment = new GradesTrackerFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Course course = ((CourseAdapter) getListAdapter()).getItem(position);
		Intent i = new Intent(getActivity(), GradesListActivity.class);
		i.putExtra(GradesListFragment.extraCourse, course.getID());
		i.putExtra(GradesListFragment.extraTerm, course.getTerm().getID());
		startActivity(i);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		((CourseAdapter) getListAdapter()).notifyDataSetChanged();
	}

	private class CourseAdapter extends ArrayAdapter<Course> {
		public CourseAdapter(ArrayList<Course> courses) {
			super(getActivity(), 0, courses);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_course_gradetracker, null);
			}
			Course course = getItem(position);
			TextView code = (TextView) convertView.findViewById(R.id.gradestracker_course_code);
			code.setText(course.getCode());
			TextView name = (TextView) convertView.findViewById(R.id.gradestracker_course_name);
			name.setText(course.getName());
			TextView score = (TextView) convertView.findViewById(R.id.gradestracker_grade);
			if (course.getPercentage() == -1)
				score.setText("--%");
			else {
				DecimalFormat formatter = new DecimalFormat("0.#");
				score.setText(formatter.format(course.getPercentage()) + "%");
			}
			return convertView;
		}
	}
}
