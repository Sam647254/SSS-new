package net.givreardent.sam.sss.dialogs;

import java.util.UUID;

import net.givreardent.sam.sss.R;
import net.givreardent.sam.sss.data.courses.ActivityList;
import net.givreardent.sam.sss.data.courses.Assessment;
import net.givreardent.sam.sss.data.courses.AssessmentList;
import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.CourseList;
import net.givreardent.sam.sss.data.courses.Exam;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RecordScoreFragment extends DialogFragment {
	private static final String extraAssessmentID = "Assessment", extraSection = "Section", extraTerm = "Term",
			extraCourse = "Course", extraIsExam = "Is exam?", extraExamID = "Exam";
	private Assessment assessment;
	private Exam exam;
	private EditText score, outOf, name;
	private boolean isExam;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		isExam = getArguments().getBoolean(extraIsExam);
		UUID sectionID = (UUID) getArguments().getSerializable(extraSection);
		UUID termID = (UUID) getArguments().getSerializable(extraTerm);
		UUID courseID = (UUID) getArguments().getSerializable(extraCourse);
		Course course = CourseList.get(getActivity(), termID).getCourse(courseID);
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_record_grade, null);
		score = (EditText) v.findViewById(R.id.record_grade_score);
		outOf = (EditText) v.findViewById(R.id.record_grade_outof);
		name = (EditText) v.findViewById(R.id.task_name);
		if (!isExam) {
			UUID assessmentID = (UUID) getArguments().getSerializable(extraAssessmentID);
			assessment = AssessmentList.get(getActivity(), sectionID, course).getAssessment(assessmentID);
			builder.setTitle(assessment.getName());
			if (assessment.isRecorded()) {
				score.setText(Double.toString(assessment.getScore()));
				outOf.setText(Double.toString(assessment.getOutOf()));
			}
			name.setText(assessment.getName());
		} else {
			UUID examID = (UUID) getArguments().getSerializable(extraExamID);
			exam = (Exam) ActivityList.get(getActivity(), courseID, termID).getActivity(examID);
			builder.setTitle(exam.getName());
			if (exam.isRecorded()) {
				score.setText(Double.toString(exam.getScore()));
				outOf.setText(Double.toString(exam.getOutOf()));
			}
			name.setText(exam.getName());
		}
		builder.setView(v);
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (isExam) {
					enterExamScore();
				} else
					enterScore();
				if (getTargetFragment() == null)
					return;
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
			}
		});
		if (assessment != null && assessment.isRecorded()) {
			builder.setNeutralButton(R.string.remove_grade, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (isExam)
						exam.removeRecord();
					else
						assessment.removeRecord();
					if (getTargetFragment() == null)
						return;
					getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
				}
			});
		}
		return builder.create();
	}

	private void enterScore() {
		Log.i("tag", "score: " + score.getText().toString() + "out of: " + outOf.getText().toString());
		assessment.setName(name.getText().toString());
		assessment.setOutOf(Double.parseDouble(outOf.getText().toString()));
		assessment.setScore(Double.parseDouble(score.getText().toString()));
	}
	
	private void enterExamScore() {
		exam.setTitle(name.getText().toString());
		exam.setOutOf(Double.parseDouble(outOf.getText().toString()));
		exam.setScore(Double.parseDouble(score.getText().toString()));
	}

	public static RecordScoreFragment newInstance(Assessment assessment) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraAssessmentID, assessment.getID());
		arguments.putSerializable(extraSection, assessment.getSection().getID());
		arguments.putSerializable(extraTerm, assessment.getCourse().getTerm().getID());
		arguments.putSerializable(extraCourse, assessment.getCourse().getID());
		RecordScoreFragment fragment = new RecordScoreFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
	
	public static RecordScoreFragment newInstnace(Exam exam) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraExamID, exam.getID());
		arguments.putSerializable(extraSection, exam.getSection().getID());
		arguments.putSerializable(extraTerm, exam.getCourse().getTerm().getID());
		arguments.putSerializable(extraCourse, exam.getCourse().getID());
		arguments.putBoolean(extraIsExam, true);
		RecordScoreFragment fragment = new RecordScoreFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
}
