package org.thoughtcrime.ssl.pinning;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import android.os.Build;
import android.util.Log;

public class SystemKeyStore {
	private static final String TAG = "SystemKeyStore";
	private final PKIXParameters parameters;
	private final CertPathValidator validator;
	private final CertificateFactory certificateFactory;

	public SystemKeyStore() throws CertificateException {
		try {
			parameters = this.getPkixParameters();
			certificateFactory = CertificateFactory.getInstance("X509");
			validator = CertPathValidator.getInstance("PKIX");
		} catch (final NoSuchAlgorithmException nsae) {
			throw new CertificateException(nsae);
		}
	}

	public X509Certificate getTrustRoot(final X509Certificate[] chain)
			throws CertificateException {
		try {
			final CertPath certPath = certificateFactory
					.generateCertPath(Arrays.asList(chain));
			final PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) validator
					.validate(certPath, parameters);

			if (result == null) {
				return null;
			} else {
				return result.getTrustAnchor().getTrustedCert();
			}
		} catch (final CertPathValidatorException e) {
			return null;
		} catch (final InvalidAlgorithmParameterException e) {
			throw new CertificateException(e);
		}
	}

	private PKIXParameters getPkixParameters() {
		try {
			final KeyStore trustStore = this.getTrustStore();
			final Set<TrustAnchor> trusted = new HashSet<TrustAnchor>();

			for (final Enumeration<String> aliases = trustStore.aliases(); aliases
					.hasMoreElements();) {
				final String alias = aliases.nextElement();
				final X509Certificate cert = (X509Certificate) trustStore
						.getCertificate(alias);

				if (cert != null) {
					trusted.add(new TrustAnchor(cert, null));
				}
			}

			final PKIXParameters parameters = new PKIXParameters(trusted);
			parameters.setRevocationEnabled(false);

			return parameters;
		} catch (final InvalidAlgorithmParameterException e) {
			throw new AssertionError(e);
		} catch (final KeyStoreException e) {
			throw new AssertionError(e);
		}
	}

	protected KeyStore getTrustStore() {
		try {
			KeyStore trustStore;

			Log.d(TAG, "Beginning keystore load");
			if (Build.VERSION.SDK_INT >= 14) {
				trustStore = KeyStore.getInstance("AndroidCAStore");
				trustStore.load(null, null);
			} else {
				trustStore = KeyStore.getInstance("BKS");
				trustStore.load(new BufferedInputStream(new FileInputStream(
						getTrustStorePath())), getTrustStorePassword()
						.toCharArray());
			}
			Log.d(TAG, "Loaded keystore");

			return trustStore;
		} catch (final NoSuchAlgorithmException nsae) {
			throw new AssertionError(nsae);
		} catch (final KeyStoreException e) {
			throw new AssertionError(e);
		} catch (final CertificateException e) {
			throw new AssertionError(e);
		} catch (final FileNotFoundException e) {
			throw new AssertionError(e);
		} catch (final IOException e) {
			throw new AssertionError(e);
		}
	}

	protected String getTrustStorePath() {
		String path = System.getProperty("javax.net.ssl.trustStore");

		if (path == null) {
			path = System.getProperty("java.home") + File.separator + "etc"
					+ File.separator + "security" + File.separator
					+ "cacerts.bks";
		}

		return path;
	}

	protected String getTrustStorePassword() {
		String password = System
				.getProperty("javax.net.ssl.trustStorePassword");

		if (password == null) {
			password = "changeit";
		}

		return password;
	}
}
