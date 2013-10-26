package net.rdrei.android.scdl2.api.entity;

import net.rdrei.android.scdl2.api.SoundcloudEntity;
import android.os.Parcel;
import android.os.Parcelable;

public class UserEntity implements SoundcloudEntity, Parcelable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String username;
	private String uri;

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(final String uri) {
		this.uri = uri;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeLong(id);
		dest.writeString(username);
		dest.writeString(uri);
	}

	private UserEntity(final Parcel in) {
		id = in.readLong();
		username = in.readString();
		uri = in.readString();
	}

	public UserEntity() {
	}

	public static final Parcelable.Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
		@Override
		public UserEntity[] newArray(final int size) {
			return new UserEntity[size];
		}

		@Override
		public UserEntity createFromParcel(final Parcel source) {
			return new UserEntity(source);
		}
	};
}
