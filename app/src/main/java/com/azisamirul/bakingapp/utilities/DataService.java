package com.azisamirul.bakingapp.utilities;

import com.azisamirul.bakingapp.model.Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by azisamirul on 25/08/2017.
 */

public interface DataService {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Model>> getBakingMenu();
}
