package net.givreardent.sam.sss;

import java.util.Locale;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import net.givreardent.sam.sss.data.Settings;

public class SettingsActivity extends FragmentActivity {
	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT >= 11) {
			setTheme(android.R.style.Theme_Holo_Light_DialogWhenLarge);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = new SettingsFragment();
			fm.beginTransaction().add(R.id.fragmentContainer, fragment)
					.commit();
		}
	}
	
	protected void changeLanguage() {
		int language = Settings.getLanguage(this);
		Resources resources = getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		Configuration configuration = resources.getConfiguration();
		Locale newLocale = null;
		switch (Settings.getLanguage(this)) {
			case 0:
				newLocale = new Locale("en");
				break;
			case 1:
				newLocale = new Locale("fr");
				break;
			case 2:
				newLocale = new Locale("ja");
				break;
			default:
				newLocale = Locale.getDefault();
		}
		configuration.locale = newLocale;
		resources.updateConfiguration(configuration, metrics);
	}
}
