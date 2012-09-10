package net.rdrei.android.scdl2.ui;

import roboguice.fragment.RoboFragment;
import android.app.Activity;

/* Base fragment to ensure the parent activity implements a contract interface. */
public abstract class ContractFragment<T> extends RoboFragment {
	private T mContract;

	/**
	 * Suppressing unchecked warning here, because we do actually check for the
	 * class cast exception. So we're safe here.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onAttach(final Activity activity) {
		try {
			mContract = (T) activity;
		} catch (final ClassCastException e) {
			throw new IllegalStateException(activity.getClass().getSimpleName()
					+ " does not implement " + getClass().getSimpleName()
					+ "'s contract interface.", e);
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
