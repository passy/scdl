package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ui.BuyAdFreeTeaserFragment.BuyAdFreeFragmentContract;
import net.robotmedia.billing.BillingController;
import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.inject.Inject;

/**
 * This class acts as mediator between the three different fragments loaded into
 * it that are responsible for a) the billing logic b) the teaser view leading
 * to a purchase, giving explanations c) the thanks view after a successful
 * purchase
 * 
 * @author pascal
 */
public class BuyAdFreeActivity extends RoboFragmentActivity implements
		BuyAdFreeFragmentContract {

	private static final String ADFREE_ITEM = "adfree";
	private static final String BILLING_FRAGMENT_TAG = "BILLING";

	@Inject
	private ApplicationPreferences mPreferences;
	private Fragment mContentFragment;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.buy_ad_free);

		loadFragments();
	}

	private void loadFragments() {
		final AdFreeBillingFragment billingFragment = AdFreeBillingFragment
				.newInstance();
		if (mPreferences.isAdFree()) {
			mContentFragment = BuyAdFreeThanksFragment.newInstance();
		} else {
			mContentFragment = BuyAdFreeTeaserFragment.newInstance();
		}

		getSupportFragmentManager().beginTransaction()
				.add(billingFragment, BILLING_FRAGMENT_TAG)
				.add(R.id.main_layout, mContentFragment).commit();
	}

	@Override
	public void onBuyClicked() {
		BillingController.requestPurchase(this, ADFREE_ITEM);
	}

	@Override
	public void onBuyError() {
		// TODO
	}

	@Override
	public void onBillingChecked(final boolean supported) {
		if (mContentFragment instanceof BuyAdFreeTeaserFragment) {
			((BuyAdFreeTeaserFragment) mContentFragment)
					.setBillingEnabled(supported);
		}
	}
}
