package br.com.bezerra.diego.bakingapp.gui.mainActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.database.contract.RecipeContract;

public class RecipeModelAdapter implements Parcelable {

    private long id;
    private String name;
    private int servings;
    private String image;

    public RecipeModelAdapter() {
    }

    public RecipeModelAdapter(Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(RecipeContract._ID));
        this.name = cursor.getString(cursor.getColumnIndex(RecipeContract.NAME));
        this.servings = cursor.getInt(cursor.getColumnIndex(RecipeContract.SERVINGS));
        this.image = cursor.getString(cursor.getColumnIndex(RecipeContract.IMAGE));
    }

    protected RecipeModelAdapter(Parcel in) {
        id = in.readLong();
        name = in.readString();
        servings = in.readInt();
        image = in.readString();
    }

    public static final Creator<RecipeModelAdapter> CREATOR = new Creator<RecipeModelAdapter>() {
        @Override
        public RecipeModelAdapter createFromParcel(Parcel in) {
            return new RecipeModelAdapter(in);
        }

        @Override
        public RecipeModelAdapter[] newArray(int size) {
            return new RecipeModelAdapter[size];
        }
    };

    public static RecipeModelAdapter[] recipesFromCursor(Cursor data) {
        RecipeModelAdapter[] recipes = new RecipeModelAdapter[data.getCount()];
        int i = 0;
        while (data.moveToNext()) {
            recipes[i] = new RecipeModelAdapter(data);
            i++;
        }

        return recipes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServings() {
        return servings;
    }

    public String getStringFormatedServings(Context context) {
        String servingFormat = context.getString(R.string.serving_format);
        return String.format(servingFormat, servings + "");
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(servings);
        dest.writeString(image);
    }
}
