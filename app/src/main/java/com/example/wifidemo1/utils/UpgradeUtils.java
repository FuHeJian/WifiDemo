package com.example.wifidemo1.utils;

import android.net.Network;
import android.util.Log;
import android.widget.Toast;

import com.example.wifidemo1.App;
import com.example.wifidemo1.customview.MyTimer;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.model.ExtraDevVersionInfo;
import com.example.wifidemo1.model.FirmWareVersionInfo;
import com.example.wifidemo1.model.MyNetwork;
import com.example.wifidemo1.model.PolarisVersion;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UpgradeUtils {

    public static UpgradeUtils INSTANCE;

    static {
        INSTANCE = new UpgradeUtils();
    }

    private static final String TAG = "FirmWareUpdateDialog11";
    private static final String downloadErrorMessage = "Upgrade instruction status no response";
    private static final String orderNoresponseMessage = "Upgrade instruction status no response";
    private static final String uploadErrorMessage = "uploaderrormessage totalLen != position";
    private static final String uploadErrorMessage1 = "uploaderrormessage1";
    private static final String BOUNDARY = "WebKitFormBoundaryCR3ePJ61mAc18ElXfy";

    private static final String connectHost = "192.168.0.1";
    private static final int connectPort = 80;
    private static final String targetFilepath = "/sd";
    private Socket socket;

    public static void getPolarisServerVersion(boolean hasTriaxial, MyNetwork network, UgradeRequestServiceLisener ugradeRequestServiceLisener) {
        if (network == null || !network.available) {
            ugradeRequestServiceLisener.onRequestServiceError();
            return;
        }
        Observable
                .create(new ObservableOnSubscribe<PolarisVersion>() {
                    @Override
                    public void subscribe(ObservableEmitter<PolarisVersion> emitter) throws Exception {

                        PolarisVersion polarisVersion = new PolarisVersion();

                        String timeStamp = null;
                        try {
                            timeStamp = UpdateVersionUtil.getTimeStampNew();
                        } catch (Exception e) {
                            emitter.onError(null);
                        }
                        String signature = "SNOPPAANDROID@#101" + timeStamp;
                        String md5Str = UpdateVersionUtil.getMD5Str(signature);
                        String lan = Locale.getDefault().getLanguage();
                        URL downloadurl = new URL(UrlUtils.CHECK_LATEST_FIRMWARE_VERSION);
                        HttpURLConnection urlcon = (HttpURLConnection) network.network.openConnection(downloadurl);
                        urlcon.setConnectTimeout(5000);
                        urlcon.setRequestMethod("POST");
                        urlcon.setRequestProperty("auth-deviceid", "snoppa_app_1.0_android");
                        urlcon.setRequestProperty("auth-timestamp", timeStamp);
                        urlcon.setRequestProperty("auth-signature", md5Str);
                        urlcon.setRequestProperty("Accept-Language", lan);

//                        String body = "product_id=" + (UrlUtils.FIRMWARE_PRODUCT_ID) + "&state=2";
                        String body = "product_id=" + (UrlUtils.FIRMWARE_PRODUCT_ID);

                        OutputStream os = urlcon.getOutputStream();
                        os.write(body.getBytes());
                        os.flush();
                        os.close();
                        int ResponseCode = urlcon.getResponseCode();
                        if (ResponseCode == 200) {
                            InputStream inputStream = urlcon.getInputStream();
                            InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                            BufferedReader reader = new BufferedReader(isr);
                            String line;
                            StringBuilder buffer = new StringBuilder();
                            while ((line = reader.readLine()) != null) {//如果还没有读完
                                buffer.append(line);//一直追加内容
                            }
                            String content = buffer.toString();
                            JSONObject jsonObject = new JSONObject(content);
                            if (jsonObject.length() < 1) {
                                emitter.onError(new Throwable("app update info is empty"));
                                return;
                            }

                            FirmWareVersionInfo latestFirmWareVersionInfo = new FirmWareVersionInfo();
                            if (jsonObject.has("current_version")) {
                                String version = (jsonObject.getString("current_version")).trim();
                                if (version.contains("V") || version.contains("v")) {
                                    version = version.substring(1);
                                }
                                latestFirmWareVersionInfo.setSoftwareVersion(version);
                            }

                            if (jsonObject.has("system_rom_url")) {
                                String system_rom_url = jsonObject.getString("system_rom_url");
                                latestFirmWareVersionInfo.setRomUrl(system_rom_url);
                                latestFirmWareVersionInfo.setRomFileName(system_rom_url.substring(system_rom_url.lastIndexOf("/") + 1));
                            }

                            if (jsonObject.has("system_rom_size")) {
                                latestFirmWareVersionInfo.setRomSize(jsonObject.getString("system_rom_size"));
                            }

                            if (jsonObject.has("release_date")) {
                                latestFirmWareVersionInfo.setRomReleaseDate(jsonObject.getString("release_date"));
                            }
                            if (jsonObject.has("system_description")) {
                                latestFirmWareVersionInfo.setRomReleaseDescription((jsonObject.getString("system_description")));
                            }
                            polarisVersion.firmWareVersionInfo = latestFirmWareVersionInfo;
                        }

                        if (hasTriaxial) {
                            downloadurl = new URL(UrlUtils.CHECK_LATEST_FIRMWARE_VERSION);
                            urlcon = (HttpURLConnection) network.network.openConnection(downloadurl);
                            urlcon.setConnectTimeout(5000);
                            urlcon.setRequestMethod("POST");
                            urlcon.setRequestProperty("auth-deviceid", "snoppa_app_1.0_android");
                            urlcon.setRequestProperty("auth-timestamp", timeStamp);
                            urlcon.setRequestProperty("auth-signature", md5Str);
                            urlcon.setRequestProperty("Accept-Language", lan);

//                            body = "product_id=" + (UrlUtils.POLARIS_EXTRADEV_ID) + "&state=2";
                            body = "product_id=" + (UrlUtils.POLARIS_EXTRADEV_ID);
                            os = urlcon.getOutputStream();
                            os.write(body.getBytes());
                            os.flush();
                            os.close();
                            ResponseCode = urlcon.getResponseCode();
                            if (ResponseCode == 200) {
                                InputStream inputStream = urlcon.getInputStream();
                                InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
                                BufferedReader reader = new BufferedReader(isr);
                                String line;
                                StringBuilder buffer = new StringBuilder();
                                while ((line = reader.readLine()) != null) {//如果还没有读完
                                    buffer.append(line);//一直追加内容
                                }
                                String content = buffer.toString();
                                JSONObject jsonObject = new JSONObject(content);
                                if (jsonObject.length() < 1) {
                                    emitter.onError(new Throwable("app update info is empty"));
                                    return;
                                }
                                Log.d(TAG, "subscribe: " + jsonObject);
                                ExtraDevVersionInfo extraDevVersionInfo = new ExtraDevVersionInfo();

                                if (jsonObject.has("current_version")) {
                                    String version = (jsonObject.getString("current_version")).trim();
                                    if (version.contains("V") || version.contains("v")) {
                                        version = version.substring(1);
                                    }
                                    extraDevVersionInfo.setExAxisVersion(version);
                                }

                                if (jsonObject.has("system_rom_url")) {
                                    String system_rom_url = jsonObject.getString("system_rom_url");
                                    extraDevVersionInfo.setRomUrl(system_rom_url);
                                    extraDevVersionInfo.setRomFileName(system_rom_url.substring(system_rom_url.lastIndexOf("/") + 1));
                                }

                                if (jsonObject.has("system_rom_size")) {
                                    extraDevVersionInfo.setRomSize(jsonObject.getString("system_rom_size"));
                                }

                                if (jsonObject.has("release_date")) {
                                    extraDevVersionInfo.setRomReleaseDate(jsonObject.getString("release_date"));
                                }
                                if (jsonObject.has("system_description")) {
                                    extraDevVersionInfo.setRomReleaseDescription((jsonObject.getString("system_description")));
                                }
                                polarisVersion.extraDevVersionInfo = extraDevVersionInfo;
                            }

                        }
                        Log.d(TAG, " 获取服务器返回最新的版本信息 ---------》 " + polarisVersion);
                        emitter.onNext(polarisVersion);
                        emitter.onComplete();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PolarisVersion>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PolarisVersion versionInfo) {
                        ugradeRequestServiceLisener.onRequestServiceComplete(versionInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: e =" + e);
                        ugradeRequestServiceLisener.onRequestServiceError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private boolean updating;

    public void closeUploadSocket() throws IOException {
        if (this.socket != null)
            this.socket.close();
    }

    public Socket getSocket() throws IOException {
        if (this.socket == null) {
            this.socket = new Socket();
            socket.connect(new InetSocketAddress(connectHost, connectPort), 60000);
        }
        return socket;
    }


    public void newUploadFile(ObservableEmitter<Integer> emitter, File updateFile, Network netWork) throws Exception {

        if (netWork == null) {
            return;
        }
        MyTimer.INSTANCE.singleSchedule(0, new Runnable() {
            @Override
            public void run() {
                try {
                    MyLog.printLog("当前类:UpgradeUtils,当前方法：newUploadFile,信息:network" + netWork);
                    URL url = new URL("http://192.168.0.1/dav/" + updateFile.getName());
                    HttpURLConnection urlConn = (HttpURLConnection) netWork.openConnection(url);
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setRequestMethod("PUT");
                    urlConn.setRequestProperty("Content-Type", "application/zip");
                    urlConn.setConnectTimeout(0); //设置连接超时时间。

                    OutputStream output = new DataOutputStream(urlConn.getOutputStream());
                    InputStream dataInputStream = new DataInputStream(new FileInputStream(updateFile));
                    long totalLength = updateFile.length();
                    long curentLength = 0;
                    int count;
                    byte[] buffer = new byte[2048 * 8];//缓冲数组2kB
                    OrderCommunication.getInstance().SP_LOAD_UPGRADE_FW_STATE(1);
                    while ((count = dataInputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, count);
                        curentLength += count;

                        int progress = (int) (curentLength * 1.0f / totalLength);

                        emitter.onNext(progress);
                    }
                    output.flush();
                    output.close();
                    dataInputStream.close();

                    int responseCode = urlConn.getResponseCode();
                    urlConn.disconnect();

                    if (responseCode == 201) {
                        emitter.onComplete();
                    } else {
                        emitter.onError(new Throwable(uploadErrorMessage));
                    }
                } catch (Exception e) {
                    emitter.onError(null);
                    Toast.makeText(App.GlobalManager.INSTANCE.getContext("HomeActivity"), "网络不可用", Toast.LENGTH_SHORT).show();
                    MyLog.printLog("当前类:UpgradeUtils,当前方法：newUploadFile,信息:" + e.getMessage());
                }
            }
        });

    }

    private void oldUploadFile(ObservableEmitter<Integer> emitter, File updateFile, String fileName) throws Exception {


        boolean flag = false;
        ByteArrayOutputStream bos = null;
        ByteArrayInputStream bis = null;
        OutputStream os = null;
        try {
            try {
                closeUploadSocket();
            } catch (Exception e) {
            }
            socket = null;


            os = getSocket().getOutputStream();
            String disposition = getDisposition(fileName, 0);
            String end = "\r\n------" + BOUNDARY + "--";
            long contentLength = disposition.length() + end.length() + updateFile.length();
            String header = getHeader(String.valueOf(contentLength));

            byte[] headerBytes = header.getBytes();
            byte[] dispositionBytes = disposition.getBytes();
            byte[] fileBytes = getBytesFromFile(updateFile);
            byte[] endBytes = end.getBytes();
            os.write(headerBytes);
            os.write(dispositionBytes);
            bis = new ByteArrayInputStream(fileBytes);
            long totalLen = fileBytes.length;
            long position = 0;

            byte[] bytes1 = new byte[1024000];
            int len = -1;
            while ((len = bis.read(bytes1)) != -1) {
                os.write(bytes1, 0, len);
                position = position + len;
                if (totalLen != 0) {
                    emitter.onNext((int) (position * 1.0f / totalLen * 100) / 2 + 50);
                }
            }

            if (totalLen == position) {
                os.write(endBytes);
                flag = true;
            }

            if (!flag) {
                if (bos != null)
                    bos.close();
                if (bis != null)
                    bis.close();
                closeUploadSocket();
                emitter.onError(new Throwable(uploadErrorMessage));
                return;
            }
            InputStream is = getSocket().getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            while ((len = is.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            baos.flush();
            byte[] dest = baos.toByteArray();
            String result = new String(dest, 0, dest.length);
            if (flag && result != null && result.contains("200 OK"))
                flag = true;
            else
                flag = false;

        } catch (Exception e) {
            flag = false;
            Log.e(TAG, "upload: Exception111 e =" + e);
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "upload: Exception222 e =" + e);
            }
        }

        if (flag) {
            emitter.onComplete();
        } else {
            emitter.onError(new Throwable(uploadErrorMessage1));
        }
    }


    private String getDisposition(String filename, int start) {
        String rangeBytes = "";
        if (start > 0) {
            rangeBytes = "Range:bytes=" + start + "-\r\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("------").append(BOUNDARY).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"file_attactment\"; filename=\"").append(filename).append("\"\r\n");
        sb.append(rangeBytes);
        sb.append("Content-Type: application/octet-stream\r\n\0");
        sb.append("\r\n");
        sb.append("\r\n");
        return sb.toString();
    }

    private String getHeader(String contentLength) {
        //用于使得头部消息length=600
        String token = "491586493178619376481362946346193284421543624352436544264125346245623462452454626134634564739573957154154543264163";
        String h = connectHost;
        String u = targetFilepath;
        int l = contentLength.length() + connectHost.length() + targetFilepath.length();
        token = token.substring(0, token.length() - l);

        StringBuilder sb = new StringBuilder();
        sb.append("POST ").append(u).append(" HTTP/1.1\r\n");
        sb.append("Host: ").append(h).append("\r\n");
        sb.append("Connection: keep-alive\r\n");
        sb.append("Content-Length: ").append(contentLength).append("\r\n");
        sb.append("Pragma: no-cache\r\n");
        sb.append("Cache-Control: no-cache\r\n");
        sb.append("Accept: application/json, text/javascript, */*; q=0.01\r\n");
        sb.append("Origin: null\r\n");
        sb.append("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36\r\n");
        sb.append("Content-Type: multipart/form-data; boundary=----").append(BOUNDARY).append("\r\n");
        sb.append("Accept-Encoding: gzip, deflate\r\n");
        sb.append("Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\r\n");
        sb.append("Token: ").append(token).append("\r\n");
        sb.append("\r\n");
        sb.append("\r\n");
        return sb.toString();
    }

    private byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is to large " + file.getName());
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }


    //0:不需要升级，1：强制升级 2：普通升级
    public static int checkFirmwareUpdate(String oldSystemVersion, String latestSystemVersion) {
        Log.d(TAG, "checkFirmwareUpdate: oldSystemVersion =" + oldSystemVersion + ",latestSystemVersion =" + latestSystemVersion);
        if (latestSystemVersion == null || oldSystemVersion == null) {
            return 0;
        }
        String[] splitOldSysC;
        String[] splitlatestSysC;
        try {
            if (oldSystemVersion != null && latestSystemVersion != null) {
                splitOldSysC = oldSystemVersion.split("\\.");
                splitlatestSysC = latestSystemVersion.split("\\.");
                if (splitOldSysC.length == 4 && splitlatestSysC.length == 4) {
                    if (Integer.parseInt(splitlatestSysC[0]) > Integer.parseInt(splitOldSysC[0])) {
                        return 1;
                    } else if (Integer.parseInt(splitlatestSysC[0]) < Integer.parseInt(splitOldSysC[0]))
                        return 0;
                    else {
                        if (Integer.parseInt(splitlatestSysC[1]) > Integer.parseInt(splitOldSysC[1])) {
                            return 2;
                        } else if (Integer.parseInt(splitlatestSysC[1]) < Integer.parseInt(splitOldSysC[1])) {
                            return 0;
                        } else {
                            if (Integer.parseInt(splitlatestSysC[2]) > Integer.parseInt(splitOldSysC[2])) {
                                return 2;
                            } else if (Integer.parseInt(splitlatestSysC[2]) < Integer.parseInt(splitOldSysC[2])) {
                                return 0;
                            } else {
                                if (Integer.parseInt(splitlatestSysC[3]) > Integer.parseInt(splitOldSysC[3])) {
                                    return 2;
                                } else if (Integer.parseInt(splitlatestSysC[3]) <= Integer.parseInt(splitOldSysC[3])) {
                                    return 0;
                                } else
                                    return 2;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }


    //0:不需要升级，1：强制升级 2：普通升级
    public static int checkExtraDevFirmwareUpdate(String oldSystemVersion, String latestSystemVersion) {
        Log.d(TAG, "checkExtraDevFirmwareUpdate: oldSystemVersion =" + oldSystemVersion + ",latestSystemVersion =" + latestSystemVersion);
        if (latestSystemVersion == null || oldSystemVersion == null) {
            return 0;
        }
        String[] splitOldSysC;
        String[] splitlatestSysC;
        try {
            if (oldSystemVersion != null && latestSystemVersion != null) {
                splitOldSysC = oldSystemVersion.split("\\.");
                splitlatestSysC = latestSystemVersion.split("\\.");
                if (splitOldSysC.length == 4 && splitlatestSysC.length == 4) {

                    if (Integer.parseInt(splitlatestSysC[0]) > Integer.parseInt(splitOldSysC[0])) {
                        return 1;
                    } else if (Integer.parseInt(splitlatestSysC[0]) < Integer.parseInt(splitOldSysC[0]))
                        return 0;
                    else {
                        if (Integer.parseInt(splitlatestSysC[1]) > Integer.parseInt(splitOldSysC[1])) {
                            return 1;
                        } else if (Integer.parseInt(splitlatestSysC[1]) < Integer.parseInt(splitOldSysC[1])) {
                            return 0;
                        } else {
                            if (Integer.parseInt(splitlatestSysC[2]) > Integer.parseInt(splitOldSysC[2])) {
                                return 1;
                            } else if (Integer.parseInt(splitlatestSysC[2]) < Integer.parseInt(splitOldSysC[2])) {
                                return 0;
                            } else {
                                if (Integer.parseInt(splitlatestSysC[3]) > Integer.parseInt(splitOldSysC[3])) {
                                    return 2;
                                } else if (Integer.parseInt(splitlatestSysC[3]) <= Integer.parseInt(splitOldSysC[3])) {
                                    return 0;
                                } else
                                    return 2;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }


    public interface UgradeProcessLisener {
        void onUpdateProgress(Integer data);

        void onUpdateError();

        void onUpdateProcessComplete();
    }


    public interface UgradeRequestServiceLisener {


        void onRequestServiceError();

        void onRequestServiceComplete(PolarisVersion polarisVersion);
    }

    public void releaseSource() {
        try {
            closeUploadSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
