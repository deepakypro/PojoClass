package com.thelosers.gyandhanproject;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by deepak on 03/01/18.
 */

public class MyApp  extends Application{

    private static MyApp mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public MyApp() {
    }

    private MyApp(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyApp getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyApp(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


}
