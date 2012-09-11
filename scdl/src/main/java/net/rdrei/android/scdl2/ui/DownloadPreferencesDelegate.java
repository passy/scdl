package net.rdrei.android.scdl2.ui;

import android.content.SharedPreferences;

public interface DownloadPreferencesDelegate {

	void onCreate();

	void onSharedPreferenceChanged(
			SharedPreferences sharedPreferences, String key);

	void onPause();

	void onResume();

}