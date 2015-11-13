package net.givreardent.sam.sss.dialogs;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.givreardent.sam.sss.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerFragment extends DialogFragment {
	public static final String extraDate = "net.givreardent.sam.sss.date";
	private Date date;
	private DatePicker datePicker;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d("Dialog", "Creating dialog...");
		date = (Date) getArguments().getSerializable(extraDate);
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_date_picker, null);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		datePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePicker);
		datePicker.init(calendar.get(1), calendar.get(2), calendar.get(5),
				new OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						date = new GregorianCalendar(year, monthOfYear,
								dayOfMonth).getTime();
						getArguments().putSerializable(extraDate, date);
					}
				});
		Log.d("Dialog", "Version check complete.");
		return new AlertDialog.Builder(getActivity())
				.setView(v)
				.setTitle(R.string.date_picker_title)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								sendResult(Activity.RESULT_OK);
							}
						}).create();
	}

	public static DatePickerFragment newInstance(Date date) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraDate, date);
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	private void sendResult(int resultCode) {
		if (getTargetFragment() == null)
			return;
		Intent i = new Intent();
		i.putExtra(extraDate, date);
		getTargetFragment().onActivityResult(getTargetRequestCode(),
				resultCode, i);
	}
}
