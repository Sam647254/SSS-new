package net.givreardent.sam.sss.dialogs;

import net.givreardent.sam.sss.CourseCreateFragment;
import net.givreardent.sam.sss.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;

public class CreateActivitySelectFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_activity_select, null);
		builder.setTitle(R.string.create_activity_title);
		builder.setView(v);
		Button lecture = (Button) v.findViewById(R.id.create_lecture_button);
		lecture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendResult(CourseCreateFragment.resultLecture);
			}
		});
		Button lab = (Button) v.findViewById(R.id.create_lab_button);
		lab.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendResult(CourseCreateFragment.resultLab);
			}
		});
		Button tutorial = (Button) v.findViewById(R.id.create_tutorial_button);
		tutorial.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendResult(CourseCreateFragment.resultTutorial);
			}
		});
		Button exam = (Button) v
				.findViewById(R.id.create_exam_button);
		exam.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendResult(CourseCreateFragment.resultExam);
			}
		});
		Button other = (Button) v
				.findViewById(R.id.create_other_activity_button);
		other.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendResult(CourseCreateFragment.resultOther);
			}
		});
		return builder.create();
	}

	private void sendResult(int selection) {
		if (getTargetFragment() == null)
			return;
		getTargetFragment().onActivityResult(getTargetRequestCode(), selection,
				null);
		dismiss();
	}
}
