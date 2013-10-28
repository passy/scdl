package net.rdrei.android.scdl2.api.service;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

import net.rdrei.android.scdl2.ApplicationSoundcloudApiQueryFactory;
import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.SoundcloudApiQuery;
import net.rdrei.android.scdl2.api.SoundcloudApiService;
import net.rdrei.android.scdl2.api.URLWrapper;
import net.rdrei.android.scdl2.api.entity.PlaylistEntity;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import roboguice.util.Ln;

/**
 * Resolves a playlist by its ID to a PlaylistEntity
 */
public class PlaylistService extends SoundcloudApiService {
	private static final String RESOURCE_URL = "/playlists/";
	private static final TypeToken<PlaylistEntity> TYPE_TOKEN = new TypeToken<PlaylistEntity>() {
	};

	@Inject
	private ApplicationSoundcloudApiQueryFactory mPlaylistQueryFactory;

	/**
	 * Resolves a playlist based on its unique soundcloud ID.
	 *
	 * @param id Numeric ID of the playlist.
	 * @return
	 * @throws APIException
	 */
	public PlaylistEntity getPlaylist(final String id) throws APIException {
		final URLWrapper url;
		try {
			url = this.buildUrl(RESOURCE_URL + id);
		} catch (final MalformedURLException e) {
			Ln.e(e);
			throw new IllegalArgumentException("URL creation failed!", e);
		}

		return mPlaylistQueryFactory.create(url, SoundcloudApiQuery.HttpMethod.GET, TYPE_TOKEN)
				.execute(HttpURLConnection.HTTP_OK);
	}
}
