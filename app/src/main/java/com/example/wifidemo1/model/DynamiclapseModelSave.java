package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Table;

@Table("DynamiclapseModelSave")
public class DynamiclapseModelSave extends BaseModel {
    public float interval;
    public int pictureCount;
    public DynamiclapsePoint point;
}
