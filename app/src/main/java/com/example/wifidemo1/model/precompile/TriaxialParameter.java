package com.example.wifidemo1.model.precompile;

import com.litesuits.orm.db.annotation.Table;
import com.example.wifidemo1.model.BaseModel;

@Table("TriaxialParameter")
public class TriaxialParameter extends BaseModel {
    public int time;
    public boolean isLineModel;
    public boolean isEditing;

    public String panPosition;
    public String tiltPosition;
    public String rollPosition;

    public TriaxialParameter(String panPosition, String tiltPosition, String rollPosition) {
        this.panPosition = panPosition;
        this.tiltPosition = tiltPosition;
        this.rollPosition = rollPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TriaxialParameter that = (TriaxialParameter) o;
        return time == that.time;
    }

    @Override
    public String toString() {
        return "TriaxialParameter{" +
                "time=" + time +
                ",isLineModel=" + isLineModel +
                ",panPosition=" + panPosition +
                ", tiltPosition=" + tiltPosition +
                ", rollPosition=" + rollPosition +
                '}';
    }
}
