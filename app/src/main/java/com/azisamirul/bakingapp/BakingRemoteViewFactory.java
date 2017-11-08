package com.azisamirul.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.azisamirul.bakingapp.widgetdata.BakingAppDbContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azisamirul on 11/10/2017.
 */

class BakingRemoteViewFactory implements BakingWidgetService.RemoteViewsFactory {
    Context context;
    List<String> recipeData = new ArrayList<>();

    public BakingRemoteViewFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        loadData();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return recipeData.size();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(),
                android.R.layout.simple_list_item_1);
        views.setTextViewText(android.R.id.text1, recipeData.get(position));
        return views;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public void onDataSetChanged() {
        loadData();
    }

    public void loadData() {
        Uri uri = BakingAppDbContract.BakingAppEntry.CONTENT_URI;
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        if (c.getCount() != 0) {
            int ingredientsColumnIndex = c.getColumnIndex(BakingAppDbContract.BakingAppEntry.COLUMN_INGREDIENT);
            int quantityColumnIndex = c.getColumnIndex(BakingAppDbContract.BakingAppEntry.COLUMN_QUANTITY);
            int measureColumnIndex = c.getColumnIndex(BakingAppDbContract.BakingAppEntry.COLUMN_MEASURE);
            c.moveToFirst();
            while (c.moveToNext()) {
                String quantity = String.valueOf(c.getLong(quantityColumnIndex));
                String measure = c.getString(measureColumnIndex);
                String ingredients = c.getString(ingredientsColumnIndex);

                String list = context.getString(R.string.widget_lists, quantity, measure, ingredients);
                Log.d("widgetlist",list);
                recipeData.add(list);
            }
        }
        Log.d("rvFactory",String.valueOf(uri));
    }

}