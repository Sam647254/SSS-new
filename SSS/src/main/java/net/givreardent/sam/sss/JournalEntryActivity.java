package net.givreardent.sam.sss;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class JournalEntryActivity extends SingleFragmentActivity {
	private JournalEntryFragment mJournalEntryFragment;

	@Override
	protected Fragment createFragment() {
		if (getIntent().hasExtra(JournalEntryFragment.extraDate))
			mJournalEntryFragment = JournalEntryFragment.newInstance((UUID) getIntent().getSerializableExtra(
					JournalEntryFragment.extraDate));
		else
			mJournalEntryFragment = new JournalEntryFragment();
		return mJournalEntryFragment;
	}

	@Override
	public void onBackPressed() {
		mJournalEntryFragment.showDialog();
	}
}
