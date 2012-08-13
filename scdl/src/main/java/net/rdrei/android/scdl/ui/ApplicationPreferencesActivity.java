package net.rdrei.android.scdl.ui;

import java.util.List;

import net.rdrei.android.scdl.R;
import roboguice.activity.RoboPreferenceActivity;
import android.os.Build;
import android.os.Bundle;

public class ApplicationPreferencesActivity extends RoboPreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.download_preferences);
		}
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		super.onBuildHeaders(target);

		loadHeadersFromResource(R.xml.preference_headers, target);
	}
}