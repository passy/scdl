package net.rdrei.android.scdl2;

import android.app.Activity;

import roboguice.fragment.RoboFragment;

/* Base fragment to ensure the parent activity implements a contract interface. */
public abstract class RoboContractFragment<T> extends RoboFragment {
	private T mContract;

	@SuppressWarnings("unchecked")
	@Override
	public void onAttach(Activity activity) {
		try {
			mContract = (T) activity;
		} catch (ClassCastException e) {
			throw new IllegalStateException(activity.getClass()
					.getSimpleName() + " does not implement " +
					((Object)this).getClass().getSimpleName() +
					"'s contract interface.", e);
		}
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mContract = null;
	}

	public final T getContract() {
		return mContract;
	}
}
