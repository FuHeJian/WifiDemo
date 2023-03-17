package com.example.wifidemo1.model;

public class RemoteDeviceInfo {


    public String socketIP;
    public int socketPort;
    public boolean socketStatus;

    public String streamUrl;
    public boolean streamStatus;

    public String resourceUrl;
    public boolean resourceStatus;

    @Override
    public String toString() {
        return "RemoteDeviceInfo{" +
                "socketIP='" + socketIP + '\'' +
                "socketPort='" + socketPort + '\'' +
                ", socketStatus='" + socketStatus + '\'' +
                ", streamUrl='" + streamUrl + '\'' +
                ", streamStatus='" + streamStatus + '\'' +
                ", resourceUrl='" + resourceUrl + '\'' +
                ", resourceStatus='" + resourceStatus + '\'' +
                '}';
    }
}
