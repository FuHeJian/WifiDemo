package com.example.wifidemo1.network.retrofit.i;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author: fuhejian
 * @date: 2023/3/11
 */
public interface PolarisResourceRetrofitService {


    @Streaming
    @GET
    Call<ResponseBody> downloadRom(@Url() String url);

}
