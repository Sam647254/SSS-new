package net.givreardent.sam.sss;

import java.util.Locale;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import net.givreardent.sam.sss.data.Settings;

public abstract class SingleFragmentActivity extends FragmentActivity {
	protected abstract Fragment createFragment();
	
	protected int getLayoutResID() {
		return R.layout.activity_fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResID());
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = createFragment();
			fm.beginTransaction().add(R.id.fragmentContainer, fragment)
					.commit();
		}
		changeLanguage();
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
