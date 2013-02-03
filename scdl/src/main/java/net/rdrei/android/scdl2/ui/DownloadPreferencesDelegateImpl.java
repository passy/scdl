package net.rdrei.android.scdl2.ui;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.scdl2.ActivityStarter;
import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.ApplicationPreferences.StorageType;
import net.rdrei.android.scdl2.DownloadPathValidator;
import net.rdrei.android.scdl2.DownloadPathValidator.DownloadPathValidationException;
import net.rdrei.android.scdl2.PreferenceManagerWrapper;
import net.rdrei.android.scdl2.R;
import roboguice.util.Ln;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Environment;
import android.os.StatFs;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

import com.google.analytics.tracking.android.Tracker;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class DownloadPreferencesDelegateImpl implements
		OnSharedPreferenceChangeListener, DownloadPreferencesDelegate {

	private static final String DOWNLOAD_DIRECTORY_NAME = "SoundCloud";
	private static final String ANALYTICS_TAG = "DOWNLOAD_PREFERENCES";
	private static final int REQUEST_DOWNLOAD_DIRECTORY_CHOOSER = 0;

	private ListPreference mTypePreference;

	private Preference mPathPreference;

	@Inject
	private ApplicationPreferences mAppPreferences;

	@Inject
	private SharedPreferences mSharedPreferences;

	@Inject
	private CustomPathChangeValidator mCustomPathValidator;

	@Inject
	private Context mContext;
	private ActivityStarter mActivityStarter;

	@Inject
	Tracker mTracker;

	private final PreferenceManagerWrapper mPreferenceManager;

	@Inject
	public DownloadPreferencesDelegateImpl(
			@Assisted final PreferenceManagerWrapper manager) {
		mPreferenceManager = manager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rdrei.android.scdl2.ui.DownloadPreferencesDelegate#onCreate()
	 */
	@Override
	public void onCreate(final ActivityStarter activityStarter) {
		mTypePreference = (ListPreference) mPreferenceManager
				.findPreference(ApplicationPreferences.KEY_STORAGE_TYPE);
		mPathPreference = mPreferenceManager
				.findPreference(ApplicationPreferences.KEY_STORAGE_CUSTOM_PATH);
		mPathPreference.setOnPreferenceChangeListener(mCustomPathValidator);
		mPathPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						startDownloadDirectoryChooser();
						return true;
					}
				});

		loadStorageTypeOptions();
		mActivityStarter = activityStarter;
		mTracker.trackEvent(ANALYTICS_TAG, "create", null, null);
	}

	private void startDownloadDirectoryChooser() {
		final Intent chooseIntent = new Intent(mContext,
				DirectoryChooserActivity.class);
		chooseIntent.putExtra(DirectoryChooserActivity.EXTRA_NEW_DIR_NAME,
				DOWNLOAD_DIRECTORY_NAME);
		mActivityStarter.startActivityForResult(chooseIntent,
				REQUEST_DOWNLOAD_DIRECTORY_CHOOSER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rdrei.android.scdl2.ui.DownloadPreferencesDelegate#
	 * onSharedPreferenceChanged(android.content.SharedPreferences,
	 * java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(
			final SharedPreferences sharedPreferences, final String key) {

		trackChange(sharedPreferences, key);
		mTypePreference.setSummary(mAppPreferences.getStorageTypeDisplay());
		mPathPreference.setSummary(mAppPreferences.getCustomPath());
		mPathPreference
				.setEnabled(mAppPreferences.getStorageType() == StorageType.CUSTOM);
	}

	/**
	 * Let analytics know that there was a change.
	 *
	 * @param sharedPreferences
	 * @param key
	 */
	private void trackChange(SharedPreferences sharedPreferences, String key) {
		String value = null;

		if (key == ApplicationPreferences.KEY_SSL_ENABLED) {
			value = String.valueOf(sharedPreferences.getBoolean(key, false));
		} else if (key == ApplicationPreferences.KEY_STORAGE_TYPE
				|| key == ApplicationPreferences.KEY_STORAGE_CUSTOM_PATH) {
			value = sharedPreferences.getString(key, "<undef>");
		}

		if (value != null) {
			mTracker.trackEvent(ANALYTICS_TAG, "change",
					String.format("%s:%s", key, value), null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rdrei.android.scdl2.ui.DownloadPreferencesDelegate#onPause()
	 */
	@Override
	public void onPause() {
		mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rdrei.android.scdl2.ui.DownloadPreferencesDelegate#onResume()
	 */
	@Override
	public void onResume() {
		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		// Trigger manually for initial display.
		onSharedPreferenceChanged(mSharedPreferences, null);
	}

	private void loadStorageTypeOptions() {
		mTypePreference.setEntries(new CharSequence[] { getExternalLabel(),
				getPhoneLabel(),
				mContext.getString(R.string.storage_custom_label) });
		mTypePreference.setEntryValues(new String[] {
				StorageType.EXTERNAL.toString(), StorageType.LOCAL.toString(),
				StorageType.CUSTOM.toString(), });

		mTypePreference.setSummary(mAppPreferences.getStorageTypeDisplay());
	}

	private String getExternalLabel() {
		final double free = getFreeExternalStorage() / Math.pow(1024, 3);

		if (free >= 0) {
			return String.format(mContext.getString(R.string.storage_sd_label),
					free);
		}

		return mContext.getString(R.string.storage_sd_label_no_free);
	}

	private String getPhoneLabel() {
		final double free = getFreeInternalStorage() / Math.pow(1024, 3);

		if (free >= 0) {
			return String.format(
					mContext.getString(R.string.storage_phone_label), free);
		}

		return mContext.getString(R.string.storage_phone_label_no_free);
	}

	/**
	 * Returns the free bytes on external storage.
	 */
	public static long getFreeExternalStorage() {
		final StatFs statFs = new StatFs(Environment
				.getExternalStorageDirectory().getPath());
		return statFs.getAvailableBlocks() * statFs.getBlockSize();
	}

	/**
	 * Returns the free bytes on internal storage.
	 */
	public static long getFreeInternalStorage() {
		final String path = Environment.getDataDirectory().getPath();
		final StatFs statFs = new StatFs(path);
		return statFs.getAvailableBlocks() * statFs.getBlockSize();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Ln.i("onActivityResult: %d, %d, %s", requestCode, resultCode, data);
		if (requestCode == REQUEST_DOWNLOAD_DIRECTORY_CHOOSER
				&& resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
			final String directory = data
					.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);

			updateCustomPath(directory);

			Ln.i("New custom download path: %s",
					mAppPreferences.getCustomPath());
		}
	}

	private void updateCustomPath(String directory) {
		if (mCustomPathValidator.onPreferenceChange(mPathPreference, directory)) {
			mPathPreference
					.getEditor()
					.putString(ApplicationPreferences.KEY_STORAGE_CUSTOM_PATH,
							directory).commit();
		}
	}

	private static class CustomPathChangeValidator implements
			Preference.OnPreferenceChangeListener {

		@Inject
		private DownloadPathValidator mValidator;

		@Override
		public boolean onPreferenceChange(final Preference preference,
				final Object newValue) {
			try {
				mValidator.validateCustomPathOrThrow((String) newValue);
			} catch (final DownloadPathValidationException e) {
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

		public void showErrorDialog(final Context context, final int errorMsgId) {
			final Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(errorMsgId)
					.setTitle(R.string.custom_path_error_title)
					.setIcon(android.R.drawable.ic_dialog_alert).show();
		}
	}
}
