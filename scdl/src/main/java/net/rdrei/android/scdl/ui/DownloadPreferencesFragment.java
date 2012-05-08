package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class DownloadPreferencesFragment extends PreferenceFragment {

	public DownloadPreferencesFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.download_preferences);
	}
}
