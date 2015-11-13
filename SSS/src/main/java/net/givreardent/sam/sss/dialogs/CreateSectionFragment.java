package net.givreardent.sam.sss.dialogs;

import java.util.ArrayList;
import java.util.UUID;

import net.givreardent.sam.sss.R;
import net.givreardent.sam.sss.SectionEditorFragment;
import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.CourseList;
import net.givreardent.sam.sss.data.courses.Exam;
import net.givreardent.sam.sss.data.courses.GradeSection;
import net.givreardent.sam.sss.data.courses.SectionList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateSectionFragment extends DialogFragment {
	public static final String extraType = "Type", extraExam = "Exam", extraWeight = "Weight", extraName = "Name",
			extraIsNew ="Is new", extraOldID = "Old ID";

	private Spinner type, exam;
	private EditText name, weight;
	private LinearLayout examLayout;
	private Course course;
	private GradeSection section;
	private boolean isNew = true;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		course = CourseList.get(getActivity(), (UUID) getArguments().getSerializable(SectionEditorFragment.extraTerm))
				.getCourse((UUID) getArguments().getSerializable(SectionEditorFragment.extraCourse));
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_create_section, null);
		builder.setView(v);
		type = (Spinner) v.findViewById(R.id.section_type_spinner);
		exam = (Spinner) v.findViewById(R.id.section_exam_spinner);
		name = (EditText) v.findViewById(R.id.section_name);
		weight = (EditText) v.findViewById(R.id.section_weight);
		examLayout = (LinearLayout) v.findViewById(R.id.section_exam_LinearLayout);
		String[] types = getResources().getStringArray(R.array.section_types);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, types);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		type.setAdapter(adapter);
		final ArrayList<String> exams = new ArrayList<String>();
		for (Exam e : course.getExams()) {
			exams.add(e.getName());
		}
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
				exams);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		exam.setAdapter(adapter2);
		name = (EditText) v.findViewById(R.id.section_name);
		type.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					examLayout.setVisibility(View.GONE);
					name.setVisibility(View.VISIBLE);
					break;
				case 1:
					if (exams.size() == 0) {
						Toast.makeText(getActivity(), R.string.section_no_exam_toast, Toast.LENGTH_LONG).show();
						type.setSelection(0);
						break;
					}
					examLayout.setVisibility(View.VISIBLE);
					name.setVisibility(View.GONE);
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				type.setSelection(0);
			}
		});
		if (getArguments().getSerializable(SectionEditorFragment.extraSection) != null) {
			isNew = false;
			type.setEnabled(false);
			UUID sectionID = (UUID) getArguments().getSerializable(SectionEditorFragment.extraSection);
			section = SectionList.get(getActivity(), course.getID(), course.getTerm().getID()).getSection(
					sectionID);
			weight.setText(Double.toString(section.getWeight()));
			if (section.isExam()) {
				type.setSelection(1, true);
				for (int i = 0; i < exams.size(); i++) {
					if (exams.get(i).equals(section.getExam().getName())) {
						exam.setSelection(i, true);
						break;
					}
				}
			} else {
				name.setText(section.getName());
			}
		}
		builder.setNeutralButton(android.R.string.cancel, null);
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendResult();
			}
		});
		builder.setTitle(R.string.section_create_title);
		return builder.create();
	}

	public static CreateSectionFragment newInstance(UUID courseID, UUID termID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(SectionEditorFragment.extraCourse, courseID);
		arguments.putSerializable(SectionEditorFragment.extraTerm, termID);
		CreateSectionFragment fragment = new CreateSectionFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	public static CreateSectionFragment newInstance(UUID courseID, UUID termID, UUID sectionID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(SectionEditorFragment.extraCourse, courseID);
		arguments.putSerializable(SectionEditorFragment.extraTerm, termID);
		arguments.putSerializable(SectionEditorFragment.extraSection, sectionID);
		CreateSectionFragment fragment = new CreateSectionFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	private void sendResult() {
		if (getTargetFragment() == null)
			return;
		Intent data = new Intent();
		data.putExtra(extraIsNew, isNew);
		if (!isNew) {
			data.putExtra(extraOldID, section.getID());
		}
		data.putExtra(extraType, type.getSelectedItemPosition() == 1);
		data.putExtra(extraWeight, Double.parseDouble(weight.getText().toString()));
		if (type.getSelectedItemPosition() == 1) {
			data.putExtra(extraExam, course.getExams().get(exam.getSelectedItemPosition()).getID());
			data.putExtra(extraName, course.getExams().get(exam.getSelectedItemPosition()).getName());
		} else
			data.putExtra(extraName, name.getText().toString());
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
	}
}
