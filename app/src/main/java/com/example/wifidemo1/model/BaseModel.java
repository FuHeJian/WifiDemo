package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by wku on 2017/7/21.
 */
@Table("BaseMode")
public abstract class BaseModel implements Serializable {
    private static final long serialVersionUID = 1234567891001L;
    // 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    // 指定列名
    @Column("_id")
    public int id;
    public String TAG = this.getClass().getSimpleName();

    @Override
    public String toString() {
        return "BaseModel{" +
                "id=" + id +
                '}';
    }
}
