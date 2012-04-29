package net.rdrei.android.scdl.api;

import net.rdrei.android.scdl.api.service.ResolveService;
import net.rdrei.android.scdl.api.service.TrackService;
import roboguice.RoboGuice;
import android.app.Application;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * Singleton managing creation of API services and setting them up with default
 * parameters, in case need to access APIs with authentication requirements.
 * 
 * @author pascal
 * 
 */
@Singleton
public class ServiceManager {
	private final Injector mInjector;
	
	@Inject
	public ServiceManager(Application application) {
		mInjector = RoboGuice.getBaseApplicationInjector(application);
	}
	
	
	private void setupService(SoundcloudApiService service) {
		// No-op without authentication.
	}
	
	public TrackService trackService() {
		TrackService service = mInjector.getInstance(TrackService.class);
		setupService(service);
		return service;
	}
	
	public ResolveService resolveService() {
		ResolveService service = mInjector.getInstance(ResolveService.class);
		setupService(service);
		return service;
		
	}
}
