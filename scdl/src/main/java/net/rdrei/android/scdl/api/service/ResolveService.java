package net.rdrei.android.scdl.api.service;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import roboguice.util.Ln;

import net.rdrei.android.scdl.api.APIException;
import net.rdrei.android.scdl.api.SoundcloudApiQueryFactory;
import net.rdrei.android.scdl.api.SoundcloudApiService;
import net.rdrei.android.scdl.api.URLWrapper;
import net.rdrei.android.scdl.api.SoundcloudApiQuery.HttpMethod;
import net.rdrei.android.scdl.api.entity.ResolveEntity;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

public class ResolveService extends SoundcloudApiService {

	private static final String RESOURCE_URL = "/resolve.json";
	private static final String URL_PARAMETER = "url";

	private static final TypeToken<ResolveEntity> TYPE_TOKEN = new TypeToken<ResolveEntity>() {
	};

	@Inject
	private SoundcloudApiQueryFactory<ResolveEntity> mResolveQueryFactory;

	public ResolveEntity resolve(String url) throws APIException {
		final Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(URL_PARAMETER, url);

		return executeGet(parameters);
	}

	/**
	 * Sends the GET request to the resource and tries converting it to the
	 * requested entity from JSON.
	 * 
	 * @param parameters
	 * @throws APIException
	 */
	protected ResolveEntity executeGet(Map<String, String> parameters)
			throws APIException {
		final URLWrapper url;
		try {
			url = buildUrl(RESOURCE_URL, parameters);
		} catch (MalformedURLException e) {
			// This is okay to crash the app, because this error can only be
			// caused by static values.
			Ln.e(e);
			throw new IllegalArgumentException(e);
		}

		return mResolveQueryFactory.create(url, HttpMethod.GET, TYPE_TOKEN)
				.execute(HttpURLConnection.HTTP_MOVED_TEMP);
	}
}
