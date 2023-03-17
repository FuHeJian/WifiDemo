package com.example.wifidemo1.socket;


import androidx.annotation.NonNull;

import com.example.wifidemo1.log.MyLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 * @author: fuhejian
 * @date: 2023/3/13
 */
public class ResponseCode {

    public int order;
    public Code retCode;

    public Code stateCode;

    public Code hwCode;

    public Code swCode;

    public Code sdHintCode;

    public ResponseCode() {

    }


    static public ResponseCode parseToResponseCode(String s) {
        ResponseCode responseCode = new ResponseCode();
        try {
            responseCode.order = Integer.parseInt(s.substring(0,s.indexOf("@")));
            s = s.substring(s.indexOf("@")+1,s.length()-1);
            String[] parses = s.split(";");
            ArrayList<Code> codes = new ArrayList<>();
            for (int i = 0; i < parses.length; i++) {
                String[] singleCode = parses[i].trim().split(":");
                if (singleCode.length == 2) {
                    Code aCode = new Code(singleCode[0], singleCode[1]);
                    codes.add(aCode);
                    switch (singleCode[0]) {
                        case "ret": {
                            responseCode.retCode = aCode;
                            break;
                        }
                        case "state": {
                            responseCode.stateCode = aCode;
                            break;
                        }
                        case  "hw":{
                            responseCode.hwCode = aCode;
                            break;
                        }
                        case  "sw":{
                            responseCode.swCode = aCode;
                            break;
                        }
                        case "hintId":{
                            responseCode.sdHintCode = aCode;
                            break;
                        }
                        default: {

                        }
                    }
                }
            }
        }catch (Exception e){
            MyLog.printLog("当前类:ResponseCode,当前方法：parseToResponseCode,信息:解析失败");
            return null;
        }
        return responseCode;
    }

    public static class Code {

        public String code;
        public String value;

        public Code(String _code, String _value) {
            code = _code;
            value = _value;
        }
    }

    @NonNull
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
