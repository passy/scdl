package net.rdrei.android.scdl2.api.service;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import net.rdrei.android.scdl2.ApplicationSoundcloudApiQueryFactory;
import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.SoundcloudApiQuery.HttpMethod;
import net.rdrei.android.scdl2.api.SoundcloudApiService;
import net.rdrei.android.scdl2.api.URLWrapper;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import roboguice.util.Ln;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

public class TrackService extends SoundcloudApiService {

	private static final String RESOURCE_URL = "/tracks/";
	private static final TypeToken<TrackEntity> TYPE_TOKEN = new TypeToken<TrackEntity>() {
	};

	@Inject
	private ApplicationSoundcloudApiQueryFactory mTrackQueryFactory;

	/**
	 * Resolves a track based on its unique soundcloud ID.
	 * 
	 * @param id
	 * @return
	 * @throws APIException
	 */
	public TrackEntity getTrack(final String id) throws APIException {
		// I'm not sure if escaping here makes any sense.
		final URLWrapper url;
		try {
			url = this.buildUrl(RESOURCE_URL + id);
		} catch (final MalformedURLException e) {
			Ln.e(e);
			throw new IllegalArgumentException("URL creation failed!", e);
		}

		return mTrackQueryFactory.create(url, HttpMethod.GET, TYPE_TOKEN)
				.execute(HttpURLConnection.HTTP_OK);
	}
}
