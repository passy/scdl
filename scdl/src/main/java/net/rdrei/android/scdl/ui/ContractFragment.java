package net.rdrei.android.scdl.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;

/* Base fragment to ensure the parent activity implements a contract interface. */
public abstract class ContractFragment<T> extends Fragment {
	private T mContract;

	/**
	 * Suppressing unchecked warning here, because we do actually check for the class cast exception. So we're safe here.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onAttach(Activity activity) {
		try {
			mContract = (T) activity;
		} catch (ClassCastException e) {
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
