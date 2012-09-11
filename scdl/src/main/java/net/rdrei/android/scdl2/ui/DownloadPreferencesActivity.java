package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.PreferenceManagerWrapper;
import net.rdrei.android.scdl2.PreferenceManagerWrapperFactory;
import net.rdrei.android.scdl2.R;
import roboguice.activity.RoboPreferenceActivity;
import android.os.Bundle;

import com.google.inject.Inject;

/**
 * The activity replacing {@link DownloadPreferencesFragment} on pre-ICS.
 * 
 * @author pascal
 * 
 */
public class DownloadPreferencesActivity extends RoboPreferenceActivity {
	@Inject
	private DownloadPreferencesDelegateFactory mDelegateFactory;
	private DownloadPreferencesDelegate mDelegate;

	@Inject
	private PreferenceManagerWrapperFactory mPreferenceManagerFactory;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final PreferenceManagerWrapper preferenceManagerWrapper = mPreferenceManagerFactory
				.create(getPreferenceManager());
		mDelegate = mDelegateFactory.create(preferenceManagerWrapper);

		addPreferencesFromResource(R.xml.download_preferences);
		mDelegate.onCreate();
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
