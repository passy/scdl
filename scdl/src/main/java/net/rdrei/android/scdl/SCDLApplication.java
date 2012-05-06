package net.rdrei.android.scdl;

import roboguice.util.Ln;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.StrictMode;

public class SCDLApplication extends Application {
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
			// This is irrelevant on older platforms, anyway.
			if (Build.VERSION.SDK_INT > 11) {
				this.enableStrictMode();
			}
		}
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
