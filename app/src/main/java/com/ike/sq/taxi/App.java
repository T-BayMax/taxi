package com.ike.sq.taxi;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;

import com.ike.sq.taxi.db.DBManager;
import com.zhy.autolayout.config.AutoLayoutConifg;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by T-BayMax on 2017/5/12.
 */

public class App extends MultiDexApplication {
    public static Map<String, Activity> activityMap = new HashMap<String, Activity>(0);
    @Override
    public void onCreate()
    {
        super.onCreate();
        AutoLayoutConifg.getInstance().useDeviceSize().init(this);
        DBManager.copyDB(getApplicationContext());
    }
}
