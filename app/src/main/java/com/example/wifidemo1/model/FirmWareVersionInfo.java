package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Table;


@Table("FirmwareModel")
public class FirmWareVersionInfo extends BaseModel {
    private static final long serialVersionUID = 1234567891006L;
    private String hardwareVersion;
    private String softwareVersion;
    private String romUrl;//升级包地址
    private String romSize;//包大小
    private String romFileName;//系统固件名称
    private String romReleaseDate;//更新日期
    private String romReleaseDescription;//更新描述

    private String romMd5;//下载完成后的校验

    public void setRomMd5(String romMd5) {
        this.romMd5 = romMd5;
    }

    public String getRomMd5() {
        return romMd5;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
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
        return "FirmWareVersionInfo{" +
                " softwareVersion = " + softwareVersion +
                ", romFileName = " + romFileName +
                " hardwareVersion = " + hardwareVersion +
                ", romUrl = " + romUrl +
                ", romReleaseDescription = " + romReleaseDescription +
                ", romSize = " + romSize +
                ", romReleaseDate = " + romReleaseDate +
                "} ";
    }
}
