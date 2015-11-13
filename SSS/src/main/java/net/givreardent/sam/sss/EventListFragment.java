package net.givreardent.sam.sss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.events.StrayActivityList;
import net.givreardent.sam.sss.dialogs.ActivityInfoFragment;
import net.givreardent.sam.sss.dialogs.CreateActivityFragment;
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
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EventListFragment extends ListFragment {
	private ArrayList<Activity> activities;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activities = StrayActivityList.get(getActivity()).getActivities();
	}

	public void resetAdapter() {
		activities = StrayActivityList.get(getActivity()).getActivities();
		DayActivityAdapter adapter = new DayActivityAdapter(activities);
		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.tasks_context, menu);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Activity activity = ((DayActivityAdapter) getListAdapter()).getItem(position);
		ActivityInfoFragment dialog;
		dialog = ActivityInfoFragment.newInstance(activity.getID(), null, null, activity.getStartTime().getTime());
		dialog.show(getActivity().getSupportFragmentManager(), "Activity info");
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, container, false);
		DayActivityAdapter adapter = new DayActivityAdapter(activities);
		setListAdapter(adapter);
		ListView list = (ListView) v.findViewById(android.R.id.list);
		if (Build.VERSION.SDK_INT < 11) {
			registerForContextMenu(list);
		} else {
			list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);;
			list.setMultiChoiceModeListener(new MultiChoiceModeListener() {
				
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
					inflater.inflate(R.menu.tasks_context, menu);
					return true;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch (item.getItemId()) {
					case R.id.edit_entry:
						DayActivityAdapter adapter = (DayActivityAdapter) getListAdapter();
						Activity event = null;
						for (int i = 0; i < adapter.getCount(); i++) {
							if (getListView().isItemChecked(i)) {
								event = adapter.getItem(i);
								break;
							}
						}
						CreateActivityFragment dialog = CreateActivityFragment.newInstance(event.getID(), null, null, false);
						dialog.setTargetFragment(EventListFragment.this, 0);
						dialog.show(getActivity().getSupportFragmentManager(), "Edit activity");
						mode.finish();
						break;
					case R.id.delete_entry:
						adapter = (DayActivityAdapter) getListAdapter();
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								StrayActivityList.get(getActivity()).removeActivity(adapter.getItem(i));
							}
						}
						mode.finish();
						resetAdapter();
					}
					return true;
				}
				
				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
					if (getListView().getCheckedItemCount() > 1) {
						mode.getMenu().findItem(R.id.edit_entry).setEnabled(false).setVisible(false);
					} else {
						mode.getMenu().findItem(R.id.edit_entry).setEnabled(true).setVisible(true);
					}
				}
			});
		}
		return v;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		resetAdapter();
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
			TextView date = (TextView) convertView.findViewById(R.id.day_activity_time);
			DateFormat df = DateFormat.getDateInstance();
			SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
			date.setText(df.format(activity.getStartTime()));
			LinearLayout timeLayout = (LinearLayout) convertView.findViewById(R.id.activity_start_end_LinearLayout);
			timeLayout.setVisibility(View.VISIBLE);
			TextView start = (TextView) convertView.findViewById(R.id.activity_start);
			TextView end = (TextView) convertView.findViewById(R.id.activity_end);
			start.setText(tf.format(activity.getStartTime()));
			end.setText(tf.format(activity.getEndTime()));
			TextView location = (TextView) convertView.findViewById(R.id.day_activity_location);
			location.setText(activity.getLocation());
			View colour = convertView.findViewById(R.id.day_colour_list);
			if (activity.getCourse() != null)
				colour.setBackgroundColor(activity.getCourse().getColour());
			return convertView;
		}
	}
}
