package org.upv.audiolibros;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import org.upv.audiolibros.controller.BooksController;
import org.upv.audiolibros.controller.NetworkController;
import org.upv.audiolibros.database.BooksDatabase;
import org.upv.audiolibros.database.BooksDatabaseSharedPref;
import org.upv.audiolibros.libs.EventBus;
import org.upv.audiolibros.libs.GreenRobotEventBus;

public class AudioBooks extends Application {
    private static AudioBooks mInstance;
    private static Context mAppContext;
    private BooksController controller;
    private NetworkController networkController;

    @Override
    public void onCreate() {
        super.onCreate();
        controller = BooksController.getInstance(getApplicationContext());
        mInstance = this;
        this.setAppContext(getApplicationContext());
        networkController = NetworkController.getInstance();
    }

    public static AudioBooks getInstance(){
        return mInstance;
    }

    public BooksController getBooksController(){
        return controller;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public void setAppContext(Context mAppContext) {
        AudioBooks.mAppContext = mAppContext;
    }

    public SharedPreferences getAppPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public RequestQueue getRequestQueue() {
        return networkController.getRequestQueue();
    }

    public ImageLoader getImageLoader() {
        return networkController.getImageLoader();
    }

    public EventBus getEventBus() {
        return new GreenRobotEventBus(org.greenrobot.eventbus.EventBus.getDefault());
    }

    public BooksDatabase getBooksDatabase() {
        return BooksDatabaseSharedPref.getInstance(getAppContext());
    }
}
