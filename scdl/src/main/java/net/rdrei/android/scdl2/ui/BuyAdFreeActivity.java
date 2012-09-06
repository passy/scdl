package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ui.BuyAdFreeTeaserFragment.BuyAdFreeFragmentContract;
import net.robotmedia.billing.BillingController;
import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.inject.Inject;

public class BuyAdFreeActivity extends RoboFragmentActivity implements BuyAdFreeFragmentContract {

	private static final String ADFREE_ITEM = "adfree";
	private static final String BILLING_FRAGMENT_TAG = "BILLING";

	@Inject
	private ApplicationPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.buy_ad_free);

		loadFragments();
	}

	private void loadFragments() {
		AdFreeBillingFragment billingFragment = AdFreeBillingFragment
				.newInstance();
		final Fragment contentFragment;

		if (mPreferences.isAdFree()) {
			contentFragment = BuyAdFreeThanksFragment.newInstance();
		} else {
			contentFragment = BuyAdFreeTeaserFragment.newInstance();
		}

		getSupportFragmentManager().beginTransaction()
				.add(billingFragment, BILLING_FRAGMENT_TAG)
				.add(R.id.main_layout, contentFragment).commit();
	}

	@Override
	public void onBuyClicked() {
		BillingController.requestPurchase(this, ADFREE_ITEM);
	}

	@Override
	public void onBuyError() {
		// TODO
	}
}
