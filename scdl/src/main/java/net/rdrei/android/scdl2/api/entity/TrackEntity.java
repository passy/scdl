package net.rdrei.android.scdl2.api.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import net.rdrei.android.scdl2.Config;
import net.rdrei.android.scdl2.api.SoundcloudEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import roboguice.util.Ln;

/**
 * Entity tracking some of the Track information.
 * <p/>
 * TODO: Add and display user information.
 *
 * @author pascal
 */
public class TrackEntity implements SoundcloudEntity, Parcelable {
	private static final long serialVersionUID = 3L;

	private long id;
	private long duration;
	private String title;
	private boolean downloadable;

	@SerializedName("purchase_url")
	private String purchaseUrl;

	@SerializedName("purchase_title")
	private String purchaseTitle;

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

	private TrackEntity(final Parcel in) {
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
		purchaseTitle = in.readString();
		purchaseUrl = in.readString();
		user = in.readParcelable(UserEntity.class.getClassLoader());
	}

	public static final Parcelable.Creator<TrackEntity> CREATOR = new Creator<TrackEntity>() {
		@Override
		public TrackEntity[] newArray(final int size) {
			return new TrackEntity[size];
		}

		@Override
		public TrackEntity createFromParcel(final Parcel source) {
			Ln.d("Creating new TrackEntity from parcel source.");
			return new TrackEntity(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
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
		dest.writeString(getPurchaseTitle());
		dest.writeString(getPurchaseUrl());
		dest.writeParcelable(user, 0);
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(final long duration) {
		this.duration = duration;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public boolean isDownloadable() {
		return downloadable;
	}

	public void setDownloadable(final boolean downloadable) {
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

	public void setDownloadUrl(final String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getArtworkUrl() {
		return artworkUrl;
	}

	public Uri getArtworkUri() {
		return Uri.parse(artworkUrl);
	}

	public void setArtworkUrl(final String artworkUrl) {
		this.artworkUrl = artworkUrl;
	}

	/**
	 * @return the originalFormat
	 */
	public String getOriginalFormat() {
		return originalFormat;
	}

	/**
	 * @param originalFormat the originalFormat to set
	 */
	public void setOriginalFormat(final String originalFormat) {
		this.originalFormat = originalFormat;
	}

	public String getDownloadFilename() {
		return permalink + "." + originalFormat;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(final String permalink) {
		this.permalink = permalink;
	}

	public String getFormattedDuration() {
		final SimpleDateFormat formatter = new SimpleDateFormat("m:ss", Locale.US);
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getDuration());
		return formatter.format(calendar.getTime());
	}

	public String getFormattedSize() {
		return String.format(Locale.US, "%.1fMB", this.getOriginalContentSize() / (1024f * 1024f));
	}

	public long getOriginalContentSize() {
		return originalContentSize;
	}

	public void setOriginalContentSize(final long originalContentSize) {
		this.originalContentSize = originalContentSize;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(final UserEntity user) {
		this.user = user;
	}

	public boolean isPurchasable() {
		if (purchaseUrl != null) {
			final String lowerUrl = purchaseUrl.toLowerCase();
			return lowerUrl.startsWith("http://") || lowerUrl.startsWith("https://");
		}
		return false;
	}

	public String getPurchaseUrl() {
		if (purchaseUrl != null) {
			return purchaseUrl.toLowerCase();
		} else {
			return null;
		}
	}

	public String getPurchaseTitle() {
		return purchaseTitle;
	}

	public void setPurchaseUrl(String purchaseUrl) {
		this.purchaseUrl = purchaseUrl;
	}
}
