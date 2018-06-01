package br.com.bezerra.diego.bakingapp.data.database.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import java.util.List;

import br.com.bezerra.diego.bakingapp.data.database.BakingAppProvider;
import br.com.bezerra.diego.bakingapp.data.database.contract.RecipeContract;
import br.com.bezerra.diego.bakingapp.data.service.model.RecipeModel;

public class RecipeProviderUtil {

    public static CursorLoader getAllRecipesCursorLoader(Context context) {
        return new CursorLoader(context, BakingAppProvider.Recipes.CONTENT_URI
                , null, null, null, null);
    }

    public static Cursor getAllRecipesCursor(Context context) {
        return context.getContentResolver().query(BakingAppProvider.Recipes.CONTENT_URI
                , null, null, null, null);
    }

    public static int bulkInsert(Context context, List<RecipeModel> recipes) {
        ContentValues[] values = getRecipesValues(recipes);
        return context.getContentResolver().bulkInsert(BakingAppProvider.Recipes.CONTENT_URI, values);
    }

    private static ContentValues[] getRecipesValues(List<RecipeModel> recipes) {
        ContentValues[] values = new ContentValues[recipes.size()];
        int index = 0;
        for (RecipeModel recipeModel : recipes) {
            values[index] = new ContentValues();
            values[index].put(RecipeContract._ID, recipeModel.getId());
            values[index].put(RecipeContract.NAME, recipeModel.getName());
            values[index].put(RecipeContract.IMAGE, recipeModel.getImage());
            values[index].put(RecipeContract.SERVINGS, recipeModel.getServings());
            index++;
        }

        return values;
    }
}
