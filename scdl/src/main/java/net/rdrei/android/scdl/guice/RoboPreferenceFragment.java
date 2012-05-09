package net.rdrei.android.scdl.guice;

import roboguice.RoboGuice;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Careful, this can't inject views, because - at its current state - roboguice
 * doesn't support native fragments!
 * 
 * @author pascal
 * 
 */
public abstract class RoboPreferenceFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
	}
}
