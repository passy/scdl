package net.rdrei.android.scdl2.ui;

import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.BillingController.BillingStatus;
import net.robotmedia.billing.BillingRequest.ResponseCode;
import net.robotmedia.billing.helper.AbstractBillingActivity;
import net.robotmedia.billing.helper.AbstractBillingObserver;
import net.robotmedia.billing.model.Transaction.PurchaseState;
import android.support.v4.app.Fragment;

public abstract class AbstractBillingFragment extends Fragment implements BillingController.IConfiguration {

	protected AbstractBillingObserver mBillingObserver;

	@Override
	public byte[] getObfuscationSalt() {
		return new byte[] { -10, -43, 67, 6, -100, 64, -61, 94, -62, -91, 10,
				-87, -74, 97, 97, 35, 18, 82, 126, -43 };
	}

	@Override
	public String getPublicKey() {
		return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApo7iSaebSe9ygXwPO+PYH/7GDSz9poC27r5c6t/Z3ezZEMKewHjA46f2WrisQ9CW8CzVVGHksFTm+E8X9s0kbjqZG69Az4cngIvLedbZJFG8We9CO7/dY8Kn+wsJFeVzlkN0jFMixtOr43S/+vLd5BEPdNvGkd8TIqoumVMacFQ1fH8IkBCBDIiEN6tbaa6LSXi9ccp8/L6WbIQ7gETMBwsGkVc7AlvcVCsOCRahv7uOO/HD2KTHfejn2xGySDzSPAXjTaOgUsv/KoCFB+KoGEdYcRdR7cwTIj4cxEeiPPP/aZGG6mmQEZmh4TtmA6xv6nOf20819rj/rHzmg3h5yQIDAQAB";
	}

	/**
	 * <p>
	 * Returns the in-app product billing support status, and checks it
	 * asynchronously if it is currently unknown.
	 * {@link AbstractBillingActivity#onBillingChecked(boolean)} will be called
	 * eventually with the result.
	 * </p>
	 * <p>
	 * In-app product support does not imply subscription support. To check if
	 * subscriptions are supported, use
	 * {@link AbstractBillingActivity#checkSubscriptionSupported()}.
	 * </p>
	 * 
	 * @return the current in-app product billing support status (unknown,
	 *         supported or unsupported). If it is unsupported, subscriptions
	 *         are also unsupported.
	 * @see AbstractBillingActivity#onBillingChecked(boolean)
	 * @see AbstractBillingActivity#checkSubscriptionSupported()
	 */
	public BillingStatus checkBillingSupported() {
		return BillingController.checkBillingSupported(getActivity());
	}

	/**
	 * <p>
	 * Returns the subscription billing support status, and checks it
	 * asynchronously if it is currently unknown.
	 * {@link AbstractBillingActivity#onSubscriptionChecked(boolean)} will be
	 * called eventually with the result.
	 * </p>
	 * <p>
	 * No support for subscriptions does not imply that in-app products are also
	 * unsupported. To check if subscriptions are supported, use
	 * {@link AbstractBillingActivity#checkSubscriptionSupported()}.
	 * </p>
	 * 
	 * @return the current in-app product billing support status (unknown,
	 *         supported or unsupported). If it is unsupported, subscriptions
	 *         are also unsupported.
	 * @see AbstractBillingActivity#onBillingChecked(boolean)
	 * @see AbstractBillingActivity#checkSubscriptionSupported()
	 */
	public BillingStatus checkSubscriptionSupported() {
		return BillingController.checkSubscriptionSupported(getActivity());
	}

	public abstract void onBillingChecked(boolean supported);

	public abstract void onSubscriptionChecked(boolean supported);

	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBillingObserver = new AbstractBillingObserver(getActivity()) {

			public void onBillingChecked(boolean supported) {
				AbstractBillingFragment.this.onBillingChecked(supported);
			}

			public void onSubscriptionChecked(boolean supported) {
				AbstractBillingFragment.this.onSubscriptionChecked(supported);
			}

			public void onPurchaseStateChanged(String itemId, PurchaseState state) {
				AbstractBillingFragment.this.onPurchaseStateChanged(itemId, state);
			}

			public void onRequestPurchaseResponse(String itemId, ResponseCode response) {
				AbstractBillingFragment.this.onRequestPurchaseResponse(itemId, response);
			}
		};
		BillingController.registerObserver(mBillingObserver);
		BillingController.setConfiguration(this); // This fragment will provide
													// the public key and salt
		this.checkBillingSupported();
		if (!mBillingObserver.isTransactionsRestored()) {
			BillingController.restoreTransactions(getActivity());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		BillingController.unregisterObserver(mBillingObserver); // Avoid
																// receiving
																// notifications
																// after destroy
		BillingController.setConfiguration(null);
	}

	public abstract void onPurchaseStateChanged(String itemId, PurchaseState state);;

	public abstract void onRequestPurchaseResponse(String itemId, ResponseCode response);

	/**
	 * Requests the purchase of the specified item. The transaction will not be
	 * confirmed automatically; such confirmation could be handled in
	 * {@link AbstractBillingActivity#onPurchaseExecuted(String)}. If automatic
	 * confirmation is preferred use
	 * {@link BillingController#requestPurchase(android.content.Context, String, boolean)}
	 * instead.
	 * 
	 * @param itemId
	 *            id of the item to be purchased.
	 */
	public void requestPurchase(String itemId) {
		BillingController.requestPurchase(getActivity(), itemId);
	}

	/**
	 * Requests the purchase of the specified subscription item. The transaction
	 * will not be confirmed automatically; such confirmation could be handled
	 * in {@link AbstractBillingActivity#onPurchaseExecuted(String)}. If
	 * automatic confirmation is preferred use
	 * {@link BillingController#requestPurchase(android.content.Context, String, boolean)}
	 * instead.
	 * 
	 * @param itemId
	 *            id of the item to be purchased.
	 */
	public void requestSubscription(String itemId) {
		BillingController.requestSubscription(getActivity(), itemId);
	}

	/**
	 * Requests to restore all transactions.
	 */
	public void restoreTransactions() {
		BillingController.restoreTransactions(getActivity());
	}

}
