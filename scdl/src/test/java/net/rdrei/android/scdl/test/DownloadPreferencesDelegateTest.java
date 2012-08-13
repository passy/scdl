package net.rdrei.android.scdl.test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl.ui.DownloadPreferencesDelegateImpl;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TestRunner.class)
public class DownloadPreferencesDelegateTest {

	@Test
	public void testSmokeFreeExternalStorage() {
		long storage = DownloadPreferencesDelegateImpl.getFreeExternalStorage();
		assertThat(storage, equalTo(0l));
	}
}
