package net.rdrei.android.scdl2.test;

import android.view.ViewGroup;

import com.google.inject.Provider;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.guice.ActivityLayoutInflater;
import net.rdrei.android.scdl2.ui.AdViewManager;

public class AdViewManagerStub extends AdViewManager {
	public AdViewManagerStub(Provider<ApplicationPreferences> preferencesProvider,
			ActivityLayoutInflater inflater) {
		super(preferencesProvider, inflater);
	}

	@Override
	public void addToView(ViewGroup baseView) {
		// no-op
	}
}
