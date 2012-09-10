package net.rdrei.android.scdl2.ui;

import net.rdrei.android.mediator.DelayedMessageQueue;
import net.rdrei.android.scdl2.R;
import net.robotmedia.billing.BillingRequest.ResponseCode;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BuyAdFreeTeaserFragment extends
		ContractFragment<BuyAdFreeTeaserFragment.BuyAdFreeFragmentContract>
		implements DelayedMessageQueue.Handler {

	private static final String DATA_HANDLER_KEY = "HANDLER_KEY";
	private static final String DATA_BILLING_ENABLED = "BILLING_ENABLED";
	public static final int MSG_BILLING_SUPPORTED = 0;
	public static final int MSG_BILLING_UNSUPPORTED = 1;
	public static final int MSG_PURCHASE_REVERTED = 2;
	public static final int MSG_PURCHASE_CANCELLED = 3;
	public static final int MSG_PURCHASE_REQUESTED = 4;
	public static final int MSG_PURCHASE_ERROR = 5;

	@InjectView(R.id.buy_ad_free_teaser_text)
	private TextView mTeaserText;

	@InjectView(R.id.error_message)
	private TextView mErrorText;

	@InjectView(R.id.buy_ad_free_teaser_button)
	private Button mButton;

	@InjectView(R.id.progress_bar)
	private ProgressBar mProgressBar;

	private boolean mBillingEnabled = false;

	public static BuyAdFreeTeaserFragment newInstance(final String handlerKey) {
		final BuyAdFreeTeaserFragment fragment = new BuyAdFreeTeaserFragment();

		final Bundle bundle = new Bundle();
		bundle.putString(DATA_HANDLER_KEY, handlerKey);
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			mBillingEnabled = savedInstanceState.getBoolean(
					DATA_BILLING_ENABLED, false);
			Ln.d("savedInstanceState found. Billing enabled: %s",
					mBillingEnabled);
		}

		final Spanned html = Html.fromHtml(getActivity().getString(
				R.string.buy_ad_free_teaser_text));
		mTeaserText.setText(html);

		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				getContract().onBuyClicked();
			}
		});

		getContract().registerMessageHandler(
				getArguments().getString(DATA_HANDLER_KEY), this);
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putBoolean(DATA_BILLING_ENABLED, mBillingEnabled);
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.buy_ad_free_teaser, container, false);
	}

	public boolean isBillingEnabled() {
		return mBillingEnabled;
	}

	public void setBillingEnabled(final boolean billingEnabled) {
		mBillingEnabled = billingEnabled;

		Ln.d("Setting new billing enabled state to %s.", billingEnabled);
		mButton.setEnabled(billingEnabled);
		hideLoadingSpinner();
	}

	public void showError(final String error) {
		mErrorText.setText(error);
		mErrorText.setVisibility(View.VISIBLE);

		hideLoadingSpinner();
	}

	public void clearError() {
		mErrorText.setVisibility(View.GONE);
	}

	public void showLoadingSpinner() {
		mButton.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}

	public void hideLoadingSpinner() {
		mButton.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}

	@Override
	public void handleMessage(final Message message) {
		switch (message.what) {
		case MSG_BILLING_SUPPORTED:
			onBillingSupportChecked(true);
			break;
		case MSG_BILLING_UNSUPPORTED:
			onBillingSupportChecked(false);
			break;
		case MSG_PURCHASE_REVERTED:
			onBillingReverted();
			break;
		case MSG_PURCHASE_CANCELLED:
			onBillingCancelled();
			break;
		case MSG_PURCHASE_REQUESTED:
			onPurchaseRequested();
			break;
		case MSG_PURCHASE_ERROR:
			onPurchaseError();
			break;
		default:
			throw new UnsupportedOperationException("Unsupported message!");
		}

	}

	private void onPurchaseError() {
		showError(getString(R.string.error_iab_connection));
		setBillingEnabled(true);
	}

	private void onPurchaseRequested() {
		showLoadingSpinner();
	}

	private void onBillingCancelled() {
		setBillingEnabled(true);
		clearError();
	}

	private void onBillingReverted() {
		setBillingEnabled(true);
		showError(getString(R.string.error_iab_reverted));
	}

	private void onBillingSupportChecked(final boolean supported) {
		if (supported) {
			clearError();
		} else {
			showError(getString(R.string.error_no_iab));
		}
		setBillingEnabled(supported);
	}

	public static interface BuyAdFreeFragmentContract {
		void onBuyClicked();

		void onBuyError(ResponseCode response);

		void onBillingChecked(boolean supported);

		void onBuySuccess();

		void onBuyCancel();

		void onBuyRevert();

		void onPurchaseRequested();

		void registerMessageHandler(String key,
				net.rdrei.android.mediator.DelayedMessageQueue.Handler handler);
	}
}
