package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.R;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.app.AlertDialog.Builder;

import sheetrock.panda.changelog.ChangeLog;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends RoboSherlockFragmentActivity implements
		DemoFragment.DemoActionListenerContract {

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

		final DemoFragmentAdapter adapter = new DemoFragmentAdapter(mFragmentManager);
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
				.getLaunchIntentForPackage("com.soundcloud.android");

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
								
								final Intent intent = new Intent(Intent.ACTION_VIEW,
										Uri .parse(SOUNDCLOUD_MARKET_URI));
								startActivity(intent);
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
}
