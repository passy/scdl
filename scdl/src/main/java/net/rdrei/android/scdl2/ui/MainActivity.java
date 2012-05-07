package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.R;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import sheetrock.panda.changelog.ChangeLog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.inject.Inject;
import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends RoboSherlockFragmentActivity implements
		DemoFragment.DemoActionListenerContract {

	@Inject
	private FragmentManager mFragmentManager;
	
	@Inject
	private SoundcloudLauncher mSoundcloudLauncher;

	@InjectView(R.id.pager)
	private ViewPager mPager;

	@InjectView(R.id.indicator)
	private CirclePageIndicator mIndicator;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load default settings once.
		PreferenceManager.setDefaultValues(this, R.xml.download_preferences,
				false);

		setContentView(R.layout.demo);

		final DemoFragmentAdapter adapter = new DemoFragmentAdapter(
				mFragmentManager);
		mPager.setAdapter(adapter);
		mIndicator.setViewPager(mPager);

		showChangelogOnDemand();

		// Only attach if this is a fresh activity (eg. not on a screen rotated
		// onCreate call

		if (savedInstanceState == null) {
			CommonMenuFragment.injectMenu(this);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onNextPage() {
		Ln.d("Next page requested.");
		mPager.setCurrentItem(mPager.getCurrentItem() + 1);
	}

	@Override
	public void onStartSoundcloud() {
		Ln.d("SoundCloud launch requested.");
		mSoundcloudLauncher.launch();
	}

	/**
	 * Show changelog if not displayed before on this version.
	 */
	private void showChangelogOnDemand() {
		final ChangeLog cl = new ChangeLog(this);
		if (cl.firstRun()) {
			cl.getLogDialog().show();
		}
	}
}
