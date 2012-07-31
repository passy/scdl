package net.rdrei.android.scdl;

public class DownloadPathValidator {

	public enum ErrorCode {
		INSECURE_PATH,
	};

	// Injectable
	public DownloadPathValidator() {
	}

	/**
	 * Using a single class to catch all validation related errors is a
	 * deliberate choice. It reducesd boilerplate a lot as exceptions are all
	 * caught in one place and handled the same way except for the error message
	 * provided.
	 * 
	 * @author pascal
	 * 
	 */
	public static class DownloadPathValidationException extends
			Exception {

		private final ErrorCode mErrorCode;
		private static final long serialVersionUID = 1L;

		public DownloadPathValidationException(ErrorCode code) {
			this.mErrorCode = code;
		}

		public ErrorCode getErrorCode() {
			return mErrorCode;
		}
	}

	public void validateCustomPathOrThrow(final String path) throws DownloadPathValidationException {
		if (!path.startsWith("/mnt/")) {
			throw new DownloadPathValidationException(ErrorCode.INSECURE_PATH);
		}
	}
}
