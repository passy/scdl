package net.rdrei.android.scdl.api;

/**
 * Checked exception for any error that might occur during API transactions.
 * @author pascal
 *
 */
public class APIException extends Exception {
	private static final long serialVersionUID = 1L;

	public APIException() {
		super();
	}

	public APIException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public APIException(String detailMessage) {
		super(detailMessage);
	}

	public APIException(Throwable throwable) {
		super(throwable);
	}
}
