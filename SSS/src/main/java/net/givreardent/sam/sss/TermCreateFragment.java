package net.givreardent.sam.sss;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import com.codetroopers.betterpickers.datepicker.DatePickerBuilder;
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment.DatePickerDialogHandler;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.givreardent.sam.sss.data.courses.Break;
import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.CourseList;
import net.givreardent.sam.sss.data.courses.Term;
import net.givreardent.sam.sss.data.courses.TermList;
import net.givreardent.sam.sss.dialogs.CreateBreakFragment;
import net.givreardent.sam.sss.dialogs.DatePickerFragment;
import net.givreardent.sam.sss.util.SSS;

public class TermCreateFragment extends Fragment {
	private Term mTerm;
	private Button mStartDateButton;
	private Button mEndDateButton;
	private EditText mTitle;
	private Button mCreateBreakButton;
	private Button mCreateActivityButton;
	private ListView courseList;
	private ListView breakList;
	private ArrayList<Course> mCourses;
	private ArrayList<Break> mBreaks;
	private DateFormat[] dateFormats = { DateFormat.getDateInstance(), DateFormat.getTimeInstance() };
	private static final int requestStartDate = 0;
	private static final int requestEndDate = 1;
	private static final int requestAddCourse = 2;
	private static final int requestAddBreak = 3;
	private static final int requestEditBreak = 4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		UUID termID = (UUID) getArguments().getSerializable(TermListFragment.extraTerm);
		if (termID == null) {
			mTerm = new Term();
			TermList.get(getActivity()).addTerm(mTerm);
			Log.d("tag", "New term created with ID: " + mTerm.getID().toString());
		} else {
			mTerm = TermList.get(getActivity()).getTerm(termID);
			getActivity().setTitle(mTerm.getIdentifier());
		}
		mCourses = CourseList.get(getActivity(), mTerm.getID()).getCourses();
		mBreaks = mTerm.getBreaks();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_create_term, parent, false);
		mTitle = (EditText) v.findViewById(R.id.term_name);
		mTitle.setText(mTerm.getIdentifier());
		mStartDateButton = (Button) v.findViewById(R.id.start_date_select);
		mEndDateButton = (Button) v.findViewById(R.id.end_date_select);
		Calendar calendar = Calendar.getInstance();
		if (mTerm.getStartDate() == null) {
			Date today = calendar.getTime();
			mTerm.setStartDate(today);
			calendar.add(2, 4);
			Date end = calendar.getTime();
			mTerm.setEndDate(end);
		}
		mStartDateButton.setText(dateFormats[0].format(mTerm.getStartDate()));
		mStartDateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= 11) {
					DatePickerBuilder builder = new DatePickerBuilder();
					builder.addDatePickerDialogHandler(new DatePickerDialogHandler() {

						@Override
						public void onDialogDateSet(int reference, int year, int monthOfYear, int dayOfMonth) {
							GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
							mTerm.setStartDate(calendar.getTime());
							mStartDateButton.setText(dateFormats[0].format(mTerm.getStartDate()));
						}
					});
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(mTerm.getStartDate());
					builder.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
					builder.setMonthOfYear(calendar.get(Calendar.MONTH));
					builder.setYear(calendar.get(Calendar.YEAR));
					builder.setFragmentManager(getActivity().getSupportFragmentManager());
					builder.setStyleResId(R.style.BetterPickersDialogFragment_Light);
					builder.setTargetFragment(TermCreateFragment.this);
					builder.show();
				} else {
					FragmentManager fm = getActivity().getSupportFragmentManager();
					DatePickerFragment dialog = DatePickerFragment.newInstance(mTerm.getStartDate());
					dialog.setTargetFragment(TermCreateFragment.this, requestStartDate);
					dialog.show(fm, "Date");
				}
			}
		});
		mEndDateButton.setText(dateFormats[0].format(mTerm.getEndDate()));
		mEndDateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= 11) {
					DatePickerBuilder builder = new DatePickerBuilder();
					builder.addDatePickerDialogHandler(new DatePickerDialogHandler() {

						@Override
						public void onDialogDateSet(int reference, int year, int monthOfYear, int dayOfMonth) {
							GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
							mTerm.setEndDate(calendar.getTime());
							mEndDateButton.setText(dateFormats[0].format(mTerm.getEndDate()));
						}
					});
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(mTerm.getEndDate());
					builder.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
					builder.setMonthOfYear(calendar.get(Calendar.MONTH));
					builder.setYear(calendar.get(Calendar.YEAR));
					builder.setFragmentManager(getActivity().getSupportFragmentManager());
					builder.setStyleResId(R.style.BetterPickersDialogFragment_Light);
					builder.setTargetFragment(TermCreateFragment.this);
					builder.show();
				} else {
					FragmentManager fm = getActivity().getSupportFragmentManager();
					DatePickerFragment dialog = DatePickerFragment.newInstance(mTerm.getEndDate());
					dialog.setTargetFragment(TermCreateFragment.this, requestEndDate);
					dialog.show(fm, "Date");
				}
			}
		});
		mCreateActivityButton = (Button) v.findViewById(R.id.add_course_button);
		mCreateActivityButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CourseCreateActivity.class);
				i.putExtra(CourseCreateFragment.extraTerm, mTerm.getID());
				startActivityForResult(i, requestAddCourse);
			}
		});
		mCreateBreakButton = (Button) v.findViewById(R.id.add_break_button);
		mCreateBreakButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				CreateBreakFragment dialog = CreateBreakFragment.newInstance(mTerm.getID());
				dialog.setTargetFragment(TermCreateFragment.this, requestAddBreak);
				dialog.show(fm, "Create break");
			}
		});
		courseList = (ListView) v.findViewById(android.R.id.list);
		courseList.setEmptyView(v.findViewById(android.R.id.empty));
		CourseAdapter adapter = new CourseAdapter(mCourses);
		courseList.setAdapter(adapter);
		courseList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Course course = (Course) parent.getItemAtPosition(position);
				Intent i = new Intent(getActivity(), CourseCreateActivity.class);
				i.putExtra(CourseCreateFragment.extraTerm, mTerm.getID());
				i.putExtra(CourseCreateFragment.extraCourse, course.getID());
				startActivityForResult(i, requestAddCourse);
			}
		});
		breakList = (ListView) v.findViewById(R.id.break_list);
		breakList.setEmptyView(v.findViewById(R.id.break_empty));
		BreakAdapter adapter2 = new BreakAdapter(mBreaks);
		breakList.setAdapter(adapter2);
		if (Build.VERSION.SDK_INT < 11) {
			registerForContextMenu(courseList);
			registerForContextMenu(breakList);
		} else {
			courseList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
			courseList.setMultiChoiceModeListener(new MultiChoiceModeListener() {

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
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch (item.getItemId()) {
					case R.id.delete_entry:
						CourseAdapter adapter = (CourseAdapter) courseList.getAdapter();
						for (int i = 0; i < adapter.getCount(); i++) {
							if (courseList.isItemChecked(i))
								CourseList.get(getActivity(), mTerm.getID()).removeCourse(adapter.getItem(i));
						}
						mode.finish();
						adapter.notifyDataSetChanged();
						return true;
					}
					return false;
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
					// TODO Auto-generated method stub

				}
			});
			breakList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
			breakList.setMultiChoiceModeListener(new MultiChoiceModeListener() {

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
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch (item.getItemId()) {
					case R.id.delete_entry:
						BreakAdapter adapter = (BreakAdapter) breakList.getAdapter();
						for (int i = 0; i < adapter.getCount(); i++) {
							if (breakList.isItemChecked(i))
								mBreaks.remove(adapter.getItem(i));
						}
						adapter.notifyDataSetChanged();
						mode.finish();
					}
					return false;
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
					// TODO Auto-generated method stub

				}
			});
			breakList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					FragmentManager fm = getActivity().getSupportFragmentManager();
					CreateBreakFragment dialog = CreateBreakFragment.newInstance(mTerm.getID(), position);
					dialog.setTargetFragment(TermCreateFragment.this, requestEditBreak);
					dialog.show(fm, "Edit break");
				}
			});
		}
		return v;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		CourseAdapter adapter = (CourseAdapter) courseList.getAdapter();
		Course course = adapter.getItem(position);

		switch (item.getItemId()) {
		case R.id.delete_entry:
			CourseList.get(getActivity(), mTerm.getID()).removeCourse(course);
			adapter.notifyDataSetChanged();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.term_create_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_term:
			if (mTitle.getText().length() == 0) {
				Toast.makeText(getActivity(), R.string.empty_term_identifier, Toast.LENGTH_SHORT).show();
				mTitle.setHintTextColor(Course.red[1]);
				return false;
			}
			mTerm.setIdentifier(mTitle.getText().toString());
			TermList.get(getActivity()).saveTerms();
			Log.i("tag", "Terms saved.");
			if (NavUtils.getParentActivityName(getActivity()) != null)
				NavUtils.navigateUpFromSameTask(getActivity());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.terms_context, menu);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == requestStartDate) {
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.extraDate);
			mTerm.setStartDate(date);
			mStartDateButton.setText(dateFormats[0].format(date));
			if (date.compareTo(mTerm.getEndDate()) >= 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(2, 4);
				mTerm.setEndDate(calendar.getTime());
				mEndDateButton.setText(dateFormats[0].format(mTerm.getEndDate()));
			}
		}
		if (requestCode == requestEndDate) {
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.extraDate);
			mTerm.setEndDate(date);
			mEndDateButton.setText(dateFormats[0].format(date));
		}
		if (requestCode == requestAddCourse) {
			((CourseAdapter) courseList.getAdapter()).notifyDataSetChanged();
			Log.d("tag", "mCourses now has: " + mCourses.size());
		}
		if (requestCode == requestAddBreak) {
			Break b = new Break(mTerm);
			String name = data.getStringExtra(CreateBreakFragment.extraName);
			Date start = new Date(data.getLongExtra(CreateBreakFragment.extraStart, 0));
			Date end = new Date(data.getLongExtra(CreateBreakFragment.extraEnd, 0));
			b.setName(name);
			b.setStartDate(start);
			b.setEndDate(end);
			mTerm.addBreak(b);
			((BreakAdapter) breakList.getAdapter()).notifyDataSetChanged();
		}
		if (requestCode == requestEditBreak) {
			Break b = new Break(mTerm);
			String name = data.getStringExtra(CreateBreakFragment.extraName);
			Date start = new Date(data.getLongExtra(CreateBreakFragment.extraStart, 0));
			Date end = new Date(data.getLongExtra(CreateBreakFragment.extraEnd, 0));
			b.setName(name);
			b.setStartDate(start);
			b.setEndDate(end);
			int position = data.getIntExtra(CreateBreakFragment.extraBreakPos, -1);
			mTerm.getBreaks().set(position, b);
			((BreakAdapter) breakList.getAdapter()).notifyDataSetChanged();
		}
	}
	
	@TargetApi(11)
	@Override
	public void onResume() {
		super.onResume();
		if (!mTitle.getText().toString().isEmpty())
			getActivity().setTitle(mTitle.getText().toString());
	}

	@Override
	public void onPause() {
		super.onPause();
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				SSS.clearNotifications(getActivity());
				SSS.setNotifications(getActivity());
			}
		});
	}

	public void cancelAdd() {
		UUID termID = (UUID) getArguments().getSerializable(TermListFragment.extraTerm);
		if (termID == null)
			TermList.get(getActivity()).removeTerm(mTerm);
	}

	public static TermCreateFragment newInstance(UUID termID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(TermListFragment.extraTerm, termID);
		TermCreateFragment fragment = new TermCreateFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	private class CourseAdapter extends ArrayAdapter<Course> {
		public CourseAdapter(ArrayList<Course> courses) {
			super(getActivity(), 0, courses);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_course, null);
			Course course = getItem(position);
			TextView codeText = (TextView) convertView.findViewById(R.id.course_code_list);
			codeText.setText(course.getCode());
			TextView nameText = (TextView) convertView.findViewById(R.id.course_name_list);
			nameText.setText(course.getName());
			View colour = convertView.findViewById(R.id.course_colour_list);
			if (Build.VERSION.SDK_INT < 11) {
				colour.setVisibility(View.GONE);
			}
			Log.d("tag", course.getCode() + " has colour: " + course.getColour());
			colour.setBackgroundColor(course.getColour());
			return convertView;
		}
	}

	private class BreakAdapter extends ArrayAdapter<Break> {
		public BreakAdapter(ArrayList<Break> breaks) {
			super(getActivity(), 0, breaks);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_break, null);
			Break b = getItem(position);
			TextView name = (TextView) convertView.findViewById(R.id.break_name_list);
			name.setText(b.getName());
			DateFormat df = DateFormat.getDateInstance();
			TextView period = (TextView) convertView.findViewById(R.id.break_period_list);
			period.setText(df.format(b.getStartDate()) + " - " + df.format(b.getEndDate()));
			return convertView;
		}
	}
}
