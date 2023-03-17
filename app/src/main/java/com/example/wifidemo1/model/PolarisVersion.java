package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Table;

@Table("PolarisVersion")
public class PolarisVersion extends BaseModel {
    public FirmWareVersionInfo firmWareVersionInfo;
    public ExtraDevVersionInfo extraDevVersionInfo;

    @Override
    public String toString() {
        return "\n" + firmWareVersionInfo + "\n" + extraDevVersionInfo;
    }
}
