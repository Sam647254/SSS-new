package net.givreardent.sam.sss;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FrontFragment extends Fragment {
	private EventPager mEventPager;
	private TaskPager mTaskPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FragmentManager fm = getChildFragmentManager();
		getActivity().setTitle(R.string.at_a_glance);
		mEventPager = (EventPager) fm.findFragmentById(R.id.event_pager);
		mTaskPager = (TaskPager) fm.findFragmentById(R.id.upcoming_tasks_pager);
		if (mTaskPager == null) {
			mEventPager = new EventPager();
			fm.beginTransaction().add(R.id.event_pager, mEventPager).commit();
			mTaskPager = new TaskPager();
			fm.beginTransaction().add(R.id.upcoming_tasks_pager, mTaskPager)
					.commit();
		}
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_front_alias, container, false);
		FragmentManager fm = getChildFragmentManager();
		FrameLayout grades = (FrameLayout) v.findViewById(R.id.grades);
		if (grades != null) {
			GradesTrackerFragment fragment = new GradesTrackerFragment();
			fm.beginTransaction().add(R.id.grades, fragment).commit();
		}
		return v;
	}
	
	public void refresh() {
		mEventPager.refresh();
		mTaskPager.refresh();
	}
}
