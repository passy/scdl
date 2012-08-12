package net.rdrei.android.scdl.test;

import net.rdrei.android.scdl.DownloadPathValidator.DownloadPathValidationException;
import net.rdrei.android.scdl.DownloadPathValidator.ErrorCode;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;

public class DownloadPathValidationErrorCodeMatcher extends
		TypeSafeMatcher<DownloadPathValidationException> {

	private final ErrorCode mErrorCode;

	public DownloadPathValidationErrorCodeMatcher(ErrorCode code) {
		this.mErrorCode = code;
	}

	public void describeTo(Description description) {
		description.appendText("exception has SQL error code ");
		description.appendText(mErrorCode.toString());
	}

	@Override
	public boolean matchesSafely(DownloadPathValidationException exception) {
		return exception.getErrorCode() == mErrorCode;
	}

	public static DownloadPathValidationErrorCodeMatcher hasErrorCode(
			ErrorCode errorCode) {
		return new DownloadPathValidationErrorCodeMatcher(errorCode);
	}
}
