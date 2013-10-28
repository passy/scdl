package net.rdrei.android.scdl2.test;

import android.os.Parcel;

import net.rdrei.android.scdl2.api.entity.PlaylistEntity;
import net.rdrei.android.scdl2.api.entity.TrackEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class PlaylistEntityTest {
	@Test
	public void testDescription() {
		final PlaylistEntity entity = new PlaylistEntity();

		entity.setDescription("This is my description");
		assertThat(entity.getDescription(), equalTo("This is my description"));
	}

	@Test
	public void testParcel() {
		final TrackEntity track0 = new TrackEntity();
		track0.setId(200l);
		track0.setTitle("Hello, World.");

		final TrackEntity track1 = new TrackEntity();
		track1.setId(201l);
		track1.setTitle("Yet another track");

		final PlaylistEntity playlist = new PlaylistEntity();
		playlist.setId(23l);
		playlist.setDescription("new stuff");
		playlist.setTracks(Arrays.asList(new TrackEntity[] { track0, track1 }));

		final byte[] bytes;
		final Parcel parcel0 = Parcel.obtain();
		try {
			parcel0.writeValue(playlist);
			bytes = parcel0.marshall();
		} finally {
			parcel0.recycle();
		}

		final PlaylistEntity newPlaylist;
		final Parcel parcel1 = Parcel.obtain();
		try {
			parcel1.unmarshall(bytes, 0, bytes.length);
			parcel1.setDataPosition(0);
			newPlaylist = (PlaylistEntity) parcel1.readValue(PlaylistEntity.class.getClassLoader());
		} finally {
			parcel1.recycle();
		}

		assertThat(newPlaylist.getId(), equalTo(23l));
		assertThat(newPlaylist.getDescription(), equalTo("new stuff"));
		assertThat(newPlaylist.getTracks().size(), equalTo(2));
		assertThat(newPlaylist.getTracks().get(0).getId(), equalTo(200l));
	}
}
