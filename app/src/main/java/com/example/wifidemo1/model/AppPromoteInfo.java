package com.example.wifidemo1.model;

import com.google.gson.annotations.SerializedName;

public class AppPromoteInfo {

    @SerializedName("code")
    private String code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private DataInfo data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataInfo getData() {
        return data;
    }

    public void setData(DataInfo data) {
        this.data = data;
    }

    public static class DataInfo {
        @SerializedName("show")
        private Integer show;
        @SerializedName("title")
        private String title;
        @SerializedName("pageUrl")
        private String pageUrl;
        @SerializedName("logo")
        private String logo;
        @SerializedName("productPageUrl")
        private String productPageUrl;
        @SerializedName("cache")
        private Integer cache;
        @SerializedName("startTimeStamp")
        private Long startTimeStamp;
        @SerializedName("endTimeStamp")
        private Long endTimeStamp;
        @SerializedName("updateTimeStamp")
        private Long updateTimeStamp;

        public Integer getShow() {
            return show;
        }

        public void setShow(Integer show) {
            this.show = show;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPageUrl() {
            return pageUrl;
        }

        public void setPageUrl(String pageUrl) {
            this.pageUrl = pageUrl;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getProductPageUrl() {
            return productPageUrl;
        }

        public void setProductPageUrl(String productPageUrl) {
            this.productPageUrl = productPageUrl;
        }

        public Integer getCache() {
            return cache;
        }

        public void setCache(Integer cache) {
            this.cache = cache;
        }

        public Long getStartTimeStamp() {
            return startTimeStamp;
        }

        public void setStartTimeStamp(Long startTimeStamp) {
            this.startTimeStamp = startTimeStamp;
        }

        public Long getEndTimeStamp() {
            return endTimeStamp;
        }

        public void setEndTimeStamp(Long endTimeStamp) {
            this.endTimeStamp = endTimeStamp;
        }

        public Long getUpdateTimeStamp() {
            return updateTimeStamp;
        }

        public void setUpdateTimeStamp(Long updateTimeStamp) {
            this.updateTimeStamp = updateTimeStamp;
        }
    }
}
