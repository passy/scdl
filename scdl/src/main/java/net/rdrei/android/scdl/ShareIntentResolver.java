package net.rdrei.android.scdl;

import net.rdrei.android.scdl.api.APIException;
import net.rdrei.android.scdl.api.ServiceManager;
import net.rdrei.android.scdl.api.entity.ResolveEntity;
import net.rdrei.android.scdl.api.service.ResolveService;
import roboguice.util.Ln;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.google.inject.Inject;

public class ShareIntentResolver {

	@Inject
	private Activity mActivity;

	@Inject
	private ServiceManager mServiceManager;

	public static class ShareIntentResolverException extends APIException {
		private static final long serialVersionUID = 1L;

		public ShareIntentResolverException(String detailMessage) {
			super(detailMessage);
		}

		public ShareIntentResolverException(String detailMessage,
				Throwable throwable) {
			super(detailMessage, throwable);
		}
	}

	/**
	 * Resolves the Intent to a canonical URL for the track or raise a
	 * {@link ShareIntentResolverException}.
	 * 
	 * <b>This is blocking the current thread!</b>
	 * 
	 * @return Canonical Track URL
	 * @throws ShareIntentResolverException
	 */
	public String resolve() throws ShareIntentResolverException {
		final Intent intent = mActivity.getIntent();

		// Check if we have a URL as extra.
		Uri uri = null;
		final String url = intent.getStringExtra(Intent.EXTRA_TEXT);

		if (url != null) {
			uri = Uri.parse(url);
		}

		if (uri == null) {
			uri = intent.getData();
		}

		if (isValidUri(uri)) {
			try {
				return resolveUri(uri);
			} catch (APIException e) {
				Ln.e(e);
				throw new ShareIntentResolverException(
						"Could not resolve URL.", e);
			}
		}

		throw new ShareIntentResolverException(
				"Given URL is not a valid soundcloud URL.");
	}

	protected boolean isValidUri(Uri uri) {
		if (uri == null) {
			return false;
		}

		final String scheme = uri.getScheme();
		final String host = uri.getHost();
		final String[] allowedHosts = { "soundcloud.com", "snd.sc" };

		if (!scheme.equals("http") && !scheme.equals("https")) {
			return false;
		}

		for (String allowedHost : allowedHosts) {
			if (host.equals(allowedHost)) {
				return true;
			}
		}

		return false;
	}

	protected String resolveUri(Uri uri) throws APIException {
		ResolveService service = mServiceManager.resolveService();
		ResolveEntity entity = service.resolve(uri.toString());

		return entity.getLocation();
	}
}
