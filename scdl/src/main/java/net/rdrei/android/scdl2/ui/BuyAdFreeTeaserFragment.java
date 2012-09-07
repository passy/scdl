package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.R;
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
import android.widget.TextView;

public class BuyAdFreeTeaserFragment extends
		ContractFragment<BuyAdFreeTeaserFragment.BuyAdFreeFragmentContract> {

	@InjectView(R.id.buy_ad_free_teaser_text)
	private TextView mTeaserText;

	@InjectView(R.id.error_message)
	private TextView mErrorText;

	@InjectView(R.id.buy_ad_free_teaser_button)
	private Button mButton;

	private boolean mBillingEnabled = false;

	public static BuyAdFreeTeaserFragment newInstance() {
		final BuyAdFreeTeaserFragment fragment = new BuyAdFreeTeaserFragment();
		return fragment;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final Spanned html = Html.fromHtml(getActivity().getString(
				R.string.buy_ad_free_teaser_text));
		mTeaserText.setText(html);

		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				getContract().onBuyClicked();
			}
		});
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

		if (getActivity() != null) {
			// Update display.
			mErrorText.setVisibility(billingEnabled ? View.VISIBLE : View.GONE);
			mButton.setEnabled(billingEnabled);
		}
	}

	public static interface BuyAdFreeFragmentContract {
		public void onBuyClicked();

		public void onBuyError();

		public void onBillingChecked(boolean supported);
	}
}
