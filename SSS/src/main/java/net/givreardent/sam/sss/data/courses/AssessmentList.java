package net.givreardent.sam.sss.data.courses;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class AssessmentList {
	private Context mContext;
	private static AssessmentList sList;
	private ArrayList<Assessment> assessments;
	private static UUID ID;

	private AssessmentList(Context c, UUID s, Course course) {
		mContext = c;
		ID = s;
		assessments = SectionList.get(mContext, course.getID(), course.getTerm().getID()).getSection(s)
				.getAssessments();
	}

	public static AssessmentList get(Context c, UUID s, Course course) {
		if (sList == null || !ID.equals(s)) {
			sList = new AssessmentList(c, s, course);
		}
		return sList;
	}

	public ArrayList<Assessment> getAssessments() {
		return assessments;
	}

	public void addAssessment(Assessment a) {
		assessments.add(a);
	}
	
	public void removeAssessment(Assessment a) {
		assessments.remove(a);
	}

	public Assessment getAssessment(UUID ID) {
		for (Assessment assessment : assessments) {
			if (assessment.getID().equals(ID))
				return assessment;
		}
		return null;
	}
}
