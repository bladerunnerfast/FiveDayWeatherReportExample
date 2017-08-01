package com.example.jamessmith.weatherexample1.fiveday;

/**
 * Created by James on 31/07/2017.
 */

public class FiveDayModel {

    private String day, temp, icon;

    public FiveDayModel(String day, String temp, String icon){
        this.day = day;
        this.temp = temp;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public String getDay() {
        return day;
    }

    public String getTemp() {
        return temp;
    }
}
