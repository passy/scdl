package net.rdrei.android.scdl2.api;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.api.service.DownloadService;
import net.rdrei.android.scdl2.api.service.ResolveService;
import net.rdrei.android.scdl2.api.service.TrackService;
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
	private ApplicationPreferences mPreferences;
	
	@Inject
	public ServiceManager(Application application) {
		mInjector = RoboGuice.getBaseApplicationInjector(application);
	}
	
	private void setupService(SoundcloudApiService service) {
		service.setUseSSL(mPreferences.getSSLEnabled());
	}
	
	public TrackService trackService() {
		final TrackService service = mInjector.getInstance(TrackService.class);
		setupService(service);
		return service;
	}
	
	public ResolveService resolveService() {
		final ResolveService service = mInjector.getInstance(ResolveService.class);
		setupService(service);
		return service;
	}

	public DownloadService downloadService() {
		final DownloadService service = mInjector.getInstance(DownloadService.class);
		setupService(service);
		return service;
	}
}
