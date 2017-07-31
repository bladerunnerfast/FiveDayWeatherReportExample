package com.example.jamessmith.weatherexample1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jamessmith.weatherexample1.api.model.Model;
import com.example.jamessmith.weatherexample1.api.observable.EndPoint;
import com.example.jamessmith.weatherexample1.api.rest.RestAdapter;
import com.example.jamessmith.weatherexample1.fiveday.Adapter;
import com.example.jamessmith.weatherexample1.fiveday.FiveDayModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    private List<FiveDayModel> fiveDayData;
    private FiveDayModel fiveDayFiveDayModel;
    private String[] days;
    private Model mModel;

    TextView _country, _location, _temperture, _pressureLevel, _hulmidityLevel, _windSpeed, _groundLevel, _seaLevel;
    ImageView _icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _country = (TextView) findViewById(R.id.tv_country);
        _location = (TextView) findViewById(R.id.tv_location);
        _temperture = (TextView) findViewById(R.id.tv_main_state);
        _pressureLevel = (TextView) findViewById(R.id.tv_pressure);
        _hulmidityLevel = (TextView) findViewById(R.id.tv_humidity);
        _windSpeed = (TextView) findViewById(R.id.tv_wind_speed);
        _groundLevel = (TextView) findViewById(R.id.tv_ground_level);
        _seaLevel = (TextView) findViewById(R.id.tv_sea_level);
        recyclerView = (RecyclerView) findViewById(R.id.rv_five_day_list);
        _icon = (ImageView) findViewById(R.id.iv_icon_main);

        intentFilter = new IntentFilter("updateAdapter");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null){
                    int position = intent.getIntExtra("index", 0);
                    String selectedDay = intent.getStringExtra("selectedDay");
                    Log.v(MainActivity.class.getName(), selectedDay);
                    updateMainUI(position);
                }
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init(){
        mModel = new Model();
        fiveDayData = new ArrayList<>();
        days = new String[5];
        downloadWeatherReport();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sd = new SimpleDateFormat("EEEE");
        String dayofweek = sd.format(c.getTime());
        days[0] = dayofweek;

        for(int i = 1; i < 5; i++){
            c.add(Calendar.DATE, 1);
            days[i] = sd.format(c.getTime());
        }
    }

    private void updateMainUI(int index){
        _country.setText("UK");
        _location.setText(mModel.getCity().getName());

        if(mModel.getList().get(index).getWeather().get(0).getIcon() != null){
            Glide.with(this)
                    .load(mModel.getList().get(index).getWeather().get(0).getIcon()+".png")
                    .asBitmap()
                    .fitCenter()
                    .centerCrop()
                    .into(_icon);
        }

        _temperture.setText(mModel.getList().get(index).getWeather().get(0).getDescription());
        _pressureLevel.setText(mModel.getList().get(index).getMain().getPressure() + " hPa");
        _hulmidityLevel.setText(mModel.getList().get(index).getMain().getHumidity() + " %");
        _windSpeed.setText(mModel.getList().get(index).getWind().getSpeed() + " mph");
        _groundLevel.setText(mModel.getList().get(index).getMain().getGrndLevel().toString());
        _seaLevel.setText(mModel.getList().get(index).getMain().getSeaLevel().toString());
    }

    private void downloadWeatherReport() {
        final RestAdapter restAdapter = new RestAdapter();
        EndPoint api = restAdapter.getRest().build().create(EndPoint.class);

        restAdapter.getCompositeSubscription().add(api.getWeatherReport("51.5074", "0.1278")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Model>() {

                    @Override
                    public void onCompleted() {
                        restAdapter.getCompositeSubscription().unsubscribe();
                        updateMainUI(0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            restAdapter.getCompositeSubscription().unsubscribe();
                            Log.v(MainActivity.class.getName(), e.toString());
                        }
                    }

                    @Override
                    public void onNext(Model model) {

                        mModel = model;
                        for (int i = 0; i < model.getList().size() && i < 5; i++) {
                            fiveDayFiveDayModel = new FiveDayModel(days[i], model.getList().get(i).getMain().getTemp().toString(),
                                    model.getList().get(i).getWeather().get(0).getIcon());
                            fiveDayData.add(fiveDayFiveDayModel);
                        }
                        init5Day();
                    }
                })
        );
    }

    private void init5Day() {
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        adapter = new Adapter(fiveDayData, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            unregisterReceiver(broadcastReceiver);
        }catch(Exception e){}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(broadcastReceiver);
        }catch(Exception e){}
    }
}
