package net.rdrei.android.scdl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static final Pattern URL_ID_PATTERN = Pattern
			.compile("^https://api.soundcloud.com/tracks/(\\d+)\\.json");
	
	private static final String[] ALLOWED_HOSTS = { "soundcloud.com", "snd.sc",
		"m.soundcloud.com" };

	public static class ShareIntentResolverException extends APIException {
		private static final long serialVersionUID = 1L;

		public ShareIntentResolverException(String detailMessage) {
			super(detailMessage, -1);
		}

		public ShareIntentResolverException(String detailMessage,
				Throwable throwable) {
			super(detailMessage, throwable, -1);
		}
	}

	public static class UnsupportedUrlException extends
			ShareIntentResolverException {
		public UnsupportedUrlException(String detailMessage) {
			super(detailMessage);
		}

		private static final long serialVersionUID = 1L;
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

		throw new UnsupportedUrlException(
				"Given URL is not a valid soundcloud URL.");
	}

	/**
	 * Same as resolve(), but returns the ID only.
	 * 
	 * @return ID as a String (even though it's a number, but who knows if that
	 *         changes)
	 * @throws ShareIntentResolverException
	 */
	public String resolveId() throws ShareIntentResolverException {
		final String url = resolve();
		final Matcher matcher = URL_ID_PATTERN.matcher(url);

		if (matcher.find()) {
			return matcher.group(1);
		}

		throw new ShareIntentResolverException("Could not parse ID from URL.");
	}

	protected boolean isValidUri(Uri uri) {
		if (uri == null) {
			return false;
		}

		final String scheme = uri.getScheme();
		final String host = uri.getHost();

		if (!scheme.equals("http") && !scheme.equals("https")) {
			return false;
		}

		for (String allowedHost : ALLOWED_HOSTS) {
			if (host.equals(allowedHost)) {
				return true;
			}
		}

		return false;
	}

	protected String resolveUri(Uri uri) throws APIException {
		final ResolveService service = mServiceManager.resolveService();
		final ResolveEntity entity = service.resolve(uri.toString());

		return entity.getLocation();
	}

}
