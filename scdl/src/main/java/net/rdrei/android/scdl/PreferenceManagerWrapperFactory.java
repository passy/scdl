/**
 * 
 */
package net.rdrei.android.scdl;

import android.preference.PreferenceManager;

public interface PreferenceManagerWrapperFactory {
	/**
	 * Creates a new {@link PreferenceManagerWrapperImpl}.
	 * 
	 * @param preferenceManager
	 */
	public PreferenceManagerWrapper create(PreferenceManager preferenceManager);
}