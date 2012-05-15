package net.rdrei.android.scdl.api.entity;

import android.os.Parcel;
import android.os.Parcelable;
import net.rdrei.android.scdl.api.SoundcloudEntity;

public class UserEntity implements SoundcloudEntity, Parcelable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String username;
	private String uri;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(username);
		dest.writeString(uri);
	}
	
	private UserEntity(Parcel in) {
		id = in.readInt();
		username = in.readString();
		uri = in.readString();
	}
	
	public static final Parcelable.Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
		@Override
		public UserEntity[] newArray(int size) {
			return new UserEntity[size];
		}
		
		@Override
		public UserEntity createFromParcel(Parcel source) {
			return new UserEntity(source);
		}
	};
}
