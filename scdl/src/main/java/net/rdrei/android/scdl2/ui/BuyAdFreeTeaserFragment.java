package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BuyAdFreeTeaserFragment extends
		ContractFragment<BuyAdFreeTeaserFragment.BuyAdFreeFragmentContract> {

	public static BuyAdFreeTeaserFragment newInstance() {
		final BuyAdFreeTeaserFragment fragment = new BuyAdFreeTeaserFragment();
		return fragment;
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
