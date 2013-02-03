package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ActivityStarter;
import net.rdrei.android.scdl2.PreferenceManagerWrapper;
import net.rdrei.android.scdl2.PreferenceManagerWrapperFactory;
import net.rdrei.android.scdl2.R;
import roboguice.activity.RoboPreferenceActivity;
import roboguice.inject.InjectExtra;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.inject.Inject;

/**
 * The activity replacing {@link DownloadPreferencesFragment} on pre-ICS.
 * 
 * @author pascal
 * 
 */
public class DownloadPreferencesActivity extends RoboPreferenceActivity {
	public static final String EXTRA_DOWNLOAD_ERROR = "download_error";

	@Inject
	private DownloadPreferencesDelegateFactory mDelegateFactory;
	private DownloadPreferencesDelegate mDelegate;

	@Inject
	private PreferenceManagerWrapperFactory mPreferenceManagerFactory;

	@InjectExtra(optional = true, value = EXTRA_DOWNLOAD_ERROR)
	private Integer mExtraDownloadError;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final PreferenceManagerWrapper preferenceManagerWrapper = mPreferenceManagerFactory
				.create(getPreferenceManager());
		mDelegate = mDelegateFactory.create(preferenceManagerWrapper);

		addPreferencesFromResource(R.xml.download_preferences);
		mDelegate.onCreate(new ActivityStarter(this));

		if (mExtraDownloadError != null) {
			new AlertDialog.Builder(this)
					.setTitle("Your Download failed :(")
					.setMessage(
							"Try a different download location or disabling SSL. If the problem persists, send an email to phartig@rdrei.net!")
					.create().show();
		}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mDelegate.onActivityResult(requestCode, resultCode, data);
	}
}
