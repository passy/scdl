package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.R;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BuyAdFreeTeaserFragment extends
		ContractFragment<BuyAdFreeTeaserFragment.BuyAdFreeFragmentContract> {

	@InjectView(R.id.buy_ad_teaser_text)
	private TextView mTeaserText;

	public static BuyAdFreeTeaserFragment newInstance() {
		final BuyAdFreeTeaserFragment fragment = new BuyAdFreeTeaserFragment();
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final Spanned html = Html.fromHtml(getActivity().getString(
				R.string.buy_ad_free_teaser_text));
		mTeaserText.setText(html);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.buy_ad_free_teaser, container, false);
	}

	public static interface BuyAdFreeFragmentContract {
		public void onBuyClicked();

		public void onBuyError();
	}
}
