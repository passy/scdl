package net.rdrei.android.scdl.api.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import javax.net.ssl.HttpsURLConnection;

import roboguice.util.Ln;

import net.rdrei.android.scdl.api.APIException;
import net.rdrei.android.scdl.api.SoundcloudApiService;
import net.rdrei.android.scdl.api.URLWrapper;
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
	public Uri resolveUri(String id) throws APIException {
		final String resource = String.format(RESOURCE_URL, id);
		final URLWrapper url;

		try {
			url = buildUrl(resource);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}

		HttpsURLConnection connection = null;
		Ln.d("Opening connection at %s.", url.toString());
		try {
			connection = (HttpsURLConnection) url.openConnection();
		} catch (IOException e) {
			throw new APIException(e, -1);
		}

		connection.setInstanceFollowRedirects(false);
		final int code;

		try {
			code = connection.getResponseCode();
		} catch (IOException e) {
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
