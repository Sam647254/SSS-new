package net.givreardent.sam.sss.dialogs;

import net.givreardent.sam.sss.R;
import net.givreardent.sam.sss.data.journals.Journal;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class JournalWeatherFragment extends DialogFragment {
	public static final String extraWeatherValue = "net.givreardent.sam.sss.weather";
	private int weatherValue;
	private RadioGroup buttons;
	private RadioButton sunnyButton;
	private RadioButton cloudyButton;
	private RadioButton rainButton;
	private RadioButton snowButton;
	private RadioButton windyButton;
	
	public static JournalWeatherFragment newInstance(int w) {
		Bundle arguments = new Bundle();
		arguments.putInt(extraWeatherValue, w);
		
		JournalWeatherFragment fragment = new JournalWeatherFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		weatherValue = getArguments().getInt(extraWeatherValue);
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_journal_weather, null);
		buttons = (RadioGroup) v.findViewById(R.id.weather_radiogroup);
		switch (weatherValue) {
			case Journal.sunny:
				buttons.check(R.id.radiobutton_sunny);
				break;
			case Journal.rain:
				buttons.check(R.id.radiobutton_rain);
				break;
			case Journal.snow:
				buttons.check(R.id.radiobutton_snow);
				break;
			case Journal.cloudy:
				buttons.check(R.id.radiobutton_cloudy);
				break;
			case Journal.windy:
				buttons.check(R.id.radiobutton_windy);
				break;
		}
		
		sunnyButton = (RadioButton) v.findViewById(R.id.radiobutton_sunny);
		sunnyButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				weatherValue = Journal.sunny;
			}
		});
		cloudyButton = (RadioButton) v.findViewById(R.id.radiobutton_cloudy);
		cloudyButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				weatherValue = Journal.cloudy;
			}
		});
		rainButton = (RadioButton) v.findViewById(R.id.radiobutton_rain);
		rainButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				weatherValue = Journal.rain;
			}
		});
		snowButton = (RadioButton) v.findViewById(R.id.radiobutton_snow);
		snowButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				weatherValue = Journal.snow;
			}
		});
		windyButton = (RadioButton) v.findViewById(R.id.radiobutton_windy);
		windyButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				weatherValue = Journal.windy;
			}
		});

		return new AlertDialog.Builder(getActivity()).setView(v)
				.setTitle(R.string.weather_choose)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendResult(weatherValue);
					}
				})
				.setNegativeButton(android.R.string.cancel, null).create();
	}
	
	private void sendResult(int resultCode) {
		if (getTargetFragment() == null)
			return;
		Intent i = new Intent();
		i.putExtra(extraWeatherValue, weatherValue);
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
}
