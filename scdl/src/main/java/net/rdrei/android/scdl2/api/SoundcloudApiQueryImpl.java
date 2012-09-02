package net.rdrei.android.scdl2.api;

import java.net.URLConnection;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class SoundcloudApiQueryImpl<T extends SoundcloudEntity> extends AbstractSoundcloudApiQueryImpl<T> {
	@Inject
	public SoundcloudApiQueryImpl(@Assisted URLWrapper url,
			@Assisted HttpMethod method, @Assisted TypeToken<T> typeToken) {
		super(url, method, typeToken);
	}

	@Override
	protected void setupPostRequest(HttpRequest request) {
		// No-op
	}

	@Override
	protected void setupGetConnection(URLConnection connection) {
		// No-op
	}
}
