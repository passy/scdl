package net.rdrei.android.scdl2.test;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.thoughtcrime.ssl.pinning.PinningTrustManager;

public class TrustManagerStub implements PinningTrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {

	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}