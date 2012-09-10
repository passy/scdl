package org.thoughtcrime.ssl.pinning;

/*
 * Copyright (c) 2011 Moxie Marlinspike <moxie@thoughtcrime.org>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * If you need this to be something other than GPL, send me an email.
 */

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import android.util.Log;

/**
 * A TrustManager implementation that enforces Certificate "pins." .
 * <p>
 * PinningTrustManager is layered on top of the system's default TrustManager,
 * such that the system continues to validate CA signatures for SSL connections
 * as usual. Additionally, however, PinningTrustManager will enforce certificate
 * constraints on the validated certificate chain. Specifically, it will ensure
 * that one of an arbitrary number of specified SubjectPublicKeyInfos appears
 * somewhere in the valid certificate chain.
 * </p>
 * <p>
 * To use:
 * 
 * <pre>
 * TrustManager[] trustManagers = new TrustManager[1];
 * trustManagers[0] = new PinningTrustManager(
 * 		new String[] { &quot;f30012bbc18c231ac1a44b788e410ce754182513&quot; });
 * 
 * SSLContext sslContext = SSLContext.getInstance(&quot;TLS&quot;);
 * sslContext.init(null, trustManagers, null);
 * 
 * HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(
 * 		&quot;https://encrypted.google.com/&quot;).openConnection();
 * urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
 * 
 * InputStream in = urlConnection.getInputStream();
 * </pre>
 * 
 * </p>
 * 
 * @author Moxie Marlinspike
 */

public class PinningTrustManagerImpl implements PinningTrustManager {
	private static final String TAG = "PinningTrustManager";

	private final TrustManager[] systemTrustManagers;
	private SystemKeyStore systemKeyStore;
	private final List<byte[]> pins = new LinkedList<byte[]>();

	/**
	 * Constructs a PinningTrustManager with a set of valid pins.
	 * 
	 * @param pins
	 *            A collection of pins to match a seen certificate chain
	 *            against. A pin is a hex-encoded hash of a X.509 certificate's
	 *            SubjectPublicKeyInfo. A pin can be generated using the
	 *            provided pin.py script: python ./pin.py certificate_file.pem
	 * 
	 * @throws CertificateException
	 *             If the system trust store can't be initialized.
	 */

	public PinningTrustManagerImpl(final String[] pins)
			throws CertificateException {
		this(pins, null);
	}

	public PinningTrustManagerImpl(final String[] pins,
			final SystemKeyStore systemKeyStore) throws CertificateException {
		systemTrustManagers = this.initializeSystemTrustManagers();
		if (systemKeyStore == null) {
			this.systemKeyStore = new SystemKeyStore();
		} else {
			this.systemKeyStore = systemKeyStore;
		}

		for (final String pin : pins) {
			this.pins.add(hexStringToByteArray(pin));
		}
	}

	private TrustManager[] initializeSystemTrustManagers()
			throws CertificateException {
		try {
			final TrustManagerFactory tmf = TrustManagerFactory
					.getInstance("X509");
			tmf.init((KeyStore) null);

			return tmf.getTrustManagers();
		} catch (final NoSuchAlgorithmException nsae) {
			throw new CertificateException(nsae);
		} catch (final KeyStoreException e) {
			throw new CertificateException(e);
		}
	}

	private boolean isValidPin(final X509Certificate certificate)
			throws CertificateException {
		try {
			final byte[] spki = certificate.getPublicKey().getEncoded();
			final MessageDigest digest = MessageDigest.getInstance("SHA1");
			final byte[] pin = digest.digest(spki);

			for (final byte[] validPin : pins) {
				if (Arrays.equals(validPin, pin)) {
					return true;
				}
			}

			return false;
		} catch (final NoSuchAlgorithmException nsae) {
			throw new CertificateException(nsae);
		}
	}

	@Override
	public void checkClientTrusted(final X509Certificate[] chain,
			final String authType) throws CertificateException {
		throw new CertificateException("Client certificates not supported!");
	}

	@Override
	public void checkServerTrusted(final X509Certificate[] chain,
			final String authType) throws CertificateException {

		Log.d(TAG, "Checking if server is trusted");
		for (final TrustManager systemTrustManager : systemTrustManagers) {
			((X509TrustManager) systemTrustManager).checkServerTrusted(chain,
					authType);
		}
		Log.d(TAG, "Getting trust root");
		final X509Certificate anchor = systemKeyStore.getTrustRoot(chain);

		Log.d(TAG, "checking certs for valid pin");
		for (final X509Certificate certificate : chain) {
			if (isValidPin(certificate)) {
				Log.d(TAG, "Success!");
				return;
			}
		}

		Log.d(TAG, "checking anchor for valid pin");
		if (anchor != null && isValidPin(anchor)) {
			Log.d(TAG, "Success!");
			return;
		}

		throw new CertificateException(
				"No valid Pins found in Certificate Chain!");
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	private byte[] hexStringToByteArray(final String s) {
		final int len = s.length();
		final byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

}