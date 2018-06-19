package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredient;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import br.com.bezerra.diego.bakingapp.data.database.contract.IngredientContract;

public class IngredientModelAdapter implements Parcelable {

    private long id;
    private double quantity;
    private String measure;
    private String ingredient;

    IngredientModelAdapter(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(IngredientContract._ID));
        quantity = cursor.getDouble(cursor.getColumnIndex(IngredientContract.QUANTITY));
        measure = cursor.getString(cursor.getColumnIndex(IngredientContract.MEASURE));
        ingredient = cursor.getString(cursor.getColumnIndex(IngredientContract.INGREDIENT));
    }

    public static IngredientModelAdapter[] getIngredientsFromCursor(Cursor cursor) {
        IngredientModelAdapter[] ingredientModelAdapters = new IngredientModelAdapter[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            ingredientModelAdapters[i] = new IngredientModelAdapter(cursor);
            i++;
        }

        return ingredientModelAdapters;
    }

    IngredientModelAdapter(Parcel in) {
        id = in.readLong();
        quantity = in.readDouble();
        measure = in.readString();
        ingredient = in.readString();
    }

    public static final Creator<IngredientModelAdapter> CREATOR = new Creator<IngredientModelAdapter>() {
        @Override
        public IngredientModelAdapter createFromParcel(Parcel in) {
            return new IngredientModelAdapter(in);
        }

        @Override
        public IngredientModelAdapter[] newArray(int size) {
            return new IngredientModelAdapter[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeDouble(quantity);
        dest.writeString(measure);
        dest.writeString(ingredient);
    }
}
