package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Table;


@Table("ExtraDevVersionInfo")
public class ExtraDevVersionInfo extends BaseModel {
    private static final long serialVersionUID = 1234567891006L;
    private String exAxisVersion;
    private String romUrl;//升级包地址
    private String romSize;//包大小
    private String romFileName;//系统固件名称
    private String romReleaseDate;//更新日期
    private String romReleaseDescription;//更新描述

    public String getExAxisVersion() {
        return exAxisVersion;
    }

    public void setExAxisVersion(String exAxisVersion) {
        this.exAxisVersion = exAxisVersion;
    }


    public String getRomUrl() {
        return romUrl;
    }

    public void setRomUrl(String romUrl) {
        this.romUrl = romUrl;
    }

    public String getRomSize() {
        return romSize;
    }

    public void setRomSize(String romSize) {
        this.romSize = romSize;
    }

    public String getRomFileName() {
        return romFileName;
    }

    public void setRomFileName(String romFileName) {
        this.romFileName = romFileName;
    }

    public String getRomReleaseDate() {
        return romReleaseDate;
    }

    public void setRomReleaseDate(String romReleaseDate) {
        this.romReleaseDate = romReleaseDate;
    }

    public String getRomReleaseDescription() {
        return romReleaseDescription;
    }

    public void setRomReleaseDescription(String romReleaseDescription) {
        this.romReleaseDescription = romReleaseDescription;
    }


    @Override
    public String toString() {
        return "ExtraDevVersionInfo{" +
                " exAxisVersion = " + exAxisVersion +
                ", romFileName = " + romFileName +
                ", romUrl = " + romUrl +
                ", romReleaseDescription = " + romReleaseDescription +
                ", romSize = " + romSize +
                ", romReleaseDate = " + romReleaseDate +
                ", romReleaseDescription = " + romReleaseDescription +

                "} ";
    }
}
