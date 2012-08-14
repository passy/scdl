package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.PreferenceManagerWrapper;

public interface DownloadPreferencesDelegateFactory {
	DownloadPreferencesDelegate create(PreferenceManagerWrapper preferenceManagerWrapper);
}
