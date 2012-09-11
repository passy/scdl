package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BuyAdFreeThanksFragment extends Fragment {

	public static BuyAdFreeThanksFragment newInstance() {
		return new BuyAdFreeThanksFragment();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.buy_ad_free_thanks, container, false);
	}
}
