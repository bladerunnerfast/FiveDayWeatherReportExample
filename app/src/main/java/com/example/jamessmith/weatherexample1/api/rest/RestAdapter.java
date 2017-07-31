package com.example.jamessmith.weatherexample1.api.rest;

import android.util.Log;

import com.example.jamessmith.weatherexample1.api.base.BaseLink;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by James on 31/07/2017.
 */

public class RestAdapter {

    private CompositeSubscription compositeSubscription;
    private retrofit.RestAdapter.Builder rest;

    public RestAdapter(){
        initWebServices();
    }

    private void initWebServices() {
        compositeSubscription = new CompositeSubscription();
        rest = new retrofit.RestAdapter.Builder();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        rest.setEndpoint(BaseLink.getUrl())
                .setLogLevel(retrofit.RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        Log.v(RestAdapter.class.getName(), cause.getMessage());
                        return null;
                    }
                })
                .build();
    }

    public CompositeSubscription getCompositeSubscription() {
        return compositeSubscription;
    }

    public retrofit.RestAdapter.Builder getRest() {
        return rest;
    }
}
