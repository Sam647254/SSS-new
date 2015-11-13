package net.givreardent.sam.sss;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.Term;
import net.givreardent.sam.sss.data.courses.TermList;
import net.givreardent.sam.sss.util.SSS;

import org.json.JSONException;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TermListFragment extends ListFragment {
	public static final String extraTerm = "Create mode";
	private ArrayList<Term> mTerms;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.term_list_title);
		setHasOptionsMenu(true);
		mTerms = TermList.get(getActivity()).getTerms();
		TermAdapter adapter = new TermAdapter(mTerms);
		setListAdapter(adapter);
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, container, false);
		TextView emptyMessage = (TextView) v.findViewById(android.R.id.empty);
		emptyMessage.setText(R.string.empty_term_list);
		ListView listView = (ListView) v.findViewById(android.R.id.list);
		if (Build.VERSION.SDK_INT < 11) {
			registerForContextMenu(listView);
		} else {
			listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.terms_context, menu);
					return true;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode,
						MenuItem item) {
					switch (item.getItemId()) {
					case R.id.delete_entry:
						TermAdapter adapter = (TermAdapter) getListAdapter();
						for (int i = adapter.getCount(); i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								TermList.get(getActivity()).removeTerm(
										adapter.getItem(i));
							}
						}
						mode.finish();
						adapter.notifyDataSetChanged();
						TermList.get(getActivity()).saveTerms();
						return true;
					default:
						return false;
					}
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode,
						int position, long id, boolean checked) {
				}
			});
		}
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.terms_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.create_term:
			Intent i = new Intent(getActivity(), TermCreateActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.terms_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = info.position;
		TermAdapter adapter = (TermAdapter) getListAdapter();
		Term term = adapter.getItem(position);

		switch (item.getItemId()) {
		case R.id.delete_entry:
			TermList.get(getActivity()).removeTerm(term);
			adapter.notifyDataSetChanged();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Term term = ((TermAdapter) getListAdapter()).getItem(position);
		Intent i = new Intent(getActivity(), TermCreateActivity.class);
		i.putExtra(extraTerm, term.getID());
		startActivity(i);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SSS.setNotifications(getActivity());
	}

	private class TermAdapter extends ArrayAdapter<Term> {
		public TermAdapter(ArrayList<Term> terms) {
			super(getActivity(), 0, terms);
		}

		@TargetApi(11)
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.list_term, null);
			}
			Term t = getItem(position);
			TextView title = (TextView) convertView
					.findViewById(R.id.term_name);
			title.setText(t.getIdentifier());
			TextView start = (TextView) convertView
					.findViewById(R.id.term_start);
			DateFormat df = DateFormat.getDateInstance();
			start.setText(df.format(t.getStartDate()) + " -");
			TextView end = (TextView) convertView.findViewById(R.id.term_end);
			end.setText(df.format(t.getEndDate()));
			TextView course = (TextView) convertView
					.findViewById(R.id.term_courses);
			int courses = t.getCourses().size();
			if (courses == 1)
				course.setText(courses
						+ " "
						+ getResources().getString(
								R.string.courses_counter_singular));
			else
				course.setText(courses
						+ " "
						+ getResources().getString(
								R.string.courses_counter_plural));
			LinearLayout courseList = (LinearLayout) convertView
					.findViewById(R.id.term_courses_list);
			if (courseList.getChildCount() == 0) {
				ArrayList<Course> coursesList = t.getCourses();
				for (Course c : coursesList) {
					TextView text = getCourseTextView(c);
					courseList.addView(text);
				}
			}
			return convertView;

		}

		private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
			long diffInMillies = date2.getTime() - date1.getTime();
			return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
		}

		private TextView getCourseTextView(Course c) {
			TextView course = new TextView(getActivity());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			course.setLayoutParams(params);
			course.setText(c.getCode());
			return course;
		}
	}
}
