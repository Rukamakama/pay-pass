package com.example.paypass.controler;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Singleton {

    private static Singleton myInstance;
    private static RequestQueue requestQueue;

    private Singleton(Context context){
        requestQueue = getRequestQueue(context);
    }

    public static synchronized Singleton getInstance(Context context){
        if (myInstance == null){
            myInstance = new Singleton(context);
        }
        return myInstance;
    }

    private RequestQueue getRequestQueue(Context context){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

     public <T> void addToRequestqueue(Request<T> request){
        requestQueue.add(request);
    }

}
