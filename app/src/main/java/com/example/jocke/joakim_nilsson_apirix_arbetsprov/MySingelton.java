package com.example.jocke.joakim_nilsson_apirix_arbetsprov;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Jocke on 2016-11-30.
 */

public class MySingelton {

    private static MySingelton mInstance;
    private static Context context;
    private RequestQueue requestQueue;

    private MySingelton(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue() {

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized MySingelton getmInstance(Context context) {

        if(mInstance == null) {
            mInstance = new MySingelton(context);
        }
        return mInstance;
    }
    public<T> void addToRequestQue(Request<T> request){
        getRequestQueue().add(request);
    }
}
