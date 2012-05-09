package net.rdrei.android.scdl;

import roboguice.inject.ContextSingleton;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;

@ContextSingleton
public class ApplicationPreferences {
	public static final String KEY_STORAGE_TYPE = "download_preferences_storage_type";

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
	public String getStorageTypeDisplay() {
		final StorageType type = StorageType.valueOf(mPreferences.getString(
				KEY_STORAGE_TYPE, StorageType.EXTERNAL.toString()));
		
		final int resourceId;
		
		switch (type) {
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
}
