package com.example.instagramdownloader;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    SharedPreferences sharedPreferences;
    SharedPref sharedPref=new SharedPref();

    public SharedPref getInstance(Context context){

        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        return sharedPref;
    }


    public void putBoolean ( String key, Boolean data){
        sharedPreferences.edit().putBoolean(key,data).apply();
    }

    public boolean getBoolean(String key , Boolean data ){
        return sharedPreferences.getBoolean(key,data);
    }

    public void putString(String key, String data) {
        sharedPreferences.edit().putString(key, data).apply();
    }
    public String getString(String key){
        return sharedPreferences.getString(key, "");
    }



}
