package net.rdrei.android.scdl2;

import net.rdrei.android.scdl2.api.SecureSoundcloudApiQueryImpl;
import net.rdrei.android.scdl2.api.SoundcloudApiQuery;
import net.rdrei.android.scdl2.api.SoundcloudApiQueryImpl;
import net.rdrei.android.scdl2.api.SoundcloudEntity;
import net.rdrei.android.scdl2.api.URLWrapper;
import net.rdrei.android.scdl2.api.SoundcloudApiQuery.HttpMethod;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Builds a new {@link SoundcloudApiQuery} based on the application settings.
 * 
 * @author pascal
 */
public class ApplicationSoundcloudApiQueryFactory {
	@Inject
	private ApplicationPreferences mPreferences;

	@Inject
	private Injector mInjector;

	public <T extends SoundcloudEntity> SoundcloudApiQuery<T> create(
			URLWrapper url, HttpMethod method, TypeToken<T> typeToken) {

		// Using very ugly, manual injection here, because I couldn't figure out
		// how to dynamically resolve the generic type using TypeLiterals.
		final SoundcloudApiQuery<T> instance;

		if (mPreferences.getSSLEnabled()) {
			instance = new SecureSoundcloudApiQueryImpl<T>(url, method,
					typeToken);
		} else {
			instance = new SoundcloudApiQueryImpl<T>(url, method, typeToken);
		}

		mInjector.injectMembers(instance);
		return instance;
	}
}