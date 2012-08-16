package net.rdrei.android.scdl2.api;

import java.io.File;

import com.github.kevinsawicki.http.HttpRequest.SendCallback;

public interface SoundcloudApiQuery<T extends SoundcloudEntity> {

	public static enum HttpMethod {
		GET, POST, DELETE, PUT
	};

	/**
	 * @param sendCallback
	 *            the sendCallback to set
	 */
	void setSendCallback(SendCallback sendCallback);

	SoundcloudApiQuery<T> addPostParameter(String key, String value);

	/**
	 * Add a file that should be uploaded via POST. <b>Note:</b> At the
	 * momenent, this has no effect if you choose a request method other than
	 * POST.
	 * 
	 * @param key
	 * @param file
	 * @return This query.
	 */
	SoundcloudApiQuery<T> addPartParameters(String key, File file);

	T execute(int expected) throws APIException;
}