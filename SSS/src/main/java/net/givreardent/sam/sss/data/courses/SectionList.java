package net.givreardent.sam.sss.data.courses;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class SectionList {
	private Context mContext;
	private static SectionList sList;
	private ArrayList<GradeSection> sections;
	private static Course course;
	
	private SectionList(Context c, UUID courseID, UUID termID) {
		mContext = c;
		course = CourseList.get(c, termID).getCourse(courseID);
		sections = course.getSections();
	}
	
	public static SectionList get(Context c, UUID courseID, UUID termID) {
		if (sList == null || !course.getID().equals(courseID))
			sList = new SectionList(c, courseID, termID);
		return sList;
	}
	
	public GradeSection getSection(UUID sectionID) {
		for (GradeSection s: sections) {
			if (s.getID().equals(sectionID))
				return s;
		}
		return null;
	}
	
	public ArrayList<GradeSection> getSections() {
		return sections;
	}
	
	public void addSection(GradeSection s) {
		sections.add(s);
	}
	
	public void removeSection(UUID sectionID) {
		for (GradeSection s: sections) {
			if (s.getID().equals(sectionID)) {
				sections.remove(s);
				return;
			}
		}
	}
}
