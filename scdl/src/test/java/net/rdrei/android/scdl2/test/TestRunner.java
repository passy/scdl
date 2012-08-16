package net.rdrei.android.scdl2.test;

import java.io.File;

import net.rdrei.android.scdl2.guice.SCDLModule;

import org.junit.runners.model.InitializationError;

import roboguice.RoboGuice;
import android.app.Application;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricConfig;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.bytecode.ClassHandler;
import com.xtremelabs.robolectric.bytecode.RobolectricClassLoader;
import com.xtremelabs.robolectric.util.DatabaseConfig.DatabaseMap;

public class TestRunner extends RobolectricTestRunner {

	public TestRunner(Class<?> arg0, ClassHandler arg1,
			RobolectricClassLoader arg2, RobolectricConfig arg3,
			DatabaseMap arg4) throws InitializationError {
		super(arg0, arg1, arg2, arg3, arg4);
	}

	public TestRunner(Class<?> testClass, ClassHandler classHandler,
			RobolectricClassLoader classLoader,
			RobolectricConfig robolectricConfig) throws InitializationError {
		super(testClass, classHandler, classLoader, robolectricConfig);
	}

	public TestRunner(Class<?> testClass, ClassHandler classHandler,
			RobolectricConfig robolectricConfig) throws InitializationError {
		super(testClass, classHandler, robolectricConfig);
	}

	public TestRunner(Class<?> testClass, File androidManifestPath,
			File resourceDirectory) throws InitializationError {
		super(testClass, androidManifestPath, resourceDirectory);
	}

	public TestRunner(Class<?> testClass, File androidProjectRoot)
			throws InitializationError {
		super(testClass, androidProjectRoot);
	}

	public TestRunner(Class<?> testClass, RobolectricConfig robolectricConfig,
			DatabaseMap databaseMap) throws InitializationError {
		super(testClass, robolectricConfig, databaseMap);
	}

	public TestRunner(Class<?> testClass, RobolectricConfig robolectricConfig)
			throws InitializationError {
		super(testClass, robolectricConfig);
	}

	public TestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}

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

		final Injector injector = TestRunner.getInjector();
		injector.injectMembers(instance);
	}

	public static Injector getInjector() {
		return RoboGuice.getInjector(Robolectric.application);
	}
}
