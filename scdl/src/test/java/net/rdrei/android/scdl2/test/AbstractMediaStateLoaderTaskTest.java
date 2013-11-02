package net.rdrei.android.scdl2.test;

import android.app.Activity;
import android.content.Context;

import com.google.inject.AbstractModule;

import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.MediaDownloadType;
import net.rdrei.android.scdl2.api.PendingDownload;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import net.rdrei.android.scdl2.api.service.PlaylistService;
import net.rdrei.android.scdl2.api.service.TrackService;
import net.rdrei.android.scdl2.ui.AbstractMediaStateLoaderTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(RobolectricTestRunner.class)
public class AbstractMediaStateLoaderTaskTest {
	@Mock
	TrackService mTrackService;

	@Mock
	PlaylistService mPlaylistService;

	Activity mContext;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mContext = Robolectric.buildActivity(Activity.class).create().get();

		TestHelper.overridenInjector(this, new AbstractModule() {
			@Override
			protected void configure() {
				bind(TrackService.class).toInstance(mTrackService);
				bind(PlaylistService.class).toInstance(mPlaylistService);
			}
		});

	}

	@Test
	public void testCallForTrack() throws Exception {
		final PendingDownload pendingDownload = new PendingDownload("1234",
				MediaDownloadType.TRACK);
		final AbstractMediaStateLoaderTask task = new AbstractMediaStateLoaderTask(
				mContext, pendingDownload);
		task.call();

		verify(mTrackService).getTrack("1234");
		verifyZeroInteractions(mPlaylistService);
	}

	@Test
	public void testCallForPlayList() throws Exception {
		final PendingDownload pendingDownload = new PendingDownload("1234",
				MediaDownloadType.PLAYLIST);
		final AbstractMediaStateLoaderTask task = new AbstractMediaStateLoaderTask(
				mContext, pendingDownload);
		task.call();

		verify(mPlaylistService).getPlaylist("1234");
		verifyZeroInteractions(mTrackService);
	}
}
