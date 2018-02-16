package org.upv.audiolibros.controller;


import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.upv.audiolibros.AudioBooks;

public class NetworkController {
    private static NetworkController mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private NetworkController(){
        mRequestQueue = Volley.newRequestQueue(AudioBooks.getAppContext());
        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    public static NetworkController getInstance(){
        if(mInstance == null){
            mInstance = new NetworkController();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        return this.mRequestQueue;
    }

    public ImageLoader getImageLoader(){
        return this.mImageLoader;
    }
}
