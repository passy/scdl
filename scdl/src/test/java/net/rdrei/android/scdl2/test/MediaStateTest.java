package net.rdrei.android.scdl2.test;

import android.os.Parcel;

import net.rdrei.android.scdl2.api.MediaDownloadType;
import net.rdrei.android.scdl2.api.MediaState;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import net.rdrei.android.scdl2.api.entity.UserEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class MediaStateTest {
	@Test
	public void testTrackType() {
		final TrackEntity track = new TrackEntity();
		final MediaState mediaState = MediaState.fromEntity(track);

		assertThat(mediaState.getType()).isEqualTo(MediaDownloadType.TRACK);
		assertThat(mediaState.getTrackOption().isEmpty()).isFalse();
		assertThat(mediaState.getPlaylistOption().isEmpty()).isTrue();
	}

	@Test
	public void testTrackParcel() {
		final TrackEntity track = new TrackEntity();
		track.setTitle("My track");
		track.setUser(new UserEntity());

		final MediaState entity = MediaState.fromEntity(track);

		final byte[] bytes;
		final Parcel parcel = Parcel.obtain();
		try {
			parcel.writeValue(entity);
			bytes = parcel.marshall();
		} finally {
			parcel.recycle();
		}

		final MediaState newEntity;
		final Parcel parcel2 = Parcel.obtain();
		try {
			parcel2.unmarshall(bytes, 0, bytes.length);
			parcel2.setDataPosition(0);
			newEntity = (MediaState) parcel2.readValue(MediaState.class.getClassLoader());
		} finally {
			parcel2.recycle();
		}

		assertThat(newEntity.getType()).isEqualTo(MediaDownloadType.TRACK);
		assertThat(newEntity.getTrackOption().get().getTitle()).isEqualTo("My track");
	}
}
