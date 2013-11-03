package net.rdrei.android.scdl2.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.rdrei.android.scdl2.R;

public class SimpleLoadingFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.loading_fragment, container, false);
	}

	public static SimpleLoadingFragment newInstance() {
		return new SimpleLoadingFragment();
	}
}
