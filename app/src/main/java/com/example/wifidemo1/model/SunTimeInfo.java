package com.example.wifidemo1.model;

import java.util.Calendar;

public class SunTimeInfo {

    public Calendar sunsetTime;
    public Calendar sunriseTime;


    public boolean sunTimeAvailable() {
        if (sunsetTime == null || sunriseTime == null)
            return false;
        return true;
    }

    public long getSunRiseTime(){
        if(sunriseTime==null)
            return System.currentTimeMillis();
        return sunriseTime.getTime().getTime();
    }

    public long getSunSetTime(){
        if(sunsetTime==null)
            return System.currentTimeMillis();
        return sunsetTime.getTime().getTime();
    }
}
