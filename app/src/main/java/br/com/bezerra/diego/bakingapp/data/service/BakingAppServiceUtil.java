package br.com.bezerra.diego.bakingapp.data.service;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.util.List;

import br.com.bezerra.diego.bakingapp.data.database.util.IngredientsProviderUtil;
import br.com.bezerra.diego.bakingapp.data.database.util.RecipeProviderUtil;
import br.com.bezerra.diego.bakingapp.data.database.util.StepsProviderUtil;
import br.com.bezerra.diego.bakingapp.data.service.model.RecipeModel;
import br.com.bezerra.diego.bakingapp.util.ConnectivityReceiver;
import br.com.bezerra.diego.bakingapp.util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Response;

public class BakingAppServiceUtil {

    public interface NoConnectivityReceiverListener {
        void noInternetConnection();
    }

    public static synchronized AsyncTaskLoader<Cursor> syncRecipesData(final Context context
            , final NoConnectivityReceiverListener noConnectivityReceiverListener) {

        return new AsyncTaskLoader<Cursor>(context) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {

                Cursor cursor = RecipeProviderUtil.getAllRecipesCursor(context);
                if (cursor == null || cursor.getCount() == 0) {

                    if (!ConnectivityReceiver.isConnected()) {
                        if (noConnectivityReceiverListener != null) {
                            noConnectivityReceiverListener.noInternetConnection();
                        }
                        return cursor;
                    }

                    BakingAppService bakingAppService = NetworkUtil.retrofit.create(BakingAppService.class);
                    Call<List<RecipeModel>> listCall = bakingAppService.getRecipes();

                    Response<List<RecipeModel>> response = null;
                    try {
                        response = listCall.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response != null) {
                        List<RecipeModel> recipes = response.body();
                        if (recipes != null && recipes.size() > 0) {
                            RecipeProviderUtil.bulkInsert(context, recipes);
                            IngredientsProviderUtil.bulkInsert(context, recipes);
                            StepsProviderUtil.bulkInsert(context, recipes);
                            cursor = RecipeProviderUtil.getAllRecipesCursor(context);
                        }
                    }
                }

                return cursor;
            }
        };
    }
}
