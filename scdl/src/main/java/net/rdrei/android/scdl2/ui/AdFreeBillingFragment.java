package net.rdrei.android.scdl2.ui;

import net.rdrei.android.mediator.DelayedMessageQueue;
import net.rdrei.android.scdl2.ui.BuyAdFreeTeaserFragment.BuyAdFreeFragmentContract;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.BillingRequest.ResponseCode;
import net.robotmedia.billing.model.Transaction.PurchaseState;
import roboguice.util.Ln;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;

/**
 * Fragment holding the billing logic to buy the "adfree" option.
 * 
 * @author pascal
 * 
 */
public class AdFreeBillingFragment extends AbstractBillingFragment implements
		DelayedMessageQueue.Handler {
	private static final String ADFREE_ITEM = "adfree";

	private static final String DATA_HANDLER_KEY = "HANDLER_KEY";

	/**
	 * Message for requesting a new purchase of the defined adfree item.
	 */
	public static final int MSG_REQUEST_PURCHASE = 0;

	private BuyAdFreeFragmentContract mContract;

	public static AdFreeBillingFragment newInstance(final String handlerKey) {

		final Bundle bundle = new Bundle();
		bundle.putString(DATA_HANDLER_KEY, handlerKey);
		final AdFreeBillingFragment fragment = new AdFreeBillingFragment();
		fragment.setArguments(bundle);

		return fragment;
	}

	public void requestPurchase() {
		BillingController.requestPurchase(getActivity(), ADFREE_ITEM);
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

		// Need to manually attach here, because we still don't have
		// mixins, traits or multiple inheritence. ):
		mContract = (BuyAdFreeFragmentContract) activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mContract.registerMessageHandler(
				getArguments().getString(DATA_HANDLER_KEY), this);
	}

	@Override
	public void onBillingChecked(final boolean supported) {
		Ln.d("onBillingChecked(): " + supported);
		mContract.onBillingChecked(supported);
	}

	@Override
	public void onSubscriptionChecked(final boolean supported) {
		throw new UnsupportedOperationException("There are no subscriptions.");
	}

	@Override
	public void onPurchaseStateChanged(final String itemId,
			final PurchaseState state) {
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
	public void onRequestPurchaseResponse(final String itemId,
			final ResponseCode response) {
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

	@Override
	public void handleMessage(Message message) {
		// Only one supported message, but keeping this in the ususal schema.
		switch (message.what) {
		case MSG_REQUEST_PURCHASE:
			requestPurchase();
			break;
		default:
			throw new UnsupportedOperationException("Unsupported message!");

		}
	}
}
