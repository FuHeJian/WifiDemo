package com.example.wifidemo1.model;

import java.io.Serializable;

/**
 * Created by HLW on 2019/8/22.
 */

public class BroadcastActionEvent implements Serializable {

    private String action;
    private String extra;
    private String extra1;
    private String extra2;


    private String extra3;
    private float aFloat;
    private float bFloat;

    private boolean aBoolean;
    private boolean aBoolean1;
    private boolean aBoolean2;
    private boolean aBoolean3;


    private byte aByte;
    private int anInt1;
    private int anInt2;
    private int anInt3;
    private int anInt4;
    private int anInt5;
    private int anInt6;
    private int anInt7;
    private int anInt8;
    private ConstellationModel constellationModel;

    public BroadcastActionEvent(String action) {
        this.action = action;
    }


    public BroadcastActionEvent(String action, ConstellationModel constellationModel) {
        this.action = action;
        this.constellationModel = constellationModel;
    }


    public BroadcastActionEvent(String action, int anInt1) {
        this.action = action;
        this.anInt1 = anInt1;
    }


    public BroadcastActionEvent(String action, float aFloat, float bFloat) {
        this.action = action;
        this.aFloat = aFloat;
        this.bFloat = bFloat;
    }


    public BroadcastActionEvent(String action, byte aByte) {
        this.action = action;
        this.aByte = aByte;
    }


    public BroadcastActionEvent(String action, int anInt1, byte aByte) {
        this.action = action;
        this.anInt1 = anInt1;
        this.aByte = aByte;
    }

    public BroadcastActionEvent(String action, int anInt1, int anInt2) {
        this.action = action;
        this.anInt1 = anInt1;
        this.anInt2 = anInt2;
    }

    public BroadcastActionEvent(String action, int anInt1, boolean aBoolean) {
        this.action = action;
        this.anInt1 = anInt1;
        this.aBoolean = aBoolean;
    }


    public BroadcastActionEvent(String action, int anInt1, int anInt2, boolean aBoolean) {
        this.action = action;
        this.anInt1 = anInt1;
        this.anInt2 = anInt2;
        this.aBoolean = aBoolean;
    }


    public BroadcastActionEvent(String action, int anInt1, boolean aBoolean, boolean aBoolean1) {
        this.action = action;
        this.anInt1 = anInt1;
        this.aBoolean = aBoolean;
        this.aBoolean1 = aBoolean1;
    }


    public BroadcastActionEvent(String action, int anInt1, int anInt2, boolean aBoolean, boolean aBoolean1) {
        this.action = action;
        this.anInt1 = anInt1;
        this.anInt2 = anInt2;
        this.aBoolean = aBoolean;
        this.aBoolean1 = aBoolean1;
    }

    public BroadcastActionEvent(String action, boolean aBoolean) {
        this.action = action;
        this.aBoolean = aBoolean;
    }

    public BroadcastActionEvent(String action, boolean aBoolean, boolean aBoolean1) {
        this.action = action;
        this.aBoolean = aBoolean;
        this.aBoolean1 = aBoolean1;
    }

    public BroadcastActionEvent(String action, boolean aBoolean, boolean aBoolean1, boolean aBoolean2, String extra) {
        this.action = action;
        this.aBoolean = aBoolean;
        this.aBoolean1 = aBoolean1;
        this.aBoolean2 = aBoolean2;
        this.extra = extra;
    }

    public BroadcastActionEvent(String action, boolean aBoolean, boolean aBoolean1, boolean aBoolean2) {
        this.action = action;
        this.aBoolean = aBoolean;
        this.aBoolean1 = aBoolean1;
        this.aBoolean2 = aBoolean2;
    }


    public BroadcastActionEvent(String action, boolean aBoolean, boolean aBoolean1, boolean aBoolean2, boolean aBoolean3) {
        this.action = action;
        this.aBoolean = aBoolean;
        this.aBoolean1 = aBoolean1;
        this.aBoolean2 = aBoolean2;
        this.aBoolean3 = aBoolean3;
    }

    public BroadcastActionEvent(String action, String extra) {
        this.action = action;
        this.extra = extra;
    }

    public BroadcastActionEvent(String action, String extra, String extra1) {
        this.action = action;
        this.extra = extra;
        this.extra1 = extra1;

    }

    public BroadcastActionEvent(String action, String extra, String extra1, String extra2) {
        this.action = action;
        this.extra = extra;
        this.extra1 = extra1;
        this.extra2 = extra2;
    }

    public BroadcastActionEvent(String action, String extra, String extra1, String extra2, String extra3) {
        this.action = action;
        this.extra = extra;
        this.extra1 = extra1;
        this.extra2 = extra2;
        this.extra3 = extra3;
    }

    public BroadcastActionEvent(String action, String extra, int anInt1) {
        this.action = action;
        this.extra = extra;
        this.anInt1 = anInt1;
    }
    public BroadcastActionEvent(String action, String extra, int anInt1,boolean aBoolean) {
        this.action = action;
        this.extra = extra;
        this.anInt1 = anInt1;
        this.aBoolean = aBoolean;
    }

    public BroadcastActionEvent(String action, String extra, int anInt1, int anInt2) {
        this.action = action;
        this.extra = extra;
        this.anInt1 = anInt1;
        this.anInt2 = anInt2;
    }


    public BroadcastActionEvent(String action, String extra, boolean aBoolean) {
        this.action = action;
        this.aBoolean = aBoolean;
        this.extra = extra;
    }


    public BroadcastActionEvent(String action, String extra, String extra1, boolean aBoolean) {
        this.action = action;
        this.aBoolean = aBoolean;
        this.extra = extra;
        this.extra1= extra1;
    }

    public BroadcastActionEvent(String action, boolean aBoolean, boolean aBoolean1, boolean aBoolean2, String extra, String extra1) {
        this.action = action;
        this.aBoolean = aBoolean;
        this.aBoolean1 = aBoolean1;
        this.aBoolean2 = aBoolean2;
        this.extra = extra;
        this.extra1 = extra1;
    }

    public BroadcastActionEvent(String action, int anInt1, int anInt2, int anInt3, int anInt4, int anInt5) {
        this.action = action;
        this.anInt1 = anInt1;
        this.anInt2 = anInt2;
        this.anInt3 = anInt3;
        this.anInt4 = anInt4;
        this.anInt5 = anInt5;
    }

    public BroadcastActionEvent(String action, int anInt1, int anInt2, int anInt3, int anInt4, int anInt5, int anInt6, int anInt7, int anInt8) {
        this.action = action;
        this.anInt1 = anInt1;
        this.anInt2 = anInt2;
        this.anInt3 = anInt3;
        this.anInt4 = anInt4;
        this.anInt5 = anInt5;
        this.anInt6 = anInt6;
        this.anInt7 = anInt7;
        this.anInt8 = anInt8;
    }

    public ConstellationModel getConstellationModel() {
        return constellationModel;
    }

    public byte getaByte() {
        return aByte;
    }

    public void setaByte(byte aByte) {
        this.aByte = aByte;
    }

    public int getAnInt8() {
        return anInt8;
    }

    public int getAnInt7() {
        return anInt7;
    }

    public int getAnInt6() {
        return anInt6;
    }

    public int getAnInt3() {
        return anInt3;
    }

    public int getAnInt4() {
        return anInt4;
    }

    public int getAnInt5() {
        return anInt5;
    }

    public boolean isaBoolean3() {
        return aBoolean3;
    }

    public void setaBoolean3(boolean aBoolean3) {
        this.aBoolean3 = aBoolean3;
    }

    public boolean isaBoolean1() {
        return aBoolean1;
    }

    public void setaBoolean1(boolean aBoolean1) {
        this.aBoolean1 = aBoolean1;
    }

    public boolean isaBoolean2() {
        return aBoolean2;
    }

    public void setaBoolean2(boolean aBoolean2) {
        this.aBoolean2 = aBoolean2;
    }

    public int getAnInt1() {
        return anInt1;
    }

    public void setAnInt1(int anInt1) {
        this.anInt1 = anInt1;
    }

    public int getAnInt2() {
        return anInt2;
    }

    public void setAnInt2(int anInt2) {
        this.anInt2 = anInt2;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getExtra1() {
        return extra1;
    }

    public String getExtra2() {
        return extra2;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }


    public float getaFloat() {
        return aFloat;
    }

    public float getbFloat() {
        return bFloat;
    }


    public String getExtra3() {
        return extra3;
    }

    public void setExtra3(String extra3) {
        this.extra3 = extra3;
    }
}
