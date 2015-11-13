package net.givreardent.sam.sss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.events.StrayActivityList;
import net.givreardent.sam.sss.dialogs.ActivityInfoFragment;
import net.givreardent.sam.sss.dialogs.CreateActivityFragment;
import net.givreardent.sam.sss.util.SSS;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UpcomingEventFragment extends ListFragment {
	private ArrayList<Activity> activities;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_upcoming_events, container, false);
		activities = SSS.getUpcomingEvents(getActivity());
		ListView list = (ListView) v.findViewById(android.R.id.list);
		registerForContextMenu(list);
		DayActivityAdapter adapter = new DayActivityAdapter(activities);
		setListAdapter(adapter);
		return v;
	}
	
	public void resetAdapter() {
		activities = SSS.getUpcomingEvents(getActivity());
		setListAdapter(new DayActivityAdapter(activities));
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(3, R.id.edit_entry, 0, R.string.edit);
		menu.add(3, R.id.delete_entry, 1, R.string.delete_entry);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() != 3)
			return false;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		Activity activity = ((DayActivityAdapter) getListAdapter()).getItem(position);
		switch(item.getItemId()) {
		case R.id.edit_entry:
			CreateActivityFragment dialog = CreateActivityFragment.newInstance(activity.getID(), null, null, false);
			dialog.setTargetFragment(this, 0);
			dialog.show(getActivity().getSupportFragmentManager(), "Edit activity");
			break;
		case R.id.delete_entry:
			StrayActivityList.get(getActivity()).removeActivity(activity);
			resetAdapter();
		}
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		resetAdapter();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Activity activity = (Activity) getListAdapter().getItem(position);
		ActivityInfoFragment dialog = ActivityInfoFragment.newInstance(activity.getID(), null, null, false);
		dialog.show(getActivity().getSupportFragmentManager(), "Activity info");
	}

	private class DayActivityAdapter extends ArrayAdapter<Activity> {

		public DayActivityAdapter(ArrayList<Activity> activities) {
			super(getActivity(), 0, activities);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_day_activity, null);
			}
			Activity activity = getItem(position);
			TextView title = (TextView) convertView.findViewById(R.id.day_activity_name);
			if (activity.getCourse() == null)
				title.setText(activity.getName());
			else
				title.setText(activity.getName() + " - " + activity.getCourse().getCode());
			TextView time = (TextView) convertView.findViewById(R.id.day_activity_time);
			DateFormat df = DateFormat.getDateInstance();
			SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
			time.setText(df.format(activity.getStartTime()));
			LinearLayout timeLayout = (LinearLayout) convertView.findViewById(R.id.activity_start_end_LinearLayout);
			timeLayout.setVisibility(View.VISIBLE);
			TextView start = (TextView) convertView.findViewById(R.id.activity_start);
			TextView end = (TextView) convertView.findViewById(R.id.activity_end);
			start.setText(tf.format(activity.getStartTime()));
			end.setText(tf.format(activity.getEndTime()));
			TextView location = (TextView) convertView.findViewById(R.id.day_activity_location);
			location.setText(activity.getLocation());
			View colour = convertView.findViewById(R.id.day_colour_list);
			colour.setVisibility(View.GONE);
			return convertView;
		}
	}
}
