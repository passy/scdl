package net.rdrei.android.scdl2.test;

import net.rdrei.android.scdl2.DownloadPathValidator.DownloadPathValidationException;
import net.rdrei.android.scdl2.DownloadPathValidator.ErrorCode;

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
