package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.PreferenceManagerWrapper;

public interface DownloadPreferencesDelegateFactory {
	DownloadPreferencesDelegate create(
			PreferenceManagerWrapper preferenceManagerWrapper);
}
