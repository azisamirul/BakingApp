package com.azisamirul.bakingapp.widgetdata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by azisamirul on 11/10/2017.
 */

public class BakingAppDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bakingapp.db";
    private static final int DATABASE_VERSION = 1;

    BakingAppDbHelper(Context ct) {
        super(ct, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " +
                BakingAppDbContract.BakingAppEntry.TABLE_NAME+" ("+
                BakingAppDbContract.BakingAppEntry._ID + " INTEGER PRIMARY KEY, " +
                BakingAppDbContract.BakingAppEntry.COLUMN_INGREDIENT + " TEXT NOT NULL, " +
                BakingAppDbContract.BakingAppEntry.COLUMN_QUANTITY + " DOUBLE NOT NULL, " +
                BakingAppDbContract.BakingAppEntry.COLUMN_MEASURE + " TEXT NOT NULL)";
        Log.d("onCreateSqlite",CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BakingAppDbContract.BakingAppEntry.TABLE_NAME);
        onCreate(db);
    }
}
