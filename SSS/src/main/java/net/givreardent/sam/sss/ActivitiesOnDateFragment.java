package net.givreardent.sam.sss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.givreardent.sam.sss.data.CancelledList;
import net.givreardent.sam.sss.data.DeferredList;
import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.events.StrayActivityList;
import net.givreardent.sam.sss.dialogs.ActivityInfoFragment;
import net.givreardent.sam.sss.dialogs.DeferActivityFragment;
import net.givreardent.sam.sss.util.SSS;
import android.annotation.TargetApi;
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

public class ActivitiesOnDateFragment extends ListFragment {
	private Date date;
	private ArrayList<Activity> activities;
	private ListView list;

	@TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		date = new Date(getArguments().getLong(ActivitiesOnDateActivity.extraDate));
		DateFormat df = DateFormat.getDateInstance();
		getActivity().setTitle(getActivity().getTitle() + ": " + df.format(date));
		activities = SSS.getActivitiesOnDate(getActivity(), date);
		setListAdapter(new DayActivityAdapter(activities));
		if (Build.VERSION.SDK_INT >= 11) {
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, parent, false);
		list = (ListView) v.findViewById(android.R.id.list);
		registerForContextMenu(list);
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Activity activity = ((DayActivityAdapter) getListAdapter()).getItem(position);
		ActivityInfoFragment dialog;
		if (activity.getCourse() == null) {
			dialog = ActivityInfoFragment.newInstance(activity.getID(), null, null, date.getTime());
		} else {
			dialog = ActivityInfoFragment.newInstance(activity.getID(), activity.getCourse().getID(), activity
					.getCourse().getTerm().getID(), date.getTime());
		}
		dialog.show(getActivity().getSupportFragmentManager(), "Activity info");
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.day_activity_context, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int position = info.position;
		Activity activity = ((DayActivityAdapter) getListAdapter()).getItem(position);
		if (CancelledList.get(getActivity()).isInList(activity, date)) {
			menu.getItem(0).setEnabled(false).setVisible(false);
			menu.getItem(1).setEnabled(false).setVisible(false);
			menu.getItem(3).setVisible(false).setEnabled(false);
		} else if (DeferredList.get(getActivity()).isInList(activity, date)) {
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
		if (getUserVisibleHint()) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			int position = info.position;
			Activity activity = ((DayActivityAdapter) getListAdapter()).getItem(position);

			switch (item.getItemId()) {
			case R.id.day_activity_cancel:
				Log.i("tag", "Preparing to cancel item...");
				CancelledList.get(getActivity()).addCancelledActivity(activity, date);
				((DayActivityAdapter) getListAdapter()).notifyDataSetChanged();
				return true;
			case R.id.day_activity_defer:
				Log.i("tag", "Preparing to defer item...");
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DeferActivityFragment dialog = DeferActivityFragment.newInstance(activity, date);
				dialog.setTargetFragment(this, 1);
				dialog.show(fm, "Defer activity");
				return true;
			case R.id.day_activity_reset:
				if (CancelledList.get(getActivity()).isInList(activity, date)) {
					CancelledList.get(getActivity()).removeActivity(activity, date);
					((DayActivityAdapter) getListAdapter()).notifyDataSetChanged();
					Log.d("tag", "Preparing to reset item...");
				} else if (DeferredList.get(getActivity()).isInList(activity, date)) {
					DeferredList.get(getActivity()).removeActivity(activity.getID());
					((DayActivityAdapter) getListAdapter()).notifyDataSetChanged();
					activities = SSS.getTodayActivities(getActivity());
					setListAdapter(new DayActivityAdapter(activities));
				}
				return true;
			case R.id.day_activity_stray_cancel:
				if (DeferredList.get(getActivity()).isTargetActivity(activity))
					DeferredList.get(getActivity()).removeActivity(activity);
				StrayActivityList.get(getActivity()).removeActivity(activity);
				activities = SSS.getTodayActivities(getActivity());
				setListAdapter(new DayActivityAdapter(activities));
				return true;
			}
			return super.onContextItemSelected(item);
		} else {
			return false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		activities = SSS.getActivitiesOnDate(getActivity(), date);
		setListAdapter(new DayActivityAdapter(activities));
	}

	public static ActivitiesOnDateFragment newInstance(long date) {
		Bundle arguments = new Bundle();
		arguments.putLong(ActivitiesOnDateActivity.extraDate, date);
		ActivitiesOnDateFragment fragment = new ActivitiesOnDateFragment();
		fragment.setArguments(arguments);
		return fragment;
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
			LinearLayout countdown = (LinearLayout) convertView.findViewById(R.id.day_activity_starts_in_LinearLayout);
			TextView startsIn = (TextView) convertView.findViewById(R.id.day_activity_starts_in);
			if (CancelledList.get(getActivity()).isInList(activity, date)) {
				cancelledLayer.setVisibility(View.VISIBLE);
				title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				countdown.setVisibility(View.GONE);
			} else if (DeferredList.get(getActivity()).isInList(activity, date)) {
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
