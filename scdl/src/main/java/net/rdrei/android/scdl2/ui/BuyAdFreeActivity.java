package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ui.BuyAdFreeTeaserFragment.BuyAdFreeFragmentContract;
import net.robotmedia.billing.BillingRequest.ResponseCode;
import roboguice.activity.RoboFragmentActivity;
import roboguice.util.Ln;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

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

	private static final String BILLING_FRAGMENT_TAG = "BILLING";

	@Inject
	private ApplicationPreferences mPreferences;
	
	@Inject
	private ActionBar mActionBar;
	
	private Fragment mContentFragment;
	private AdFreeBillingFragment mBillingFragment;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.buy_ad_free);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		loadFragments();
	}

	private void loadFragments() {
		final FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		if (mPreferences.isAdFree()) {
			mContentFragment = BuyAdFreeThanksFragment.newInstance();
		} else {
			mBillingFragment = AdFreeBillingFragment.newInstance();
			mContentFragment = BuyAdFreeTeaserFragment.newInstance();

			transaction.add(mBillingFragment, BILLING_FRAGMENT_TAG);
		}

		transaction.add(R.id.main_layout, mContentFragment).commit();
	}

	@Override
	public void onBuyClicked() {
		final BuyAdFreeTeaserFragment fragment = ((BuyAdFreeTeaserFragment) mContentFragment);
		fragment.setBillingEnabled(false);
		mBillingFragment.requestPurchase();
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
	public void onBillingChecked(final boolean supported) {
		if (mContentFragment instanceof BuyAdFreeTeaserFragment) {
			final BuyAdFreeTeaserFragment fragment = (BuyAdFreeTeaserFragment) mContentFragment;
			if (!supported) {
				fragment.showError(getString(R.string.error_no_iab));
			} else {
				fragment.clearError();
			}

			fragment.setBillingEnabled(supported);
		}
	}

	@Override
	public void onBuyError(ResponseCode response) {
		final BuyAdFreeTeaserFragment fragment = (BuyAdFreeTeaserFragment) mContentFragment;
		Ln.e("IAB error: %s", response.toString());
		fragment.showError("There was an error contacting the billing service. Please try again later.");
	}

	@Override
	public void onBuySuccess() {
		mPreferences.setAdFree(true);

		getSupportFragmentManager().beginTransaction().remove(mContentFragment)
				.add(R.id.main_layout, BuyAdFreeThanksFragment.newInstance())
				.commit();
	}

	@Override
	public void onBuyRevert() {
		mPreferences.setAdFree(false);
	}

	@Override
	public void onBuyCancel() {
		final BuyAdFreeTeaserFragment fragment = ((BuyAdFreeTeaserFragment) mContentFragment);

		fragment.setBillingEnabled(true);
		fragment.clearError();
	}
}
