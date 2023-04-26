package com.example.wifidemo1.customview.helper.scrollerview;

public class ScorllPickerFocusItem {
    public String string;
    public float integer;
    public float x;
    public float y;

    public ScorllPickerFocusItem(String string, float integer,float x,float y) {
        this.string = string;
        this.integer = integer;
        this.x = x;
        this.y = y;
    }


    @Override
    public String toString() {
        return "" + integer;
    }
}
