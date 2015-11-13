package net.givreardent.sam.sss;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class CourseCreateActivity extends SingleFragmentActivity {
	Fragment fragment;

	@Override
	protected Fragment createFragment() {
		UUID courseID = (UUID) getIntent().getSerializableExtra(CourseCreateFragment.extraCourse);
		UUID termID = (UUID) getIntent().getSerializableExtra(CourseCreateFragment.extraTerm);
		fragment = CourseCreateFragment.newInstance(courseID, termID);
		return fragment;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (fragment == null)
			fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
		((CourseCreateFragment) fragment).cancelCreate();
	}
}
