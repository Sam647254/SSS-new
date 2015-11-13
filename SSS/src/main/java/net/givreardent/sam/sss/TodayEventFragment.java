package net.givreardent.sam.sss;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment.NumberPickerDialogHandler;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import net.givreardent.sam.sss.data.CancelledList;
import net.givreardent.sam.sss.data.CustomReminderList;
import net.givreardent.sam.sss.data.DeferredList;
import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.events.StrayActivityList;
import net.givreardent.sam.sss.dialogs.ActivityInfoFragment;
import net.givreardent.sam.sss.dialogs.DeferActivityFragment;
import net.givreardent.sam.sss.util.SSS;

public class TodayEventFragment extends ListFragment {
	public static final int quickCreateActivityRequest = 0;
	public static final int deferActivityRequest = 1;
	private ListView activityList;
	private ArrayList<Activity> activities;
	private Timer timer;
	private TimerTask timerTask;
	private Handler timerHandler, inClassHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_today_event, container, false);
		activityList = (ListView) v.findViewById(android.R.id.list);
		activityList.setEmptyView(v.findViewById(android.R.id.empty));
		registerForContextMenu(activityList);
		return v;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(0, R.id.day_activity_cancel, 0, R.string.cancel_activity);
		menu.add(0, R.id.day_activity_defer, 1, R.string.defer_activity);
		menu.add(0, R.id.day_activity_reset, 2, R.string.reset_activity);
		menu.add(0, R.id.day_activity_stray_cancel, 3, R.string.cancel_stray_activity);
		menu.add(0, R.id.day_change_reminder, 4, R.string.change_reminder);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int position = info.position;
		Activity activity = ((DayActivityAdapter) activityList.getAdapter()).getItem(position);
		if (activity.getType() == Activity.stray)
			menu.getItem(4).setVisible(false);
		else {
			Calendar calendar = Calendar.getInstance();
			Calendar aCalendar = Calendar.getInstance();
			aCalendar.setTime(activity.getStartTime());
			calendar.set(Calendar.HOUR_OF_DAY, aCalendar.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, aCalendar.get(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, aCalendar.get(Calendar.SECOND));
			calendar.set(Calendar.MILLISECOND, aCalendar.get(Calendar.MILLISECOND));
			if (CustomReminderList.get(getActivity()).isInList(activity, calendar.getTime())) {
				menu.getItem(4).setEnabled(false).setVisible(false);
				menu.add(0, R.id.day_reset_reminder, 5, R.string.reset_activity);
			}
		}
		if (activity.hasPassed() || activity.isCurrent()) {
			menu.getItem(0).setEnabled(false);
			menu.getItem(1).setEnabled(false);
			menu.getItem(2).setVisible(false).setEnabled(false);
			menu.getItem(3).setVisible(false).setEnabled(false);
			menu.getItem(4).setEnabled(false);
		} else if (CancelledList.get(getActivity()).isInList(activity, true)) {
			menu.getItem(0).setEnabled(false).setVisible(false);
			menu.getItem(1).setEnabled(false).setVisible(false);
			menu.getItem(3).setVisible(false).setEnabled(false);
			menu.getItem(4).setVisible(false).setEnabled(false);
		} else if (DeferredList.get(getActivity()).isInList(activity, true)) {
			menu.getItem(0).setEnabled(false).setVisible(false);
			menu.getItem(1).setEnabled(false).setVisible(false);
			menu.getItem(3).setVisible(false).setEnabled(false);
			menu.getItem(4).setVisible(false).setEnabled(false);
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
		if (item.getGroupId() == 0) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			int position = info.position;
			final Activity activity = ((DayActivityAdapter) getListAdapter()).getItem(position);

			switch (item.getItemId()) {
			case R.id.day_activity_cancel:
				Log.i("tag", "Preparing to cancel item...");
				CancelledList.get(getActivity()).addCancelledActivity(activity, true);
				((DayActivityAdapter) getListAdapter()).notifyDataSetChanged();
				return true;
			case R.id.day_activity_defer:
				Log.i("tag", "Preparing to defer item...");
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DeferActivityFragment dialog = DeferActivityFragment.newInstance(activity, true);
				dialog.setTargetFragment(this, 1);
				dialog.show(fm, "Defer activity");
				return true;
			case R.id.day_activity_reset:
				if (CancelledList.get(getActivity()).isInList(activity, true)) {
					CancelledList.get(getActivity()).removeActivity(activity, true);
					((DayActivityAdapter) getListAdapter()).notifyDataSetChanged();
					Log.d("tag", "Preparing to reset item...");
				} else if (DeferredList.get(getActivity()).isInList(activity, true)) {
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
			case R.id.day_change_reminder:
				NumberPickerBuilder builder = new NumberPickerBuilder();
				builder.setDecimalVisibility(View.GONE);
				builder.setFragmentManager(getActivity().getSupportFragmentManager());
				builder.setPlusMinusVisibility(View.GONE);
				builder.setStyleResId(R.style.BetterPickersDialogFragment_Light);
				builder.addNumberPickerDialogHandler(new NumberPickerDialogHandler() {

					@Override
					public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative,
							double fullNumber) {
						Calendar calendar = Calendar.getInstance();
						Calendar aCalendar = Calendar.getInstance();
						aCalendar.setTime(activity.getStartTime());
						calendar.set(Calendar.HOUR_OF_DAY, aCalendar.get(Calendar.HOUR_OF_DAY));
						calendar.set(Calendar.MINUTE, aCalendar.get(Calendar.MINUTE));
						calendar.set(Calendar.SECOND, aCalendar.get(Calendar.SECOND));
						calendar.set(Calendar.MILLISECOND, aCalendar.get(Calendar.MILLISECOND));
						CustomReminderList.get(getActivity()).addCustomReminder(activity, calendar.getTime(), number);
					}
				});
				builder.show();
				return true;
			case R.id.day_reset_reminder:
				Calendar calendar = Calendar.getInstance();
				Calendar aCalendar = Calendar.getInstance();
				aCalendar.setTime(activity.getStartTime());
				calendar.set(Calendar.HOUR_OF_DAY, aCalendar.get(Calendar.HOUR_OF_DAY));
				calendar.set(Calendar.MINUTE, aCalendar.get(Calendar.MINUTE));
				calendar.set(Calendar.SECOND, aCalendar.get(Calendar.SECOND));
				calendar.set(Calendar.MILLISECOND, aCalendar.get(Calendar.MILLISECOND));
				CustomReminderList.get(getActivity()).removeCustomReminder(activity, calendar.getTime());
				return true;
			}
			return super.onContextItemSelected(item);
		} else {
			return false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("tag", "Today event fragment on Activity result called.");
		activities = SSS.getTodayActivities(getActivity());
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
			TextView passedLayer = (TextView) convertView.findViewById(R.id.day_activity_passed_layer);
			TextView currentLayer = (TextView) convertView.findViewById(R.id.day_activity_current_layer);
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
			if (CancelledList.get(getActivity()).isInList(activity, true)) {
				cancelledLayer.setVisibility(View.VISIBLE);
				title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				countdown.setVisibility(View.GONE);
			} else if (DeferredList.get(getActivity()).isInList(activity, true)) {
				deferredLayer.setVisibility(View.VISIBLE);
			} else if (activity.hasPassed()) {
				title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				passedLayer.setVisibility(View.VISIBLE);
				countdown.setVisibility(View.GONE);
			} else if (activity.isCurrent()) {
				currentLayer.setVisibility(View.VISIBLE);
				countdown.setVisibility(View.GONE);
			} else {
				countdown.setVisibility(View.VISIBLE);
				String h = getResources().getString(R.string.h_format);
				String m = getResources().getString(R.string.min_format);
				startsIn.setText(activity.getRemainingTime(m, h));
				cancelledLayer.setVisibility(View.GONE);
				deferredLayer.setVisibility(View.GONE);
				passedLayer.setVisibility(View.GONE);
				title.setPaintFlags(0);
			}
			if (activity.getCourse() != null)
				colour.setBackgroundColor(activity.getCourse().getColour());
			return convertView;
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Activity activity = (Activity) getListAdapter().getItem(position);
		if ((activity.getType() >= Activity.lecture && activity.getType() < Activity.exam) && activity.isCurrent()
				&& !CancelledList.get(getActivity()).isInList(activity, true)
				&& !DeferredList.get(getActivity()).isInList(activity, true)) {
			Activity next = null;
			if (position < getListAdapter().getCount() - 1)
				next = (Activity) getListAdapter().getItem(position + 1);
			startInClass(activity, next);
		} else {
			ActivityInfoFragment dialog;
			if (activity.getCourse() == null) {
				dialog = ActivityInfoFragment.newInstance(activity.getID(), null, null, true);
			} else {
				dialog = ActivityInfoFragment.newInstance(activity.getID(), activity.getCourse().getID(), activity
						.getCourse().getTerm().getID(), true);
			}
			dialog.show(getActivity().getSupportFragmentManager(), "Activity info");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		timer.cancel();
	}

	@Override
	public void onResume() {
		super.onResume();
		activities = SSS.getTodayActivities(getActivity());
		setListAdapter(new DayActivityAdapter(activities));
		SSS.setNotifications(getActivity());
		inClassHandler = new Handler();
		inClassHandler.post(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < activities.size(); i++) {
					Activity a = activities.get(i);
					if (a.getType() >= Activity.lecture && a.getType() < Activity.exam)
						if (a.isCurrent() && !SSS.exitedFromInClass
								&& !CancelledList.get(getActivity()).isInList(a, true)
								&& !DeferredList.get(getActivity()).isInList(a, true)) {
							SSS.exitedFromInClass = true;
							if (i < activities.size() - 1)
								startInClass(a, activities.get(i + 1));
							else
								startInClass(a, null);
						}
				}
			}
		});
		timerHandler = new Handler();
		timerTask = new TimerTask() {

			@Override
			public void run() {
				timerHandler.post(new Runnable() {

					@Override
					public void run() {
						((DayActivityAdapter) getListAdapter()).notifyDataSetChanged();
					}
				});
			}
		};
		timer = new Timer();
		timer.schedule(timerTask, 0, 5000);
	}

	public void refresh() {
		activities = SSS.getTodayActivities(getActivity());
		setListAdapter(new DayActivityAdapter(activities));
	}

	private void startInClass(Activity activity, Activity next) {
		Intent i = new Intent(getActivity(), InClassActivity.class);
		i.putExtra(InClassFragment.extraCurrentActivity, activity.getID());
		i.putExtra(InClassFragment.extraCurrentCourse, activity.getCourse().getID());
		i.putExtra(InClassFragment.extraCurrentTerm, activity.getCourse().getTerm().getID());
		if (next != null) {
			i.putExtra(InClassFragment.extraNextActivity, next.getID());
			if (next.getCourse() != null)
				i.putExtra(InClassFragment.extraNextCourse, next.getCourse().getID());
		}
		startActivity(i);
	}
}
