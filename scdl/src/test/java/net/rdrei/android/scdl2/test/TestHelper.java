package net.rdrei.android.scdl2.test;

import android.app.Application;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import net.rdrei.android.scdl2.guice.SCDLModule;

import org.robolectric.Robolectric;

import roboguice.RoboGuice;

public class TestHelper  {
	public static void overridenInjector(Object instance, AbstractModule module) {
		final Application app = Robolectric.application;
		
		// Allow overriding of integrated classes like Activity
		Module moduleOverride = Modules.override(
				RoboGuice.newDefaultRoboModule(app)).with(module);
		// Also allow overriding custom bindings like URLWrapper
		moduleOverride = Modules.override(
				new SCDLModule()).with(moduleOverride);
		
		RoboGuice.setBaseApplicationInjector(app, RoboGuice.DEFAULT_STAGE,
				moduleOverride);

		final Injector injector = TestHelper.getInjector();
		injector.injectMembers(instance);
	}

	public static Injector getInjector() {
		return RoboGuice.getInjector(Robolectric.application);
	}
}
