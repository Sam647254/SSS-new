package net.givreardent.sam.sss;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class TermCreateActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		UUID termID = (UUID) getIntent().getSerializableExtra(
				TermListFragment.extraTerm);
		return TermCreateFragment.newInstance(termID);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		((TermCreateFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).cancelAdd();
	}

}
