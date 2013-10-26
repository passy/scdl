package net.rdrei.android.scdl2.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.google.inject.Inject;

import roboguice.util.Ln;

public class SoundcloudLauncher {
	private static final String SOUNDCLOUD_PACKAGE = "com.soundcloud.android";

	final private Context mContext;
	final private Intent mLaunchIntent;
    private final SoundcloudInstallAsker mAsker;

    @Inject
	public SoundcloudLauncher(final Context context,
			final PackageManager packageManager,
            final SoundcloudInstallAsker asker) {

		mContext = context;
		mLaunchIntent = packageManager
				.getLaunchIntentForPackage(SOUNDCLOUD_PACKAGE);
        mAsker = asker;
	}

	/**
	 * Launches the app or displays a dialog asking the user to download it.
	 */
	public void launch() {
        boolean success = false;
		if (isInstalled()) {
            try {
                mContext.startActivity(mLaunchIntent);
                success = true;
            } catch (ActivityNotFoundException exc) {
                Ln.w(exc);
            }
        }

        if (!success) {
			askForDownload();
		}
	}

    private void askForDownload() {
        mAsker.ask();
    }

    /**
	 * Checks whether the SoundCloud app is installed on the device.
	 * 
	 * @return
	 */
	public boolean isInstalled() {
		return mLaunchIntent != null;
	}

}
