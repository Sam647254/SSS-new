package net.givreardent.sam.sss;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.givreardent.sam.sss.data.JournalList;
import net.givreardent.sam.sss.data.journals.Journal;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TodayJournalFragment extends ListFragment {
	private static final String tag = "TJF";
	private ArrayList<Journal> mJournals;
	
	public static TodayJournalFragment newInstance(Date date) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(tag, date);
		TodayJournalFragment fragment = new TodayJournalFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
	
	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (Build.VERSION.SDK_INT >= 11) {
			if (NavUtils.getParentActivityIntent(getActivity()) != null)
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		Date date = (Date) getArguments().getSerializable(tag);
		mJournals = new ArrayList<Journal>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		for(Journal j: JournalList.get(getActivity(), year).getJournals()) {
			Calendar journalDate = Calendar.getInstance();
			journalDate.setTime(j.getDate());
			if(calendar.get(2) == journalDate.get(2) && calendar.get(5) == journalDate.get(5))
				mJournals.add(j);
		}
		JournalListAdapter adapter = new JournalListAdapter(mJournals);
		setListAdapter(adapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, container, false);
		TextView emptyMessage = (TextView) v.findViewById(R.string.empty_journals);
		return v;
	}

	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.journal_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (NavUtils.getParentActivityIntent(getActivity()) != null)
					NavUtils.navigateUpFromSameTask(getActivity());
				return true;
			case R.id.create_journal:
				Intent i = new Intent(getActivity(), JournalEntryActivity.class);
				startActivity(i);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Journal j = ((JournalListAdapter)getListAdapter()).getItem(position);
		Log.d("Journal List Fragment", j.getName() + " was clicked.");
		Intent i = new Intent(getActivity(), JournalViewActivity.class);
		i.putExtra(JournalViewFragment.extraJournalID, j.getID());
		startActivity(i);
	}
	
	private class JournalListAdapter extends ArrayAdapter<Journal> {
		public JournalListAdapter(ArrayList<Journal> journals) {
			super(getActivity(), 0, journals);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_journal_entry, null);
			}
			
			Journal j = getItem(position);
			TextView dateTextView = (TextView) convertView.findViewById(R.id.list_journal_date);
			dateTextView.setText(j.getDate().toString());
			TextView titleTextView = (TextView) convertView.findViewById(R.id.list_journal_title);
			titleTextView.setText(j.getName());
			
			return convertView;
		}
	}
}
