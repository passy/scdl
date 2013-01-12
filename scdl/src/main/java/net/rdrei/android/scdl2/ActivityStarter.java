package net.rdrei.android.scdl2;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import com.google.inject.Inject;

/**
 * Helper proxy that starts an activity either from an activity or a fragment.
 * 
 * @author pascal
 * 
 */
public class ActivityStarter {
	@Inject
	@Nullable
	private Fragment mFragment;

	@Inject
	@Nullable
	private Activity mActivity;

	// Injectable
	public ActivityStarter() {
	}
	
	public ActivityStarter(final Fragment fragment) {
		mFragment = fragment;
	}
	
	public ActivityStarter(final Activity activity) {
		mActivity = activity;
	}

	public void startActivityForResult(Intent intent, int requestCode) {
		if (mFragment != null) {
			mFragment.startActivityForResult(intent, requestCode);
		} else if (mActivity != null) {
			mActivity.startActivityForResult(intent, requestCode);
		} else {
			throw new NullPointerException(
					"Neither Fragment nor Activity could be injected.");
		}
	}
	
	public void setFragment(final Fragment fragment) {
		mFragment = fragment;
	}
	
	public void setActivity(final Activity activity) {
		mActivity = activity;
	}
}
