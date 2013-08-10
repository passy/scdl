package net.rdrei.android.scdl2;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.StrictMode;

import roboguice.util.Ln;

public class SCDLApplication extends Application {
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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void enableStrictMode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				// Only on 11+
				.detectLeakedClosableObjects().penaltyLog().build());
	}
}