package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import roboguice.activity.RoboFragmentActivity;
import roboguice.util.Ln;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabHelper.OnIabSetupFinishedListener;
import com.android.vending.billing.IabResult;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.google.inject.Inject;
import com.squareup.otto.Bus;

/**
 * This activity loads the appropriate fragments based on whether
 * the user has done a purchase yet or not.
 * It will also handle the payment processing via messages from the
 * corresponding fragments.
 * 
 * @author pascal
 */
public class BuyAdFreeActivity extends RoboFragmentActivity implements OnIabSetupFinishedListener {

	private static final String ANALYTICS_TAG = "BUY_ADFREE";

	@Inject
	private ApplicationPreferences mPreferences;

	@Inject
	private ActionBar mActionBar;
	
	@Inject
	private Tracker mTracker;
	
	@Inject
	private IabHelper mIabHelper;
	
	@Inject
	private Bus mBus;

	private Fragment mContentFragment;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.buy_ad_free);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mIabHelper.startSetup(this);

		if (savedInstanceState == null) {
			loadFragments();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		EasyTracker.getInstance().activityStart(this);
		mTracker.trackEvent(ANALYTICS_TAG, "start", null, null);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		EasyTracker.getInstance().activityStop(this);
	}

	private void loadFragments() {
		final FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		if (mPreferences.isAdFree()) {
			mContentFragment = BuyAdFreeThanksFragment.newInstance();
		} else {
			mContentFragment = BuyAdFreeTeaserFragment.newInstance();
		}

		transaction.add(R.id.main_layout, mContentFragment).commit();
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			final Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onIabSetupFinished(IabResult result) {
		Ln.d("onIabSetupFinished: %s", result);
		mBus.post(new IabSetupFinished(result.isSuccess()));
	}
	
	public static final class IabSetupFinished {
		public final boolean enabled;
		
		public IabSetupFinished(boolean enabled) {
			this.enabled = enabled;
		}
	}
}
