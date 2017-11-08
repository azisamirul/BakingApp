package com.azisamirul.bakingapp.widgetdata;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.azisamirul.bakingapp.widgetdata.BakingAppDbContract.BakingAppEntry.TABLE_NAME;

/**
 * Created by azisamirul on 11/10/2017.
 */

public class BakingAppContentProvider extends ContentProvider {
    public static final int BAKING_APP = 100;
    public static final int BAKING_APP_WITH_ID = 200;

    private static final UriMatcher matcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BakingAppDbContract.AUTHORITY, BakingAppDbContract.PATH_BAKING_APP, BAKING_APP);
        uriMatcher.addURI(BakingAppDbContract.AUTHORITY, BakingAppDbContract.PATH_BAKING_APP + "/*", BAKING_APP_WITH_ID);
        return uriMatcher;
    }

    private BakingAppDbHelper bakingAppDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        bakingAppDbHelper = new BakingAppDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = bakingAppDbHelper.getWritableDatabase();
        int match = matcher.match(uri);
        Uri uri1;
        switch (match) {
            case BAKING_APP:
                Log.d("baking app match", "insert");
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    uri1 = ContentUris.withAppendedId(BakingAppDbContract.BakingAppEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri1;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
//references from : https://stackoverflow.com/questions/12730908/how-to-use-bulkinsert-function-in-android
        int numInserted = 0;
        final SQLiteDatabase db = bakingAppDbHelper.getWritableDatabase();
        int match = matcher.match(uri);
        switch (match) {
            case BAKING_APP:
                    for (ContentValues contentValues : values) {
                        db.insert(TABLE_NAME, null, contentValues);
                    }
//                    for(int i=0;i<values.length;i++){
//                        db.insert(TABLE_NAME, null, values[i]);
//                    }
                    numInserted = values.length;

                return numInserted;
            default:
                throw new UnsupportedOperationException("unsupported uri : " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = bakingAppDbHelper.getReadableDatabase();

        Cursor cursor;
        cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = bakingAppDbHelper.getWritableDatabase();
        int match = matcher.match(uri);
        int deleted;
        switch (match) {
            case BAKING_APP_WITH_ID:
                String id = uri.getPathSegments().get(1);
                deleted = db.delete(TABLE_NAME, BakingAppDbContract.BakingAppEntry._ID + "=?", new String[]{id});
                Log.d("Content provider", String.valueOf(deleted));
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
        if (deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
