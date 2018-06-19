package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredientStep;

import android.os.Parcel;
import android.os.Parcelable;

import br.com.bezerra.diego.bakingapp.gui.detailsActivity.BaseModelAdapter;

public class StepModelAdapter extends BaseModelAdapter implements Parcelable {

    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;

    StepModelAdapter() {
    }

    StepModelAdapter(Parcel in) {
        super(in);
        shortDescription = in.readString();
        description = in.readString();
        videoURL = in.readString();
        thumbnailURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoURL);
        dest.writeString(thumbnailURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StepModelAdapter> CREATOR = new Creator<StepModelAdapter>() {
        @Override
        public StepModelAdapter createFromParcel(Parcel in) {
            return new StepModelAdapter(in);
        }

        @Override
        public StepModelAdapter[] newArray(int size) {
            return new StepModelAdapter[size];
        }
    };

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

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
