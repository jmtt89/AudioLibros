package org.upv.audiolibros;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import org.upv.audiolibros.controller.NetworkController;
import org.upv.audiolibros.database.BooksDatabase;
import org.upv.audiolibros.database.BooksDatabaseSharedPref;

public class AudioBooks extends Application {
    private static AudioBooks mInstance;
    private static Context mAppContext;
    private BooksDatabase database;
    private NetworkController networkController;

    @Override
    public void onCreate() {
        super.onCreate();
        database = new BooksDatabaseSharedPref(getApplicationContext());
        mInstance = this;
        this.setAppContext(getApplicationContext());
        networkController = NetworkController.getInstance();
    }

    public static AudioBooks getInstance(){
        return mInstance;
    }

    public BooksDatabase getDatabase(){
        return database;
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
}
