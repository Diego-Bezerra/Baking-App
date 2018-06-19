package br.com.bezerra.diego.bakingapp.gui.detailsActivity.step;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import br.com.bezerra.diego.bakingapp.data.database.contract.StepContract;

public class StepModelData implements Parcelable {

    private long id;
    private String shortDescription;
    private String description;
    private String videoUrl;
    private  String thumbnailUrl;

    StepModelData(Cursor data) {

        data.moveToFirst();

        id = data.getLong(data.getColumnIndex(StepContract._ID));
        shortDescription = data.getString(data.getColumnIndex(StepContract.SHORT_DESCRIPTION));
        description = data.getString(data.getColumnIndex(StepContract.DESCRIPTION));
        videoUrl = data.getString(data.getColumnIndex(StepContract.VIDEO_URL));
        thumbnailUrl = data.getString(data.getColumnIndex(StepContract.THUMBNAIL_URL));
    }

    StepModelData(Parcel in) {
        id = in.readLong();
        shortDescription = in.readString();
        description = in.readString();
        videoUrl = in.readString();
        thumbnailUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoUrl);
        dest.writeString(thumbnailUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StepModelData> CREATOR = new Creator<StepModelData>() {
        @Override
        public StepModelData createFromParcel(Parcel in) {
            return new StepModelData(in);
        }

        @Override
        public StepModelData[] newArray(int size) {
            return new StepModelData[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
