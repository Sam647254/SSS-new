package net.givreardent.sam.sss;

import android.support.v4.app.Fragment;

public class GradesTrackerActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new GradesTrackerFragment();
	}

}
