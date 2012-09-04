package net.rdrei.android.scdl2.ui;

import net.robotmedia.billing.BillingRequest.ResponseCode;
import net.robotmedia.billing.model.Transaction.PurchaseState;

public class AdFreeBillingFragment extends AbstractBillingFragment {

	public static AdFreeBillingFragment newInstance() {
		// No configuration needed at this point.
		return new AdFreeBillingFragment();
	}

	@Override
	public void onBillingChecked(boolean supported) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSubscriptionChecked(boolean supported) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPurchaseStateChanged(String itemId, PurchaseState state) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRequestPurchaseResponse(String itemId, ResponseCode response) {
		// TODO Auto-generated method stub
	}

}
