package net.givreardent.sam.sss.dialogs;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import net.givreardent.sam.sss.R;
import net.givreardent.sam.sss.data.courses.Break;
import net.givreardent.sam.sss.data.courses.Term;
import net.givreardent.sam.sss.data.courses.TermList;
import net.givreardent.sam.sss.util.ScrollProofDatePicker;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;

public class CreateBreakFragment extends DialogFragment {
	public static final String extraName = "Name";
	public static final String extraStart = "Start", extraEnd = "End";
	public static final String extraTermID = "Term ID";
	public static final String extraBreakPos = "Break position";
	private EditText name;
	private ScrollProofDatePicker startDate;
	private ScrollProofDatePicker endDate;
	private Break mBreak;

	@TargetApi(11)
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Term term = TermList.get(getActivity()).getTerm((UUID) getArguments().getSerializable(extraTermID));
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_create_break, null);
		builder.setView(v);
		int pos = getArguments().getInt(extraBreakPos, -1);
		if (pos == -1) {
			mBreak = new Break(term);
			builder.setTitle(R.string.create_break_title);
		} else {
			mBreak = term.getBreaks().get(pos);
			builder.setTitle(mBreak.getName());
		}
		name = (EditText) v.findViewById(R.id.create_break_name);
		name.setText(mBreak.getName());
		startDate = (ScrollProofDatePicker) v.findViewById(R.id.break_start_DatePicker);
		endDate = (ScrollProofDatePicker) v.findViewById(R.id.break_end_DatePicker);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mBreak.getStartDate());
		startDate.init(calendar.get(1), calendar.get(2), calendar.get(5), new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				// TODO Auto-generated method stub

			}
		});
		if (Build.VERSION.SDK_INT >= 11) {
			startDate.setMinDate(term.getStartDate().getTime());
			startDate.setMaxDate(term.getEndDate().getTime());
		}
		calendar.setTime(mBreak.getEndDate());
		endDate.init(calendar.get(1), calendar.get(2), calendar.get(5), new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				// TODO Auto-generated method stub

			}
		});
		if (Build.VERSION.SDK_INT >= 11) {
			endDate.setMinDate(term.getStartDate().getTime());
			endDate.setMaxDate(term.getEndDate().getTime());
		}
		builder.setNeutralButton(android.R.string.cancel, null);
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendResult();
			}
		});
		return builder.create();
	}

	private void sendResult() {
		if (getTargetFragment() == null)
			return;
		Intent data = new Intent();
		data.putExtra(extraName, name.getText().toString());
		GregorianCalendar calendar = new GregorianCalendar(startDate.getYear(), startDate.getMonth(),
				startDate.getDayOfMonth());
		data.putExtra(extraStart, calendar.getTimeInMillis());
		calendar.set(1, endDate.getYear());
		calendar.set(2, endDate.getMonth());
		calendar.set(5, endDate.getDayOfMonth());
		data.putExtra(extraEnd, calendar.getTimeInMillis());
		data.putExtra(extraBreakPos, getArguments().getInt(extraBreakPos, 0));
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
	}

	public static CreateBreakFragment newInstance(UUID termID) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraTermID, termID);
		CreateBreakFragment fragment = new CreateBreakFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	public static CreateBreakFragment newInstance(UUID termID, int breakPos) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(extraTermID, termID);
		arguments.putInt(extraBreakPos, breakPos);
		CreateBreakFragment fragment = new CreateBreakFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
}
