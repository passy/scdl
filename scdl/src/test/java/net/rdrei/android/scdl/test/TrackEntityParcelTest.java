package net.rdrei.android.scdl.test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import android.os.Parcel;
import net.rdrei.android.scdl.api.entity.TrackEntity;
import net.rdrei.android.scdl.api.entity.UserEntity;

public class TrackEntityParcelTest {

	public void testNestedTrackMarshallUnmarshall() {
		final UserEntity user = new UserEntity();
		user.setId(23);
		user.setUsername("passy");

		final TrackEntity entity = new TrackEntity();
		entity.setId(200);
		entity.setTitle("Hello, World.");
		entity.setUser(user);

		final byte[] bytes;
		final Parcel parcel = Parcel.obtain();
		try {
			parcel.writeValue(entity);
			bytes = parcel.marshall();
		} finally {
			parcel.recycle();
		}

		final TrackEntity newEntity;
		final Parcel parcel2 = Parcel.obtain();
		try {
			parcel2.unmarshall(bytes, 0, bytes.length);
			parcel2.setDataPosition(0);
			newEntity = (TrackEntity) parcel2.readValue(TrackEntity.class
					.getClassLoader());
		} finally {
			parcel2.recycle();
		}
		
		assertThat(newEntity.getId(), equalTo(23l));
		assertThat(newEntity.getTitle(), equalTo("Hello, World."));
		assertThat(newEntity.getUser().getUsername(), equalTo("passy"));
	}

}
