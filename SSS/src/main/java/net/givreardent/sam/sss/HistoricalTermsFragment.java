package net.givreardent.sam.sss;

import java.text.DateFormat;
import java.util.ArrayList;

import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.Term;
import net.givreardent.sam.sss.dialogs.HistoricalGradesFragment;
import net.givreardent.sam.sss.util.SSS;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HistoricalTermsFragment extends ListFragment {
	private ArrayList<Term> terms;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(getArguments());
		terms = SSS.getHistoricalTerms(getActivity());
		setListAdapter(new TermAdapter(terms));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, container, false);
		TextView emptyMessage = (TextView) v.findViewById(R.string.empty_historical_grades);
		return v;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Term term = ((TermAdapter) getListAdapter()).getItem(position);
		HistoricalGradesFragment dialog = HistoricalGradesFragment.newInstance(term);
		dialog.show(getActivity().getSupportFragmentManager(), "Historical grades");
	}

	private class TermAdapter extends ArrayAdapter<Term> {
		public TermAdapter(ArrayList<Term> terms) {
			super(getActivity(), 0, terms);
		}

		@TargetApi(11)
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_term, null);
			}
			Term t = getItem(position);
			TextView title = (TextView) convertView.findViewById(R.id.term_name);
			title.setText(t.getIdentifier());
			TextView start = (TextView) convertView.findViewById(R.id.term_start);
			DateFormat df = DateFormat.getDateInstance();
			start.setText(df.format(t.getStartDate()) + " -");
			TextView end = (TextView) convertView.findViewById(R.id.term_end);
			end.setText(df.format(t.getEndDate()));
			TextView course = (TextView) convertView.findViewById(R.id.term_courses);
			int courses = t.getCourses().size();
			if (courses == 1)
				course.setText(courses + " " + getResources().getString(R.string.courses_counter_singular));
			else
				course.setText(courses + " " + getResources().getString(R.string.courses_counter_plural));
			LinearLayout courseList = (LinearLayout) convertView.findViewById(R.id.term_courses_list);
			if (courseList.getChildCount() == 0) {
				ArrayList<Course> coursesList = t.getCourses();
				for (Course c : coursesList) {
					TextView text = getCourseTextView(c);
					courseList.addView(text);
				}
			}
			return convertView;

		}

		private TextView getCourseTextView(Course c) {
			TextView course = new TextView(getActivity());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			course.setLayoutParams(params);
			course.setText(c.getCode());
			return course;
		}
	}
}
