package br.com.bezerra.diego.bakingapp.gui.detailsActivity.ingredientStep;

import android.os.Parcel;
import android.os.Parcelable;

import br.com.bezerra.diego.bakingapp.gui.detailsActivity.BaseModelAdapter;

public class IngredientModelAdapter extends BaseModelAdapter implements Parcelable {

    private long recipeId;
    private String ingredient;

    IngredientModelAdapter() {
    }

    IngredientModelAdapter(Parcel in) {
        super(in);
        recipeId = in.readLong();
        ingredient = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(recipeId);
        dest.writeString(ingredient);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
