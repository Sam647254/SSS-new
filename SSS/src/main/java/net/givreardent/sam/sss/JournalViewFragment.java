package net.givreardent.sam.sss;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import net.givreardent.sam.sss.data.JournalList;
import net.givreardent.sam.sss.data.journals.Journal;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class JournalViewFragment extends Fragment {
	public static final String extraJournalID = "net.givreardent.sam.journal.journalID";
	private Journal journal;
	private TextView mTitle;
	private TextView mDate;
	private ImageView mWeather;
	private TextView mMood;
	private TextView mEntry;
	
	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		UUID journalID = (UUID) getActivity().getIntent().getSerializableExtra(extraJournalID);
		journal = JournalList.get(getActivity()).getJournal(journalID);
		if (Build.VERSION.SDK_INT >= 11) {
			if (NavUtils.getParentActivityIntent(getActivity()) != null)
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.journal_view, menu);
		if (!avaiableForEdit())
			menu.getItem(0).setEnabled(false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.journal_edit:
			Intent i = new Intent(getActivity(), JournalEntryActivity.class);
			i.putExtra(JournalEntryFragment.extraDate, journal.getID());
			startActivity(i);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_journal_view, container, false);
		getActivity().setTitle(journal.getName());
		mTitle = (TextView) v.findViewById(R.id.view_journal_title);
		mTitle.setText(journal.getName());
		mDate = (TextView) v.findViewById(R.id.view_journal_date);
		mDate.setText(journal.getDate().toString());
		mWeather = (ImageView) v.findViewById(R.id.view_journal_weather);
		switch(journal.getWeather()) {
			case Journal.sunny:
				mWeather.setImageResource(R.drawable.ic_weather_sunny);
				break;
			case Journal.cloudy:
				mWeather.setImageResource(R.drawable.ic_weather_cloudy);
				break;
			case Journal.rain:
				mWeather.setImageResource(R.drawable.ic_weather_rain);
				break;
			case Journal.snow:
				mWeather.setImageResource(R.drawable.ic_weather_snow);
				break;
			case Journal.windy:
				mWeather.setImageResource(R.drawable.ic_weather_windy);
				break;
		}
		mMood = (TextView) v.findViewById(R.id.view_mood_value);
		mMood.setText(Integer.toString(journal.getMoodRating()));
		mEntry = (TextView) v.findViewById(R.id.view_journal_entry);
		mEntry.setText(journal.getEntry());
		return v;
	}
	
	private boolean avaiableForEdit() {
		Date date = journal.getDate();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return date.compareTo(calendar.getTime()) > 0;
	}
}
