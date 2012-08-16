package net.rdrei.android.scdl2.api;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class URLConnectionFactoryImpl implements URLConnectionFactory {
	@Override
	public URLConnection create(URL url) throws IOException {
		return url.openConnection();
	}
}
