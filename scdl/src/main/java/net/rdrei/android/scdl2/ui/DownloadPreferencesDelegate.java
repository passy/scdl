package net.rdrei.android.scdl2.ui;

import android.content.SharedPreferences;

public interface DownloadPreferencesDelegate {

	public abstract void onCreate();

	public abstract void onSharedPreferenceChanged(
			SharedPreferences sharedPreferences, String key);

	public abstract void onPause();

	public abstract void onResume();

}