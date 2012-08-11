package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.ApplicationPreferences;
import net.rdrei.android.scdl.ApplicationPreferences.StorageType;
import net.rdrei.android.scdl.DownloadPathValidator;
import net.rdrei.android.scdl.DownloadPathValidator.DownloadPathValidationException;
import net.rdrei.android.scdl.R;
import net.rdrei.android.scdl.guice.RoboPreferenceFragment;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.google.inject.Inject;

public class DownloadPreferencesFragment extends RoboPreferenceFragment
		implements OnSharedPreferenceChangeListener {

	private static final String DEFAULT_CUSTOM_PATH = "/mnt/sdcard/SoundcloudDownloader/";

	private ListPreference mTypePreference;

	private EditTextPreference mPathPreference;

	@Inject
	private ApplicationPreferences mAppPreferences;

	@Inject
	private SharedPreferences mSharedPreferences;

	@Inject
	private CustomPathChangeValidator mCustomPathValidator;

	public DownloadPreferencesFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.download_preferences);
		mTypePreference = (ListPreference) findPreference(ApplicationPreferences.KEY_STORAGE_TYPE);
		mPathPreference = (EditTextPreference) findPreference(ApplicationPreferences.KEY_STORAGE_CUSTOM_PATH);
		// Enable "enter" to submit.
		final EditText pathEditText = mPathPreference.getEditText();
		pathEditText.setSingleLine(true);
		pathEditText.setHint(DEFAULT_CUSTOM_PATH);
		pathEditText.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		mPathPreference.setOnPreferenceChangeListener(mCustomPathValidator);

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

	private static class CustomPathChangeValidator implements
			Preference.OnPreferenceChangeListener {

		@Inject
		private DownloadPathValidator mValidator;

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			try {
				mValidator.validateCustomPathOrThrow((String) newValue);
			} catch (DownloadPathValidationException e) {
				int errorMsgId;

				switch (e.getErrorCode()) {
				case INSECURE_PATH:
					errorMsgId = R.string.custom_path_error_insecure_path;
					break;
				case NOT_A_DIRECTORY:
					errorMsgId = R.string.custom_path_error_not_a_directory;
					break;
				case PERMISSION_DENIED:
					errorMsgId = R.string.custom_path_error_permission_denied;
					break;
				default:
					errorMsgId = R.string.custom_path_error_unknown;
					break;
				}

				showErrorDialog(preference.getContext(), errorMsgId);
				return false;
			}

			return true;
		}

		public void showErrorDialog(Context context, int errorMsgId) {
			final Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(errorMsgId)
					.setTitle(R.string.custom_path_error_title)
					.setIcon(android.R.drawable.ic_dialog_alert).show();
		}
	}
}
