package net.givreardent.sam.sss;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EventPager extends Fragment {
	private ViewPager mViewPager;
	private final String[] tabs = { "Today", "Tomorrow" };
	private TodayEventFragment todayEventFragment;
	private TomorrowEventFragment tomorrowEventFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_event_pager, container, false);
		todayEventFragment = new TodayEventFragment();
		tomorrowEventFragment = new TomorrowEventFragment();
		FragmentManager fm = getChildFragmentManager();
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
			
			@Override
			public int getCount() {
				return tabs.length;
			}
			
			@Override
			public Fragment getItem(int arg0) {
				if(arg0 == 0)
					return todayEventFragment;
				else
					return tomorrowEventFragment;
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
		todayEventFragment.refresh();
		tomorrowEventFragment.refresh();
	}
}
