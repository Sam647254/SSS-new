package net.givreardent.sam.sss;

import java.util.Date;

import android.support.v4.app.Fragment;

public class ActivitiesOnDateActivity extends SingleFragmentActivity {
	public static final String extraDate = "Date";

	@Override
	protected Fragment createFragment() {
		return ActivitiesOnDateFragment.newInstance(getIntent().getLongExtra(extraDate, 0));
	}

}
