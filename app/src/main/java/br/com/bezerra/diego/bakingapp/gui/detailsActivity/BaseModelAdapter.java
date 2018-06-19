package br.com.bezerra.diego.bakingapp.gui.detailsActivity;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseModelAdapter implements Parcelable {

    private long id;
    private int viewType;

    protected BaseModelAdapter() {

    }

    protected BaseModelAdapter(Parcel in) {
        id = in.readLong();
        viewType = in.readInt();
    }

    public static final Creator<BaseModelAdapter> CREATOR = new Creator<BaseModelAdapter>() {
        @Override
        public BaseModelAdapter createFromParcel(Parcel in) {
            return new BaseModelAdapter(in);
        }

        @Override
        public BaseModelAdapter[] newArray(int size) {
            return new BaseModelAdapter[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(viewType);
    }
}
