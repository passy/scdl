package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.R;
import net.rdrei.android.scdl.ui.TrackErrorActivity.ErrorCode;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.app.AlertDialog.Builder;

import sheetrock.panda.changelog.ChangeLog;
import com.google.inject.Inject;
import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends RoboFragmentActivity implements
		DemoFragment.DemoActionListenerContract {

	private static final String SOUNDCLOUD_PACKAGE = "com.soundcloud.android";

	private static final String SOUNDCLOUD_MARKET_URI = "market://details?id=com.soundcloud.android";

	@Inject
	FragmentManager mFragmentManager;

	@InjectView(R.id.pager)
	ViewPager mPager;

	@InjectView(R.id.indicator)
	CirclePageIndicator mIndicator;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo);

		final DemoFragmentAdapter adapter = new DemoFragmentAdapter(
				mFragmentManager);
		mPager.setAdapter(adapter);
		mIndicator.setViewPager(mPager);

		showChangelogOnDemand();
	}

	@Override
	public void onNextPage() {
		Ln.d("Next page requested.");
		mPager.setCurrentItem(mPager.getCurrentItem() + 1);
	}

	@Override
	public void onStartSoundcloud() {
		Ln.d("Next page requested.");
		launchSoundcloud();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.preferences) {
			final Intent intent = new Intent(this,
					ApplicationPreferencesActivity.class);
			intent.putExtra(ApplicationPreferencesActivity.EXTRA_SHOW_FRAGMENT,
					"net.rdrei.android.scdl.ui.DownloadPreferencesFragment");
			startActivity(intent);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Show changelog if not displayed before on this version.
	 */
	private void showChangelogOnDemand() {
		ChangeLog cl = new ChangeLog(this);
		if (cl.firstRun()) {
			cl.getLogDialog().show();
		}
	}

	/**
	 * Launches the soundcloud app via a launch intent.
	 */
	private void launchSoundcloud() {
		final PackageManager packageManager = this.getPackageManager();
		final Intent intent = packageManager
				.getLaunchIntentForPackage(SOUNDCLOUD_PACKAGE);

		// Soundcloud is not yet installed.
		if (intent == null) {
			askForSoundcloudDownload();
			return;
		}

		startActivity(intent);
	}

	private void askForSoundcloudDownload() {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_install_soundcloud)
				.setCancelable(true)
				.setPositiveButton(R.string.dialog_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								startSoundcloudMarketActivity();
							}
						})
				.setNegativeButton(R.string.dialog_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).show();
	}

	/**
	 * Start the market intent to install Soundcloud.
	 */
	private void startSoundcloudMarketActivity() {
		final Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(SOUNDCLOUD_MARKET_URI));
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			final Intent errorIntent = new Intent(this,
					TrackErrorActivity.class);
			errorIntent.putExtra(TrackErrorActivity.EXTRA_ERROR_CODE,
					ErrorCode.NO_MARKET);
			
			startActivity(errorIntent);
		}
	}
}
