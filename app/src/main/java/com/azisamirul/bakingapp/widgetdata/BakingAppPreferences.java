package com.azisamirul.bakingapp.widgetdata;

import android.content.Context;
import android.content.SharedPreferences;

import com.azisamirul.bakingapp.R;

import static com.azisamirul.bakingapp.cons.Cons.PREFERENCES_WIDGET_RECIPE_NAME;

/**
 * Created by azisamirul on 14/10/2017.
 */

public class BakingAppPreferences {
  Context context;
    SharedPreferences sharedPreferences;
    public BakingAppPreferences(Context context){
        this.context=context;
   sharedPreferences=context.getSharedPreferences(PREFERENCES_WIDGET_RECIPE_NAME,Context.MODE_PRIVATE);
    }
    public void putRecipeName(String name){

        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(PREFERENCES_WIDGET_RECIPE_NAME,name);
        editor.commit();
    }
    public void removeRecipeName(){
        if(getRecipeName()!=null||getRecipeName()!="") {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(PREFERENCES_WIDGET_RECIPE_NAME);
            editor.commit();
        }
    }
    public String getRecipeName(){
     String name=sharedPreferences.getString(PREFERENCES_WIDGET_RECIPE_NAME,context.getString(R.string.app_name));
        return name;
    }
}

