/**
 * 
 */
package net.rdrei.android.scdl2;

import android.preference.PreferenceManager;

public interface PreferenceManagerWrapperFactory {
	/**
	 * Creates a new {@link PreferenceManagerWrapperImpl}.
	 * 
	 * @param preferenceManager
	 */
	PreferenceManagerWrapper create(PreferenceManager preferenceManager);
}