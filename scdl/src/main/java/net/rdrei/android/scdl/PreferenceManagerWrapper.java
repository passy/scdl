package net.rdrei.android.scdl;

import android.preference.Preference;

public interface PreferenceManagerWrapper {

	public abstract Preference findPreference(CharSequence key);

}