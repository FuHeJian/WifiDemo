package com.example.wifidemo1.network.retrofit.i;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Polaris服务器
 * @author: fuhejian
 * @date: 2023/3/11
 */
public interface PolarisServiceRetrofitService {

    @FormUrlEncoded
    @POST(value = "https://service.snoppa.com/snoppa/user/auth.do?cmd=queryromversion")
    @Headers({"auth-deviceid:snoppa_app_1.0_android"})
    Call<JsonObject> getVersion(@Header("auth-timestamp") String timestamp, @Header("auth-signature") String signature, @Header("Accept-Language") String language, @FieldMap() Map<String,String> body);

}
