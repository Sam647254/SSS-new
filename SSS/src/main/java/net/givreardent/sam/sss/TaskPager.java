package net.givreardent.sam.sss;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TaskPager extends Fragment {
	private ViewPager mViewPager;
	private final String[] tabs = { "Tasks", "Events" };
	private UpcomingEventFragment upcomingEventFragment;
	private UpcomingTaskFragment upcomingTaskFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_task_pager, container, false);
		FragmentManager fm = getChildFragmentManager();
		upcomingEventFragment = new UpcomingEventFragment();
		upcomingTaskFragment = new UpcomingTaskFragment();
		mViewPager = (ViewPager) view.findViewById(R.id.task_pager);
		mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
			
			@Override
			public int getCount() {
				return tabs.length;
			}
			
			@Override
			public Fragment getItem(int arg0) {
				if(arg0 == 0)
					return upcomingTaskFragment;
				else
					return upcomingEventFragment;
			}
			
			@Override
			public CharSequence getPageTitle(int position) {
				return tabs[position];
			}
		});
		mViewPager.setCurrentItem(0);
		return view;
	}
	
	public void refresh() {
		upcomingTaskFragment.resetAdapter();
		upcomingEventFragment.resetAdapter();
	}
}
