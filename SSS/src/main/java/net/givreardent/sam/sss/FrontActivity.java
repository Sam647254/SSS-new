package net.givreardent.sam.sss;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import net.givreardent.sam.sss.data.Settings;
import net.givreardent.sam.sss.data.courses.Activity;
import net.givreardent.sam.sss.data.courses.TermList;
import net.givreardent.sam.sss.dialogs.CreateActivityFragment;
import net.givreardent.sam.sss.dialogs.TaskCreateFragment;

public class FrontActivity extends FragmentActivity {
	private Fragment frontFragment;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private String title, appName;

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * if (Build.VERSION.SDK_INT > 11) { Random RNG = new Random(); int
		 * colorOfTheDay = 0; int result = RNG.nextInt(5); switch (result) {
		 * case 0: colorOfTheDay = Course.blue[1]; break; case 1: colorOfTheDay
		 * = Course.green[0]; break; case 2: colorOfTheDay = Course.orange[1];
		 * break; case 3: colorOfTheDay = Course.red[1]; break; case 4:
		 * colorOfTheDay = Course.purple[1]; break; }
		 * getActionBar().setBackgroundDrawable(new
		 * ColorDrawable(colorOfTheDay)); }
		 */
		appName = getResources().getString(R.string.app_name);
		setContentView(R.layout.activity_front);
		FragmentManager fm = getSupportFragmentManager();
		frontFragment = fm.findFragmentById(R.id.front_fragment_container);
		if (frontFragment == null) {
			frontFragment = new FrontFragment();
			fm.beginTransaction().add(R.id.front_fragment_container, frontFragment).commit();
		}
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		mDrawerList = (ListView) findViewById(R.id.drawer_ListView);
		String[] entries = getResources().getStringArray(R.array.drawer_entries);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, entries));
		mDrawerList.setItemChecked(0, true);
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FragmentManager fm = getSupportFragmentManager();
				switch (position) {
				case 0:
					Fragment frontFragment = new FrontFragment();
					fm.beginTransaction().replace(R.id.front_fragment_container, frontFragment).commit();
					title = getResources().getString(R.string.at_a_glance);
					mDrawerLayout.closeDrawer(mDrawerList);
					break;
				case 1:
					Fragment taskListFragment = null;
					title = getResources().getString(R.string.task_list_title);
					if (Build.VERSION.SDK_INT < 11)
						taskListFragment = new TaskListFragment11();
					else
						taskListFragment = new TaskListFragment();
					fm.beginTransaction().replace(R.id.front_fragment_container, taskListFragment).commit();
					mDrawerLayout.closeDrawer(mDrawerList);
					break;
				case 2:
					Fragment eventListFragment = new EventListFragment();
					title = getResources().getString(R.string.event_list_title);
					fm.beginTransaction().replace(R.id.front_fragment_container, eventListFragment).commit();
					mDrawerLayout.closeDrawer(mDrawerList);
					break;
				}
			}
		});
		if (Build.VERSION.SDK_INT >= 14) {
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
					R.string.drawer_close) {
				@Override
				public void onDrawerOpened(View arg0) {
					title = getTitle().toString();
					setTitle(appName);
					invalidateOptionsMenu();
				}

				@Override
				public void onDrawerClosed(View arg0) {
					setTitle(title);
					invalidateOptionsMenu();
				}
			};
			mDrawerToggle.setDrawerIndicatorEnabled(true);
			mDrawerLayout.setDrawerListener(mDrawerToggle);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setHomeButtonEnabled(true);
		} else {
			mDrawerLayout.setDrawerListener(new DrawerListener() {

				@Override
				public void onDrawerStateChanged(int arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDrawerSlide(View arg0, float arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDrawerOpened(View arg0) {
					title = getTitle().toString();
					setTitle(appName);
					if (Build.VERSION.SDK_INT > 11)
						invalidateOptionsMenu();
				}

				@Override
				public void onDrawerClosed(View arg0) {
					if (Build.VERSION.SDK_INT > 11)
						invalidateOptionsMenu();
				}
			});
		}
		changeLanguage();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.event_calendar).setVisible(!drawerOpen);
		menu.findItem(R.id.todo_list).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.today_event, menu);
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (Build.VERSION.SDK_INT >= 14) {
			mDrawerToggle.onConfigurationChanged(newConfig);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (Build.VERSION.SDK_INT >= 14) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		if (!(fm.findFragmentById(R.id.front_fragment_container) instanceof FrontFragment)) {
			fm.beginTransaction().replace(R.id.front_fragment_container, new FrontFragment()).commit();
		} else {
			super.onBackPressed();
		}
	}
	
	@TargetApi(11)
	@Override
	public void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= 11)
			invalidateOptionsMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (Build.VERSION.SDK_INT >= 14) {
			if (mDrawerToggle.onOptionsItemSelected(item))
				return true;
		}
		int id = item.getItemId();
		FragmentManager fm = getSupportFragmentManager();
		switch (id) {
		case R.id.todo_list:
			TaskCreateFragment dialog = new TaskCreateFragment();
			dialog.show(fm, "Create task");
			return true;
		case R.id.event_calendar:
			CreateActivityFragment dialog2 = CreateActivityFragment.newInstance(Activity.stray, false);
			dialog2.show(fm, "Stray activity");
			return true;
		case R.id.journal_calendar:
			Intent i = new Intent(getApplicationContext(), JournalActivity.class);
			startActivity(i);
			return true;
		case R.id.course_edit:
			i = new Intent(getApplicationContext(), TermListActivity.class);
			startActivity(i);
			return true;
		case R.id.track_grades:
			i = new Intent(getApplicationContext(), GradesTrackerActivity.class);
			startActivity(i);
			return true;
		case R.id.activity_on_date:
			Calendar calendar = Calendar.getInstance();
			CalendarDatePickerDialogFragment dialog3 = CalendarDatePickerDialogFragment
					.newInstance(new CalendarDatePickerDialogFragment.OnDateSetListener() {

						@Override
						public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear,
								int dayOfMonth) {
							GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
							Intent i = new Intent(getApplicationContext(), ActivitiesOnDateActivity.class);
							i.putExtra(ActivitiesOnDateActivity.extraDate, calendar.getTimeInMillis());
							startActivity(i);
						}
					}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
			dialog3.show(getSupportFragmentManager(), "Date");
			return true;
		case R.id.settings:
			i = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void changeLanguage() {
		int language = Settings.getLanguage(this);
		Resources resources = getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		Configuration configuration = resources.getConfiguration();
		Locale newLocale = null;
		switch (language) {
			case 0:
				newLocale = new Locale("en");
				break;
			case 1:
				newLocale = new Locale("fr");
				break;
			case 2:
				newLocale = new Locale("ja");
				break;
			default:
				newLocale = Locale.getDefault();
		}
		configuration.locale = newLocale;
		resources.updateConfiguration(configuration, metrics);
	}

	public void onDialogResult() {
		FragmentManager fm = getSupportFragmentManager();
		Fragment visibleFragment = fm.findFragmentById(R.id.front_fragment_container);
		if (visibleFragment instanceof FrontFragment) {
			((FrontFragment) visibleFragment).refresh();
		} else if (visibleFragment instanceof TaskListFragment11) {
			((TaskListFragment11) visibleFragment).resetAdapter();
		} else if (visibleFragment instanceof TaskListFragment) {
			((TaskListFragment) visibleFragment).resetAdapter();
		} else if (visibleFragment instanceof EventListFragment) {
			((EventListFragment) visibleFragment).resetAdapter();
		}
	}

	public void onDialogResult2() {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				TermList.get(FrontActivity.this).saveTerms();
				onDialogResult();
				Log.d("tag", "Terms saved from dialog.");
			}
		});
	}
}
