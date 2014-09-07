package net.rdrei.android.scdl2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.inject.Inject;
import com.gu.option.Option;

import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.MediaDownloadType;
import net.rdrei.android.scdl2.api.PendingDownload;
import net.rdrei.android.scdl2.api.ServiceManager;
import net.rdrei.android.scdl2.api.entity.ResolveEntity;
import net.rdrei.android.scdl2.api.service.ResolveService;

import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShareIntentResolver {
	@Inject
	private Activity mActivity;

	@Inject
	private ServiceManager mServiceManager;

	public static final Pattern URL_ID_PATTERN = Pattern.compile(
			"^https?://api.soundcloud.com/tracks/(\\d+)\\.json");

	public static final Pattern URL_PLAYLIST_PATTERN = Pattern.compile(
			"^https?://api.soundcloud.com/playlists/(\\d+)\\.json");

	public static final String SOUNDCLOUD_URI_SCHEME = "soundcloud";
	public static final String SOUNDCLOUD_URI_HOST = "tracks";
	public static final String SOUNDCLOUD_URI_PATH_RE = "^/\\d+$";

	private static final String[] ALLOWED_HOSTS = {"soundcloud.com", "snd.sc", "m.soundcloud.com"};

	public static class ShareIntentResolverException extends APIException {
		private static final long serialVersionUID = 1L;

		public ShareIntentResolverException(final String detailMessage) {
			super(detailMessage, -1);
		}

		public ShareIntentResolverException(final String detailMessage, final Throwable throwable) {
			super(detailMessage, throwable, -1);
		}
	}

	public static class UnsupportedUrlException extends ShareIntentResolverException {
		public UnsupportedUrlException(final String detailMessage) {
			super(detailMessage);
		}

		private static final long serialVersionUID = 1L;
	}

	public static class TrackNotFoundException extends ShareIntentResolverException {

		public TrackNotFoundException(final String detailMessage, final Throwable throwable) {
			super(detailMessage, throwable);
		}

		private static final long serialVersionUID = 1L;

	}

	public static class UnsupportedPlaylistUrlException extends ShareIntentResolverException {

		public UnsupportedPlaylistUrlException(String detailMessage) {
			super(detailMessage);
		}
	}

	/**
	 * Resolves the Intent to a canonical URL for the track or raise a {@link
	 * ShareIntentResolverException}.
	 * <p/>
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

		if (isDataUri(uri)) {
			final Option<String> maybeDataUrl = convertDataToWebUri(uri);
			if (maybeDataUrl.isDefined()) {
				return maybeDataUrl.get();
			}
		}

		if (isValidUri(uri)) {
			try {
				return resolveUri(uri);
			} catch (final APIException e) {
				final int code = e.getCode();

				if (code == HttpURLConnection.HTTP_NOT_FOUND) {
					throw new TrackNotFoundException(
							String.format("The given track could not be resolved for URL %s",
									uri.toString()), e);
				}

				throw new ShareIntentResolverException(
						String.format("Could not resolve URL: %s", uri.toString()), e);
			}
		}

		throw new UnsupportedUrlException(
				String.format("Given URL '%s' is not a valid soundcloud URL.",
						(uri == null) ? "unknown" : uri.toString()));
	}

	private Option<String> convertDataToWebUri(Uri uri) {
		try {
			final Long id = Long.parseLong(uri.getLastPathSegment());
			return Option.some(String.format("https://api.soundcloud.com/tracks/%d.json", id));
		} catch (NumberFormatException err) {
			return Option.none();
		}
	}

	/**
	 * Check whether a given URI looks like an internal URI.
	 *
	 * @param uri The URI to parse
	 * @return True if the given URI is a SoundCloud app link.
	 */
	private boolean isDataUri(@Nullable Uri uri) {
		if (uri != null && SOUNDCLOUD_URI_SCHEME.equals(uri.getScheme()) &&
				SOUNDCLOUD_URI_HOST.equals(uri.getHost())) {
			final String path = uri.getPath();
			return path != null && path.matches(SOUNDCLOUD_URI_PATH_RE);
		}
		return false;
	}

	/**
	 * Resolves the intent to a PendingDownload describing the ID and type of the download.
	 *
	 * @return PendingDownload containing the resolved track or playlist ID together with its type.
	 * @throws ShareIntentResolverException
	 */
	public PendingDownload resolvePendingDownload() throws ShareIntentResolverException {
		final String id;
		final String url = resolve();
		final Matcher idMatcher = URL_ID_PATTERN.matcher(url);
		final Matcher playlistMatcher = URL_PLAYLIST_PATTERN.matcher(url);

		if (!Config.Features.PLAYLIST_DOWNLOADS && playlistMatcher.find()) {
			throw new UnsupportedPlaylistUrlException(
					mActivity.getString(R.string.track_error_unsupported_playlist));
		}

		if (idMatcher.find()) {
			id = idMatcher.group(1);
		} else {
			throw new ShareIntentResolverException(
					String.format("Could not parse ID from URL '%s'.", url));
		}

			return new PendingDownload(id, MediaDownloadType.TRACK);
	}

	protected boolean isValidUri(final Uri uri) {
		if (uri == null) {
			return false;
		}

		final String scheme = uri.getScheme();
		final String host = uri.getHost();

		// Safeguard if we have some seriously weird uri as in BS/33260701.
		if (scheme == null || host == null) {
			return false;
		}

		if (!scheme.equals("http") && !scheme.equals("https")) {
			return false;
		}

		for (final String allowedHost : ALLOWED_HOSTS) {
			if (host.equals(allowedHost)) {
				return true;
			}
		}

		return false;
	}

	protected String resolveUri(final Uri uri) throws APIException {
		final ResolveService service = mServiceManager.resolveService();
		final ResolveEntity entity = service.resolve(uri.toString());

		return entity.getLocation();
	}

}
