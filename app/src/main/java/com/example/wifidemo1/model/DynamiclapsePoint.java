package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Table;

@Table("DynamiclapsePoint")
public class DynamiclapsePoint extends BaseModel {
    private String x;
    private String y;
    private String z;

    public DynamiclapsePoint(String x, String y, String z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }


    public String getZ() {
        return z;
    }


}
