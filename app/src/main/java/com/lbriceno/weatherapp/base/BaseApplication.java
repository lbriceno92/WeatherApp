package com.lbriceno.weatherapp.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by luis_ on 9/30/2016.
 */
public class BaseApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfig = new RealmConfiguration
                .Builder(getApplicationContext())
                .deleteRealmIfMigrationNeeded()
                .name("WeatherRealm")
                .build();
        context = this;
        Realm.setDefaultConfiguration(realmConfig);
        MultiDex.install(this);
    }

    public static Context getContext(){
        return context;
    }
}
