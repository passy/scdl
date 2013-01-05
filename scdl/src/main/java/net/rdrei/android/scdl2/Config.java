package net.rdrei.android.scdl2;

public interface Config {
	enum MARKETPLACE_TYPE {
		GOOGLE_PLAY,
		AMAZON
	};
	
	String API_CONSUMER_KEY = "429caab2811564cb27f52a7a4964269b";
	String API_SSL_PIN_HASH = "d8d7d4701117c87a1fb50360e52a778ab332361e";
	String TMP_DOWNLOAD_POSTFIX = ".tmp";
	boolean PAID_BUILD = true;
	MARKETPLACE_TYPE STORE = MARKETPLACE_TYPE.AMAZON;
}
