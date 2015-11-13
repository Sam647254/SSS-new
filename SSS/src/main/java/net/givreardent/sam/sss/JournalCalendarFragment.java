package net.givreardent.sam.sss;

import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;

public class JournalCalendarFragment extends Fragment {
	private CalendarView mCalendar;
	private Callbacks mCallbacks;
	
	public interface Callbacks {
		void onDateSelected(Date date);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.journal_title);
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_journal_calendar, container, false);
		mCalendar = (CalendarView) v.findViewById(R.id.calendar);
		mCallbacks.onDateSelected(new Date(mCalendar.getDate()));
		mCalendar.setOnDateChangeListener(new OnDateChangeListener() {
			
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month,
					int dayOfMonth) {
				Date newDate = new GregorianCalendar(year,month,dayOfMonth).getTime();
				mCallbacks.onDateSelected(newDate);
			}
		});
		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (Callbacks) activity;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

}
