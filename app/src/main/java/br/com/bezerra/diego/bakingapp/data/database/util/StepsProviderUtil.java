package br.com.bezerra.diego.bakingapp.data.database.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import java.util.List;

import br.com.bezerra.diego.bakingapp.data.database.BakingAppProvider;
import br.com.bezerra.diego.bakingapp.data.database.contract.RecipeContract;
import br.com.bezerra.diego.bakingapp.data.database.contract.StepContract;
import br.com.bezerra.diego.bakingapp.data.service.model.RecipeJsonModel;
import br.com.bezerra.diego.bakingapp.data.service.model.StepJsonModel;

public class StepsProviderUtil {

    public static CursorLoader getStepsByRecipeId(long recipeId, Context context) {
        Uri uri = BakingAppProvider.Steps.withRecipeId(recipeId);
        return new CursorLoader(context, uri, null, null, null, null);
    }

    public static CursorLoader getStepsById(long stepId, Context context) {
        Uri uri = BakingAppProvider.Steps.withStepId(stepId);
        return new CursorLoader(context, uri, null, null, null, null);
    }

    public static int bulkInsert(Context context, List<RecipeJsonModel> recipes) {
        Cursor cursor = RecipeProviderUtil.getAllRecipesCursor(context);
        int insertedRows = 0;
        while (cursor.moveToNext()) {
            long recipeId = cursor.getInt(cursor.getColumnIndex(RecipeContract._ID));
            ContentValues[] values = getStepsValues(recipes.get(cursor.getPosition()).getSteps(), recipeId);
            Uri uri = BakingAppProvider.Steps.withRecipeId(recipeId);
            insertedRows += context.getContentResolver().bulkInsert(uri, values);
        }

        return insertedRows;
    }

    private static ContentValues[] getStepsValues(List<StepJsonModel> steps, long recipeId) {
        ContentValues[] values = new ContentValues[steps.size()];
        int index = 0;
        for (StepJsonModel step : steps) {
            values[index] = new ContentValues();
            values[index].put(StepContract.ORDER_API, step.getId());
            values[index].put(StepContract.DESCRIPTION, step.getDescription());
            values[index].put(StepContract.SHORT_DESCRIPTION, step.getShortDescription());
            values[index].put(StepContract.THUMBNAIL_URL, step.getThumbnailURL());
            values[index].put(StepContract.VIDEO_URL, step.getVideoURL());
            values[index].put(StepContract.RECIPE, recipeId);
            index++;
        }

        return values;
    }
}
