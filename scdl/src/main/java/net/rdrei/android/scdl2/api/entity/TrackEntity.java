package net.rdrei.android.scdl2.api.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import roboguice.util.Ln;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import net.rdrei.android.scdl2.Config;
import net.rdrei.android.scdl2.api.SoundcloudEntity;

/**
 * Entity tracking some of the Track information.
 * 
 * TODO: Add and display user information.
 * 
 * @author pascal
 * 
 */
public class TrackEntity implements SoundcloudEntity, Parcelable {
	private static final long serialVersionUID = 2L;

	private long id;
	private long duration;
	private String title;
	private boolean downloadable;

	@SerializedName("download_url")
	private String downloadUrl;

	@SerializedName("artwork_url")
	private String artworkUrl;

	@SerializedName("original_format")
	private String originalFormat;

	private String description;
	private String permalink;

	@SerializedName("original_content_size")
	private long originalContentSize;
	
	private UserEntity user;
	
	public TrackEntity() {
		super();
	}
	
	private TrackEntity(Parcel in) {
		id = in.readLong();
		duration = in.readLong();
		title = in.readString();
		downloadable = in.readByte() == 1;
		downloadUrl = in.readString();
		artworkUrl = in.readString();
		originalFormat = in.readString();
		description = in.readString();
		permalink = in.readString();
		originalContentSize = in.readLong();
		user = in.readParcelable(UserEntity.class.getClassLoader());
	}

	public static final Parcelable.Creator<TrackEntity> CREATOR = new Creator<TrackEntity>() {
		@Override
		public TrackEntity[] newArray(int size) {
			return new TrackEntity[size];
		}
		
		@Override
		public TrackEntity createFromParcel(Parcel source) {
			Ln.d("Creating new TrackEntity from parcel source.");
			return new TrackEntity(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(duration);
		dest.writeString(title);
		dest.writeByte((byte) (downloadable ? 1 : 0));
		dest.writeString(downloadUrl);
		dest.writeString(artworkUrl);
		dest.writeString(originalFormat);
		dest.writeString(description);
		dest.writeString(permalink);
		dest.writeLong(originalContentSize);
		dest.writeParcelable(user, 0);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDownloadable() {
		return downloadable;
	}

	public void setDownloadable(boolean downloadable) {
		this.downloadable = downloadable;
	}

	/**
	 * Get the download URL as string, including client id parameter.
	 * 
	 * @return
	 */
	public String getDownloadUrl() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(downloadUrl);
		buffer.append("?client_id=");
		buffer.append(Config.API_CONSUMER_KEY);
		return buffer.toString();
	}

	public Uri getDownloadUri() {
		return Uri.parse(downloadUrl);
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getArtworkUrl() {
		return artworkUrl;
	}

	public Uri getArtworkUri() {
		return Uri.parse(artworkUrl);
	}

	public void setArtworkUrl(String artworkUrl) {
		this.artworkUrl = artworkUrl;
	}

	/**
	 * @return the originalFormat
	 */
	public String getOriginalFormat() {
		return originalFormat;
	}

	/**
	 * @param originalFormat
	 *            the originalFormat to set
	 */
	public void setOriginalFormat(String originalFormat) {
		this.originalFormat = originalFormat;
	}

	public String getDownloadFilename() {
		return permalink + "." + originalFormat;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public String getFormattedDuration() {
		final SimpleDateFormat formatter = new SimpleDateFormat("m:ss");
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getDuration());
		return formatter.format(calendar.getTime());
	}

	public String getFormattedSize() {
		return String.format("%.1fMB", this.getOriginalContentSize()
				/ (1024f * 1024f));
	}

	public long getOriginalContentSize() {
		return originalContentSize;
	}

	public void setOriginalContentSize(long originalContentSize) {
		this.originalContentSize = originalContentSize;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}
}
