package net.rdrei.android.scdl2;

import android.content.Context;
import android.net.Uri;

import com.google.inject.Inject;

public class PackageHelper {
	private final Context mContext;

	@Inject
	public PackageHelper(final Context context) {
		mContext = context;
	}

	public Uri getMarketUri() {
		final String baseURL;

		switch (Config.STORE) {
			case GOOGLE_PLAY:
				baseURL = "market://details?id=";
				break;
			case AMAZON:
				baseURL = "http://www.amazon.com/gp/mas/dl/android?p=";
				break;
			default:
				throw new IllegalStateException("Unknown store type");
		}

		return Uri.parse(baseURL + mContext.getPackageName());
	}
}
