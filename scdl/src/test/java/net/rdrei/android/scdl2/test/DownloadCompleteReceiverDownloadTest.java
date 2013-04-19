package net.rdrei.android.scdl2.test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.receiver.DownloadCompleteReceiver;
import net.rdrei.android.scdl2.receiver.DownloadCompleteReceiver.Download;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TestRunner.class)
public class DownloadCompleteReceiverDownloadTest {

	@Test
	public void testGetNormalizedPath() {
		final Download download = new DownloadCompleteReceiver.Download();
		download.setPath("file:/storage/emulated/0/Music/Soundcloud/"
				+ "pieces-of-light-alex-white-1.mp3");

		assertThat(
				download.getNormalizedPath(),
				is("/storage/emulated/0/Music/Soundcloud/pieces-of-light-alex-white-1.mp3"));
	}

}
