package net.givreardent.sam.sss;

import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment.NumberPickerDialogHandler;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;
import net.givreardent.sam.sss.data.Settings;
import net.givreardent.sam.sss.util.SSS;

public class SettingsFragment extends Fragment {
	@TargetApi(14)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v;
		if (Build.VERSION.SDK_INT >= 14) {
			v = inflater.inflate(R.layout.fragment_settings, parent, false);
			Switch notifications = (Switch) v.findViewById(R.id.notifications_switch);
			notifications.setChecked(Settings.areNotificationsEnabled(getActivity()));
			notifications.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Settings.setNotificationsEnabled(isChecked);
				}
			});
			Switch automute = (Switch) v.findViewById(R.id.mute_switch);
			automute.setChecked(Settings.mutes(getActivity()));
			automute.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Settings.setMute(isChecked);
				}
			});
			Switch startInClass = (Switch) v.findViewById(R.id.inclass_switch);
			startInClass.setChecked(Settings.startsInClass(getActivity()));
			startInClass.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Settings.setStartsInClass(isChecked);
				}
			});
		} else {
			v = inflater.inflate(R.layout.fragment_settings_compat, parent, false);
			ToggleButton notifications = (ToggleButton) v.findViewById(R.id.notifications_switch);
			notifications.setChecked(Settings.areNotificationsEnabled(getActivity()));
			notifications.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Settings.setNotificationsEnabled(isChecked);
				}
			});
			ToggleButton automute = (ToggleButton) v.findViewById(R.id.mute_switch);
			automute.setChecked(Settings.mutes(getActivity()));
			automute.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Settings.setMute(isChecked);
				}
			});
			ToggleButton startInClass = (ToggleButton) v.findViewById(R.id.inclass_switch);
			startInClass.setChecked(Settings.startsInClass(getActivity()));
			startInClass.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Settings.setStartsInClass(isChecked);
				}
			});
		}
		Spinner language = (Spinner) v.findViewById(R.id.language_spinner);
		ArrayAdapter<CharSequence> languages = ArrayAdapter.createFromResource(getActivity(), R.array.languages,
				android.R.layout.simple_spinner_item);
		languages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		language.setAdapter(languages);
		language.setSelection(Settings.getLanguage(getActivity()) + 1, true);
		language.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Settings.setLanguage(position - 1);
				Log.d("SettingsFragment", "Set language at: " + position);
				((SettingsActivity) getActivity()).changeLanguage();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		final Button reminder = (Button) v.findViewById(R.id.reminder_spinner);
		reminder.setText(Integer.toString(Settings.getReminder(getActivity())));
		reminder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NumberPickerBuilder builder = new NumberPickerBuilder();
				builder.addNumberPickerDialogHandler(new NumberPickerDialogHandler() {

					@Override
					public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative,
							double fullNumber) {
						Settings.setReminder(number);
						reminder.setText(Integer.toString(number));
					}
				});
				builder.setDecimalVisibility(View.INVISIBLE);
				builder.setPlusMinusVisibility(View.INVISIBLE);
				builder.setFragmentManager(getActivity().getSupportFragmentManager());
				builder.setStyleResId(R.style.BetterPickersDialogFragment_Light);
				builder.setReference(0);
				builder.show();
			}
		});
		final Button examReminder = (Button) v.findViewById(R.id.exam_reminder_spinner);
		examReminder.setText(Integer.toString(Settings.getExamReminder(getActivity())));
		examReminder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NumberPickerBuilder builder = new NumberPickerBuilder();
				builder.addNumberPickerDialogHandler(new NumberPickerDialogHandler() {

					@Override
					public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative,
							double fullNumber) {
						Settings.setExamReminder(number);
						examReminder.setText(Integer.toString(number));
					}
				});
				builder.setDecimalVisibility(View.INVISIBLE);
				builder.setPlusMinusVisibility(View.INVISIBLE);
				builder.setFragmentManager(getActivity().getSupportFragmentManager());
				builder.setStyleResId(R.style.BetterPickersDialogFragment_Light);
				builder.show();
			}
		});
		return v;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (Settings.saveSettings(getActivity()))
			Toast.makeText(getActivity(), "Settings saved.", Toast.LENGTH_SHORT).show();
		SSS.clearNotifications(getActivity());
		if (Settings.areNotificationsEnabled(getActivity())) {
			SSS.setNotifications(getActivity());
		}
	}
}
