package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ActivityStarter;
import net.rdrei.android.scdl2.PreferenceManagerWrapper;
import net.rdrei.android.scdl2.PreferenceManagerWrapperFactory;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.guice.RoboPreferenceFragment;
import android.content.Intent;
import android.os.Bundle;

import com.google.inject.Inject;

public class DownloadPreferencesFragment extends RoboPreferenceFragment {

	@Inject
	private DownloadPreferencesDelegateFactory mDelegateFactory;
	private DownloadPreferencesDelegate mDelegate;

	@Inject
	private PreferenceManagerWrapperFactory mPreferenceManagerWrapperFactory;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final PreferenceManagerWrapper preferenceManager = mPreferenceManagerWrapperFactory
				.create(getPreferenceManager());
		mDelegate = mDelegateFactory.create(preferenceManager);

		addPreferencesFromResource(R.xml.download_preferences);
		mDelegate.onCreate(new ActivityStarter(this));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mDelegate.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();

		mDelegate.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		mDelegate.onResume();
	}
}
