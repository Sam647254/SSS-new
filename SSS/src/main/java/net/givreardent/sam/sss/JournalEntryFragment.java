package net.givreardent.sam.sss;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import net.givreardent.sam.sss.data.journals.Journal;
import net.givreardent.sam.sss.data.journals.JournalList;
import net.givreardent.sam.sss.dialogs.JournalConfirmationFragment;
import net.givreardent.sam.sss.dialogs.JournalWeatherFragment;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class JournalEntryFragment extends Fragment {
	public static final String extraDate = "Date";
	public static final int yes = 101;
	public static final int no = 102;
	public static final int saveRequest = 0;
	public static final int weatherRequest = 1;

	private TextView mDateTextView;
	private SeekBar moodBar;
	private TextView mTitleView;
	private TextView mEntryView;
	private ImageButton mWeatherButton;
	private TextView mMoodValue;
	private int mWeatherValue = Journal.sunny;
	private Date mDate = Calendar.getInstance().getTime();
	private boolean edit = false;
	private static final String confirmDialogTag = "Save?";
	private Journal journal;

	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (getArguments() != null && getArguments().containsKey(extraDate)) {
			edit = true;
			journal = JournalList.get(getActivity()).getEntry((UUID) getArguments().getSerializable(extraDate));
			mDate = journal.getDate();
		}
		if (Build.VERSION.SDK_INT >= 11)
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_journal_entry, container, false);
		mDateTextView = (TextView) v.findViewById(R.id.date_field);
		mDateTextView.setText(mDate.toString());
		mTitleView = (TextView) v.findViewById(R.id.title_field);
		mMoodValue = (TextView) v.findViewById(R.id.mood_value);
		moodBar = (SeekBar) v.findViewById(R.id.mood_bar);
		moodBar.setMax(10);
		moodBar.setProgress(5);
		mMoodValue.setText(Integer.toString(5));
		moodBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mMoodValue.setText(Integer.toString(progress));
			}
		});
		mEntryView = (TextView) v.findViewById(R.id.entry_field);
		mWeatherButton = (ImageButton) v.findViewById(R.id.weather_button);
		mWeatherButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				JournalWeatherFragment dialog = JournalWeatherFragment.newInstance(mWeatherValue);
				dialog.setTargetFragment(JournalEntryFragment.this, weatherRequest);
				dialog.show(fm, "Weather dialog");
			}
		});
		if (journal != null) {
			mTitleView.setText(journal.getName());
			mWeatherValue = journal.getWeather();
			moodBar.setProgress(journal.getMoodRating());
			mEntryView.setText(journal.getEntry());
		}
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.journal_entry, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			showDialog();
			return true;
		case R.id.menu_save:
			saveJournal();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == saveRequest) {
			if (resultCode == yes) {
				saveJournal();
			} else if (resultCode == no) {
				if (NavUtils.getParentActivityName(getActivity()) != null)
					NavUtils.navigateUpFromSameTask(getActivity());
			}
		}
		if (requestCode == weatherRequest) {
			mWeatherValue = resultCode;
			setWeather();
		}
	}

	public void showDialog() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		JournalConfirmationFragment dialog = new JournalConfirmationFragment();
		dialog.setTargetFragment(JournalEntryFragment.this, 0);
		dialog.show(fm, confirmDialogTag);
	}

	private void saveJournal() {
		if (edit) {
			journal.setName(mTitleView.getText().toString());
			journal.setWeather(mWeatherValue);
			journal.setMoodRating(moodBar.getProgress());
			journal.setEntry(mEntryView.getText().toString());
			JournalList.get(getActivity()).saveJournals();
		} else {
			Journal journal = new Journal();
			journal.setDate(mDate);
			journal.setName(mTitleView.getText().toString());
			journal.setWeather(mWeatherValue);
			journal.setMoodRating(moodBar.getProgress());
			journal.setEntry(mEntryView.getText().toString());
			JournalList.get(getActivity()).addEntry(journal);
			JournalList.get(getActivity()).saveJournals();
		}
		if (NavUtils.getParentActivityName(getActivity()) != null)
			NavUtils.navigateUpFromSameTask(getActivity());
	}
	
	private void setWeather() {
		switch (mWeatherValue) {
		case Journal.sunny:
			mWeatherButton.setImageResource(R.drawable.ic_weather_sunny);
			break;
		case Journal.cloudy:
			mWeatherButton.setImageResource(R.drawable.ic_weather_cloudy);
			break;
		case Journal.rain:
			mWeatherButton.setImageResource(R.drawable.ic_weather_rain);
			break;
		case Journal.snow:
			mWeatherButton.setImageResource(R.drawable.ic_weather_snow);
			break;
		case Journal.windy:
			mWeatherButton.setImageResource(R.drawable.ic_weather_windy);
			break;
		}
	}
	
	public static JournalEntryFragment newInstance(UUID ID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraDate, ID);
		JournalEntryFragment fragment = new JournalEntryFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
}
