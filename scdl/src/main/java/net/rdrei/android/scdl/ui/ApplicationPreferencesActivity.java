package net.rdrei.android.scdl.ui;

import java.util.List;

import net.rdrei.android.scdl.R;
import roboguice.activity.RoboPreferenceActivity;

public class ApplicationPreferencesActivity extends RoboPreferenceActivity {
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
	}
}
