package net.rdrei.android.scdl.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import net.rdrei.android.scdl.api.URLConnectionFactory;

public class FakeURLConnectionFactoryImpl implements URLConnectionFactory {

	private final String mStreamFixture;
	private Integer mResponseCode;
	protected URL mUrl;

	/**
	 * Creates a new FakeURL factory that returns the given resource fixture as
	 * stream instead of actually opening a network connection.
	 * 
	 * @param streamFixture
	 */
	public FakeURLConnectionFactoryImpl(String streamFixture) {
		mStreamFixture = streamFixture;
	}

	@Override
	public URLConnection create(URL url) {
		mUrl = url;
		final FakeURLConnection connection = new FakeURLConnection(url,
				mStreamFixture);

		if (mResponseCode != null) {
			connection.setResponseCode(mResponseCode);
		}

		return connection;
	}

	public URL getURL() {
		return mUrl;
	}

	public void setResponseCode(int i) {
		mResponseCode = i;
	}
}

/*
 * Fakes an HTTP connection to always return the messages HTML file after a
 * successful login without actual network connection.
 * 
 * @author pascal
 */
class FakeURLConnection extends HttpsURLConnection {

	private final String mStreamFixture;
	private Integer mResponseCode;
	private final OutputStream mOutputStream;

	protected FakeURLConnection(URL url, String streamFixture) {
		super(url);
		mStreamFixture = streamFixture;
		mOutputStream = new ByteArrayOutputStream();
	}

	@Override
	public void connect() throws IOException {
		throw new IllegalArgumentException("Not implemented!");
	}

	public InputStream getInputStream() throws IOException {
		// This is were the magic happens.
		final InputStream stream = this.getClass().getResourceAsStream(
				mStreamFixture);

		if (stream == null) {
			throw new IOException("Could not find test fixture "
					+ mStreamFixture);
		}

		return stream;
	}

	@Override
	public void disconnect() {
	}

	@Override
	public boolean usingProxy() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.HttpURLConnection#getResponseCode()
	 */
	@Override
	public int getResponseCode() throws IOException {
		if (mResponseCode != null) {
			return mResponseCode;
		} else {
			return super.getResponseCode();
		}
	}

	public void setResponseCode(int responseCode) {
		mResponseCode = responseCode;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return mOutputStream;
	}

	@Override
	public String getCipherSuite() {
		return null;
	}

	@Override
	public Certificate[] getLocalCertificates() {
		return null;
	}

	@Override
	public Certificate[] getServerCertificates()
			throws SSLPeerUnverifiedException {
		return null;
	}
}