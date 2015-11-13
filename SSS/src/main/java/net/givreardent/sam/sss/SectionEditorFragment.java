package net.givreardent.sam.sss;

import java.util.ArrayList;
import java.util.UUID;

import net.givreardent.sam.sss.data.courses.ActivityList;
import net.givreardent.sam.sss.data.courses.Course;
import net.givreardent.sam.sss.data.courses.CourseList;
import net.givreardent.sam.sss.data.courses.Exam;
import net.givreardent.sam.sss.data.courses.GradeSection;
import net.givreardent.sam.sss.data.courses.SectionList;
import net.givreardent.sam.sss.data.courses.TermList;
import net.givreardent.sam.sss.dialogs.CreateSectionFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SectionEditorFragment extends ListFragment {
	public static final String extraCourse = "Course";
	public static final String extraTerm = "Term";
	public static final String extraSection = "Section";
	private ArrayList<GradeSection> sections;
	private Course course;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		course = CourseList.get(getActivity(), (UUID) getArguments().getSerializable(extraTerm)).getCourse(
				(UUID) getArguments().getSerializable(extraCourse));
		sections = SectionList.get(getActivity(), course.getID(), course.getTerm().getID()).getSections();
		setHasOptionsMenu(true);
		SectionAdapter adapter = new SectionAdapter(sections);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, parent, false);
		TextView emptyMessage = (TextView) v.findViewById(android.R.id.empty);
		emptyMessage.setText(R.string.empty_sections);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.terms_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.create_term:
			FragmentManager fm = getActivity().getSupportFragmentManager();
			CreateSectionFragment dialog = CreateSectionFragment.newInstance(course.getID(), course.getTerm().getID());
			dialog.setTargetFragment(SectionEditorFragment.this, 0);
			dialog.show(fm, "Create section");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		GradeSection section = ((SectionAdapter) getListAdapter()).getItem(position);
		CreateSectionFragment dialog = CreateSectionFragment.newInstance(section.getCourse().getID(), section
				.getCourse().getTerm().getID(), section.getID());
		dialog.setTargetFragment(this, 0);
		dialog.show(getActivity().getSupportFragmentManager(), "Edit section");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean isExam = data.getBooleanExtra(CreateSectionFragment.extraType, false);
		boolean isNew = data.getBooleanExtra(CreateSectionFragment.extraIsNew, true);
		double weight = data.getDoubleExtra(CreateSectionFragment.extraWeight, 0);
		String name = data.getStringExtra(CreateSectionFragment.extraName);
		GradeSection section;
		if (isNew) {
			section = new GradeSection(name, weight, course);
			section.setIsExam(isExam);
			sections.add(section);
		} else {
			UUID sectionID = (UUID) data.getSerializableExtra(CreateSectionFragment.extraOldID);
			section = SectionList.get(getActivity(), course.getID(), course.getTerm().getID()).getSection(sectionID);
			section.setName(name);
			section.setWeight(weight);
		}
		if (isExam) {
			if (section.getExam() != null)
				section.getExam().removeSection();
			UUID examID = (UUID) data.getSerializableExtra(CreateSectionFragment.extraExam);
			Exam exam = (Exam) ActivityList.get(getActivity(), course.getID(), course.getTerm().getID()).getActivity(
					examID);
			Log.d("tag", "In on Activity Result, setting section of " + exam.getName() + " to: " + section.getName());
			section.setExam(exam);
		}
		((SectionAdapter) getListAdapter()).notifyDataSetChanged();
		TermList.get(getActivity()).saveTerms();
		Log.d("tag", "Terms saved.");
	}

	public static SectionEditorFragment newInstance(UUID courseID, UUID termID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraCourse, courseID);
		arguments.putSerializable(extraTerm, termID);
		SectionEditorFragment fragment = new SectionEditorFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
	
	private class ViewHolder {
		TextView name;
		TextView type;
		TextView weight;
		TextView total;
	}

	private class SectionAdapter extends ArrayAdapter<GradeSection> {
		public SectionAdapter(ArrayList<GradeSection> sections) {
			super(getActivity(), 0, sections);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_section, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.section_name_list);
				holder.type = (TextView) convertView.findViewById(R.id.section_type_list);
				holder.weight = (TextView) convertView.findViewById(R.id.section_weight_list);
				holder.total = (TextView) convertView.findViewById(R.id.section_weight_total);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			GradeSection section = getItem(position);
			holder.name.setText(section.getName());
			String[] types = getResources().getStringArray(R.array.section_types);
			if (section.isExam()) {
				holder.type.setText(types[1]);
			} else
				holder.type.setText(types[0]);
			holder.weight.setText(Double.toString(section.getWeight()));
			holder.total.setText("/" + course.getSectionsTotal());
			return convertView;
		}
	}
}
