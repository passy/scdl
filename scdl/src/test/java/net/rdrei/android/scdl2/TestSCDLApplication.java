package net.rdrei.android.scdl2;

import android.app.Application;

import org.robolectric.Robolectric;
import org.robolectric.TestLifecycleApplication;
import org.robolectric.shadows.ShadowApplication;

import java.lang.reflect.Method;

@SuppressWarnings("UnusedDeclaration")
public class TestSCDLApplication extends Application implements TestLifecycleApplication {
	@Override
	public void beforeTest(Method method) {
		// GMS tries to bind to non-existent services, causing NPEs all over the place.
		ShadowApplication shadowApplication = Robolectric.shadowOf(Robolectric.application);
		shadowApplication.declareActionUnbindable("com.google.android.gms.analytics.service.START");
	}

	@Override
	public void prepareTest(Object test) {

	}

	@Override
	public void afterTest(Method method) {

	}
}
