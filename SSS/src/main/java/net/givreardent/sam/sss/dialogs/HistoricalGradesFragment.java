package net.givreardent.sam.sss.dialogs;

import java.text.DecimalFormat;
import java.util.UUID;

import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.Term;
import net.givreardent.sam.sss.data.courses.TermList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class HistoricalGradesFragment extends DialogFragment {
	private static final String extraTermID = "Term";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Term term = TermList.get(getActivity()).getTerm((UUID) getArguments().getSerializable(extraTermID));
		builder.setTitle(term.getIdentifier());
		builder.setPositiveButton(android.R.string.ok, null);
		LinearLayout mainLayout = new LinearLayout(getActivity());
		LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		mainLayout.setLayoutParams(parameters);
		for (Course c : term.getCourses())
			mainLayout.addView(getLL(c));
		builder.setView(mainLayout);
		return builder.create();
	}

	public static HistoricalGradesFragment newInstance(Term term) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraTermID, term.getID());
		HistoricalGradesFragment fragment = new HistoricalGradesFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	private LinearLayout getLL(Course course) {
		LinearLayout LL = new LinearLayout(getActivity());
		int padding = (int) (8 * getResources().getDisplayMetrics().density + 0.5F);
		LL.setPadding(padding, padding, padding, padding);
		LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		LL.setOrientation(LinearLayout.HORIZONTAL);
		LL.setLayoutParams(parameters);
		TextView courseName = new TextView(getActivity());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		courseName.setLayoutParams(params);
		courseName.setGravity(Gravity.LEFT);
		courseName.setText(course.getCode());
		LL.addView(courseName);
		TextView grade = new TextView(getActivity());
		grade.setTextColor(0xFFB40404);
		if (course.getPercentage() == -1)
			grade.setText("--%");
		else {
			DecimalFormat formatter = new DecimalFormat("0.#");
			grade.setText(formatter.format(course.getPercentage()) + "%");
		}
		LL.addView(grade);
		grade.setGravity(Gravity.RIGHT);
		return LL;
	}
}
