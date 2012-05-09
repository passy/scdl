package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.ApplicationPreferences;
import net.rdrei.android.scdl.R;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class DownloadPreferencesFragment extends PreferenceFragment {

	public DownloadPreferencesFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.download_preferences);

		loadStorageTypeOptions();
	}

	private void loadStorageTypeOptions() {
		ListPreference typePreference = (ListPreference) findPreference(ApplicationPreferences.KEY_STORAGE_TYPE);
		typePreference.setEntries(new CharSequence[] {"SD Card", "Phone", "Custom Path"});
		typePreference.setEntryValues(R.array.storage_types);
	}
}
