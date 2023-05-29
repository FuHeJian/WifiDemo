package test;

import androidx.annotation.NonNull;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * test
 */
public class Network {
//    .setLevel(HttpLoggingInterceptor.Level.BODY)

    public static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor()).build();

    //    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",7890)))
    public static OkHttpClient proxyClient = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor()).build();

    public static Gson mGson = new Gson();

    public static Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.yingwangtech.net/").addConverterFactory(GsonConverterFactory.create()).client(client).build();
    public static NetInterface retrofit_bianace = retrofit.create(NetInterface.class);

    public static JsonObject getSymbols() throws IOException {
        return mGson.fromJson(retrofit_bianace.getSymbols().execute().body().string(), JsonObject.class);
    }

    public static JsonArray getSymBolKLine_2h(String symbol) throws IOException {
        String url = "api/v3/uiKlines?symbol=" + symbol + "&limit=50&interval=2h";
        return mGson.fromJson(retrofit_bianace.getSymBolKLine_4h(url).execute().body().string(), JsonArray.class);
    }

    public static Retrofit retrofit_wechat = new Retrofit.Builder().baseUrl("https://qyapi.weixin.qq.com/").addConverterFactory(GsonConverterFactory.create()).client(client).build();


    public static NetInterface WeCaht_Service = retrofit_wechat.create(NetInterface.class);

    public static void sendMsg(String msg) throws IOException {
        Response<ResponseBody> execute = WeCaht_Service.sendMessage(mGson.fromJson(msg, JsonObject.class)).execute();
        System.out.println(execute);
    }


    public static Retrofit retrofit_gateIo = new Retrofit.Builder().baseUrl("https://api.gateio.ws/api/v4/").addConverterFactory(GsonConverterFactory.create()).client(proxyClient).build();

    public static NetInterface retrofit_gateIo_service = retrofit_gateIo.create(NetInterface.class);

    public static JsonArray getGateIoSymbols() throws IOException {
        JsonArray jsonElements = mGson.fromJson(retrofit_gateIo_service.getGateIoSymbols().execute().body().string(), JsonArray.class);
        return jsonElements;
    }

    public static JsonArray getSymBolKLine_2h_GateIo(String symbol) throws IOException {
        String url = "spot/candlesticks?currency_pair=" + symbol + "&limit=50&interval=1h";
        return mGson.fromJson(retrofit_gateIo_service.getSymBolKLine_4h_GateIo(url).execute().body().string(), JsonArray.class);
    }

}
