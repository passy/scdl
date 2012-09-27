package net.rdrei.android.scdl2;

import net.robotmedia.billing.BillingController;
import roboguice.util.Ln;
import roboguice.app.RoboApplication;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.StrictMode;

import net.rdrei.android.scdl2.Config;
import com.bugsense.trace.BugSenseHandler;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.google.inject.Inject;

public class SCDLApplication extends RoboApplication {
	@Inject
	private MixpanelAPI mMixpanel;

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		if (isDebuggable()) {
			Ln.d("Debug mode enabled.");
			BillingController.setDebug(true);

			// This is irrelevant on older platforms, anyway.
			if (Build.VERSION.SDK_INT > 11) {
				this.enableStrictMode();
			}
		} else {
			BugSenseHandler.setup(this, getString(R.string.bugsense_id));
		}
	}

	@Override
	public void onDestroy() {
		mMixpanel.flush();
		super.onDestroy();
	}

	public boolean isDebuggable() {
		final int applicationFlags = this.getApplicationInfo().flags;
		return (applicationFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
	}

	private void enableStrictMode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				// Only on 11+
				.detectLeakedClosableObjects().penaltyLog().build());
	}
}
