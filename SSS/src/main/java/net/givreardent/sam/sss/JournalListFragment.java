package net.givreardent.sam.sss;

import java.util.ArrayList;

import net.givreardent.sam.sss.data.journals.Journal;
import net.givreardent.sam.sss.data.journals.JournalList;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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

public class JournalListFragment extends ListFragment {
	private ArrayList<Journal> journals;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		journals = JournalList.get(getActivity()).getJournals();
		
		JournalListAdapter adapter = new JournalListAdapter(journals);
		setListAdapter(adapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, container, false);
		
		return v;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.journal_menu, menu);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Journal j = ((JournalListAdapter)getListAdapter()).getItem(position);
		Log.d("Journal List Fragment", j.getName() + " was clicked.");
		Intent i = new Intent(getActivity(), JournalViewActivity.class);
		i.putExtra(JournalViewFragment.extraJournalID, j.getID());
		startActivity(i);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.create_journal:
				Intent i = new Intent(getActivity(), JournalEntryActivity.class);
				startActivity(i);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
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
