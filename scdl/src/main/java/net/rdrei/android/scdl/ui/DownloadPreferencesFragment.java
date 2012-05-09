package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.ApplicationPreferences;
import net.rdrei.android.scdl.ApplicationPreferences.StorageType;
import net.rdrei.android.scdl.R;
import net.rdrei.android.scdl.guice.RoboPreferenceFragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.EditTextPreference;
import android.preference.ListPreference;

import com.google.inject.Inject;

public class DownloadPreferencesFragment extends RoboPreferenceFragment
		implements OnSharedPreferenceChangeListener {

	@Inject
	private ApplicationPreferences mAppPreferences;

	@Inject
	private SharedPreferences mSharedPreferences;

	private ListPreference mTypePreference;

	private EditTextPreference mPathPreference;

	public DownloadPreferencesFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.download_preferences);
		mTypePreference = (ListPreference) findPreference(ApplicationPreferences.KEY_STORAGE_TYPE);
		mPathPreference = (EditTextPreference) findPreference(ApplicationPreferences.KEY_STORAGE_CUSTOM_PATH);

		loadStorageTypeOptions();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		mTypePreference.setSummary(mAppPreferences.getStorageTypeDisplay());
		mPathPreference.setSummary(mAppPreferences.getCustomPath());
		mPathPreference
				.setEnabled(mAppPreferences.getStorageType() == StorageType.CUSTOM);
	}

	@Override
	public void onPause() {
		super.onPause();

		mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		// Trigger manually for initial display.
		onSharedPreferenceChanged(mSharedPreferences, null);
	}

	private void loadStorageTypeOptions() {
		mTypePreference.setEntries(new CharSequence[] { getExternalLabel(),
				getPhoneLabel(), getString(R.string.storage_custom_label) });
		mTypePreference.setEntryValues(new String[] {
				StorageType.EXTERNAL.toString(), StorageType.LOCAL.toString(),
				StorageType.CUSTOM.toString(), });

		mTypePreference.setSummary(mAppPreferences.getStorageTypeDisplay());
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
