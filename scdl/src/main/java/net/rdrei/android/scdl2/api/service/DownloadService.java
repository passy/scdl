package net.rdrei.android.scdl2.api.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.SoundcloudApiService;
import net.rdrei.android.scdl2.api.URLWrapper;
import roboguice.util.Ln;
import android.net.Uri;

public class DownloadService extends SoundcloudApiService {

	private static final String RESOURCE_URL = "/tracks/%s/download";

	/**
	 * Resolve
	 * 
	 * @param source
	 * @return
	 * @throws APIException
	 */
	public Uri resolveUri(final String id) throws APIException {
		final String resource = String.format(RESOURCE_URL, id);
		final URLWrapper url;

		try {
			url = buildUrl(resource);
		} catch (final MalformedURLException e) {
			throw new IllegalStateException(e);
		}

		HttpURLConnection connection = null;
		Ln.d("Opening connection at %s.", url.toString());
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (final IOException e) {
			throw new APIException(e, -1);
		}

		connection.setInstanceFollowRedirects(false);
		final int code;

		try {
			code = connection.getResponseCode();
		} catch (final IOException e) {
			connection.disconnect();
			throw new APIException(e, -1);
		}

		if (code != HttpURLConnection.HTTP_MOVED_TEMP) {
			connection.disconnect();
			throw new APIException("Download is not available.", code);
		}

		final Uri uri = Uri.parse(connection.getHeaderField("Location"));
		connection.disconnect();
		return uri;
	}
}
