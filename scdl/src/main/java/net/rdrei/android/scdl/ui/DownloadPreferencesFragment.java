package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.ApplicationPreferences;
import net.rdrei.android.scdl.ApplicationPreferences.StorageType;
import net.rdrei.android.scdl.R;
import net.rdrei.android.scdl.guice.RoboPreferenceFragment;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.ListPreference;

import com.google.inject.Inject;

public class DownloadPreferencesFragment extends RoboPreferenceFragment {
	@Inject
	private ApplicationPreferences mAppPreferences;
	
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
				getPhoneLabel(), getString(R.string.storage_custom_label) });
		typePreference.setEntryValues(new String[] {
				StorageType.EXTERNAL.toString(), StorageType.LOCAL.toString(),
				StorageType.CUSTOM.toString(), });

		typePreference.setSummary(mAppPreferences.getStorageTypeDisplay());
	}

	private String getExternalLabel() {
		return String.format(getString(R.string.storage_sd_label),
				getFreeExternalStorage() / Math.pow(1024, 3));
	}

	private String getPhoneLabel() {
		return String.format(getString(R.string.storage_phone_label),
				getFreeInternalStorage() / Math.pow(1024, 3));
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
