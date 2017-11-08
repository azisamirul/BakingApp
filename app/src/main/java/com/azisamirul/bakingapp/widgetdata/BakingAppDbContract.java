package com.azisamirul.bakingapp.widgetdata;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by azisamirul on 11/10/2017.
 */

public class BakingAppDbContract {
    public static final String AUTHORITY="com.azisamirul.bakingapp";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+AUTHORITY);
    public static final String PATH_BAKING_APP="bakingapp";

    public static final class BakingAppEntry implements BaseColumns{
        public static final Uri CONTENT_URI=
                BASE_CONTENT_URI.buildUpon()
                        .appendEncodedPath(PATH_BAKING_APP)
                .build();
        public static final String TABLE_NAME="bakingapp";
        public static final String COLUMN_QUANTITY="quantity";
        public static final String COLUMN_MEASURE="measure";
        public static final String COLUMN_INGREDIENT="ingredient";
    }
}
