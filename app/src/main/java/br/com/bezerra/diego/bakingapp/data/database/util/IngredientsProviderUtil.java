package br.com.bezerra.diego.bakingapp.data.database.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import br.com.bezerra.diego.bakingapp.data.database.BakingAppProvider;
import br.com.bezerra.diego.bakingapp.data.database.contract.IngredientContract;
import br.com.bezerra.diego.bakingapp.data.database.contract.RecipeContract;
import br.com.bezerra.diego.bakingapp.data.service.model.IngredientModel;
import br.com.bezerra.diego.bakingapp.data.service.model.RecipeModel;

public class IngredientsProviderUtil {

    public static Cursor getIngredientsByRecipeId(long recipeId, Context context) {
        Uri uri = BakingAppProvider.Ingredients.withId(recipeId);
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    public static int bulkInsert(Context context, List<RecipeModel> recipes) {

        Cursor cursor = RecipeProviderUtil.getAllRecipesCursor(context);
        int insertedRows = 0;
        while(cursor.moveToNext()) {
            long recipeId = cursor.getInt(cursor.getColumnIndex(RecipeContract._ID));
            ContentValues[] values = getIngredientsValues(recipes.get(cursor.getPosition()).getIngredients(), recipeId);
            Uri uri = BakingAppProvider.Ingredients.withId(recipeId);
            insertedRows += context.getContentResolver().bulkInsert(uri, values);
        }

        return insertedRows;
    }

    private static ContentValues[] getIngredientsValues(List<IngredientModel> ingredients, long recipeId) {
        ContentValues[] values = new ContentValues[ingredients.size()];
        int index = 0;
        for (IngredientModel ingredient : ingredients) {
            values[index] = new ContentValues();
            values[index].put(IngredientContract.QUANTITY, ingredient.getQuantity());
            values[index].put(IngredientContract.INGREDIENT, ingredient.getIngredient());
            values[index].put(IngredientContract.MEASURE, ingredient.getMeasure());
            values[index].put(IngredientContract.RECIPE, recipeId);
            index++;
        }

        return values;
    }
}
