package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ui.BuyAdFreeTeaserFragment.BuyAdFreeFragmentContract;
import net.robotmedia.billing.BillingRequest.ResponseCode;
import net.robotmedia.billing.model.Transaction.PurchaseState;
import roboguice.util.Ln;
import android.app.Activity;

/**
 * Fragment holding the billing logic to buy the "adfree" option.
 * @author pascal
 *
 */
public class AdFreeBillingFragment extends AbstractBillingFragment {
	
	private BuyAdFreeFragmentContract mContract;

	public static AdFreeBillingFragment newInstance() {
		// No configuration needed at this point.
		return new AdFreeBillingFragment();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Need to manually attach here, because we still don't have
		// mixins, traits or multiple inheritence. ):
		mContract = (BuyAdFreeFragmentContract) activity;
	}

	@Override
	public void onBillingChecked(boolean supported) {
		Ln.d("onBillingChecked(): " + supported);
		mContract.onBillingChecked(supported);
	}

	@Override
	public void onSubscriptionChecked(boolean supported) {
		throw new UnsupportedOperationException("There are no subscriptions.");
	}

	@Override
	public void onPurchaseStateChanged(String itemId, PurchaseState state) {
		Ln.d("onPurchaseStateChanged: %s, %s", itemId, state.toString());
	}

	@Override
	public void onRequestPurchaseResponse(String itemId, ResponseCode response) {
		Ln.d("onRequestPurchaseResponse: %s, %s", itemId, response.toString());
	}
}
