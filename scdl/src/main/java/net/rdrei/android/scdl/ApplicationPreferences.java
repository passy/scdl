package net.rdrei.android.scdl;

import java.io.File;

import roboguice.inject.ContextSingleton;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.google.inject.Inject;

@ContextSingleton
public class ApplicationPreferences {
	public static final String DEFAULT_STORAGE_DIRECTORY = "Soundcloud";

	public static final String KEY_STORAGE_TYPE = "download_preferences_storage_type";

	public static final String KEY_STORAGE_CUSTOM_PATH = "download_preferences_storage_custom_path";

	public static enum StorageType {
		EXTERNAL, LOCAL, CUSTOM
	};

	@Inject
	private Context mContext;

	@Inject
	private SharedPreferences mPreferences;

	/**
	 * Displayable representation of the selected storage type.
	 * 
	 * @return
	 */
	public CharSequence getStorageTypeDisplay() {
		final int resourceId;

		switch (getStorageType()) {
		case EXTERNAL:
			resourceId = R.string.storage_sd;
			break;
		case LOCAL:
			resourceId = R.string.storage_phone;
			break;
		default:
			resourceId = R.string.sd_custom;
		}

		return mContext.getString(resourceId);
	}

	public CharSequence getCustomPath() {
		return mPreferences.getString(KEY_STORAGE_CUSTOM_PATH, null);
	}

	public StorageType getStorageType() {
		return StorageType.valueOf(mPreferences.getString(KEY_STORAGE_TYPE,
				StorageType.EXTERNAL.toString()));
	}

	public File getDefaultStorageDirectory() {
		return new File(Environment.DIRECTORY_MUSIC, DEFAULT_STORAGE_DIRECTORY);
	}

	/**
	 * Get the storage directory, based on either the user set value or the
	 * default value.
	 * 
	 * @return File
	 */
	public File getStorageDirectory() {
		StorageType storageType = getStorageType();
		if (storageType == StorageType.CUSTOM) {
			String customPath = (String) getCustomPath();

			if (customPath != null) {
				return new File(customPath);
			}
		}

		return new File(Environment.getExternalStorageDirectory().toString(),
				getDefaultStorageDirectory().toString());

	}
}
