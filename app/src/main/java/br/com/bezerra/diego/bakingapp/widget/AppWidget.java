package br.com.bezerra.diego.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import br.com.bezerra.diego.bakingapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        appWidgetManager.updateAppWidget(appWidgetId, getWidgetRemoteView(context));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static RemoteViews getWidgetRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        Intent intent = new Intent(context, ListViewWidgetService.class);
        views.setRemoteAdapter(R.id.list, intent);

        Intent appIntent = new Intent(context, WidgetReceiver.class);
        appIntent.setAction(context.getString(R.string.widget_action));
        appIntent.addCategory(context.getString(R.string.widget_category));
        PendingIntent appPendingIntent = PendingIntent.getBroadcast(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.list, appPendingIntent);

        views.setEmptyView(R.id.list, R.id.noResults);
        return views;
    }
}

