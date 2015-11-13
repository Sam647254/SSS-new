package net.givreardent.sam.sss;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.courses.ActivityList;
import net.givreardent.sam.sss.data.courses.Lab;
import net.givreardent.sam.sss.data.courses.Lecture;
import net.givreardent.sam.sss.data.courses.Tutorial;
import net.givreardent.sam.sss.data.events.StrayActivityList;
import net.givreardent.sam.sss.dialogs.TaskCreateFragment;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InClassFragment extends Fragment {
	public static final String extraCurrentActivity = "Activity", extraCurrentCourse = "Course",
			extraCurrentTerm = "Term", extraNextActivity = "Next", extraNextCourse = "Next course";
	private Activity current;
	private Activity next;

	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UUID currentID = (UUID) getArguments().getSerializable(InClassFragment.extraCurrentActivity);
		UUID currentCourse = (UUID) getArguments().getSerializable(InClassFragment.extraCurrentCourse);
		UUID currentTerm = (UUID) getArguments().getSerializable(InClassFragment.extraCurrentTerm);
		UUID nextID = (UUID) getArguments().getSerializable(InClassFragment.extraNextActivity);
		UUID nextCourse = (UUID) getArguments().getSerializable(InClassFragment.extraNextCourse);
		if (currentCourse == null)
			current = StrayActivityList.get(getActivity()).getActivity(currentID);
		else
			current = ActivityList.get(getActivity(), currentCourse, currentTerm).getActivity(currentID);
		if (nextCourse == null)
			next = StrayActivityList.get(getActivity()).getActivity(nextID);
		else
			next = ActivityList.get(getActivity(), nextCourse, currentTerm).getActivity(nextID);
		if (Build.VERSION.SDK_INT >= 11)
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_in_class, parent, false);
		if (next == null) {
			LinearLayout nextLayout = (LinearLayout) v.findViewById(R.id.in_class_next_LinearLayout);
			TextView endOfDay = (TextView) v.findViewById(R.id.in_class_end_of_day);
			endOfDay.setVisibility(View.VISIBLE);
			nextLayout.setVisibility(View.GONE);
		}
		TextView superLabel = (TextView) v.findViewById(R.id.in_class_super_name_label);
		TextView superName = (TextView) v.findViewById(R.id.in_class_super_name);
		TextView currentName = (TextView) v.findViewById(R.id.in_class_name);
		TextView courseName = (TextView) v.findViewById(R.id.activity_course);
		String[] titles = getResources().getStringArray(R.array.prof_names);
		switch (current.getType()) {
		case Activity.lecture:
			superLabel.setText(titles[0]);
			superName.setText(((Lecture) current).getProfName());
			break;
		case Activity.tutorial:
			superLabel.setText(titles[2]);
			superName.setText(((Tutorial) current).getTutorName());
			break;
		case Activity.lab:
			superLabel.setText(titles[1]);
			superName.setText(((Lab) current).getSupervisorName());
			break;
		}
		currentName.setText(current.getName());
		courseName.setText(current.getCourse().getName());
		TextView startTime = (TextView) v.findViewById(R.id.activity_start);
		SimpleDateFormat df = new SimpleDateFormat("H:mm");
		startTime.setText(df.format(current.getStartTime()));
		TextView endTime = (TextView) v.findViewById(R.id.activity_end);
		endTime.setText(df.format(current.getEndTime()));
		TextView location = (TextView) v.findViewById(R.id.activity_location);
		location.setText(current.getLocation());
		if (next != null) {
			TextView nextName = (TextView) v.findViewById(R.id.in_class_name_next);
			nextName.setText(next.getName());
			if (next.getCourse() == null) {
				LinearLayout courseLayout = (LinearLayout) v.findViewById(R.id.activity_course_LinearLayout);
				courseLayout.setVisibility(View.GONE);
			} else {
				TextView course = (TextView) v.findViewById(R.id.activity_next_course);
				course.setText(next.getCourse().getName());
				View tile = v.findViewById(R.id.in_class_next_colour);
				tile.setBackgroundColor(next.getCourse().getColour());
			}
			TextView location2 = (TextView) v.findViewById(R.id.activity_next_location);
			location2.setText(next.getLocation());
			TextView nextTime = (TextView) v.findViewById(R.id.activity_next_time);
			nextTime.setText(df.format(next.getStartTime()) + " - " + df.format(next.getEndTime()));
			TextView nextCD = (TextView) v.findViewById(R.id.activity_remain_time);
			String minF = getResources().getString(R.string.in_class_next_min);
			String hF = getResources().getString(R.string.in_class_next_h);
			nextCD.setText(calculateGap(minF, hF, current, next));
		}
		Button addTask = (Button) v.findViewById(R.id.in_class_add_task_Button);
		addTask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TaskCreateFragment fragment = TaskCreateFragment.newInstance(current.getCourse().getID(), current
						.getCourse().getTerm().getID());
				fragment.setTargetFragment(InClassFragment.this, 0);
				fragment.show(fm, "Add task from in-class");
			}
		});
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == android.app.Activity.RESULT_OK) {
			Toast.makeText(getActivity(), R.string.add_task_success, Toast.LENGTH_SHORT).show();
		}
	}

	private String calculateGap(String minF, String hF, Activity a1, Activity a2) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(a1.getEndTime());
		calendar2.setTime(a2.getStartTime());
		int diff = (calendar2.get(Calendar.HOUR_OF_DAY) - calendar1.get(Calendar.HOUR_OF_DAY)) * 60
				+ (calendar2.get(Calendar.MINUTE) - calendar1.get(Calendar.MINUTE));
		if (diff >= 60) {
			return String.format(hF, diff / 60);
		} else {
			return String.format(minF, diff);
		}
	}

	public static InClassFragment newInstance(UUID currentID, UUID currentCourse, UUID currentTerm, UUID nextID,
			UUID nextCourse) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraCurrentActivity, currentID);
		if (currentCourse != null) {
			arguments.putSerializable(extraCurrentCourse, currentCourse);
			arguments.putSerializable(extraCurrentTerm, currentTerm);
		}
		arguments.putSerializable(extraNextActivity, nextID);
		if (nextCourse != null) {
			arguments.putSerializable(extraNextCourse, nextCourse);
		}
		InClassFragment fragment = new InClassFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
}
