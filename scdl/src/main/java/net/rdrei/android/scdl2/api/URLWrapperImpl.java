package net.rdrei.android.scdl2.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class URLWrapperImpl implements URLWrapper {
	@Inject
	private URLConnectionFactory mUrlConnectionFactory;
	private final URL mUrl;

	@Inject
	public URLWrapperImpl(@Assisted final String spec)
			throws MalformedURLException {
		mUrl = new URL(spec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rdrei.android.wakimail.URLWrapper#openConnection()
	 */
	@Override
	public URLConnection openConnection() throws IOException {
		return mUrlConnectionFactory.create(mUrl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mUrl.toString();
	}

}