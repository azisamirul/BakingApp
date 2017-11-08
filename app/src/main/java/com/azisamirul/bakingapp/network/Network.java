package com.azisamirul.bakingapp.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.azisamirul.bakingapp.cons.Cons;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by azisamirul on 23/08/2017.
 */

public class Network {
    Context ct;

    public Network(Context ct) {
        this.ct = ct;
    }


    public Retrofit initialize() {
        if (checkNetwork()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Cons.URL_DATA)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit;
        } else {
            Toast.makeText(ct, "No Internet Connection", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public boolean checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ct.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}

