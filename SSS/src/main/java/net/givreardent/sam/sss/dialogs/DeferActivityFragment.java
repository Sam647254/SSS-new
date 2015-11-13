package net.givreardent.sam.sss.dialogs;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import net.givreardent.sam.sss.R;
import net.givreardent.sam.sss.data.DeferredList;
import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.courses.ActivityList;
import net.givreardent.sam.sss.data.events.StrayActivityList;
import net.givreardent.sam.sss.util.ScrollProofDatePicker;
import net.givreardent.sam.sss.util.ScrollProofTimePicker;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

public class DeferActivityFragment extends DialogFragment {
	public static final String extraActivityID = "Activity", extraCourseID = "Course", extraTermID = "Term",
			extraToday = "Today", extraDate = "Date";
	private EditText location;
	private ScrollProofDatePicker date;
	private ScrollProofTimePicker time;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_defer_activity, null);
		location = (EditText) v.findViewById(R.id.defer_location);
		date = (ScrollProofDatePicker) v.findViewById(R.id.defer_datePicker);
		time = (ScrollProofTimePicker) v.findViewById(R.id.defer_timePicker);
		time.setIs24HourView(false);
		builder.setView(v);
		builder.setTitle(R.string.defer_dialog_title);
		builder.setNeutralButton(android.R.string.cancel, null);
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendResult();
			}
		});
		return builder.create();
	}

	private void sendResult() {
		Activity newActivity = new Activity(null);
		Activity activity = ActivityList.get(getActivity(), (UUID) getArguments().getSerializable(extraCourseID),
				(UUID) getArguments().getSerializable(extraTermID)).getActivity(
				(UUID) getArguments().getSerializable(extraActivityID));
		GregorianCalendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth(),
				time.getCurrentHour(), time.getCurrentMinute());
		if (activity.getCourse() == null)
			newActivity.setTitle(activity.getName());
		else
			newActivity.setTitle(activity.getName() + " - " + activity.getCourse().getCode());
		if (location.getText().toString().length() == 0)
			newActivity.setLocation(activity.getLocation());
		else
			newActivity.setLocation(location.getText().toString());
		newActivity.setStartTime(calendar.getTime());
		calendar.add(Calendar.MINUTE, activity.getDuration());
		newActivity.setEndTime(calendar.getTime());
		StrayActivityList.get(getActivity()).addActivity(newActivity);
		if (getArguments().getLong(extraDate, 0) != 0) {
			DeferredList.get(getActivity()).addActivity(activity, newActivity,
					new Date(getArguments().getLong(extraDate)));
		} else
			DeferredList.get(getActivity()).addActivity(activity, newActivity, getArguments().getBoolean(extraToday));
		if (getTargetFragment() == null)
			return;
		getTargetFragment().onActivityResult(getTargetRequestCode(), android.app.Activity.RESULT_OK, null);
	}

	public static DeferActivityFragment newInstance(Activity a, boolean isToday) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraCourseID, a.getCourse().getID());
		arguments.putSerializable(extraTermID, a.getCourse().getTerm().getID());
		arguments.putSerializable(extraActivityID, a.getID());
		arguments.putBoolean(extraToday, isToday);
		DeferActivityFragment fragment = new DeferActivityFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	public static DeferActivityFragment newInstance(Activity a, Date date2) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraCourseID, a.getCourse().getID());
		arguments.putSerializable(extraTermID, a.getCourse().getTerm().getID());
		arguments.putSerializable(extraActivityID, a.getID());
		arguments.putLong(extraDate, date2.getTime());
		DeferActivityFragment fragment = new DeferActivityFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
}
