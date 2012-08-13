package net.rdrei.android.scdl.ui;

import android.preference.PreferenceManager;

public interface DownloadPreferencesDelegateFactory {
	DownloadPreferencesDelegate create(PreferenceManager manager);
}
