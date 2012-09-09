package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ui.BuyAdFreeTeaserFragment.BuyAdFreeFragmentContract;
import net.robotmedia.billing.BillingController;
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
	private static final String ADFREE_ITEM = "adfree";
	
	private BuyAdFreeFragmentContract mContract;

	public static AdFreeBillingFragment newInstance() {
		// No configuration needed at this point.
		return new AdFreeBillingFragment();
	}
	
	public void requestPurchase() {
		BillingController.requestPurchase(getActivity(), ADFREE_ITEM);
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
		
		if (itemId.equals(ADFREE_ITEM)) {
			if (state == PurchaseState.PURCHASED) {
				mContract.onBuySuccess();
			} else {
				mContract.onBuyRevert();
			}
		}
	}

	@Override
	public void onRequestPurchaseResponse(String itemId, ResponseCode response) {
		Ln.d("onRequestPurchaseResponse: %s, %s", itemId, response.toString());
		
		switch (response) {
		case RESULT_BILLING_UNAVAILABLE:
		case RESULT_DEVELOPER_ERROR:
		case RESULT_ERROR:
		case RESULT_ITEM_UNAVAILABLE:
		case RESULT_SERVICE_UNAVAILABLE:
			mContract.onBuyError(response);
			break;
		case RESULT_OK:
			// We wait for the purchage state change.
			mContract.onPurchaseRequested();
			break;
		case RESULT_USER_CANCELED:
		default:
			mContract.onBuyCancel();
		}
	}
}
