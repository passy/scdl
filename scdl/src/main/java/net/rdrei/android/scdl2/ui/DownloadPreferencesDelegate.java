package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ActivityStarter;
import android.content.Intent;
import android.content.SharedPreferences;

public interface DownloadPreferencesDelegate {

	void onCreate(ActivityStarter activityStarter);

	void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key);

	void onPause();

	void onResume();

	void onActivityResult(int requestCode, int resultCode, Intent data);
}