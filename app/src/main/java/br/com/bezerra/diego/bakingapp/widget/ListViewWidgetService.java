package br.com.bezerra.diego.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.data.service.BakingAppServiceUtil;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivity;
import br.com.bezerra.diego.bakingapp.gui.mainActivity.RecipeModelAdapter;

public class ListViewWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory(getApplicationContext());
    }

    class ListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

        Context mContext;
        RecipeModelAdapter[] recipesData;

        public ListRemoteViewFactory(Context applicationContext) {
            mContext = applicationContext;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            Cursor data = BakingAppServiceUtil.syncRecipesData(mContext);
            if (data != null) {
                if (data.getCount() > 0) {
                    recipesData = RecipeModelAdapter.recipesFromCursor(data);
                }
                data.close();
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return recipesData != null ? recipesData.length : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {

            if (recipesData == null || recipesData.length == 0) return null;
            RecipeModelAdapter recipe = recipesData[position];

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item_recipes_widget);
            views.setTextViewText(R.id.title, recipe.getName());
            views.setTextViewText(R.id.serving, recipe.getStringFormatedServings(mContext));

            Bundle extras = new Bundle();
            extras.putLong(DetailsActivity.RECIPE_ID_EXTRA, recipe.getId());
            Intent intent = new Intent();
            intent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.container, intent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
