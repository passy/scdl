package net.rdrei.android.scdl2.guice;

import android.app.ActionBar;
import android.app.Activity;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ActionBarProvider implements Provider<ActionBar> {

	@Inject
	private Activity mActivity;

	@Override
	public ActionBar get() {
		return mActivity.getActionBar();
	}

}
