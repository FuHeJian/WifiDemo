package com.example.wifidemo1.model;

import android.net.Network;

public class MyNetwork {
    public Network network;
    public boolean available;
    public boolean isWifi;

    public MyNetwork(Network network) {



        this.network = network;
    }

    @Override
    public String toString() {
        return "MyNetwork{" +
                "network=" + network +
                ", available=" + available +
                ", isWifi=" + isWifi +
                '}';
    }
}
