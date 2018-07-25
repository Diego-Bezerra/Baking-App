package br.com.bezerra.diego.bakingapp.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import br.com.bezerra.diego.bakingapp.R;
import br.com.bezerra.diego.bakingapp.gui.detailsActivity.DetailsActivity;
import br.com.bezerra.diego.bakingapp.gui.mainActivity.MainActivity;

public class WidgetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action == null) return;

        if (action.equals(context.getString(R.string.widget_action))) {

            Bundle extras = intent.getExtras();
            if (extras == null) return;

            String recipeTitle = extras.getString(DetailsActivity.RECIPE_TITLE_EXTRA);
            long recipeId = extras.getLong(DetailsActivity.RECIPE_ID_EXTRA);

            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent detailsActivityIntent = new Intent(context, DetailsActivity.class);
            detailsActivityIntent.putExtra(DetailsActivity.RECIPE_ID_EXTRA, recipeId);
            detailsActivityIntent.putExtra(DetailsActivity.RECIPE_TITLE_EXTRA, recipeTitle);

            context.startActivities(new Intent[] { mainActivityIntent, detailsActivityIntent });
        }
    }
}
