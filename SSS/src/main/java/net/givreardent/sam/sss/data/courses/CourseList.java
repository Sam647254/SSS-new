package net.givreardent.sam.sss.data.courses;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class CourseList {
	private static CourseList sCourseList;
	private Context mContext;
	private static Term sTerm;
	private ArrayList<Course> courses;
	
	private CourseList(Context c, UUID termID) {
		mContext = c;
		sTerm = TermList.get(c).getTerm(termID);
		courses = sTerm.getCourses();
	}
	
	public static CourseList get(Context c, UUID termID) {
		if (sCourseList == null || !sTerm.getID().equals(termID))
			sCourseList = new CourseList(c, termID);
		return sCourseList;
	}
	
	public void addCourse(Course c) {
		// sTerm.addCourse(c);
		courses.add(c);
	}
	
	public void removeCourse(UUID ID) {
		// sTerm.removeCourse(ID);
		for (Course c: courses){
			if (c.getID().equals(ID)) {
				courses.remove(c);
				return;
			}
		}
	}
	
	public Course getCourse(UUID ID) {
		return sTerm.getCourse(ID);
	}
	
	public ArrayList<Course> getCourses() {
		return sTerm.getCourses();
	}
	
	public void removeCourse(Course c) {
		courses.remove(c);
	}
	
}
