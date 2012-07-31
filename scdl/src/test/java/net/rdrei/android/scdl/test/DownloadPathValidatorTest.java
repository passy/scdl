package net.rdrei.android.scdl.test;

import net.rdrei.android.scdl.DownloadPathValidator;
import net.rdrei.android.scdl.DownloadPathValidator.DownloadPathValidationException;
import net.rdrei.android.scdl.DownloadPathValidator.ErrorCode;

import static net.rdrei.android.scdl.test.DownloadPathValidationErrorCodeMatcher.hasErrorCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(TestRunner.class)
public class DownloadPathValidatorTest {
	@Inject
	private DownloadPathValidator mValidator;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void inject() {
		TestRunner.getInjector().injectMembers(this);
	}

	@Test
	public void testInsecurePath() throws DownloadPathValidationException {
		thrown.expect(DownloadPathValidationException.class);
		thrown.expect(hasErrorCode(ErrorCode.INSECURE_PATH));
		
		mValidator.validateCustomPathOrThrow("/data/data/");
	}
}
