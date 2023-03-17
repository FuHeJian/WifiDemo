package com.example.wifidemo1.model;

import com.google.gson.annotations.SerializedName;

public class FrpProxyResult {
    @SerializedName("code")
    private String code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("socket")
    private SocketInfo socket;
    @SerializedName("stream")
    private StreamInfo stream;
    @SerializedName("resource")
    private ResourceInfo resource;
    @SerializedName("mqtt")
    private MqttInfo mqtt;

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

    public SocketInfo getSocket() {
        return socket;
    }

    public void setSocket(SocketInfo socket) {
        this.socket = socket;
    }

    public StreamInfo getStream() {
        return stream;
    }

    public void setStream(StreamInfo stream) {
        this.stream = stream;
    }

    public ResourceInfo getResource() {
        return resource;
    }

    public void setResource(ResourceInfo resource) {
        this.resource = resource;
    }

    public MqttInfo getMqtt() {
        return mqtt;
    }

    public void setMqtt(MqttInfo mqtt) {
        this.mqtt = mqtt;
    }

    public static class SocketInfo {
        @SerializedName("url")
        private String url;
        @SerializedName("status")
        private String status;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class StreamInfo {
        @SerializedName("url")
        private String url;
        @SerializedName("status")
        private String status;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class ResourceInfo {
        @SerializedName("url")
        private String url;
        @SerializedName("status")
        private String status;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class MqttInfo {
        @SerializedName("user")
        private String user;
        @SerializedName("host")
        private String host;
        @SerializedName("port")
        private Integer port;
        @SerializedName("url")
        private String url;
        @SerializedName("loginTime")
        private Long loginTime;
        @SerializedName("status")
        private String status;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Long getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(Long loginTime) {
            this.loginTime = loginTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}
