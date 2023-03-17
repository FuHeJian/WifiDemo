package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Table;

import java.util.Objects;

@Table("GimbalPosition")
public class GimbalPosition extends BaseModel{
    @Override
    public String toString() {
        return "GimbalPosition{" +
                "x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", z='" + z + '\'' +
                '}';
    }

    public GimbalPosition(){}

    public GimbalPosition(String x, String y, String z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String x;
    public String y;
    public String z;


    public void reset(){
        x="0";
        y="0";
        z="0";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GimbalPosition that = (GimbalPosition) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y) && Objects.equals(z, that.z);
    }

    public static GimbalPosition copy(GimbalPosition gimbalPosition){
        return new GimbalPosition(gimbalPosition.x,gimbalPosition.y,gimbalPosition.z);
    }

}
