package net.givreardent.sam.sss;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class JournalViewActivity extends FragmentActivity {
	private Fragment mJournalViewFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		FragmentManager fm = getSupportFragmentManager();
		mJournalViewFragment = new JournalViewFragment();
		fm.beginTransaction().add(R.id.fragmentContainer, mJournalViewFragment).commit();
	}
}
