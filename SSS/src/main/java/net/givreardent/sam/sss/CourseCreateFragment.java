package net.givreardent.sam.sss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import net.givreardent.sam.sss.data.Settings;
import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.courses.ActivityList;
import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.CourseList;
import net.givreardent.sam.sss.data.courses.Exam;
import net.givreardent.sam.sss.data.courses.GradeSection;
import net.givreardent.sam.sss.data.courses.Lab;
import net.givreardent.sam.sss.data.courses.Lecture;
import net.givreardent.sam.sss.data.courses.SectionList;
import net.givreardent.sam.sss.data.courses.TermList;
import net.givreardent.sam.sss.data.courses.Tutorial;
import net.givreardent.sam.sss.dialogs.CourseColourSelectFragment;
import net.givreardent.sam.sss.dialogs.CreateActivityFragment;
import net.givreardent.sam.sss.dialogs.CreateActivitySelectFragment;
import net.givreardent.sam.sss.util.SSS;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CourseCreateFragment extends Fragment {
	public static final String extraCourse = "Edit mode";
	public static final String extraTerm = "Belonging term";
	public static final int requestColour = 0;
	public static final int requestCreateAcitivty = 1;
	public static final int requestCreatedActivity = 2;
	public static final int resultCourseCreated = -1;
	public static final int resultLecture = 100;
	public static final int resultLab = 101;
	public static final int resultTutorial = 102;
	public static final int resultExam = 103;
	public static final int resultOther = 99;
	private EditText title;
	private EditText code;
	private Course mCourse;
	private View mColourTile;
	private boolean newCourse;
	private Button mCreateActivityButton;
	private ArrayList<Activity> activities;
	private ListView activityList;
	private int selectedActivity;
	private UUID termID;
	private CheckBox gradable;

	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		UUID courseID = (UUID) getArguments().getSerializable(extraCourse);
		termID = (UUID) getArguments().getSerializable(extraTerm);
		if (courseID == null) {
			Log.i("tag", "Creating a new course.");
			mCourse = new Course(TermList.get(getActivity()).getTerm(termID));
			mCourse.setColour(Course.blue[2]);
			newCourse = true;
			CourseList.get(getActivity(), termID).addCourse(mCourse);
		} else {
			mCourse = TermList.get(getActivity()).getTerm(termID).getCourse(courseID);
			newCourse = false;
			getActivity().setTitle(mCourse.getCode());
			if (Build.VERSION.SDK_INT >= 11)
				getActivity().getActionBar().setSubtitle(mCourse.getName());
		}
		activities = ActivityList.get(getActivity(), mCourse.getID(), mCourse.getTerm().getID()).getActivities();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_create_course, parent, false);
		title = (EditText) v.findViewById(R.id.course_name);
		title.setText(mCourse.getName());
		code = (EditText) v.findViewById(R.id.course_code);
		code.setText(mCourse.getCode());
		mColourTile = v.findViewById(R.id.course_colour);
		if (Build.VERSION.SDK_INT < 11) {
			mColourTile.setEnabled(false);
			mColourTile.setVisibility(View.GONE);
		}
		mColourTile.setBackgroundColor(mCourse.getColour());
		mColourTile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				CourseColourSelectFragment dialog = new CourseColourSelectFragment();
				dialog.setTargetFragment(CourseCreateFragment.this, requestColour);
				dialog.show(fm, "Colour picker");
			}
		});
		mCreateActivityButton = (Button) v.findViewById(R.id.create_activity_button);
		mCreateActivityButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				CreateActivitySelectFragment dialog = new CreateActivitySelectFragment();
				dialog.setTargetFragment(CourseCreateFragment.this, requestCreateAcitivty);
				dialog.show(fm, "Activity select");
			}
		});
		activityList = (ListView) v.findViewById(android.R.id.list);
		activityList.setEmptyView(v.findViewById(android.R.id.empty));
		ActivityAdapter adapter = new ActivityAdapter(activities);
		activityList.setAdapter(adapter);
		if (Build.VERSION.SDK_INT < 11) {
			registerForContextMenu(activityList);
		} else {
			activityList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
			activityList.setMultiChoiceModeListener(new MultiChoiceModeListener() {

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
					mode.getMenuInflater().inflate(R.menu.terms_context, menu);
					return true;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch (item.getItemId()) {
					case R.id.delete_entry:
						ActivityAdapter adapter = (ActivityAdapter) activityList.getAdapter();
						for (int i = 0; i < adapter.getCount(); i++) {
							if (activityList.isItemChecked(i)) {
								Activity activity = adapter.getItem(i);
								ActivityList.get(getActivity(), mCourse.getID(), mCourse.getTerm().getID())
										.removeActivity(activity);
								if (activity.getType() == Activity.exam) {
									if (((Exam) activity).getSection() != null)
										SectionList.get(getActivity(), mCourse.getID(), termID).removeSection(
												((Exam) activity).getSection().getID());
								}
							}
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
		}
		activityList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedActivity = position;
				Activity activity = (Activity) parent.getItemAtPosition(position);
				FragmentManager fm = getActivity().getSupportFragmentManager();
				CreateActivityFragment dialog = CreateActivityFragment.newInstance(activity.getID(), activity
						.getCourse().getID(), activity.getCourse().getTerm().getID(), gradable.isChecked());
				dialog.setTargetFragment(CourseCreateFragment.this, requestCreatedActivity);
				dialog.show(fm, "Edit activity");
			}
		});
		gradable = (CheckBox) v.findViewById(R.id.course_gradable);
		gradable.setChecked(mCourse.isGradable());
		gradable.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCourse.setGradable(isChecked);
			}
		});
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.term_create_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_term:
			if (code.getText().length() == 0) {
				code.setHintTextColor(Course.red[1]);
				Toast.makeText(getActivity(), R.string.empty_course_code, Toast.LENGTH_SHORT).show();
				return false;
			}
			mCourse.setName(title.getText().toString());
			mCourse.setCode(code.getText().toString());
			if (!newCourse) {
				CourseList.get(getActivity(), mCourse.getTerm().getID()).removeCourse(mCourse.getID());
				CourseList.get(getActivity(), mCourse.getTerm().getID()).addCourse(mCourse);
			}
			getActivity().setResult(resultCourseCreated);
			SSS.clearNotifications(getActivity());
			getActivity().finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.terms_context, menu);
	}
	
	@TargetApi(11)
	@Override
	public void onResume() {
		super.onResume();
		if (!code.getText().toString().isEmpty())
			getActivity().setTitle(code.getText().toString());
		if (Build.VERSION.SDK_INT >= 11) {
			if (!title.getText().toString().isEmpty())
				getActivity().getActionBar().setTitle(code.getText().toString());
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		ActivityAdapter adapter = (ActivityAdapter) activityList.getAdapter();
		Activity activity = adapter.getItem(position);

		switch (item.getItemId()) {
		case R.id.delete_entry:
			ActivityList.get(getActivity(), mCourse.getID(), mCourse.getTerm().getID()).removeActivity(activity);
			adapter.notifyDataSetChanged();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == requestColour) {
			mCourse.setColour(resultCode);
			mColourTile.setBackgroundColor(resultCode);
		}
		if (requestCode == requestCreateAcitivty) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			CreateActivityFragment dialog = CreateActivityFragment.newInstance(resultCode - 99, gradable.isChecked());
			dialog.setTargetFragment(CourseCreateFragment.this, requestCreatedActivity);
			dialog.show(fm, "Selected type");
		}
		if (requestCode == requestCreatedActivity) {
			Activity activity = null;
			int type = data.getIntExtra(CreateActivityFragment.extraType, Activity.generic);
			switch (type) {
			case Activity.generic:
				activity = new Activity(mCourse);
				break;
			case Activity.lecture:
				activity = new Lecture(mCourse);
				((Lecture) activity).setProfName(data.getStringExtra(CreateActivityFragment.extraSuperName));
				break;
			case Activity.lab:
				activity = new Lab(mCourse);
				((Lab) activity).setSupervisorName(data.getStringExtra(CreateActivityFragment.extraSuperName));
				break;
			case Activity.tutorial:
				activity = new Tutorial(mCourse);
				((Tutorial) activity).setTutorName(data.getStringExtra(CreateActivityFragment.extraSuperName));
				break;
			case Activity.exam:
				activity = new Exam(mCourse);
				boolean forMarks = data.getBooleanExtra(CreateActivityFragment.extraForMarks, false);
				((Exam) activity).setForMarks(forMarks);
				break;
			}
			activity.setTitle(data.getStringExtra(CreateActivityFragment.extraName));
			activity.setLocation(data.getStringExtra(CreateActivityFragment.extraLocation));
			int recur = data.getIntExtra(CreateActivityFragment.extraRecurrence, Activity.noRecurrence);
			activity.setRecurrence(recur);
			if (recur == Activity.weeklyRecurrence) {
				boolean[] daysChecked = data.getBooleanArrayExtra(CreateActivityFragment.extraDays);
				for (int i = 0; i < 7; i++) {
					activity.setWeekday(i, daysChecked[i]);
				}
			}
			activity.setStartTime(new Date(data.getLongExtra(CreateActivityFragment.extraStart, 0)));
			activity.setEndTime(new Date(data.getLongExtra(CreateActivityFragment.extraEnd, 1)));
			activity.setReminder(data.getIntExtra(
					CreateActivityFragment.extraReminder,
					type == Activity.exam ? Settings.getExamReminder(getActivity()) : Settings
							.getExamReminder(getActivity())));
			if (resultCode == android.app.Activity.RESULT_OK) {
				activities.add(activity);
				Log.i("tag", "Activity added!");
			} else {
				activities.set(selectedActivity, activity);
				Log.i("tag", "Activity updated!");
			}
			((ActivityAdapter) activityList.getAdapter()).notifyDataSetChanged();
		}
	}

	public void cancelCreate() {
		if (newCourse)
			CourseList.get(getActivity(), termID).removeCourse(mCourse);
	}

	public static Fragment newInstance(UUID courseID, UUID termID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraCourse, courseID);
		arguments.putSerializable(extraTerm, termID);
		CourseCreateFragment fragment = new CourseCreateFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	private class ActivityAdapter extends ArrayAdapter<Activity> {
		public ActivityAdapter(ArrayList<Activity> activities) {
			super(getActivity(), 0, activities);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_activity, null);

			Activity activity = getItem(position);
			TextView titleText = (TextView) convertView.findViewById(R.id.activity_name_list);
			titleText.setText(activity.getName());
			TextView superText = (TextView) convertView.findViewById(R.id.activity_super_name_list);
			int type = activity.getType();
			if (type == Activity.generic || type == Activity.exam) {
				superText.setVisibility(View.GONE);
			} else {
				switch (type) {
				case Activity.lecture:
					superText.setText(((Lecture) activity).getProfName());
					break;
				case Activity.lab:
					superText.setText(((Lab) activity).getSupervisorName());
					break;
				case Activity.tutorial:
					superText.setText(((Tutorial) activity).getTutorName());
					break;
				}
			}
			TextView locationText = (TextView) convertView.findViewById(R.id.activity_location_name_list);
			locationText.setText(activity.getLocation());
			int recurrence = activity.getRecurrence();
			TextView dateText = (TextView) convertView.findViewById(R.id.activity_date_list);
			TableRow daysRow = (TableRow) convertView.findViewById(R.id.activity_days_list);
			switch (recurrence) {
			case Activity.noRecurrence:
				daysRow.setVisibility(View.GONE);
				DateFormat df = DateFormat.getDateInstance();
				dateText.setText(df.format(activity.getStartTime()));
				break;
			case Activity.weeklyRecurrence:
				dateText.setVisibility(View.GONE);
				String[] days = getResources().getStringArray(R.array.days_of_the_week_one_letter);
				for (int i = 0; i < 7; i++) {
					TextView day = (TextView) daysRow.getChildAt(i);
					day.setText(days[i]);
					if (activity.getWeekday(i))
						day.setTextColor(getResources().getColor(android.R.color.black));
				}
				break;
			}
			SimpleDateFormat tf = new SimpleDateFormat("H:mm");
			Date startTime = activity.getStartTime(), endTime = activity.getEndTime();
			TextView timeText = (TextView) convertView.findViewById(R.id.activity_time_range_list);
			timeText.setText(tf.format(startTime) + " - " + tf.format(endTime));
			return convertView;
		}
	}
}
