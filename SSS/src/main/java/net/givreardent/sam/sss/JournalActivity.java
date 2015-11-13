package net.givreardent.sam.sss;

import java.util.Date;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class JournalActivity extends FragmentActivity implements JournalCalendarFragment.Callbacks {
	private Fragment mJournalCalendarFragment;
	private Fragment mJournalListFragment;
	private Fragment mTodayJournalFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();
		if (Build.VERSION.SDK_INT < 11) {
			setContentView(R.layout.activity_fragment);
			mJournalListFragment = fm.findFragmentById(R.id.fragmentContainer);
			if (mJournalListFragment == null) {
				mJournalListFragment = new JournalListFragment();
				fm.beginTransaction()
						.add(R.id.fragmentContainer, mJournalListFragment)
						.commit();
			}
		} else {
			setContentView(R.layout.activity_journal);
			mJournalCalendarFragment = fm.findFragmentById(R.id.fragment_calendar);
			mTodayJournalFragment = fm.findFragmentById(R.id.journal_day);
			if (mJournalCalendarFragment == null) {
				mJournalCalendarFragment = new JournalCalendarFragment();
				fm.beginTransaction()
						.add(R.id.fragment_calendar, mJournalCalendarFragment)
						.commit();
			}
		}
	}

	@Override
	public void onDateSelected(Date date) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Fragment prevList = fm.findFragmentById(R.id.journal_day);
		Fragment newList = TodayJournalFragment.newInstance(date);
		
		if (prevList != null) {
			ft.remove(prevList);
		}
		ft.add(R.id.journal_day, newList);
		ft.commit();
	}
}
