package net.rdrei.android.scdl2.test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import net.rdrei.android.scdl2.api.entity.UserEntity;
import android.os.Parcel;

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
