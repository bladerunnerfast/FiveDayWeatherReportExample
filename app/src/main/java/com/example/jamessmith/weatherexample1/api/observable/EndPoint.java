package com.example.jamessmith.weatherexample1.api.observable;

import com.example.jamessmith.weatherexample1.api.model.Model;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by James on 31/07/2017.
 */

public interface EndPoint {
    @GET("/data/2.5/forecast?&appid=b7e22ec66926eea5b6755f40b31a6b65")
    Observable<Model> getWeatherReport(@Query("lat") String lat, @Query("lon") String lon);
}
