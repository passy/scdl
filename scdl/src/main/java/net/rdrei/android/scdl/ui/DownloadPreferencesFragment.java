package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.ApplicationPreferences;
import net.rdrei.android.scdl.R;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
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
		typePreference.setEntries(new CharSequence[] { getExternalLabel(),
				getPhoneLabel(), "Custom Path" });
		typePreference.setEntryValues(R.array.storage_types);
	}

	private String getExternalLabel() {
		return String.format("SD Card (%.2fGB free)", getFreeExternalStorage()
				/ Math.pow(1024, 3));
	}

	private String getPhoneLabel() {
		return String.format("Phone (%.2fGB free)", getFreeInternalStorage()
				/ Math.pow(1024, 3));
	}

	/**
	 * Returns the free bytes on external storage.
	 */
	public static long getFreeExternalStorage() {
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		return statFs.getAvailableBlocks() * statFs.getBlockSize();
	}

	/**
	 * Returns the free bytes on internal storage.
	 */
	public static long getFreeInternalStorage() {
		StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
		return statFs.getAvailableBlocks() * statFs.getBlockSize();
	}
}
