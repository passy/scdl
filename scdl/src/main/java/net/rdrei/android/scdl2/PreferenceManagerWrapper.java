package net.rdrei.android.scdl2;

import android.preference.Preference;

public interface PreferenceManagerWrapper {

	Preference findPreference(CharSequence key);

}