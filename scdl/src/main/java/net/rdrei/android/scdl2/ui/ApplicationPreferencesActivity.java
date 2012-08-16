package net.rdrei.android.scdl2.ui;

import java.util.List;

import net.rdrei.android.scdl2.R;
import roboguice.activity.RoboPreferenceActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class ApplicationPreferencesActivity extends RoboPreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// As long as we only have one pane, directly skip to the Download
		// Preference Fragment.
		getIntent().putExtra(
				ApplicationPreferencesActivity.EXTRA_SHOW_FRAGMENT,
				"net.rdrei.android.scdl2.ui.DownloadPreferencesFragment");

		super.onCreate(savedInstanceState);

		// Directly start into the download fragment activity.
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			startActivity(new Intent(this, DownloadPreferencesActivity.class));
			finish();
		}
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		super.onBuildHeaders(target);

		// This isn't called at the moment.
		loadHeadersFromResource(R.xml.preference_headers, target);
	}
}