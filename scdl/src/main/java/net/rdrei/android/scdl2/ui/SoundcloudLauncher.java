package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ui.TrackErrorActivity.ErrorCode;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.google.inject.Inject;

public class SoundcloudLauncher {
	private static final String SOUNDCLOUD_PACKAGE = "com.soundcloud.android";
	private static final String SOUNDCLOUD_PLAY_URI = "market://details?id=com.soundcloud.android";

	final private Context mContext;
	final private Intent mLaunchIntent;

	@Inject
	public SoundcloudLauncher(final Context context,
			final PackageManager packageManager) {

		mContext = context;
		mLaunchIntent = packageManager
				.getLaunchIntentForPackage(SOUNDCLOUD_PACKAGE);
	}

	/**
	 * Launches the app or displays a dialog asking the user to download it.
	 */
	public void launch() {
		if (isInstalled()) {
			mContext.startActivity(mLaunchIntent);
		} else {
			askForDownload();
		}
	}

	/**
	 * Checks whether the SoundCloud app is installed on the device.
	 * 
	 * @return
	 */
	public boolean isInstalled() {
		return mLaunchIntent != null;
	}

	/**
	 * Opens a dialog in the given context asking the user to download
	 * SoundCloud from their play store or equivalent.
	 */
	public void askForDownload() {
		// TODO!
		showDownloadDialog();
	}

	private void showDownloadDialog() {
		final Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(R.string.dialog_install_soundcloud)
				.setCancelable(true)
				.setPositiveButton(R.string.dialog_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int which) {

								startSoundcloudMarketActivity();
							}
						})
				.setNegativeButton(R.string.dialog_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int which) {
								dialog.cancel();
							}
						}).show();
	}

	/**
	 * Start the market intent to install Soundcloud.
	 */
	public void startSoundcloudMarketActivity() {
		final Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(SOUNDCLOUD_PLAY_URI));
		try {
			mContext.startActivity(intent);
		} catch (final ActivityNotFoundException e) {
			final Intent errorIntent = new Intent(mContext,
					TrackErrorActivity.class);
			errorIntent.putExtra(TrackErrorActivity.EXTRA_ERROR_CODE,
					ErrorCode.NO_MARKET);

			mContext.startActivity(errorIntent);
		}
	}
}
