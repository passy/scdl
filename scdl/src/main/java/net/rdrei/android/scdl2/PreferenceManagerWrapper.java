package net.rdrei.android.scdl2;

import android.preference.Preference;

public interface PreferenceManagerWrapper {

	public abstract Preference findPreference(CharSequence key);

}