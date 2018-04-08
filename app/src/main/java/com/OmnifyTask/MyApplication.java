package com.OmnifyTask;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by chavali on 2018-04-07.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Relam intilization for setup
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("onmifytask.realm").build();
        Realm.setDefaultConfiguration(config);

    }
}
