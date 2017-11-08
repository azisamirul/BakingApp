package com.azisamirul.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by azisamirul on 14/10/2017.
 */

public class BakingWidgetUpdateService extends IntentService {

    public static final String ACTION_UPDATE_WIDGET = "com.azisamirul.bakingapp.updatewidget";

    public BakingWidgetUpdateService() {
        super("BakingWidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action == ACTION_UPDATE_WIDGET) {
                Log.d("action : ","action update");
                handleUpdateWidget();
            }
        }
    }

    public static void startActionUpdateBakingWidgets(Context context) {
        Intent intent = new Intent(context, BakingWidgetUpdateService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.startService(intent);
    }

    private void handleUpdateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingAppWidget.class));
        BakingAppWidget.updateBakingWidget(this, appWidgetManager, appWidgetIds);
    }


}
