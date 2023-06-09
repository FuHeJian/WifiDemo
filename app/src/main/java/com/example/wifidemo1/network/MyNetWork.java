package com.example.wifidemo1.network;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;

/**
 * @author: fuhejian
 * @date: 2023/5/26
 */
public class MyNetWork {

    private String mBaseUrl;

    public MyNetWork(String baseUrl) {
        mBaseUrl = baseUrl;

        init();

    }

    public OkHttpClient mOkHttpClient;


    private Gson mGson = new Gson();

    void init() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

/*      HttpLoggingInterceptor logger = new HttpLoggingInterceptor();

        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(logger);*/

        builder.addNetworkInterceptor(chain -> {

            Request request = chain.request();

            HttpUrl url = request.url().newBuilder()
                    .build();

            //转成json格式
            RequestBody body = request.body();
            HashMap<String, String> newBody = new HashMap<>();
            if (body instanceof FormBody) {
                for (int i = 0; i < ((FormBody) body).size(); i++) {
                    newBody.put(((FormBody) body).encodedName(i), ((FormBody) body).encodedValue(i));
                }
            }

            MediaType mediaType = MediaType.parse("application/json");
            String requestBody = mGson.toJson(newBody);

            request = request.newBuilder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")//设置成json格式
                    .method(request.method(), RequestBody.create(requestBody.getBytes()))
                    .build();

            RequestBody requestBody2 = request.body();

            Buffer buffer = new Buffer();
            requestBody2.writeTo(buffer);

            //输出请求体
            System.out.println("请求体->" + buffer.readUtf8());

            return chain.proceed(request);
        });


        mOkHttpClient = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApiService = retrofit.create(ApiService.class);

    }

    private ApiService mApiService;

    public ApiService getApiService() {
        return mApiService;
    }

    public interface ApiService {

        @GET("{path}")
        Call<ResponseBody> get(@Path("path") String path, @QueryMap Map<String, String> params, @HeaderMap Map<String, String> headers);

        @FormUrlEncoded
        @POST("{path}")
        Call<ResponseBody> post(@Path("path") String path, @FieldMap Map<String, String> params, @HeaderMap Map<String, String> headers);

        @Streaming
        Call<ResponseBody> getInputStream(@Path("path") String path, @QueryMap Map<String, String> params, @HeaderMap Map<String, String> headers);

    }


}
