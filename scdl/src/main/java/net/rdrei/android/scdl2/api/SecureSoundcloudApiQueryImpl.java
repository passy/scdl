package net.rdrei.android.scdl2.api;

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.thoughtcrime.ssl.pinning.PinningTrustManager;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

public class SecureSoundcloudApiQueryImpl<T extends SoundcloudEntity> extends
		AbstractSoundcloudApiQueryImpl<T> {

	@Inject
	private Injector mInjector;

	@Inject
	public SecureSoundcloudApiQueryImpl(@Assisted URLWrapper url,
			@Assisted HttpMethod method, @Assisted TypeToken<T> typeToken) {
		super(url, method, typeToken);
	}

	@Override
	protected void setupPostRequest(HttpRequest request) {
		pinSSLConnection(request);
	}

	/**
	 * Applies the pinning SSL manager to the connection.
	 * 
	 * @param connection
	 */
	private void pinSSLConnection(HttpURLConnection connection) {
		if (!(connection instanceof HttpsURLConnection)) {
			throw new IllegalStateException("Not an SSL connection!");
		}

		final TrustManager[] trustManagers = getPinningTrustManagers();
		final SSLContext sslContext;

		try {
			sslContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			// Again, should not happen if I didn't type it wrong.
			throw new IllegalArgumentException(e);
		}

		try {
			sslContext.init(null, trustManagers, null);
		} catch (KeyManagementException e) {
			throw new IllegalStateException(e);
		}

		((HttpsURLConnection) connection).setSSLSocketFactory(sslContext
				.getSocketFactory());
	}

	private void pinSSLConnection(HttpRequest request) {
		request.applyTrustManager(getPinningTrustManagers());
	}

	/**
	 * @return An array containing an instance of the PinningTrustManager.
	 */
	private TrustManager[] getPinningTrustManagers() {
		final TrustManager[] trustManagers = new TrustManager[1];
		trustManagers[0] = mInjector.getInstance(PinningTrustManager.class);
		return trustManagers;
	}

	@Override
	protected void setupGetConnection(URLConnection connection) {
		pinSSLConnection((HttpsURLConnection) connection);
	}
}
