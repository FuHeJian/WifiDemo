package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Table;

@Table("SunsetSunriseParameter")
public class SunsetSunriseParameter  extends BaseModel{
    public boolean isSunRise;
    public long sunRealTime;
    public long sunTime;
    public long beginTime;
    public long endTime;
    public float interval = 1;

    @Override
    public String toString() {
        return "SunsetSunriseParameter{" +
                "isSunRise=" + isSunRise +
                "sunTime=" + sunTime +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", interval=" + interval +
                '}';
    }
}
