package net.givreardent.sam.sss;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class GradesListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		UUID termID = (UUID) getIntent().getSerializableExtra(GradesListFragment.extraTerm);
		UUID courseID = (UUID) getIntent().getSerializableExtra(GradesListFragment.extraCourse);
		return GradesListFragment.newInstance(courseID, termID);
	}

}
