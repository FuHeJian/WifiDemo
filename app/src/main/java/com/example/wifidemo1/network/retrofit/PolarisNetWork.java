package com.example.wifidemo1.network.retrofit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.example.wifidemo1.App;
import com.example.wifidemo1.customview.MyTimer;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.model.FirmWareVersionInfo;
import com.example.wifidemo1.model.PolarisVersion;
import com.example.wifidemo1.network.NetWorkUtil;
import com.example.wifidemo1.network.retrofit.i.PolarisResourceRetrofitService;
import com.example.wifidemo1.network.retrofit.i.PolarisServiceRetrofitService;
import com.example.wifidemo1.utils.FileUtil;
import com.example.wifidemo1.utils.UpdateVersionUtil;
import com.example.wifidemo1.utils.UrlUtils;
import com.example.wifidemo1.wifi.WifiUtilHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author: fuhejian
 * @date: 2023/3/11
 */
public class PolarisNetWork {

    public static PolarisNetWork INSTANCE;

    static {
        INSTANCE = new PolarisNetWork();
    }

    private final String serviceBaseUrl = "https://service.snoppa.com";
    private final String resourceBaseUrl = "https://service.snoppa.com";

    private Retrofit serviceRetrofit;
    private Retrofit reSourceRetrofit;

    private PolarisServiceRetrofitService polarisServiceRetrofitService;
    private PolarisResourceRetrofitService polarisResourceRetrofitService;

    @SuppressLint("CheckResult")
    public void getVersion(Consumer<PolarisVersion> consumer) throws Exception {
        initRetrofit();
        if (!canNetWork()) return;
        String timeStamp = UpdateVersionUtil.getTimeStampNew();
        String signature = "SNOPPAANDROID@#101" + timeStamp;
        String md5Str = UpdateVersionUtil.getMD5Str(signature);
        String lan = Locale.getDefault().getLanguage();
        Map<String, String> body = new HashMap<>();
        body.put("product_id", UrlUtils.FIRMWARE_PRODUCT_ID);
        Observable.create(new ObservableOnSubscribe<PolarisVersion>() {
            @Override
            public void subscribe(ObservableEmitter<PolarisVersion> e) throws Exception {
                PolarisVersion polarisVersion = new PolarisVersion();
                Call<JsonObject> response = polarisServiceRetrofitService.getVersion(timeStamp, md5Str, lan, body);
                JsonObject jsonObject = response.execute().body();
                FirmWareVersionInfo latestFirmWareVersionInfo = new FirmWareVersionInfo();
                if ("00".equals(jsonObject.get("code").getAsString())) {
                    if (jsonObject.get("current_version") != null) {
                        String version;
                        version = jsonObject.get("current_version").getAsString();
                        if (version.contains("V") || version.contains("v")) {
                            version = version.substring(1);
                        }
                        latestFirmWareVersionInfo.setSoftwareVersion(version);
                    }

                    MyLog.printLog("当前类:PolarisNetWork,当前方法：subscribe,信息:" + jsonObject);

                    if (jsonObject.has("system_rom_url")) {
                        String system_rom_url = jsonObject.get("system_rom_url").getAsString();
                        latestFirmWareVersionInfo.setRomUrl(system_rom_url);
                        latestFirmWareVersionInfo.setRomFileName(system_rom_url.substring(system_rom_url.lastIndexOf("/") + 1));
                    }

                    if (jsonObject.has("system_md5")) {
                        String system_MD5 = jsonObject.get("system_md5").getAsString();
                        latestFirmWareVersionInfo.setRomMd5(system_MD5);
                    }

                    if (jsonObject.has("system_rom_size")) {
                        latestFirmWareVersionInfo.setRomSize(jsonObject.get("system_rom_size").getAsString());
                    }

                    if (jsonObject.has("release_date")) {
                        latestFirmWareVersionInfo.setRomReleaseDate(jsonObject.get("release_date").getAsString());
                    }
                    if (jsonObject.has("system_description")) {
                        latestFirmWareVersionInfo.setRomReleaseDescription((jsonObject.get("system_description").getAsString()));
                    }
                    polarisVersion.firmWareVersionInfo = latestFirmWareVersionInfo;
                    e.onNext(polarisVersion);
                }
                e.onComplete();//内部会主动调用dispose()
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                MyLog.printLog("当前类:PolarisNetWork,当前方法：accept,信息:网络错误,message:" + throwable.getMessage());
                NetWorkUtil.INSTANCE.setNetWorkForNet(null);
            }
        }).subscribeOn(Schedulers.io()).subscribe(consumer);
    }

    @SuppressLint("CheckResult")
    public void downloadRom(String url, Context context, String fileName, DownLoadListener downLoadListener) {
        initRetrofit();
        if (!canNetWork()) return;
        MyTimer.INSTANCE.singleSchedule(0, new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> response = polarisResourceRetrofitService.downloadRom(url);

                ResponseBody responseBody = null;
                try {
                    responseBody = response.execute().body();
                    if (responseBody != null) {
                        downLoadListener.onStart();
                        long contentLength = responseBody.contentLength();
                        InputStream inputStream = responseBody.byteStream();
                        File file = context.getExternalFilesDir(UrlUtils.firmware);
                        File file1 = new File(file.getAbsoluteFile() + "/" + fileName);
                        if (file1.exists()) {
                            file1.delete();
                        }
                        writeToFile(inputStream, file.getAbsoluteFile() + "/" + fileName, contentLength, downLoadListener);
                    }
                } catch (IOException e) {
                    NetWorkUtil.INSTANCE.setNetWorkForNet(null);
                }
            }
        });
    }

    /**
     * 写入文件
     *
     * @param inputStream
     * @param totalLength
     * @param listener
     */
    private void writeToFile(InputStream inputStream, String filePathName, long totalLength, DownLoadListener listener) {
        File file = new File(filePathName);
        if (file.exists() && file.length() != 0) {
            file.delete();
        }
        //FileOutputStream(filePathName)会新建该文件
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePathName)) {
            byte[] buffer = new byte[8192];
            int l = 0;
            long currentLength = 0;
            while ((l = inputStream.read(buffer, 0, 8192)) != -1) {
                currentLength += l;
                fileOutputStream.write(buffer, 0, l);
                listener.onDownLoad((int) (100 * currentLength / totalLength));
            }
            fileOutputStream.flush();//读完后，将未满的内存缓存中的数据强制刷入磁盘，保证之后读取的文件完整
            file = new File(filePathName);
            listener.onComplete(FileUtil.getFileMd5(file), file);
        } catch (Exception e) {
            listener.onError(e);
        }
    }

    private void initRetrofit() {
        if (serviceRetrofit == null) {

            serviceRetrofit = new Retrofit.Builder()
                    .baseUrl(serviceBaseUrl)
                    .client(NetWorkUtil.INSTANCE.getOkHttpClient(null))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            reSourceRetrofit = new Retrofit.Builder()
                    .baseUrl(resourceBaseUrl)
                    .client(NetWorkUtil.INSTANCE.getOkHttpClient(null))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            polarisResourceRetrofitService = reSourceRetrofit.create(PolarisResourceRetrofitService.class);

            polarisServiceRetrofitService = serviceRetrofit.create(PolarisServiceRetrofitService.class);
        }
    }

    public boolean canNetWork() {
        if (NetWorkUtil.INSTANCE.getNetWorkForNet() == null) {
            WifiUtilHelper.INSTANCE.updateNetWorkForNet(App.GlobalManager.INSTANCE.getContext(null));
            Toast.makeText(App.GlobalManager.INSTANCE.getContext(null), "没有找到可以上网的网络", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public interface DownLoadListener {

        void onStart();

        void onDownLoad(int loadProcess);

        void onComplete(String md5, File file);

        void onError(Exception e);

    }

}
