package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.ApplicationPreferences;
import net.rdrei.android.scdl.R;
import net.rdrei.android.scdl.guice.RoboPreferenceFragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

import com.google.inject.Inject;

public class DownloadPreferencesFragment extends RoboPreferenceFragment
		implements OnSharedPreferenceChangeListener {

	@Inject
	private ApplicationPreferences mAppPreferences;

	@Inject
	private SharedPreferences mSharedPreferences;

	@Inject
	private DownloadPreferencesDelegateFactory mDelegateFactory;
	
	private DownloadPreferencesDelegate mDelegate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDelegate = mDelegateFactory.create(getPreferenceManager());
		
		addPreferencesFromResource(R.xml.download_preferences);
		mDelegate.onCreate();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		mDelegate.onSharedPreferenceChanged(sharedPreferences, key);
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
