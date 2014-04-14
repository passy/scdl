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

	String API_CONSUMER_KEY = "429caab2811564cb27f52a7a4964269b";
	String API_SSL_PIN_HASH = "e9fe522cd8dd960dacc046380f2ad6b7e979f687";
	String TMP_DOWNLOAD_POSTFIX = ".tmp";
	boolean PAID_BUILD = BuildConfig.PAID_BUILD;
	MARKETPLACE_TYPE STORE = BuildConfig.STORE;
}
