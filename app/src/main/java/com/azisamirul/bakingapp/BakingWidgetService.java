package com.azisamirul.bakingapp;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by azisamirul on 08/10/2017.
 */

public class BakingWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingRemoteViewFactory(this.getApplicationContext());
    }

}