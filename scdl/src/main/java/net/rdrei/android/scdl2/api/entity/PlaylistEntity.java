package net.rdrei.android.scdl2.api.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import net.rdrei.android.scdl2.api.SoundcloudEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a playlist on SoundCloud. Based on this JSON type:
 * http://developers.soundcloud.com/docs/api/reference#playlists
 */
public class PlaylistEntity implements SoundcloudEntity, Parcelable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String description;
	private UserEntity user;
	private List<TrackEntity> tracks;

	@SerializedName("artwork_url")
	private String artworkUrl;

	public PlaylistEntity() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<TrackEntity> getTracks() {
		return tracks;
	}

	public void setTracks(List<TrackEntity> tracks) {
		this.tracks = tracks;
	}

	public String getArtworkUrl() {
		return artworkUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeString(this.description);
		dest.writeParcelable(this.user, flags);
		dest.writeList(this.tracks);
		dest.writeString(this.artworkUrl);
	}

	private PlaylistEntity(Parcel in) {
		this.id = in.readLong();
		this.description = in.readString();
		this.user = in.readParcelable(UserEntity.class.getClassLoader());
		this.tracks = new ArrayList<TrackEntity>();
		in.readList(this.tracks, TrackEntity.class.getClassLoader());
		this.artworkUrl = in.readString();
	}

	public static Creator<PlaylistEntity> CREATOR = new Creator<PlaylistEntity>() {
		public PlaylistEntity createFromParcel(Parcel source) {
			return new PlaylistEntity(source);
		}

		public PlaylistEntity[] newArray(int size) {
			return new PlaylistEntity[size];
		}
	};
}
