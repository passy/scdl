package net.rdrei.android.scdl2.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.gu.option.Option;

import net.rdrei.android.scdl2.api.entity.PlaylistEntity;
import net.rdrei.android.scdl2.api.entity.TrackEntity;

public class MediaState implements Parcelable {
	private MediaDownloadType mType = MediaDownloadType.UNKNOWN;
	private Option<TrackEntity> mTrackOption = Option.none();
	private Option<PlaylistEntity> mPlaylistOption = Option.none();

	public static MediaState UNKNOWN = unknown();

	public MediaState() {
	}

	public static MediaState fromEntity(TrackEntity track) {
		final MediaState state = new MediaState();

		state.mType = MediaDownloadType.TRACK;
		state.mTrackOption = Option.some(track);

		return state;
	}

	public static MediaState fromEntity(PlaylistEntity playlist) {
		final MediaState state = new MediaState();

		state.mType = MediaDownloadType.PLAYLIST;
		state.mPlaylistOption = Option.some(playlist);

		return state;
	}

	public MediaDownloadType getType() {
		return mType;
	}

	public Option<TrackEntity> getTrackOption() {
		return mTrackOption;
	}

	public Option<PlaylistEntity> getPlaylistOption() {
		return mPlaylistOption;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mType.toString());

		if (this.mType == MediaDownloadType.TRACK) {
			dest.writeByte((byte) (mTrackOption.isEmpty() ? 0 : 1));
			if (!mTrackOption.isEmpty()) {
				dest.writeParcelable(mTrackOption.get(), flags);
				;
			}
		} else if (mType == MediaDownloadType.PLAYLIST) {
			dest.writeByte((byte) (mPlaylistOption.isEmpty() ? 0 : 1));
			if (!mPlaylistOption.isEmpty()) {
				dest.writeParcelable(mPlaylistOption.get(), flags);
				;
			}
		}
	}

	private MediaState(Parcel in) {
		mType = MediaDownloadType.valueOf(in.readString());

		if (mType == MediaDownloadType.TRACK) {
			if (in.readByte() == 1) {
				mTrackOption = Option.some(
						(TrackEntity) in.readParcelable(TrackEntity.class.getClassLoader()));
			}
		} else if (mType == MediaDownloadType.PLAYLIST) {
			if (in.readByte() == 1) {
				mPlaylistOption = Option.some(
						(PlaylistEntity) in.readParcelable(PlaylistEntity.class.getClassLoader()));
			}
		}
	}

	public static Creator<MediaState> CREATOR = new Creator<MediaState>() {
		public MediaState createFromParcel(Parcel source) {
			return new MediaState(source);
		}

		public MediaState[] newArray(int size) {
			return new MediaState[size];
		}
	};

	@Override
	public String toString() {
		return "MediaState{" +
				"mType=" + mType +
				", mTrackOption=" + mTrackOption +
				", mPlaylistOption=" + mPlaylistOption +
				'}';
	}

	private static MediaState unknown() {
		final MediaState state = new MediaState();
		state.mType = MediaDownloadType.UNKNOWN;

		return state;
	}
}
