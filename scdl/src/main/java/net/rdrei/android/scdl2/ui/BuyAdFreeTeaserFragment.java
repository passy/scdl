package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ui.BuyAdFreeActivity.IabSetupFinishedEvent;
import net.rdrei.android.scdl2.ui.BuyAdFreeActivity.PurchaseAdfreeFinishedEvent;
import net.rdrei.android.scdl2.ui.BuyAdFreeActivity.PurchaseAdfreeRequestEvent;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class BuyAdFreeTeaserFragment extends RoboFragment {

	@InjectView(R.id.buy_ad_free_teaser_text)
	private TextView mTeaserText;

	@InjectView(R.id.error_message)
	private TextView mErrorText;

	@InjectView(R.id.buy_ad_free_teaser_button)
	private Button mButton;

	@InjectView(R.id.progress_bar)
	private ProgressBar mProgressBar;

	@Inject
	private Bus mBus;

	private boolean mInitialized = false;

	public static BuyAdFreeTeaserFragment newInstance() {
		final BuyAdFreeTeaserFragment fragment = new BuyAdFreeTeaserFragment();
		return fragment;
	}

	@Override
	public void onStart() {
		super.onResume();

		Ln.d("mBus @ fragment: %s", System.identityHashCode(mBus));
		mBus.register(this);
	}

	@Override
	public void onStop() {
		super.onPause();

		mBus.unregister(this);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final Spanned html = Html.fromHtml(getActivity().getString(
				R.string.buy_ad_free_teaser_text));
		mTeaserText.setText(html);

		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				requestPurchase();
			}
		});
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.buy_ad_free_teaser, container, false);
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

	private void setBillingEnabled(boolean billingEnabled) {
		Ln.d("Setting new billing enabled state to %s.", billingEnabled);
		mButton.setEnabled(billingEnabled);
	}

	private void requestPurchase() {
		Ln.d("Requesting purchase.");
		showLoadingSpinner();

		mBus.post(new PurchaseAdfreeRequestEvent());
	}

	@Subscribe
	public void onPurchasedFinished(PurchaseAdfreeFinishedEvent event) {
		Ln.d("Received PurchaseAdfreeFinishedEvent with success=%s",
				event.success);
		hideLoadingSpinner();
		clearError();

		if (!event.success) {
			showError(getString(R.string.error_iab_connection));
		}
	}

	@Subscribe
	public void onIabStateChange(IabSetupFinishedEvent event) {
		Ln.d("Received iabStateChange: %s", event);
		setBillingEnabled(event.enabled);
		hideLoadingSpinner();

		if (event.enabled && !mInitialized) {
			clearError();
			mInitialized = true;
		}
	}
}
