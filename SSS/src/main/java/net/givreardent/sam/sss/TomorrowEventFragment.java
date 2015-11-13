package net.givreardent.sam.sss;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.givreardent.sam.sss.data.CancelledList;
import net.givreardent.sam.sss.data.DeferredList;
import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.events.StrayActivityList;
import net.givreardent.sam.sss.dialogs.ActivityInfoFragment;
import net.givreardent.sam.sss.dialogs.DeferActivityFragment;
import net.givreardent.sam.sss.util.SSS;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
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

public class TomorrowEventFragment extends ListFragment {
	private ListView activityList;
	private ArrayList<Activity> activities;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tomorrow_event, container, false);
		activityList = (ListView) v.findViewById(android.R.id.list);
		activityList.setEmptyView(v.findViewById(android.R.id.empty));
		activities = SSS.getTomorrowActivities(getActivity());
		setListAdapter(new DayActivityAdapter(SSS.getTomorrowActivities(getActivity())));
		registerForContextMenu(activityList);
		return v;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(1, R.id.day_activity_cancel, 0, R.string.cancel_activity);
		menu.add(1, R.id.day_activity_defer, 1, R.string.defer_activity);
		menu.add(1, R.id.day_activity_reset, 2, R.string.reset_activity);
		menu.add(1, R.id.day_activity_stray_cancel, 3, R.string.cancel_stray_activity);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int position = info.position;
		Activity activity = ((DayActivityAdapter) getListAdapter()).getItem(position);
		if (CancelledList.get(getActivity()).isInList(activity, false)) {
			menu.getItem(0).setEnabled(false).setVisible(false);
			menu.getItem(1).setEnabled(false).setVisible(false);
			menu.getItem(3).setVisible(false).setEnabled(false);
		} else if (DeferredList.get(getActivity()).isInList(activity, false)) {
			menu.getItem(0).setEnabled(false).setVisible(false);
			menu.getItem(1).setEnabled(false).setVisible(false);
			menu.getItem(3).setVisible(false).setEnabled(false);
		} else if (activity.getType() == Activity.stray) {
			menu.getItem(0).setEnabled(false);
			menu.getItem(1).setEnabled(false);
			menu.getItem(2).setVisible(false).setEnabled(false);
		} else {
			menu.getItem(2).setVisible(false).setEnabled(false);
			menu.getItem(3).setVisible(false).setEnabled(false);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() == 1) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			int position = info.position;
			Activity activity = ((DayActivityAdapter) activityList.getAdapter()).getItem(position);

			switch (item.getItemId()) {
			case R.id.day_activity_cancel:
				Log.i("tag", "Preparing to cancel item...");
				CancelledList.get(getActivity()).addCancelledActivity(activity, false);
				((DayActivityAdapter) getListAdapter()).notifyDataSetChanged();
				return true;
			case R.id.day_activity_defer:
				Log.i("tag", "Preparing to defer item...");
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DeferActivityFragment dialog = DeferActivityFragment.newInstance(activity, false);
				dialog.setTargetFragment(this, 1);
				dialog.show(fm, "Defer activity");
				return true;
			case R.id.day_activity_reset:
				if (CancelledList.get(getActivity()).isInList(activity, false)) {
					CancelledList.get(getActivity()).removeActivity(activity, false);
					((DayActivityAdapter) getListAdapter()).notifyDataSetChanged();
					Log.d("tag", "Preparing to reset item...");
				} else if (DeferredList.get(getActivity()).isInList(activity, false)) {
					DeferredList.get(getActivity()).removeActivity(activity.getID());
					((DayActivityAdapter) getListAdapter()).notifyDataSetChanged();
					activities = SSS.getTomorrowActivities(getActivity());
					setListAdapter(new DayActivityAdapter(activities));
				}
				return true;
			case R.id.day_activity_stray_cancel:
				if (DeferredList.get(getActivity()).isTargetActivity(activity))
					DeferredList.get(getActivity()).removeActivity(activity);
				StrayActivityList.get(getActivity()).removeActivity(activity);
				activities = SSS.getTomorrowActivities(getActivity());
				setListAdapter(new DayActivityAdapter(activities));
				return true;
			}
			return super.onContextItemSelected(item);
		} else {
			return false;
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Activity activity = (Activity) getListAdapter().getItem(position);
		ActivityInfoFragment dialog;
		if (activity.getCourse() == null) {
			dialog = ActivityInfoFragment.newInstance(activity.getID(), null, null, false);
		} else {
			dialog = ActivityInfoFragment.newInstance(activity.getID(), activity.getCourse().getID(), activity
					.getCourse().getTerm().getID(), false);
		}
		dialog.show(getActivity().getSupportFragmentManager(), "Activity info");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("tag", "Tomorrow event fragment on Activity result called.");
		refresh();
	}

	public void refresh() {
		activities = SSS.getTomorrowActivities(getActivity());
		setListAdapter(new DayActivityAdapter(activities));
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
			TextView cancelledLayer = (TextView) convertView.findViewById(R.id.day_activity_cancelled_layer);
			TextView deferredLayer = (TextView) convertView.findViewById(R.id.day_activity_deferred_layer);
			TextView title = (TextView) convertView.findViewById(R.id.day_activity_name);
			if (activity.getCourse() == null)
				title.setText(activity.getName());
			else
				title.setText(activity.getName() + " - " + activity.getCourse().getCode());
			TextView time = (TextView) convertView.findViewById(R.id.day_activity_time);
			SimpleDateFormat tf = new SimpleDateFormat("H:mm");
			time.setText(tf.format(activity.getStartTime()) + " - " + tf.format(activity.getEndTime()));
			TextView location = (TextView) convertView.findViewById(R.id.day_activity_location);
			location.setText(activity.getLocation());
			View colour = convertView.findViewById(R.id.day_colour_list);
			if (Build.VERSION.SDK_INT < 11) {
				colour.setVisibility(View.GONE);
			}
			LinearLayout countdown = (LinearLayout) convertView.findViewById(R.id.day_activity_starts_in_LinearLayout);
			TextView startsIn = (TextView) convertView.findViewById(R.id.day_activity_starts_in);
			if (CancelledList.get(getActivity()).isInList(activity, false)) {
				cancelledLayer.setVisibility(View.VISIBLE);
				title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				countdown.setVisibility(View.GONE);
			} else if (DeferredList.get(getActivity()).isInList(activity, false)) {
				deferredLayer.setVisibility(View.VISIBLE);
			} else {
				cancelledLayer.setVisibility(View.GONE);
				deferredLayer.setVisibility(View.GONE);
				title.setPaintFlags(0);
			}
			if (activity.getCourse() != null)
				colour.setBackgroundColor(activity.getCourse().getColour());
			return convertView;
		}
	}
}
