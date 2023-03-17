package com.example.wifidemo1.model;

/**
 * Created by Administrator on 2017/1/9.
 */

public class VersionInfo extends BaseModel {

    private String id;
    private String versionName;//版本名
    private String versionCode;//版本号
    private String versionDesc;//版本描述信息内容
    private String downloadUrl;//新版本的下载路径
    private String versionSize;//版本大小

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getVersionSize() {
        return versionSize;
    }

    public void setVersionSize(String versionSize) {
        this.versionSize = versionSize;
    }

    @Override
    public String toString() {
        return "versionName =" + versionName +
                ",versionCode=" + versionCode +
                ",versionDesc=" + versionDesc +
                ",downloadUrl=" + downloadUrl +
                ",versionSize=" + versionSize +
                super.toString();
    }
}
