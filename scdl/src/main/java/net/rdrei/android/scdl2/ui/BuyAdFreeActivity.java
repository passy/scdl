package net.rdrei.android.scdl2.ui;

import net.robotmedia.billing.BillingController;
import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;

public class BuyAdFreeActivity extends RoboFragmentActivity {

	private static final String ADFREE_ITEM = "adfree";
	private static final String BILLING_FRAGMENT_TAG = "BILLING";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadBillingFragment();
		BillingController.requestPurchase(this, ADFREE_ITEM);
	}

	private void loadBillingFragment() {
		AdFreeBillingFragment fragment = AdFreeBillingFragment.newInstance();
		getSupportFragmentManager().beginTransaction()
				.add(fragment, BILLING_FRAGMENT_TAG)
				.commit();
	}
}
