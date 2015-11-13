package net.givreardent.sam.sss;

import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class InClassActivity extends SingleFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.InClassTheme);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Fragment createFragment() {
		UUID currentID = (UUID) getIntent().getSerializableExtra(InClassFragment.extraCurrentActivity);
		UUID currentCourse = (UUID) getIntent().getSerializableExtra(InClassFragment.extraCurrentCourse);
		UUID currentTerm = (UUID) getIntent().getSerializableExtra(InClassFragment.extraCurrentTerm);
		UUID nextID = (UUID) getIntent().getSerializableExtra(InClassFragment.extraNextActivity);
		UUID nextCourse = (UUID) getIntent().getSerializableExtra(InClassFragment.extraNextCourse);
		return InClassFragment.newInstance(currentID, currentCourse, currentTerm, nextID, nextCourse);
	}

}
