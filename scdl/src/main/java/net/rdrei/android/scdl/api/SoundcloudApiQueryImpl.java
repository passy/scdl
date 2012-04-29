package net.rdrei.android.scdl.api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.thoughtcrime.ssl.pinning.PinningTrustManager;

import roboguice.util.Ln;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.SendCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

public class SoundcloudApiQueryImpl<T extends SoundcloudEntity> implements SoundcloudApiQuery<T> {

	private static final String NEWLINE_FALLBACK = "\n";
	private static final String GZIP = "gzip";

	private final HttpMethod mMethod;
	private final URLWrapper mUrl;
	private final TypeToken<T> mTypeToken;
	private final Map<String, String> mPostParameters = new HashMap<String, String>();
	private final Map<String, File> mPartParameters = new HashMap<String, File>();
	private SendCallback mSendCallback;

	@Inject
	private Injector mInjector;

	@Override
	public void setSendCallback(SendCallback sendCallback) {
		mSendCallback = sendCallback;
	}

	@Inject
	public SoundcloudApiQueryImpl(@Assisted URLWrapper url,
			@Assisted HttpMethod method, @Assisted TypeToken<T> typeToken) {
		super();

		mMethod = method;
		mUrl = url;
		mTypeToken = typeToken;
	}

	@Override
	public SoundcloudApiQuery<T> addPostParameter(String key, String value) {
		mPostParameters.put(key, value);

		return this;
	}

	@Override
	public SoundcloudApiQuery<T> addPartParameters(String key, File file) {
		mPartParameters.put(key, file);

		return this;
	}

	@Override
	public T execute(int expected) throws APIException {
		final HttpURLConnection response;

		switch (mMethod) {
		case GET:
			response = this.executeGet();
			break;
		case POST:
			response = this.executePost();
			break;
		default:
			throw new IllegalArgumentException("Method not implemented.");
		}

		int code;
		try {
			code = response.getResponseCode();
		} catch (IOException e) {
			// I consider this a bug. A 401 without auth challenge causes
			// an IOException, while it's perfectly valid in terms of RFC 2616.
			if (e.getMessage().equals(
					"Received authentication challenge is null")) {
				code = HttpURLConnection.HTTP_UNAUTHORIZED;
			} else {
				throw new APIException(e, -1);
			}
		}

		if (code != expected) {
			throw new APIException("HTTP request failed.", code);
		}

		final InputStream responseStream;
		try {
			responseStream = getWrappedResponseStream(response);
		} catch (IOException e) {
			throw new APIException(e, -1);
		}

		final String responseStr = convertStreamToString(responseStream);
		final Type entityType = mTypeToken.getType();

		return (new Gson()).fromJson(responseStr, entityType);
	}

	/**
	 * Executes the POST request. Instead of using the bare HttpURLConnection
	 * API, we use our modified HttpRequest client to be able to upload with
	 * multipart and other cool things.
	 * 
	 * @return
	 * @throws APIException
	 */
	protected HttpURLConnection executePost() throws APIException {
		Ln.d("Executing POST against URL " + mUrl.toString());
		final HttpRequest request = HttpRequest.post(mUrl);
		pinSSLConnection(request);

		if (mSendCallback != null) {
			request.setSendCallback(mSendCallback);
		}

		if (mPartParameters.size() > 0) {
			applyPostParametersAsPart(request);
			applyPartParameters(request);
		} else {
			applyPostParameters(request);
		}

		try {
			request.closeOutput();
		} catch (IOException e) {
			throw new APIException(e, -1);
		}
		return request.getConnection();
	}

	private void applyPostParametersAsPart(HttpRequest request) {
		final Iterator<Entry<String, String>> iterator = mPostParameters
				.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			Ln.d("Applying POST parameter as multipart: %s", entry.getKey());
			request.part(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Add multipart parameters to the given HttpRequest.
	 * 
	 * @param request
	 */
	private void applyPartParameters(HttpRequest request) {
		final Iterator<Entry<String, File>> iterator = mPartParameters
				.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, File> entry = iterator.next();
			Ln.d("Applying multipart parameter %s.", entry.getKey());
			File file = entry.getValue();
			request.part(entry.getKey(), file.getName(), file,
					"application/octet-stream");
		}
	}

	/**
	 * Add POST parameters to the given HttpRequest.
	 * 
	 * @param request
	 */
	private void applyPostParameters(HttpRequest request) {
		final Iterator<Entry<String, String>> iterator = mPostParameters
				.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			StringBuffer buf = new StringBuffer();
			Ln.d("Applying POST parameters: %s", entry.getKey());
			buf.append(entry.getKey());
			buf.append('=');
			buf.append(entry.getValue());
			request.send(buf.toString());
		}
	}

	/**
	 * Executes a GET request and returns the expected Entity.
	 * 
	 * @param parameters
	 * @param expected
	 * @return
	 * @throws APIException
	 */
	protected HttpURLConnection executeGet() throws APIException {
		Ln.d("Executing GET against URL " + mUrl.toString());
		try {
			final HttpsURLConnection connection = (HttpsURLConnection) mUrl
					.openConnection();
			pinSSLConnection(connection);
			return connection;
		} catch (IOException e) {
			throw new APIException(e, -1);
		}
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

	/**
	 * @return An array containing an instance of the PinningTrustManager.
	 */
	private TrustManager[] getPinningTrustManagers() {
		final TrustManager[] trustManagers = new TrustManager[1];
		trustManagers[0] = mInjector.getInstance(PinningTrustManager.class);
		return trustManagers;
	}

	private void pinSSLConnection(HttpRequest request) {
		request.applyTrustManager(getPinningTrustManagers());
	}

	/**
	 * Properly wrap the stream accounting for GZIP.
	 * 
	 * @param is
	 *            Stream to wrap.
	 * @param gzip
	 *            Whether or not to include a GZIP wrapper.
	 * @return Wrapped stream.
	 * @throws IOException
	 */
	protected static InputStream getWrappedInputStream(InputStream is,
			boolean gzip) throws IOException {
		if (gzip) {
			return new BufferedInputStream(new GZIPInputStream(is));
		} else {
			return new BufferedInputStream(is);
		}
	}

	protected static InputStream getWrappedResponseStream(
			HttpURLConnection response) throws IOException {
		return getWrappedInputStream(response.getInputStream(),
				GZIP.equalsIgnoreCase(response.getContentEncoding()));
	}

	/**
	 * Read an entire stream to end and assemble in a string.
	 * 
	 * @param is
	 *            Stream to read.
	 * @return Entire stream contents.
	 */
	protected static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * returns null, which means there's no more data to read. Each line
		 * will appended to a StringBuilder and returned as String.
		 */
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				is));
		final StringBuilder sb = new StringBuilder();

		String line = null;
		String newline = System.getProperty("line.seperator");
		if (newline == null) {
			newline = NEWLINE_FALLBACK;
		}
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append(newline);
			}
		} catch (IOException e) {
			Ln.e(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Ln.e(e);
			}
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return "SoundcloudApiQuery [method=" + mMethod + ", url=" + mUrl + "]";
	}
}