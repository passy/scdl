package net.rdrei.android.scdl2.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Inject;
import com.viewpagerindicator.CirclePageIndicator;

import net.rdrei.android.scdl2.R;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

public class MainActivity extends RoboFragmentActivity implements DemoFragment.DemoActionListenerContract {

	private static final String ANALYTICS_TAG = "DEMO";

	@Inject
	private FragmentManager mFragmentManager;

	@Inject
	private SoundcloudLauncher mSoundcloudLauncher;

	@Inject
	private Tracker mTracker;

	@InjectView(R.id.pager)
	private ViewPager mPager;

	@InjectView(R.id.indicator)
	private CirclePageIndicator mIndicator;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load default settings once.
		PreferenceManager.setDefaultValues(this, R.xml.download_preferences, false);

		setContentView(R.layout.demo);

		final DemoFragmentAdapter adapter = new DemoFragmentAdapter(mFragmentManager);
		mPager.setAdapter(adapter);
		mIndicator.setViewPager(mPager);

		// Only attach if this is a fresh activity (eg. not on a screen rotated
		// onCreate call

		if (savedInstanceState == null) {
			CommonMenuFragment.injectMenu(this);
		}
	}

	@Override
	public void onNextPage() {
		Ln.d("Next page requested.");
		mTracker.send(new HitBuilders.EventBuilder()
						.setCategory(ANALYTICS_TAG)
						.setAction("NEXT_PAGE_CLICK")
						.setValue(mPager.getCurrentItem())
						.build()
		);
		mPager.setCurrentItem(mPager.getCurrentItem() + 1);
	}

	@Override
	public void onStartSoundcloud() {
		Ln.d("SoundCloud launch requested.");
		mTracker.send(new HitBuilders.EventBuilder()
						.setCategory(ANALYTICS_TAG)
						.setAction("SOUNDCLOUD_LAUNCH")
						.build()
		);
		mSoundcloudLauncher.launch();
	}
}
