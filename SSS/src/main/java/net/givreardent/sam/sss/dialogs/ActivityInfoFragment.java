package net.givreardent.sam.sss.dialogs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import net.givreardent.sam.sss.R;
import net.givreardent.sam.sss.data.CancelledList;
import net.givreardent.sam.sss.data.CustomReminderList;
import net.givreardent.sam.sss.data.DeferredList;
import net.givreardent.sam.sss.data.Settings;
import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.courses.ActivityList;
import net.givreardent.sam.sss.data.courses.Lab;
import net.givreardent.sam.sss.data.courses.Lecture;
import net.givreardent.sam.sss.data.courses.Tutorial;
import net.givreardent.sam.sss.data.events.StrayActivityList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityInfoFragment extends DialogFragment {
	public static final String extraActivityID = "Activity", extraCourseID = "Course", extraTermID = "Term",
			extraIsToday = "Is today", extraDate = "Date";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		UUID termID = (UUID) getArguments().getSerializable(extraTermID);
		UUID courseID = (UUID) getArguments().getSerializable(extraCourseID);
		UUID activityID = (UUID) getArguments().getSerializable(extraActivityID);
		Activity activity = null;
		if (termID == null) {
			activity = StrayActivityList.get(getActivity()).getActivity(activityID);
		} else {
			activity = ActivityList.get(getActivity(), courseID, termID).getActivity(activityID);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.activity_info_title);
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_activity_info, null);
		TextView name = (TextView) v.findViewById(R.id.activity_name);
		name.setText(activity.getName());
		LinearLayout courseLayout = (LinearLayout) v.findViewById(R.id.activity_course_LinearLayout);
		if (termID == null)
			courseLayout.setVisibility(View.GONE);
		else {
			TextView course = (TextView) v.findViewById(R.id.activity_course);
			course.setText(activity.getCourse().getCode());
			LinearLayout superLayout = (LinearLayout) v.findViewById(R.id.activity_super_LinearLayout);
			TextView superLabel = (TextView) v.findViewById(R.id.activity_super_label);
			TextView superName = (TextView) v.findViewById(R.id.activity_super);
			superLayout.setVisibility(View.VISIBLE);
			String[] titles = getResources().getStringArray(R.array.prof_names);
			switch (activity.getType()) {
			case Activity.lecture:
				superLabel.setText(titles[0]);
				superName.setText(((Lecture) activity).getProfName());
				break;
			case Activity.tutorial:
				superLabel.setText(titles[2]);
				superName.setText(((Tutorial) activity).getTutorName());
				break;
			case Activity.lab:
				superLabel.setText(titles[1]);
				superName.setText(((Lab) activity).getSupervisorName());
				break;
			default:
				superLayout.setVisibility(View.GONE);
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("H:mm");
		DateFormat df2 = DateFormat.getDateInstance();
		TextView date = (TextView) v.findViewById(R.id.activity_date);
		if (activity.getRecurrence() == Activity.noRecurrence)
			date.setText(df2.format(activity.getStartTime()));
		long dateL = getArguments().getLong(extraDate);
		Calendar calendar = Calendar.getInstance();
		Date activityDate = new Date(dateL);
		if (dateL == 0) {
			if (!getArguments().getBoolean(extraIsToday))
				calendar.add(5, 1);
			date.setText(df2.format(calendar.getTime()));
		} else {
			date.setText(df2.format(activityDate));
		}
		TextView start = (TextView) v.findViewById(R.id.activity_start);
		start.setText(df.format(activity.getStartTime()));
		TextView end = (TextView) v.findViewById(R.id.activity_end);
		end.setText(df.format(activity.getEndTime()));
		String[] statuses = getResources().getStringArray(R.array.activity_statuses);
		TextView status = (TextView) v.findViewById(R.id.activity_status);
		if (getArguments().containsKey(extraIsToday)) {
			if (activity.hasPassed() && getArguments().getBoolean(extraIsToday)) {
				status.setText(statuses[1]);
			} else if (CancelledList.get(getActivity()).isInList(activity, getArguments().getBoolean(extraIsToday))) {
				status.setText(statuses[2]);
			} else if (activity.isCurrent() && getArguments().getBoolean(extraIsToday)) {
				status.setText(statuses[3]);
			} else if (DeferredList.get(getActivity()).isInList(activity, getArguments().getBoolean(extraIsToday))) {
				status.setText(statuses[4]);
			} else {
				status.setText(statuses[0]);
			}
		} else {
			Date today = new Date();
			if (today.compareTo(activityDate) > 0) {
				status.setText(statuses[1]);
			} else if (CancelledList.get(getActivity()).isInList(activity, activityDate)) {
				status.setText(statuses[2]);
			} else if (DeferredList.get(getActivity()).isInList(activity, activityDate)) {
				status.setText(statuses[4]);
			} else {
				status.setText(statuses[0]);
			}
		}
		TextView location = (TextView) v.findViewById(R.id.activity_location);
		location.setText(activity.getLocation());
		TextView reminder = (TextView) v.findViewById(R.id.activity_reminder);
		int reminderMin;
		if (activity.getType() == Activity.stray)
			reminderMin = activity.getReminder();
		else {
			reminderMin = CustomReminderList.get(getActivity()).getCustomReminder(activity, calendar.getTime());
			if (reminderMin == -1) {
				if (activity.getType() == Activity.exam)
					reminderMin = Settings.getExamReminder(getActivity());
				else
					reminderMin = Settings.getReminder(getActivity());
			}
		}
		reminder.setText(getResources().getString(R.string.activity_info_reminder_min, reminderMin));
		builder.setView(v);
		builder.setNeutralButton(android.R.string.ok, null);
		return builder.create();
	}

	public static ActivityInfoFragment newInstance(UUID activityID, UUID courseID, UUID termID, boolean isToday) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraActivityID, activityID);
		arguments.putSerializable(extraCourseID, courseID);
		arguments.putSerializable(extraTermID, termID);
		arguments.putBoolean(extraIsToday, isToday);
		ActivityInfoFragment fragment = new ActivityInfoFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	public static ActivityInfoFragment newInstance(UUID activityID, UUID courseID, UUID termID, long date) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraActivityID, activityID);
		arguments.putSerializable(extraCourseID, courseID);
		arguments.putSerializable(extraTermID, termID);
		arguments.putLong(extraDate, date);
		ActivityInfoFragment fragment = new ActivityInfoFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
}
