package net.givreardent.sam.sss.dialogs;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import net.givreardent.sam.sss.FrontActivity;
import net.givreardent.sam.sss.R;
import net.givreardent.sam.sss.data.Settings;
import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.courses.ActivityList;
import net.givreardent.sam.sss.data.courses.Lab;
import net.givreardent.sam.sss.data.courses.Lecture;
import net.givreardent.sam.sss.data.courses.Tutorial;
import net.givreardent.sam.sss.data.events.StrayActivityList;
import net.givreardent.sam.sss.util.SSS;
import net.givreardent.sam.sss.util.ScrollProofTimePicker;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class CreateActivityFragment extends DialogFragment {
	public static final String extraType = "Type";
	public static final String extraName = "Name", extraSuperName = "Super. name", extraLocation = "Location",
			extraStart = "Start date", extraEnd = "End date", extraDays = "Weekdays", extraRecurrence = "Recurrence",
			extraDate = "Date", extraActivityID = "Activity ID", extraTermID = "Term ID", extraCourseID = "Course ID",
			extraForMarks = "For marks", extraWeight = "Weight", extraGradable = "Gradable", extraReminder = "Reminder";

	private EditText name, superName, Location, reminder;
	private ScrollProofTimePicker start, end;
	private DatePicker date;
	private CheckBox[] days;
	private CheckBox forMarks, reminderDefault;
	private Spinner recurrence;
	private LinearLayout dateLayout;
	private LinearLayout recurLayout;
	private Resources res;
	private Activity activity;
	private int type;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_create_activity, null);
		activity = null;
		if (getArguments().getSerializable(extraActivityID) != null) {
			if (getArguments().getSerializable(extraCourseID) == null) {
				activity = StrayActivityList.get(getActivity()).getActivity(
						(UUID) getArguments().getSerializable(extraActivityID));
			} else
				activity = ActivityList.get(getActivity(), (UUID) getArguments().getSerializable(extraCourseID),
						(UUID) getArguments().getSerializable(extraTermID)).getActivity(
						(UUID) getArguments().getSerializable(extraActivityID));
		}
		res = getResources();
		String nameLabel;
		if (activity == null)
			type = getArguments().getInt(extraType);
		else
			type = activity.getType();
		switch (type) {
		case Activity.lecture:
			nameLabel = res.getString(R.string.create_lecture_button);
			break;
		case Activity.lab:
			nameLabel = res.getString(R.string.create_lab_button);
			break;
		case Activity.tutorial:
			nameLabel = res.getString(R.string.create_tutorial_button);
			break;
		case Activity.exam:
			nameLabel = res.getString(R.string.create_exam_button);
			break;
		default:
			nameLabel = res.getString(R.string.other_activity);
			break;
		}
		name = (EditText) v.findViewById(R.id.activity_name);
		superName = (EditText) v.findViewById(R.id.activity_super_name);
		Location = (EditText) v.findViewById(R.id.activity_location_name);
		reminder = (EditText) v.findViewById(R.id.activity_reminder);
		if (type == Activity.exam)
			reminder.setText(Integer.toString(Settings.getExamReminder(getActivity())));
		else
			reminder.setText(Integer.toString(Settings.getReminder(getActivity())));
		reminderDefault = (CheckBox) v.findViewById(R.id.reminder_default_checkbox);
		reminderDefault.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (type == Activity.exam)
						reminder.setText(Integer.toString(Settings.getExamReminder(getActivity())));
					else
						reminder.setText(Integer.toString(Settings.getReminder(getActivity())));
					reminder.setEnabled(false);
				} else {
					reminder.setEnabled(true);
				}
			}
		});
		forMarks = (CheckBox) v.findViewById(R.id.activity_for_marks);
		recurrence = (Spinner) v.findViewById(R.id.activity_recurrence_spinner);
		TableRow row1 = (TableRow) v.findViewById(R.id.activity_days_row1);
		TableRow row2 = (TableRow) v.findViewById(R.id.activity_days_row2);
		String hint = res.getString(R.string.activity_name_label);
		dateLayout = (LinearLayout) v.findViewById(R.id.activity_date_layout);
		date = (DatePicker) v.findViewById(R.id.activity_datePicker);
		recurLayout = (LinearLayout) v.findViewById(R.id.activity_recurrence_layout);
		start = (ScrollProofTimePicker) v.findViewById(R.id.activity_start_timePicker);
		end = (ScrollProofTimePicker) v.findViewById(R.id.activity_end_timePicker);
		// Set up
		name.setHint(nameLabel + " " + hint);
		if (type > Activity.tutorial || type <= Activity.generic)
			superName.setVisibility(View.GONE);
		else {
			String[] titles = res.getStringArray(R.array.prof_names);
			String nameLabel2 = titles[type - 1];
			superName.setHint(nameLabel2 + " " + hint);
		}
		boolean gradable = getArguments().getBoolean(extraGradable);
		if (type == Activity.exam && gradable) {
			forMarks.setVisibility(View.VISIBLE);
		}
		forMarks.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// make toast here
				}
			}
		});
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.recurrence_types,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		recurrence.setAdapter(adapter);
		String[] weekdays = res.getStringArray(R.array.days_of_the_week_short);
		days = new CheckBox[7];
		for (int i = 0; i < row1.getChildCount(); i++) {
			days[i] = (CheckBox) row1.getChildAt(i);
			days[i].setText(weekdays[i]);
		}

		for (int i = row1.getChildCount(); i < (row1.getChildCount() + row2.getChildCount()); i++) {
			days[i] = (CheckBox) row2.getChildAt(i - row1.getChildCount());
			days[i].setText(weekdays[i]);
		}
		if (type > Activity.tutorial || type <= Activity.generic) {
			recurrence.setSelection(0);
			hideDays();
			if (type == Activity.exam || type == Activity.stray) {
				recurLayout.setVisibility(View.GONE);
			}
		} else
			recurrence.setSelection(1);
		recurrence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					hideDays();
					break;
				case 1:
					showDays();
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				parent.setSelection(1);
				showDays();
			}
		});
		start.setIs24HourView(true);
		end.setIs24HourView(true);
		start.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				if (hourOfDay > end.getCurrentHour() || minute >= end.getCurrentMinute()) {
					end.setCurrentHour(hourOfDay + 1);
					end.setCurrentMinute(minute);
				}
			}
		});
		builder.setView(v);
		String temp = res.getString(R.string.create_activity_title);
		builder.setTitle(temp + " " + nameLabel);
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (activity == null)
					sendResult(true);
				else
					sendResult(false);
			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
		if (activity != null) {
			name.setText(activity.getName());
			switch (activity.getType()) {
			case Activity.lecture:
				superName.setText(((Lecture) activity).getProfName());
				break;
			case Activity.lab:
				superName.setText(((Lab) activity).getSupervisorName());
				break;
			case Activity.tutorial:
				superName.setText(((Tutorial) activity).getTutorName());
				break;
			}
			Location.setText(activity.getLocation());
			if (activity.getReminder() == Activity.defaultReminder) {
				reminderDefault.setChecked(true);
				if (activity.getType() == Activity.exam)
					reminder.setText(Integer.toString(Settings.getExamReminder(getActivity())));
				else
					reminder.setText(Integer.toString(Settings.getReminder(getActivity())));
			} else {
				reminderDefault.setChecked(false);
				reminder.setText(Integer.toString(activity.getReminder()));
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(activity.getStartTime());
			date.init(calendar.get(1), calendar.get(2), calendar.get(5), null);
			start.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			start.setCurrentMinute(calendar.get(Calendar.MINUTE));
			calendar.setTime(activity.getEndTime());
			end.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			end.setCurrentMinute(calendar.get(Calendar.MINUTE));
			for (int i = 0; i < 7; i++) {
				days[i].setChecked(activity.getWeekday(i));
			}
			switch (activity.getRecurrence()) {
			case Activity.noRecurrence:
				recurrence.setSelection(0);
				break;
			case Activity.weeklyRecurrence:
				recurrence.setSelection(1);
				break;
			}
		} else {
			start.setCurrentHour(8);
			start.setCurrentMinute(0);
			end.setCurrentHour(9);
			end.setCurrentMinute(0);
		}
		return builder.create();
	}

	private void hideDays() {
		for (int i = 0; i < 7; i++) {
			days[i].setVisibility(View.GONE);
		}
		dateLayout.setVisibility(View.VISIBLE);
	}

	private void showDays() {
		for (int i = 0; i < 7; i++) {
			days[i].setVisibility(View.VISIBLE);
		}
		dateLayout.setVisibility(View.GONE);
	}

	private void sendResult(boolean isNew) {
		if (type == Activity.stray) {
			Activity activity = new Activity(null);
			activity.setTitle(name.getText().toString());
			GregorianCalendar cal1 = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth(),
					start.getCurrentHour(), start.getCurrentMinute());
			activity.setStartTime(cal1.getTime());
			GregorianCalendar cal2 = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth(),
					end.getCurrentHour(), end.getCurrentMinute());
			if (end.getCurrentHour() < start.getCurrentHour()
					|| (start.getCurrentHour() == end.getCurrentHour() && end.getCurrentMinute() < start
							.getCurrentMinute()))
				cal2.add(5, 1);
			activity.setEndTime(cal2.getTime());
			activity.setLocation(Location.getText().toString());
			activity.setRecurrence(Activity.noRecurrence);
			activity.setReminder(Integer.parseInt(reminder.getText().toString()));
			StrayActivityList.get(getActivity()).addActivity(activity);
			StrayActivityList.get(getActivity()).removeActivity(this.activity);
			((FrontActivity) getActivity()).onDialogResult();
			SSS.clearNotifications(getActivity());
			SSS.setNotifications(getActivity());
			return;
		}
		Intent data = new Intent();
		data.putExtra(extraName, name.getText().toString());
		data.putExtra(extraSuperName, superName.getText().toString());
		data.putExtra(extraLocation, Location.getText().toString());
		data.putExtra(extraForMarks, forMarks.isChecked());
		data.putExtra(extraReminder, Integer.parseInt(reminder.getText().toString()));
		int recur = recurrence.getSelectedItemPosition();
		GregorianCalendar start, end;
		switch (recur) {
		case 0:
			data.putExtra(extraRecurrence, Activity.noRecurrence);
			start = new GregorianCalendar(this.date.getYear(), this.date.getMonth(), this.date.getDayOfMonth(),
					this.start.getCurrentHour(), this.start.getCurrentMinute());
			data.putExtra(extraStart, start.getTimeInMillis());
			end = new GregorianCalendar(this.date.getYear(), this.date.getMonth(), this.date.getDayOfMonth(),
					this.end.getCurrentHour(), this.end.getCurrentMinute());
			data.putExtra(extraEnd, end.getTimeInMillis());
			break;
		case 1:
			data.putExtra(extraRecurrence, Activity.weeklyRecurrence);
			start = new GregorianCalendar(0, 0, 0, this.start.getCurrentHour(), this.start.getCurrentMinute());
			data.putExtra(extraStart, start.getTimeInMillis());
			end = new GregorianCalendar(0, 0, 0, this.end.getCurrentHour(), this.end.getCurrentMinute());
			data.putExtra(extraEnd, end.getTimeInMillis());
			boolean[] dateChecked = new boolean[7];
			for (int i = 0; i < 7; i++) {
				dateChecked[i] = days[i].isChecked();
			}
			data.putExtra(extraDays, dateChecked);
			break;
		default:
			data.putExtra(extraRecurrence, Activity.noRecurrence);
			break;
		}
		data.putExtra(extraType, type);
		if (getTargetFragment() == null)
			return;
		if (isNew) {
			getTargetFragment().onActivityResult(getTargetRequestCode(), android.app.Activity.RESULT_OK, data);
		} else
			getTargetFragment().onActivityResult(getTargetRequestCode(), 1, data);
	}

	public static CreateActivityFragment newInstance(int type, boolean gradable) {
		Bundle arguments = new Bundle();
		arguments.putInt(extraType, type);
		arguments.putBoolean(extraGradable, gradable);
		CreateActivityFragment fragment = new CreateActivityFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	public static CreateActivityFragment newInstance(UUID activityID, UUID courseID, UUID termID, boolean gradable) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraActivityID, activityID);
		arguments.putSerializable(extraCourseID, courseID);
		arguments.putSerializable(extraTermID, termID);
		arguments.putBoolean(extraGradable, gradable);
		CreateActivityFragment fragment = new CreateActivityFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
}
