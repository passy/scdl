package net.rdrei.android.scdl2.api;

import java.net.URLConnection;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class SoundcloudApiQueryImpl<T extends SoundcloudEntity> extends
		AbstractSoundcloudApiQueryImpl<T> {
	@Inject
	public SoundcloudApiQueryImpl(@Assisted final URLWrapper url,
			@Assisted final HttpMethod method,
			@Assisted final TypeToken<T> typeToken) {
		super(url, method, typeToken);
	}

	@Override
	protected void setupPostRequest(final HttpRequest request) {
		// No-op
	}

	@Override
	protected void setupGetConnection(final URLConnection connection) {
		// No-op
	}
}
