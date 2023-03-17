package com.example.wifidemo1.model;

public class ResponseModel {
    public int code;
    public String message;

    public ResponseModel(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "" + code + ":" + message;
    }
}
