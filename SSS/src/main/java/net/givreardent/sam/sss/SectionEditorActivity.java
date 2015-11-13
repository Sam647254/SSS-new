package net.givreardent.sam.sss;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class SectionEditorActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		UUID courseID = (UUID) getIntent().getSerializableExtra(
				SectionEditorFragment.extraCourse);
		UUID termID = (UUID) getIntent().getSerializableExtra(
				SectionEditorFragment.extraTerm);
		return SectionEditorFragment.newInstance(courseID, termID);
	}

}
