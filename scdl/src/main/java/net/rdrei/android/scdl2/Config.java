package net.rdrei.android.scdl2;

public interface Config {
	// Feature switches.
	interface Features {
		boolean PLAYLIST_DOWNLOADS = false;
		boolean NEW_DONATE = false;
	}

	enum MARKETPLACE_TYPE {
		GOOGLE_PLAY,
		AMAZON
	}

	// Get your SoundCloud API key from
	// https://developers.soundcloud.com/docs/api/guide
	String API_CONSUMER_KEY = "CONSUMER_KEY";
	// The SSL PIN can be extracted via `contrib/pin.py`
	String API_SSL_PIN_HASH = "e9fe522cd8dd960dacc046380f2ad6b7e979f687";
	String TMP_DOWNLOAD_POSTFIX = ".tmp";
	boolean PAID_BUILD = BuildConfig.PAID_BUILD;
	MARKETPLACE_TYPE STORE = BuildConfig.STORE;
}
