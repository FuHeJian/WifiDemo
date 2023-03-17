package com.example.wifidemo1.singleton;

import static com.example.wifidemo1.utils.UtilFunction.getDateFileNameString;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.example.wifidemo1.model.AlbumItem;
import com.example.wifidemo1.model.BroadcastActionEvent;
import com.example.wifidemo1.model.ModelConstant;
import com.example.wifidemo1.oksocket.common.interfaces.utils.TextUtils;
import com.example.wifidemo1.utils.CMD;
import com.example.wifidemo1.utils.MyMessage;
import com.example.wifidemo1.utils.OrderCommunication;
import com.example.wifidemo1.utils.UrlUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HLW on 2019/8/21.
 */

public class MediaFileLoad {
    public static final String DOWNLOAD_STATE_DOWNLOADING = "downloading";    // 下载中
    public static final String DOWNLOAD_STATE_WAIT = "waiting";  // 等待下载
    public static final String DOWNLOAD_STATE_OVER = "over";    // 下载结束
    public static final String DOWNLOAD_STATE_ERROR = "error";  // 下载出错
    public static final String DOWNLOAD_STATE_NO_MEMORY = "download_no_memory";  // 内存不足
    private static final String TAG = "MediaFileUtils";
    private static final String FILE_DEFAULT_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + UrlUtils.FOLDER_PREFIX;
    private final static int oneTimegetFileCount = 9;
    private static MediaFileLoad mediaFileUtils = null;
    public int yuntaifilecount;//云台总文件数
    public int yuntaiNormalCount;
    public int yuntaiLapseCount;
    public int yuntaiFocusCount;
    public int yuntaiPanCount;
    public int yuntaiSunCount;
    public int yuntaiHDRCount;
    public int yuntaiStarSkyCount;
    public boolean isGetingFile;
    public boolean isOwnGet;
    private OkHttpClient mDowloadClient;
    private HashMap<String, Call> downCalls; // 用来存放各个下载的请求
    private ArrayList<AlbumItem> downloadQueue; // 用来存放各个下载队列
    private ArrayList<AlbumItem> waitDownloadQueue;           // 用来存放等待下载的请求
    private int maxDowloadCount = 1;   // 同时下载的最大个数
    private int startIndex;

    private int addMenusCount;//单次拉取已接收的文件数量
    private int prepareGetMenusCount;//单次拉取文件数量

    private int addCombineCount;
    private int prepareGetCombineCount;

    private ArrayList<AlbumItem> yuntaifileInfoList;
    private ArrayList<AlbumItem> localfileInfolocationList;
    private ArrayList<AlbumItem> currentCombineList;

    private ArrayList<AlbumItem> yuntaiNormalInfoList;
    private ArrayList<AlbumItem> yuntaiLapseInfoList;
    private ArrayList<AlbumItem> yuntaiFocusInfoList;
    private ArrayList<AlbumItem> yuntaiPanInfoList;
    private ArrayList<AlbumItem> yuntaiSunInfoList;
    private ArrayList<AlbumItem> yuntaiHDRInfoList;
    private ArrayList<AlbumItem> yuntaiStarSkyInfoList;

    private ArrayList<AlbumItem> yuntaiTempNormalInfoList;
    private ArrayList<AlbumItem> yuntaiTempLapseInfoList;
    private ArrayList<AlbumItem> yuntaiTempFocusInfoList;
    private ArrayList<AlbumItem> yuntaiTempPanInfoList;
    private ArrayList<AlbumItem> yuntaiTempSunInfoList;
    private ArrayList<AlbumItem> yuntaiTempHDRInfoList;
    private ArrayList<AlbumItem> yuntaiTempStarSkyInfoList;

    private int yuntaiFileType;

    private ArrayList<AlbumItem> localNormalInfoList;
    private ArrayList<AlbumItem> localLapseInfoList;
    private ArrayList<AlbumItem> localFocusInfoList;
    private ArrayList<AlbumItem> localPanInfoList;
    private ArrayList<AlbumItem> localSunInfoList;
    private ArrayList<AlbumItem> localHDRInfoList;
    private ArrayList<AlbumItem> localStarSkyInfoList;
    private int localFileType;

    private ArrayList<HolderFileInfo> mHolderFileInfoList;
    private int[][] sdCardFileInfo;

    private LoadLoacalDataAsyncTask loadLoacalDataAsyncTask;
    private SimpleDateFormat formatter;
    private int downloadFileCount;
    private int downloadCompleteFileCount;
    private boolean isNeedPostAlbumItem;
    private MyHandler mHandler;
    private int getFileCount;
    private int getFileOnceCount = 10;
    private int currentOnceCount;

    // TODO: 2021/10/20
    private boolean enableGetFile = true;
    // TODO: 2022/3/14 测试蜂窝网络
    public boolean remoteModel = false;

    private MediaFileLoad() {
        initOkhttpClient();

        yuntaifileInfoList = new ArrayList<>();
        localfileInfolocationList = new ArrayList<>();

        yuntaiNormalInfoList = new ArrayList<>();
        yuntaiLapseInfoList = new ArrayList<>();
        yuntaiFocusInfoList = new ArrayList<>();
        yuntaiPanInfoList = new ArrayList<>();
        yuntaiSunInfoList = new ArrayList<>();
        yuntaiHDRInfoList = new ArrayList<>();
        yuntaiStarSkyInfoList = new ArrayList<>();


        yuntaiTempNormalInfoList = new ArrayList<>();
        yuntaiTempLapseInfoList = new ArrayList<>();
        yuntaiTempFocusInfoList = new ArrayList<>();
        yuntaiTempPanInfoList = new ArrayList<>();
        yuntaiTempSunInfoList = new ArrayList<>();
        yuntaiTempHDRInfoList = new ArrayList<>();
        yuntaiTempStarSkyInfoList = new ArrayList<>();

        localNormalInfoList = new ArrayList<>();
        localLapseInfoList = new ArrayList<>();
        localFocusInfoList = new ArrayList<>();
        localPanInfoList = new ArrayList<>();
        localSunInfoList = new ArrayList<>();
        localHDRInfoList = new ArrayList<>();
        localStarSkyInfoList = new ArrayList<>();

        downCalls = new HashMap<>();
        waitDownloadQueue = new ArrayList<>();
        downloadQueue = new ArrayList<>();

        mHolderFileInfoList = new ArrayList<>();
        sdCardFileInfo = new int[6][6];
    }

    private class SdCardFileInfo {
        int fileCount;
        int fileIndex;
        int classCount;
        int classIndex;

        public SdCardFileInfo() {

        }
    }

    public static MediaFileLoad getInstance() {
        if (mediaFileUtils == null) {
            synchronized (MediaFileLoad.class) {
                if (mediaFileUtils == null) {
                    mediaFileUtils = new MediaFileLoad();
                }
            }
        }
        return mediaFileUtils;
    }

    public static void closeAll(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isEnableGetFile() {
        return enableGetFile;
    }

    public void setEnableGetFile(boolean enableGetFile) {
        this.enableGetFile = enableGetFile;
    }

    public int getYuntaiFileType() {
        return yuntaiFileType;
    }

    public void setYuntaiFileType(int yuntaiFileType) {
        this.yuntaiFileType = yuntaiFileType;
    }

    public int getLocalFileType() {
        return localFileType;
    }

    public void setLocalFileType(int localFileType) {
        this.localFileType = localFileType;
    }


    public ArrayList<AlbumItem> getDownloadQueue() {
        return downloadQueue;
    }

    public ArrayList<AlbumItem> getWaitDownloadQueue() {
        return waitDownloadQueue;
    }

    public ArrayList<AlbumItem> getCurrentCombineList() {
        if (currentCombineList == null) {
            currentCombineList = new ArrayList<>();
        }
        return currentCombineList;
    }

    public void setCurrentCombineList(ArrayList<AlbumItem> list) {
        if (currentCombineList == null) {
            currentCombineList = new ArrayList<>();
        } else {
            currentCombineList.clear();
        }
        if (list != null) {
            currentCombineList.addAll(list);
        }
    }

    public synchronized void clearWaitDownloadQueue() {
        if (waitDownloadQueue != null) {
            waitDownloadQueue.clear();
        }
    }

    private void initOkhttpClient() {
        if (mDowloadClient == null)
            mDowloadClient = new OkHttpClient.Builder().build();
    }

    public void releaseData(boolean clearLocal, boolean clearYuntai, Context context) {
        Log.d(TAG, "releaseData: clearLocal =" + clearLocal + ",clearYuntai =" + clearYuntai);
        resetGetYuntaiFileFlag();
        if (clearLocal) {
            localfileInfolocationList.clear();
            localNormalInfoList.clear();
            localLapseInfoList.clear();
            localFocusInfoList.clear();
            localPanInfoList.clear();
            localSunInfoList.clear();
            localHDRInfoList.clear();
            localStarSkyInfoList.clear();
        }

        if (clearYuntai) {
            yuntaifileInfoList.clear();
            yuntaiNormalInfoList.clear();
            yuntaiLapseInfoList.clear();
            yuntaiFocusInfoList.clear();
            yuntaiPanInfoList.clear();
            yuntaiSunInfoList.clear();
            yuntaiHDRInfoList.clear();
            yuntaiStarSkyInfoList.clear();

            yuntaiTempNormalInfoList.clear();
            yuntaiTempLapseInfoList.clear();
            yuntaiTempFocusInfoList.clear();
            yuntaiTempPanInfoList.clear();
            yuntaiTempSunInfoList.clear();
            yuntaiTempHDRInfoList.clear();
            yuntaiTempStarSkyInfoList.clear();
        }

        cancelAllDownloadTask();
        if (context != null) {
            clearImageDiskCache(context);
            clearImageMemoryCache(context);
        }
        enableGetFile = true;
        if (mHolderFileInfoList != null) {
            mHolderFileInfoList.clear();
        }
    }

    /**
     * 清理图片磁盘缓存
     */
    public void clearImageDiskCache(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(context).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片内存缓存
     */
    public void clearImageMemoryCache(final Context context) {
        try {
//            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(context).clearMemory();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetGetYuntaiFileFlag() {
        Log.d(TAG, "resetGetYuntaiFileFlag: ");
        yuntaifilecount = 0;
        startIndex = 0;
        isGetingFile = false;
        isOwnGet = false;
    }

    public ArrayList<AlbumItem> getYuntaifileInfoList() {
        Log.d(TAG, "getYuntaifileInfoList:yuntaifileInfoList.size() = " + yuntaifileInfoList.size());
        if (yuntaiFileType == CMD.FILE_TYPE_NORMAL) {
            if (yuntaiNormalInfoList == null)
                yuntaiNormalInfoList = new ArrayList<>();
            return yuntaiNormalInfoList;
        } else if (yuntaiFileType == CMD.FILE_TYPE_LAPSE) {
            if (yuntaiLapseInfoList == null)
                yuntaiLapseInfoList = new ArrayList<>();
            return yuntaiLapseInfoList;
        } else if (yuntaiFileType == CMD.FILE_TYPE_FOCUS) {
            if (yuntaiFocusInfoList == null)
                yuntaiFocusInfoList = new ArrayList<>();
            return yuntaiFocusInfoList;
        } else if (yuntaiFileType == CMD.FILE_TYPE_PAN) {
            if (yuntaiPanInfoList == null)
                yuntaiPanInfoList = new ArrayList<>();
            return yuntaiPanInfoList;
        } else if (yuntaiFileType == CMD.FILE_TYPE_SUN) {
            if (yuntaiSunInfoList == null)
                yuntaiSunInfoList = new ArrayList<>();
            return yuntaiSunInfoList;
        } else if (yuntaiFileType == CMD.FILE_TYPE_HDR) {
            if (yuntaiHDRInfoList == null) {
                yuntaiHDRInfoList = new ArrayList<>();
            }
            return yuntaiHDRInfoList;
        } else if (yuntaiFileType == CMD.FILE_TYPE_STAR_SKY_STACK) {
            if (yuntaiStarSkyInfoList == null) {
                yuntaiStarSkyInfoList = new ArrayList<>();
            }
            return yuntaiStarSkyInfoList;
        }

        if (yuntaifileInfoList == null) {
            yuntaifileInfoList = new ArrayList<>();
        } else {
            int normal = yuntaiNormalInfoList.size();
            int lapse = yuntaiLapseInfoList.size();
            int focus = yuntaiFocusInfoList.size();
            int pan = yuntaiPanInfoList.size();
            int sun = yuntaiSunInfoList.size();
            int hdr = yuntaiHDRInfoList.size();
            int starSky = yuntaiStarSkyInfoList.size();
            if (yuntaifileInfoList.size() != (normal + lapse + focus + pan + sun + hdr + starSky)) {
                yuntaifileInfoList.clear();
                yuntaifileInfoList.addAll(yuntaiNormalInfoList);
                yuntaifileInfoList.addAll(yuntaiLapseInfoList);
                yuntaifileInfoList.addAll(yuntaiFocusInfoList);
                yuntaifileInfoList.addAll(yuntaiPanInfoList);
                yuntaifileInfoList.addAll(yuntaiSunInfoList);
                yuntaifileInfoList.addAll(yuntaiHDRInfoList);
                yuntaifileInfoList.addAll(yuntaiStarSkyInfoList);
                sortList(yuntaifileInfoList);
            }
        }
        return yuntaifileInfoList;
    }

    public ArrayList<AlbumItem> getYuntaiInfoList(int type) {
        if (type == CMD.FILE_TYPE_NORMAL) {
            return getYuntaiNormalInfoList();
        } else if (type == CMD.FILE_TYPE_LAPSE) {
            return getYuntaiLapseInfoList();
        } else if (type == CMD.FILE_TYPE_FOCUS) {
            return getYuntaiFocusInfoList();
        } else if (type == CMD.FILE_TYPE_PAN) {
            return getYuntaiPanInfoList();
        } else if (type == CMD.FILE_TYPE_SUN) {
            return getYuntaiSunInfoList();
        } else if (type == CMD.FILE_TYPE_HDR) {
            return getYuntaiHDRInfoList();
        } else if (type == CMD.FILE_TYPE_STAR_SKY_STACK) {
            return getStarSkyInfoList();
        } else {
            if (yuntaifileInfoList == null)
                yuntaifileInfoList = new ArrayList<>();
            return yuntaifileInfoList;
        }
    }

    public ArrayList<AlbumItem> getLocalfileInfoloList() {
        Log.d(TAG, "getLocalfileInfoloList: ");
        if (localFileType == CMD.FILE_TYPE_NORMAL) {
            if (localNormalInfoList == null) {
                localNormalInfoList = new ArrayList<>();
            }
            return localNormalInfoList;
        } else if (localFileType == CMD.FILE_TYPE_LAPSE) {
            if (localLapseInfoList == null) {
                localLapseInfoList = new ArrayList<>();
            }
            return localLapseInfoList;
        } else if (localFileType == CMD.FILE_TYPE_FOCUS) {
            if (localFocusInfoList == null) {
                localFocusInfoList = new ArrayList<>();
            }
            return localFocusInfoList;
        } else if (localFileType == CMD.FILE_TYPE_PAN) {
            if (localPanInfoList == null) {
                localPanInfoList = new ArrayList<>();
            }
            return localPanInfoList;
        } else if (localFileType == CMD.FILE_TYPE_SUN) {
            if (localSunInfoList == null) {
                localSunInfoList = new ArrayList<>();
            }
            return localSunInfoList;
        } else if (localFileType == CMD.FILE_TYPE_HDR) {
            if (localHDRInfoList == null) {
                localHDRInfoList = new ArrayList<>();
            }
            return localHDRInfoList;
        } else if (localFileType == CMD.FILE_TYPE_STAR_SKY_STACK) {
            if (localStarSkyInfoList == null) {
                localStarSkyInfoList = new ArrayList<>();
            }
            return localStarSkyInfoList;
        }

        if (localfileInfolocationList == null)
            localfileInfolocationList = new ArrayList<>();
        return localfileInfolocationList;
    }

    public ArrayList<AlbumItem> getLocalInfoList(int type) {
        if (type == CMD.FILE_TYPE_NORMAL) {
            return getLocalNormalInfoList();
        } else if (type == CMD.FILE_TYPE_LAPSE) {
            return getLocalLapseInfoList();
        } else if (type == CMD.FILE_TYPE_FOCUS) {
            return getLocalFocusInfoList();
        } else if (type == CMD.FILE_TYPE_PAN) {
            return getLocalPanInfoList();
        } else if (type == CMD.FILE_TYPE_SUN) {
            return getLocalSunInfoList();
        } else if (type == CMD.FILE_TYPE_HDR) {
            return getLocalHDRInfoList();
        } else if (type == CMD.FILE_TYPE_STAR_SKY_STACK) {
            return getLocalStarSkyInfoList();
        } else {
            if (localfileInfolocationList == null)
                localfileInfolocationList = new ArrayList<>();
            return localfileInfolocationList;
        }
    }

    public ArrayList<AlbumItem> getLocalNormalInfoList() {
        if (localNormalInfoList == null) {
            localNormalInfoList = new ArrayList<>();
        }
        return localNormalInfoList;
    }

    public ArrayList<AlbumItem> getLocalLapseInfoList() {
        if (localLapseInfoList == null) {
            localLapseInfoList = new ArrayList<>();
        }
        return localLapseInfoList;
    }

    public ArrayList<AlbumItem> getLocalFocusInfoList() {
        if (localFocusInfoList == null) {
            localFocusInfoList = new ArrayList<>();
        }
        return localFocusInfoList;
    }

    public ArrayList<AlbumItem> getLocalPanInfoList() {
        if (localPanInfoList == null) {
            localPanInfoList = new ArrayList<>();
        }
        return localPanInfoList;
    }

    public ArrayList<AlbumItem> getLocalSunInfoList() {
        if (localSunInfoList == null) {
            localSunInfoList = new ArrayList<>();
        }
        return localSunInfoList;
    }

    public ArrayList<AlbumItem> getLocalHDRInfoList() {
        if (localHDRInfoList == null) {
            localHDRInfoList = new ArrayList<>();
        }
        return localHDRInfoList;
    }

    public ArrayList<AlbumItem> getLocalStarSkyInfoList() {
        if (localStarSkyInfoList == null) {
            localStarSkyInfoList = new ArrayList<>();
        }
        return localStarSkyInfoList;
    }

    public ArrayList<AlbumItem> getYuntaiNormalInfoList() {
        if (yuntaiNormalInfoList == null) {
            yuntaiNormalInfoList = new ArrayList<>();
        }
        return yuntaiNormalInfoList;
    }

    public ArrayList<AlbumItem> getYuntaiLapseInfoList() {
        if (yuntaiLapseInfoList == null) {
            yuntaiLapseInfoList = new ArrayList<>();
        }
        return yuntaiLapseInfoList;
    }

    public ArrayList<AlbumItem> getYuntaiFocusInfoList() {
        if (yuntaiFocusInfoList == null) {
            yuntaiFocusInfoList = new ArrayList<>();
        }
        return yuntaiFocusInfoList;
    }

    public ArrayList<AlbumItem> getYuntaiPanInfoList() {
        if (yuntaiFocusInfoList == null) {
            yuntaiFocusInfoList = new ArrayList<>();
        }
        return yuntaiPanInfoList;
    }

    public ArrayList<AlbumItem> getYuntaiSunInfoList() {
        if (yuntaiFocusInfoList == null) {
            yuntaiFocusInfoList = new ArrayList<>();
        }
        return yuntaiSunInfoList;
    }

    public ArrayList<AlbumItem> getYuntaiHDRInfoList() {
        if (yuntaiHDRInfoList == null) {
            yuntaiHDRInfoList = new ArrayList<>();
        }
        return yuntaiHDRInfoList;
    }

    public ArrayList<AlbumItem> getStarSkyInfoList() {
        if (yuntaiStarSkyInfoList == null) {
            yuntaiStarSkyInfoList = new ArrayList<>();
        }
        return yuntaiStarSkyInfoList;
    }

    //获取云台文件总数
    public boolean getYuntaifileCount() {
        if (!enableGetFile) {
            return true;
        }
        releaseData(true, true, null);

        enableGetFile = false;

        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_YUNTAI_FILE_LOAD_COMPLETE));
        Log.d(TAG, "getYuntaifileCount: isGetingFile = " + isGetingFile);
        if (isGetingFile)
            return true;
        isGetingFile = true;
        isOwnGet = true;
        startIndex = 0;
        OrderCommunication.getInstance().SP_GET_FILE_COUNT();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        } else {
            mHandler = new MyHandler();
        }
        return false;
    }

    private void checkHolderFileInfoList(int type, int count) {
        int index = -1;
        for (int i = 0; i < mHolderFileInfoList.size(); i++) {
            if (mHolderFileInfoList.get(i).type == type) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            mHolderFileInfoList.add(new HolderFileInfo(type, count));
        } else {
            mHolderFileInfoList.get(index).setClassSize(count);
        }
    }

    public void getfileCountResponse(String msg) {
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_START_GET_FILE_COUNT_RETURN));

        if (!isOwnGet)
            return;

        if (msg != null) {
            String[] data = msg.split(";");
            for (String value : data) {
                String text = value.substring(value.lastIndexOf(":") + 1).trim();
                if (value.contains("normal")) {
                    try {
                        yuntaiNormalCount = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    checkHolderFileInfoList(CMD.FILE_TYPE_NORMAL, yuntaiNormalCount);
                } else if (value.contains("lapse")) {
                    try {
                        yuntaiLapseCount = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    checkHolderFileInfoList(CMD.FILE_TYPE_LAPSE, yuntaiLapseCount);
                } else if (value.contains("focus")) {
                    try {
                        yuntaiFocusCount = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    checkHolderFileInfoList(CMD.FILE_TYPE_FOCUS, yuntaiFocusCount);
                } else if (value.contains("pan")) {
                    try {
                        yuntaiPanCount = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    checkHolderFileInfoList(CMD.FILE_TYPE_PAN, yuntaiPanCount);
                } else if (value.contains("sun")) {
                    try {
                        yuntaiSunCount = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    checkHolderFileInfoList(CMD.FILE_TYPE_SUN, yuntaiSunCount);
                } else if (value.contains("hdr")) {
                    try {
                        yuntaiHDRCount = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    checkHolderFileInfoList(CMD.FILE_TYPE_HDR, yuntaiHDRCount);
                } else if (value.contains("starskyStack")) {
                    try {
                        yuntaiStarSkyCount = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    checkHolderFileInfoList(CMD.FILE_TYPE_STAR_SKY_STACK, yuntaiStarSkyCount);
                }
            }
        }

        getFileClassCount(true);
    }

    public void startLoadIspConfigList(AlbumItem albumItem){
        if (albumItem == null) {
            return;
        }
        Log.d(TAG, "startLoadIspConfigList: albumItem = "+albumItem);
        ArrayList<AlbumItem> arrayList = getListFromType(albumItem.getFileType(), false);
        boolean isFind = false;
        AlbumItem albumItem1 = null;
        for (int i = 0; i < arrayList.size(); i++){
            albumItem1 = arrayList.get(i);
            Log.d(TAG, "startLoadIspConfigList: albumItem1 = "+albumItem1);
            if (albumItem1.getClassIndex() == albumItem.getClassIndex()) {
                isFind = true;
                break;
            }
        }

        if (isFind){
            if (albumItem1.getIspConfigCount() == -1){
                OrderCommunication.getInstance().SP_GET_ISP_CFG_FILE("type:" + albumItem1.getFileType()
                        + ";class:" + (albumItem1.getClassIndex()) + ";");
            }
        }

    }

    public boolean startLoadCombineList(AlbumItem albumItem) {
        if (albumItem == null || mHolderFileInfoList == null) {
            return false;
        }
        int type = albumItem.getFileType();
        int classIndex = albumItem.getClassIndex();

        boolean find = false;
        for (int i = 0; i < mHolderFileInfoList.size(); i++) {
            HolderFileInfo holderFileInfo = mHolderFileInfoList.get(i);
            Log.d(TAG, "startLoadCombineList: holderFileInfo = " + holderFileInfo);
            Log.d(TAG, "startLoadCombineList: type = " + type + " , classIndex = " + classIndex);
            if (holderFileInfo != null && holderFileInfo.type == type) {
                Log.d(TAG, "startLoadCombineList: classItemList.size() = " + holderFileInfo.classItemList.size());
                for (int j = 0; j < holderFileInfo.classItemList.size(); j++) {
                    if (holderFileInfo.classItemList.get(j).classIndex == classIndex) {
                        ClassItemInfo classItemInfo = holderFileInfo.classItemList.get(j);
                        Log.d(TAG, "startLoadCombineList: classItemInfo = " + classItemInfo.toString());
                        Log.d(TAG, "startLoadCombineList: classItemInfo.prepareInit = " + classItemInfo.prepareInit);
                        if (!classItemInfo.prepareInit && classItemInfo.initIndex < classItemInfo.itemCount) {
                            Log.d(TAG, "startLoadCombineList: classItemInfo.initIndex = " + classItemInfo.initIndex + " , classItemInfo.itemCount = " + classItemInfo.itemCount);
                            classItemInfo.prepareInit = true;
                            find = true;
                        }
                        break;
                    }
                }
                break;
            }
        }

        Log.d(TAG, "startLoadCombineList: find = " + find + " , albumItem = " + albumItem);
        if (find) {
            isOwnGet = true;
            getFileCombine(true, type, classIndex);
        }

        return find;
    }

    private void continueGetFileCombine(int preFileType, int preClassIndex) {
        if (mHolderFileInfoList == null) {
            return;
        }
        Log.d(TAG, "continueGetFileCombine: getFileCombine preFileType = " + preFileType + " , preClassIndex = " + preClassIndex);
        for (int i = 0; i < mHolderFileInfoList.size(); i++) {
            if (i == 0) {
                continue;
            }
            HolderFileInfo holderFileInfo = mHolderFileInfoList.get(i);
            if (holderFileInfo != null && holderFileInfo.type == preFileType) {
                for (int j = 0; j < holderFileInfo.classItemList.size(); j++) {
                    ClassItemInfo classItemInfo = holderFileInfo.classItemList.get(j);
                    if (classItemInfo != null && classItemInfo.classIndex == preClassIndex) {
                        classItemInfo.addCount++;
                        Log.d(TAG, "continueGetFileCombine: getFileCombine classItemInfo.addCount = " + classItemInfo.addCount + " , classItemInfo.prepareAddCount = " + classItemInfo.prepareAddCount);
                        if (classItemInfo.addCount >= classItemInfo.prepareAddCount) {
                            getFileCombine(false, preFileType, preClassIndex);
                        }
                    }
                }
                break;
            }
        }
    }

    //获取一级目录
    private void getFileMenus(boolean isFirstGetFile, int preFileType, int preClassIndex) {
        if (mHolderFileInfoList == null) {
            return;
        }

        if (mHandler != null) {
            mHandler.removeMessages(GET_FILE_TIME_OUT);
        }

        boolean isSendOrder = false;
        int sendType = -1;
        int sendClass = -1;

        for (int i = 0; i < mHolderFileInfoList.size(); i++) {
            HolderFileInfo holderFileInfo = mHolderFileInfoList.get(i);

            if (i == 0) {
                if (!isFirstGetFile && preFileType == holderFileInfo.type) {
                    holderFileInfo.initIndex += oneTimegetFileCount;
                    holderFileInfo.initIndex++;
                }

                if (holderFileInfo.initIndex >= (holderFileInfo.classSize)) {
                    continue;
                }

                int start = holderFileInfo.initIndex;
                int end;
                if ((holderFileInfo.classSize - 1) - holderFileInfo.initIndex > oneTimegetFileCount) {
                    end = start + oneTimegetFileCount;
                } else {
                    end = start + (holderFileInfo.classSize - 1) - holderFileInfo.initIndex;
                }
                OrderCommunication.getInstance().SP_GET_FILE_LIST("type:" + holderFileInfo.type
                        + ";class:" + 0 + ";start:" + start + ";end:" + end + ";");
                sendType = holderFileInfo.type;
                sendClass = 0;
                prepareGetMenusCount = end - start + 1;
                addMenusCount = 0;
                isSendOrder = true;
            } else {

                for (int j = 0; j < holderFileInfo.classItemList.size(); j++) {
                    ClassItemInfo classItemInfo = holderFileInfo.classItemList.get(j);

                    if (classItemInfo.itemCount == -1) {
                        continue;
                    } else if (classItemInfo.initIndex > 0) {
                        continue;
                    }

                    classItemInfo.initIndex++;
                    OrderCommunication.getInstance().SP_GET_FILE_LIST("type:" + holderFileInfo.type
                            + ";class:" + (j + 1) + ";start:" + 0 + ";end:" + 0 + ";");
                    sendType = holderFileInfo.type;
                    sendClass = j + 1;
                    prepareGetMenusCount = 1;
                    addMenusCount = 0;
                    isSendOrder = true;
                    break;

                }
            }

            if (isSendOrder) {
                if (mHandler != null) {
                    mHandler.removeMessages(GET_FILE_TIME_OUT);
                    Message message = new Message();
                    message.what = GET_FILE_TIME_OUT;
                    message.obj = isFirstGetFile;
                    message.arg1 = sendType;
                    message.arg2 = sendClass;
                    mHandler.sendMessageDelayed(message, 1000);
                }
                break;
            }

        }

        Log.d(TAG, "OrderCommunication --------- getFileMenus: isSendOrder = " + isSendOrder);
        if (!isSendOrder) {
            formatYuntaiFileInfoList(false);
        }

    }

    //获取二级目录
    private void getFileCombine(boolean isFirstGetFile, int preFileType, int preClassIndex) {
        if (mHolderFileInfoList == null) {
            return;
        }
        if (mHandler != null) {
            mHandler.removeMessages(GET_COMBINE_FILE_TIME_OUT);
        }
        Log.d(TAG, "getFileCombine: isFirstGetFile = " + isFirstGetFile + " , preFileType = " + preFileType + " , preClassIndex = " + preClassIndex);
        boolean isSendOrder = false;
        boolean hasInitClass = false;
        int sendType = -1;
        int sendClass = -1;
        for (int i = 0; i < mHolderFileInfoList.size(); i++) {
            HolderFileInfo holderFileInfo = mHolderFileInfoList.get(i);
            if (i == 0) {
                continue;
            } else {
//                Log.d(TAG, "OrderCommunication ---- getFileListItem: i = "+i+" , "+holderFileInfo.classItemList.size());
                for (int j = 0; j < holderFileInfo.classItemList.size(); j++) {
                    ClassItemInfo classItemInfo = holderFileInfo.classItemList.get(j);
                    if (!classItemInfo.prepareInit) {
                        continue;
                    }
                    hasInitClass = true;
//                    Log.e(TAG, "OrderCommunication getFileListItem: isFirstGetFile = "+isFirstGetFile
//                            +" , preFileType = "+preFileType+" , holderFileInfo.type = "+holderFileInfo.type
//                            +" , preClassIndex = "+preClassIndex);
                    if (!isFirstGetFile) {
                        if (preFileType == holderFileInfo.type && (classItemInfo.classIndex) == preClassIndex) {
                            classItemInfo.initIndex += oneTimegetFileCount;
                            classItemInfo.initIndex++;
                        } else {
                            continue;
                        }
                    } else {
                        if (preFileType != holderFileInfo.type || (classItemInfo.classIndex) != preClassIndex) {
                            continue;
                        }
                    }
//                    Log.e(TAG, "OrderCommunication getFileListItem: i = "+i+" ,j = "+j+" , classItemInfo = "+classItemInfo);
                    Log.d(TAG, "getFileCombine: classItemInfo.itemCount = " + classItemInfo.itemCount + " , classItemInfo.initIndex = " + classItemInfo.initIndex);
                    if (classItemInfo.itemCount == -1) {
                        classItemInfo.prepareInit = false;
                        continue;
                    } else if (classItemInfo.initIndex >= (classItemInfo.itemCount)) {
                        classItemInfo.prepareInit = false;
                        continue;
                    }

                    int start = classItemInfo.initIndex;
                    // TODO: 2021/11/6 从0开始获取
                    if (start == 1) {
                        start = 0;
                    }

                    int end;
                    if ((classItemInfo.itemCount - 1) - start > oneTimegetFileCount) {
                        end = start + oneTimegetFileCount;
                    } else {
                        end = classItemInfo.itemCount - 1;
                    }
                    Log.d(TAG, "getFileCombine: type = " + holderFileInfo.type + " , class = " + (classItemInfo.classIndex) + " , start = " + start + " , end = " + end);
                    OrderCommunication.getInstance().SP_GET_FILE_LIST("type:" + holderFileInfo.type
                            + ";class:" + (classItemInfo.classIndex) + ";start:" + start + ";end:" + end + ";");
                    sendType = holderFileInfo.type;
                    sendClass = classItemInfo.classIndex;
//                    prepareGetCombineCount = end - start + 1;
//                    addCombineCount = 0;
                    classItemInfo.addCount = 0;
                    classItemInfo.prepareAddCount = end - start + 1;
                    isSendOrder = true;
                    break;
                }
            }

            if (isSendOrder) {
                if (mHandler != null) {
                    mHandler.removeMessages(GET_COMBINE_FILE_TIME_OUT);
                    Message message = new Message();
                    message.what = GET_COMBINE_FILE_TIME_OUT;
                    message.obj = isFirstGetFile;
                    message.arg1 = sendType;
                    message.arg2 = sendClass;
                    mHandler.sendMessageDelayed(message, 2000);
                }
                break;
            }
        }

        Log.d(TAG, "OrderCommunication --------- getFileListItem: isSendOrder = " + isSendOrder + " , hasInitClass = " + hasInitClass);
        if (!isSendOrder && hasInitClass) {
            formatYuntaiFileInfoList(true);
        }
    }

    //更新云台文件列表
    private void formatYuntaiFileInfoList(boolean isFormatListCombine) {
        if (isFormatListCombine) {
            formatFileListCombine(yuntaiTempNormalInfoList);
        }
        yuntaiNormalInfoList.clear();
        yuntaiNormalInfoList.addAll(yuntaiTempNormalInfoList);

        if (isFormatListCombine) {
            formatFileListCombine(yuntaiTempLapseInfoList);
        }
        yuntaiLapseInfoList.clear();
        yuntaiLapseInfoList.addAll(yuntaiTempLapseInfoList);

        if (isFormatListCombine) {
            formatFileListCombine(yuntaiTempFocusInfoList);
        }
        yuntaiFocusInfoList.clear();
        yuntaiFocusInfoList.addAll(yuntaiTempFocusInfoList);

        if (isFormatListCombine) {
            formatFileListCombine(yuntaiTempPanInfoList);
        }
        yuntaiPanInfoList.clear();
        yuntaiPanInfoList.addAll(yuntaiTempPanInfoList);

        if (isFormatListCombine) {
            formatFileListCombine(yuntaiTempSunInfoList);
        }
        yuntaiSunInfoList.clear();
        yuntaiSunInfoList.addAll(yuntaiTempSunInfoList);

        if (isFormatListCombine) {
            formatFileListCombine(yuntaiTempHDRInfoList);
        }
        yuntaiHDRInfoList.clear();
        yuntaiHDRInfoList.addAll(yuntaiTempHDRInfoList);

        if (isFormatListCombine) {
            formatFileListCombine(yuntaiTempStarSkyInfoList);
        }
        yuntaiStarSkyInfoList.clear();
        yuntaiStarSkyInfoList.addAll(yuntaiTempStarSkyInfoList);

//            test(yuntaiNormalInfoList);
//            test(yuntaiLapseInfoList);
//            test(yuntaiFocusInfoList);
//            test(yuntaiPanInfoList);
//            test(yuntaiSunInfoList);
//            test(yuntaiHDRInfoList);

        yuntaifileInfoList.clear();
        yuntaifileInfoList.addAll(yuntaiNormalInfoList);
        yuntaifileInfoList.addAll(yuntaiLapseInfoList);
        yuntaifileInfoList.addAll(yuntaiFocusInfoList);
        yuntaifileInfoList.addAll(yuntaiPanInfoList);
        yuntaifileInfoList.addAll(yuntaiSunInfoList);
        yuntaifileInfoList.addAll(yuntaiHDRInfoList);
        yuntaifileInfoList.addAll(yuntaiStarSkyInfoList);
        yuntaifilecount = yuntaifileInfoList.size();
        yuntaiFileLoadComplete();
    }

    private void test(ArrayList<AlbumItem> arrayList) {
        for (AlbumItem albumItem : arrayList) {
            Log.d(TAG, "OrderCommunication -------- test: " + albumItem.toString());
        }
    }

    //修改文件名，全景、焦点、HDR，如果还没合成上传，则创建SP_out.jpg路径
    private void formatFileListCombine(ArrayList<AlbumItem> arrayList) {
//        for (int i = 0; i < arrayList.size(); i++) {
//            AlbumItem albumItem = null;
//            albumItem = AlbumItem.Created(arrayList.get(i));
//            if (albumItem.getRelativePath().contains("SP_out")) {
//                continue;
//            }
//
//            if (albumItem != null && albumItem.isCombine()) {
//                ArrayList<AlbumItem> combineItemList = albumItem.getCombineItemList();
//                if (albumItem.getFileType() == CMD.FILE_TYPE_PAN ||
//                        albumItem.getFileType() == CMD.FILE_TYPE_FOCUS ||
//                        albumItem.getFileType() == CMD.FILE_TYPE_HDR) {
//                    AlbumItem outAlbumItem = AlbumItem.Created(albumItem);
//                    String relativePath = albumItem.getRelativePath();
//                    String filePath = albumItem.getFilePath();
//                    String mediaFormat = albumItem.getMediaFormat();
//
//                    String result = relativePath.substring(0, relativePath.lastIndexOf("/")) + "/SP_out.jpg";
//                    outAlbumItem.setRelativePath(result);
//
//                    result = filePath.substring(0, filePath.lastIndexOf("/")) + "/SP_out.jpg";
//                    outAlbumItem.setFilePath(result);
//
//                    outAlbumItem.setMediaFormat("jpg");
//                    outAlbumItem.setVideo(false);
//                    outAlbumItem.setRaw(false);
//                    outAlbumItem.setRawAlbumItem(null);
//
//                    File file = new File(outAlbumItem.getFilePath());
//                    Log.d(TAG, "formatFileListCombine: getFilePath = " + outAlbumItem.getFilePath());
//                    Log.d(TAG, "formatFileListCombine: exists = " + file.exists());
//                    if (file.exists()) {
//                        outAlbumItem.setContentLength(file.length());
//                        outAlbumItem.setDownloadLength(file.length());
//                        outAlbumItem.setExistLocal(true);
//                    } else {
//                        outAlbumItem.setDownloadLength(0);
//                        outAlbumItem.setExistLocal(false);
//                    }
//
//                    outAlbumItem.setCombineItemList(combineItemList);
//                    arrayList.set(i, outAlbumItem);
//                    continue;
//                }
//
////                if (!albumItem.getRelativePath().contains("SP_out")){
////                    AlbumItem outAlbumItem = null;
////                    for (AlbumItem albumItem1 : combineItemList){
////                        if (albumItem1.getRelativePath().contains("SP_out")){
////                            outAlbumItem = AlbumItem.Created(albumItem1);
////                            break;
////                        }
////                    }
////                    if (outAlbumItem == null) {
////                        for (AlbumItem albumItem1 : combineItemList){
////                            if (albumItem1.getRelativePath().endsWith(".jpg")){
////                                outAlbumItem = AlbumItem.Created(albumItem1);
////                                break;
////                            }
////                        }
////                    }
////                    if (outAlbumItem != null) {
////                        outAlbumItem.setCombineItemList(combineItemList);
////                        arrayList.set(i,outAlbumItem);
////                    }
////                }
//            }
//        }
    }

    //解析云台返回文件查询指令
    public void getFileListFromResponse(String msg, boolean isAddFile) {
        Log.d(TAG, "getFileListFromResponse: isAddFile = " + isAddFile + ", isOwnGet = " + isOwnGet + " , msg = " + msg);
//        if (!isOwnGet)
//            return;

        if (msg == null) {
            return;
        }

        try {

            String[] data = msg.split(";");
            AlbumItem albumItem = new AlbumItem();
            int fileType = 0;
            int classIndex = -1;
            long lastModifiedTime = System.currentTimeMillis();
            for (String value : data) {
                String text = value.substring(value.lastIndexOf(":") + 1).trim();
                if (value.contains("path")) {
                    if (TextUtils.isEmpty(text)) {
                        continue;
                    }
                }
                if (value.contains("cTime")) {
                    text = value.substring(value.indexOf(":") + 1).trim();
                    lastModifiedTime = setLastModifiedTime(text, albumItem);
                } else if (value.contains("size")) {
                    albumItem.setContentLength(Long.parseLong(text));
                }

                if (value.contains("type")) {
                    try {
                        fileType = Integer.parseInt(text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    albumItem.setFileType(fileType);
                }
            }
            for (String value : data) {
                String text = value.substring(value.lastIndexOf(":") + 1).trim();
                albumItem.setExistYunTai(true);

                if (value.contains("path")) {
                    if (TextUtils.isEmpty(text)) {
                        continue;
                    }
                    String[] pathArray = text.split("/");
                    if (pathArray == null)
                        continue;
                    int pathArrayLength = pathArray.length;
                    if (pathArrayLength < 4) {
                        continue;
                    }


                    String fileName = createFileName(lastModifiedTime, pathArray[pathArrayLength - 1]);
                    String filePath = FILE_DEFAULT_FOLDER + "/" +
                            pathArray[pathArrayLength - 4] + "/" +
                            pathArray[pathArrayLength - 3] + "/" +
                            pathArray[pathArrayLength - 2] + "/" +
                            fileName;

                    if (fileType == CMD.FILE_TYPE_NORMAL) {
                        filePath = FILE_DEFAULT_FOLDER + "/" +
                                pathArray[pathArrayLength - 3] + "/" +
                                pathArray[pathArrayLength - 2] + "/" +
                                fileName;
                    }

                    Log.d(TAG, "getFileListFromResponse: filePath = " + filePath);

                    //2021-12-31 17:17:52.896 6083-6269/com.example.wifidemo1 D/MediaFileUtils: getFileListFromResponse: isAddFile = false, isOwnGet = true , msg = type:3;class:1;path:/app/sd/focusStack/class_1/SP_0001.jpg;size:5734193;cTime:2021-12-01 15:27:34;duration:0;
                    //2021-12-31 17:17:52.896 6083-6269/com.example.wifidemo1 D/MediaFileUtils: getFileListFromResponse: filePath = /storage/emulated/0/Polaris/sd/focusStack/class_1/202112011527SP_0001.jpg

                    albumItem.setExistLocal(false);
                    File file = new File(filePath);
                    Log.d(TAG, "getFileListFromResponse: file.exists() = "+file.exists());
                    if (file != null && file.exists()) {
                        albumItem.setDownloadLength(file.length());
                        Log.d(TAG, "getFileListFromResponse: getDownloadLength = " + albumItem.getDownloadLength()
                                + " , getContentLength = " + albumItem.getContentLength());
//                        if (albumItem.getDownloadLength() == albumItem.getContentLength())
//                            albumItem.setExistLocal(true);
                        // TODO: 2021/12/7 推送到云台的文件和本地文件大小不一定完全相等
                        if (albumItem.getDownloadLength() >= albumItem.getContentLength())
                            albumItem.setExistLocal(true);
                    }
                    albumItem.setRelativePath(text);
                    albumItem.setFilePath(filePath);
                    albumItem.setMediaFormat(fileName.substring(fileName.lastIndexOf(".") + 1));
                    String result = text.substring(5).trim();
                    albumItem.setUrl(OrderCommunication.getInstance().resourceAddress  + result);

                    //http://192.168.0.1/sd/panorama/class_3/out.jpg
                    //http://192.168.0.1/sd/panorama/class_2/out.jpg
                    //http://192.168.0.1/sd/panorama/class_1/out.jpg

                    if (text.endsWith(".DNG") || text.endsWith(".dng"))
                        albumItem.setDNG(true);

                    if (text.toLowerCase().endsWith(".mov")) {
                        albumItem.setVideo(true);
                        albumItem.setRaw(false);
                        albumItem.setThumbnailurl(albumItem.getUrl());
                    } else if (text.toLowerCase().endsWith(".jpg")) {
                        albumItem.setRaw(false);
                        albumItem.setVideo(false);
                        albumItem.setThumbnailurl(albumItem.getUrl());
                    } else {
                        albumItem.setRaw(true);
                        albumItem.setVideo(false);
                        albumItem.setThumbnailurl(albumItem.getUrl().replace("." + albumItem.getMediaFormat(), ".JPG"));
                    }
                }

                if (value.contains("type")) {
//                    if ("1".equals(text)){
//                        albumItem.setVideo(false);
//                    }else {
//                        albumItem.setVideo(true);
//                    }
                    try {
                        fileType = Integer.parseInt(text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    albumItem.setFileType(fileType);
                }

                if (isAddFile) {
                    if (value.contains("path")) {
                        if (TextUtils.isEmpty(text)) {
                            continue;
                        }
                        String[] pathArray = text.split("/");
                        if (pathArray == null)
                            continue;
                        int pathArrayLength = pathArray.length;
                        if (pathArrayLength < 5) {
                            continue;
                        }

                        String className = pathArray[4];
                        if (!className.contains("class_")) {
                            continue;
                        }

                        int index = className.lastIndexOf("_");
                        String result = className.substring(index + 1);
                        try {
                            classIndex = Integer.parseInt(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        albumItem.setClassIndex(classIndex);
                    }
                } else {
                    if (value.contains("class") && !value.contains("class_")) {
                        try {
                            classIndex = Integer.parseInt(text);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        albumItem.setClassIndex(classIndex);
                    }
                }

                if (value.contains("duration")) {
                    albumItem.setVideoDuration(text);
//                    if ("0".equals(text)) {
////                        albumItem.setVideo(false);
//                        if (albumItem.isDNG())
//                            albumItem.setThumbnailurl(albumItem.getUrl().replace("." + albumItem.getMediaFormat(), ".JPG"));
//                        else
//                            albumItem.setThumbnailurl(albumItem.getUrl());
//                    } else {
////                        albumItem.setVideo(true);
//                        albumItem.setThumbnailurl(albumItem.getUrl().replace("." + albumItem.getMediaFormat(), ".THM"));
//                    }
                }
            }

            if (inWaitQueus(albumItem))
                albumItem.setDownloadStatus(DOWNLOAD_STATE_WAIT);

            boolean isDisplay = addAlbumToList(fileType, classIndex, albumItem);

            ArrayList<AlbumItem> list = new ArrayList<>();
            int size = 0;
            for (AlbumItem albumItem1 : getListFromType(fileType, false)) {
                if (albumItem1.isCombine()) {
                    size += albumItem1.getCombineItemList().size();
                } else {
                    size++;
                }
            }
//            if (size < yuntaifilecount) {
//                if (addCount >= oneTimegetFileCount) {
//                    getfilelistwithdetail();
//                }else {
//                    addCount++;
//                }
////                EventBus.getDefault().post(albumItemOnepage);
//                list.add(albumItem);
//                EventBus.getDefault().post(list);
//            } else {
//                yuntaiFileLoadComplete();
//            }
            Log.d(TAG, "getFileListFromResponse: albumItem = " + albumItem.toString());
            Log.d(TAG, "getFileListFromResponse: albumItem.getThumbnailurl() = " + albumItem.getThumbnailurl());
            if (isAddFile) {
                if (isDisplay) {
                    list.add(albumItem);
                    EventBus.getDefault().post(list);
                }

                formatYuntaiFileInfoList(true);
            } else {
                addMenusCount++;
                Log.d(TAG, "OrderCommunication getFileListFromResponse: addMenusCount = "
                        + addMenusCount + " , prepareGetMenusCount = " + prepareGetMenusCount);
                if (prepareGetMenusCount > 0) {
                    if (addMenusCount >= prepareGetMenusCount) {
                        getFileClassCount(false);
                    }
                } else {
                    continueGetFileCombine(albumItem.getFileType(), albumItem.getClassIndex());
                }
                if (addMenusCount >= prepareGetMenusCount) {
//                    getFileListItem(false,albumItem.getFileType(),albumItem.getClassIndex());
//                    getFileMenus(false,albumItem.getFileType(),albumItem.getClassIndex());
                }

//                if (isGetFileCompleted()) {
//                    yuntaiFileLoadComplete();
//                }else {
                if (isDisplay) {
                    list.add(albumItem);
                    EventBus.getDefault().post(list);
                }else {

                }
//                    continueGetFileFromType(fileType,false);
//                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            yuntaiFileLoadComplete();
        }
    }

    private int isEqualsName(ArrayList<AlbumItem> arrayList, AlbumItem albumItem) {
        int position = -1;

        if (arrayList == null || albumItem == null) {
            return position;
        }

        String srcPath = albumItem.getRelativePath().substring(0, albumItem.getRelativePath().lastIndexOf("."));
//        Log.d(TAG, "isEqualsName: albumItem = "+albumItem);
//        Log.d(TAG, "isEqualsName: srcPath = "+srcPath);
        for (int i = 0; i < arrayList.size(); i++) {
            String temp = arrayList.get(i).getRelativePath().substring(0, arrayList.get(i).getRelativePath().lastIndexOf("."));
//            Log.d(TAG, "isEqualsName: temp = "+temp);
            if (srcPath.equals(temp)) {
                position = i;
                break;
            }
        }

//        Log.d(TAG, "isEqualsName: position = "+position);
        return position;
    }

    private AlbumItem formatRawAlbum(AlbumItem albumItem) {
        AlbumItem result = AlbumItem.Created(albumItem);

        if (result.getRawAlbumItem() != null) {
//            Log.d(TAG, "formatRawAlbum: "+result.getMediaFormat());
//            Log.d(TAG, "formatRawAlbum: "+result.getRawAlbumItem().getMediaFormat());
//            Log.d(TAG, "formatRawAlbum: "+result);
//            Log.d(TAG, "formatRawAlbum: "+result.getRawAlbumItem());
            if (result.getMediaFormat().toLowerCase().contains("jpg") ||
                    result.getMediaFormat().toLowerCase().contains("mov")) {
                result.setRaw(false);
                result.getRawAlbumItem().setRaw(true);
            } else {
                AlbumItem temp = AlbumItem.Created(albumItem);
                temp.setRaw(true);
                temp.setRawAlbumItem(null);
                result = AlbumItem.Created(albumItem.getRawAlbumItem());
                result.setRaw(false);
                result.setRawAlbumItem(temp);
            }
        }

        return result;
    }

    public boolean addAlbumToList(int fileType, int fileClass, AlbumItem albumItem) {
//        Log.d(TAG, "addAlbumToList: albumItem = " + albumItem.getFileType() + " , " + albumItem.getClassIndex() + " , fileClass = " + fileClass);
        boolean isDisplay = false;
        if (fileType == CMD.FILE_TYPE_NORMAL) {
            if (albumItem.isExistLocal()) {
                setAlbumItemSize(albumItem);
            }

            int index = getListFromType(fileType, false).indexOf(albumItem);

            ArrayList<AlbumItem> arrayList = getListFromType(fileType, false);
            ArrayList<AlbumItem> arrayListTemp = getListFromType(fileType, true);

            if (index < 0) {
                int position = isEqualsName(arrayList, albumItem);
                if (position == -1) {
                    arrayList.add(albumItem);
                } else {
                    arrayList.get(position).setRawAlbumItem(albumItem);
                }
                isDisplay = true;
            } else {
                getListFromType(fileType, false).set(index, albumItem);
            }

            index = getListFromType(fileType, true).indexOf(albumItem);

            if (index < 0) {
                int position = isEqualsName(arrayListTemp, albumItem);
                if (position == -1) {
                    arrayListTemp.add(albumItem);
                } else {
                    arrayListTemp.get(position).setRawAlbumItem(albumItem);
                    arrayListTemp.set(position, formatRawAlbum(arrayListTemp.get(position)));
                }
            } else {
                getListFromType(fileType, true).set(index, albumItem);
            }
        } else {
            if (albumItem.isExistLocal()) {
                setAlbumItemSize(albumItem);
            }

            ArrayList<AlbumItem> arrayList = getListFromType(fileType, false);
            int index = arrayList.indexOf(albumItem);
            if (index < 0) {
                int tempIndex = -1;
                for (int i = 0; i < arrayList.size(); i++) {
//                    Log.d(TAG, "addAlbumToList: arrayList.get("+i+") = "+arrayList.get(i));
                    if (fileClass == arrayList.get(i).getClassIndex()) {
                        tempIndex = i;
                        break;
                    }
                }
                if (tempIndex == -1) {
                    int position = isEqualsName(arrayList, albumItem);
                    if (position == -1) {
                        arrayList.add(albumItem);
                    } else {
                        arrayList.get(position).setRawAlbumItem(albumItem);
                    }
                    isDisplay = true;
                }
            } else {
                getListFromType(fileType, false).set(index, albumItem);
            }

            arrayList = getListFromType(fileType, true);
            index = arrayList.indexOf(albumItem);
            if (index < 0) {
                int tempIndex = -1;
                for (int i = 0; i < arrayList.size(); i++) {
//                    Log.d(TAG, "addAlbumToList: --- arrayList.get("+i+") = "+arrayList.get(i));
                    if (fileClass == arrayList.get(i).getClassIndex()) {
                        tempIndex = i;
                        break;
                    }
                }
                if (tempIndex == -1) {
                    AlbumItem albumItem1 = AlbumItem.Created(albumItem);
                    albumItem1.initCombineItemList();
                    arrayList.add(albumItem1);
                } else {
                    arrayList.get(tempIndex).addCombineItem(albumItem);
                }
            } else {
                arrayList.set(index, AlbumItem.Created(albumItem));
            }
        }

        return isDisplay;
    }

    //获取class下文件数量
    public void getFileClassCount(boolean isFirst) {
        if (mHolderFileInfoList == null) {
            return;
        }
        Log.d(TAG, "getFileClassCount: mHolderFileInfoList.size() = " + mHolderFileInfoList.size());
        boolean isCompleted = true;
        boolean isSendOrder = false;
        for (int i = 0; i < mHolderFileInfoList.size(); i++) {
            HolderFileInfo holderFileInfo = mHolderFileInfoList.get(i);
//            if (i == 0) {
//                continue;
//            }
            if (i == 0) {
                if (!isFirst) {
                    holderFileInfo.initIndex += oneTimegetFileCount;
                    holderFileInfo.initIndex++;
                }

                if ((holderFileInfo.initIndex) >= holderFileInfo.classSize) {
                    prepareGetMenusCount = 0;
                    continue;
                }

                int start = holderFileInfo.initIndex;
                int end;
                if ((holderFileInfo.classSize - 1) - holderFileInfo.initIndex > oneTimegetFileCount) {
                    end = start + oneTimegetFileCount;
                } else {
                    end = start + (holderFileInfo.classSize - 1) - holderFileInfo.initIndex;
                }
                OrderCommunication.getInstance().SP_GET_FILE_LIST("type:" + holderFileInfo.type
                        + ";class:" + 0 + ";start:" + start + ";end:" + end + ";");
                prepareGetMenusCount = end - start + 1;
                addMenusCount = 0;
                isSendOrder = true;
                break;
            }
            Log.d(TAG, "getFileClassCount: i = " + i);
            Log.d(TAG, "getFileClassCount: holderFileInfo = " + holderFileInfo.toString());
            if ((holderFileInfo.initIndex) < holderFileInfo.classSize) {
//                OrderCommunication.getInstance().SP_GET_CLASS_FILE_COUNT(
//                        "type:"+holderFileInfo.type+";class:"+(holderFileInfo.initIndex+1)+";");
                OrderCommunication.getInstance().SP_GET_CLASS_FILE_COUNT(
                        "type:" + holderFileInfo.type + ";");
                isCompleted = false;
                break;
            }
        }

//        if (isCompleted) {
////            getFileListItem(true,-1, -1);
//            getFileMenus(true,-1, -1);
//        }

        Log.d(TAG, "getFileClassCount: isCompleted = " + isCompleted);
        if (isCompleted) {
            formatYuntaiFileInfoList(false);
        }
    }

    //获取class目录查询返回
    public synchronized void getFileClassCountResponse(String message) {
        if (message != null) {
            String[] data = message.split(";");
            AlbumItem albumItem = new AlbumItem();
            int fileType = -1;
            int classFileCount = 0;
            int jpegCount = 0;
            int rawCount = 0;
            int classIndex = -1;
            int classCount = 0;
            int dCount = 0;
            long lastModifiedTime = System.currentTimeMillis();
            for (String value : data) {
                String text = value.substring(value.lastIndexOf(":") + 1).trim();
                if (value.contains("path")) {
                    if (TextUtils.isEmpty(text)) {
                        continue;
                    }
                }
                if (value.contains("cTime")) {
                    text = value.substring(value.indexOf(":") + 1).trim();
                    lastModifiedTime = setLastModifiedTime(text, albumItem);
                } else if (value.contains("size")) {
                    albumItem.setContentLength(Long.parseLong(text));
                }
            }
            for (String value : data) {
                try {
                    String text = value.substring(value.lastIndexOf(":") + 1).trim();
                    if (value.contains("path")) {
                        if (TextUtils.isEmpty(text)) {
                            continue;
                        }

                        String[] pathArray = text.split("/");
                        if (pathArray == null)
                            continue;
                        int pathArrayLength = pathArray.length;
                        if (pathArrayLength < 4) {
                            continue;
                        }

                        String fileName = createFileName(lastModifiedTime, pathArray[pathArrayLength - 1]);
                        String filePath = FILE_DEFAULT_FOLDER + "/" +
                                pathArray[pathArrayLength - 4] + "/" +
                                pathArray[pathArrayLength - 3] + "/" +
                                pathArray[pathArrayLength - 2] + "/" +
                                fileName;

                        Log.d(TAG, "getFileListFromResponse: filePath = " + filePath);

                        albumItem.setExistLocal(false);
                        File file = new File(filePath);

                        if (file != null && file.exists()) {
                            albumItem.setDownloadLength(file.length());
                            Log.d(TAG, "getFileListFromResponse: getDownloadLength = " + albumItem.getDownloadLength()
                                    + " , getContentLength = " + albumItem.getContentLength());
                            if (albumItem.getDownloadLength() >= albumItem.getContentLength())
                                albumItem.setExistLocal(true);
                        }
                        albumItem.setRelativePath(text);
                        albumItem.setFilePath(filePath);
                        albumItem.setMediaFormat(fileName.substring(fileName.lastIndexOf(".") + 1));
                        String result = text.substring(5).trim();
                        albumItem.setUrl(OrderCommunication.getInstance().resourceAddress  + result);

                        //http://192.168.0.1/sd/panorama/class_3/out.jpg
                        //http://192.168.0.1/sd/panorama/class_2/out.jpg
                        //http://192.168.0.1/sd/panorama/class_1/out.jpg

                        if (text.endsWith(".DNG") || text.endsWith(".dng"))
                            albumItem.setDNG(true);

                        if (text.toLowerCase().endsWith(".mov")) {
                            albumItem.setVideo(true);
                            albumItem.setRaw(false);
                            albumItem.setThumbnailurl(albumItem.getUrl());
                        } else if (text.toLowerCase().endsWith(".jpg")) {
                            albumItem.setRaw(false);
                            albumItem.setVideo(false);
                            albumItem.setThumbnailurl(albumItem.getUrl());
                        } else {
                            albumItem.setRaw(true);
                            albumItem.setVideo(false);
                            albumItem.setThumbnailurl(albumItem.getUrl().replace("." + albumItem.getMediaFormat(), ".JPG"));
                        }
                    }

                    albumItem.setExistYunTai(true);


                    if (value.contains("cTime")) {
                        text = value.substring(value.indexOf(":") + 1).trim();
                        Log.d(TAG, "getFileClassCountResponse: order --- LastModifiedTime = "+text);
                        lastModifiedTime = setLastModifiedTime(text, albumItem);
                    } else if (value.contains("size")) {
                        albumItem.setContentLength(Long.parseLong(text));
                    }

                    if (value.contains("type")) {
                        fileType = Integer.parseInt(text);
                        albumItem.setFileType(fileType);
                    }

                    if (value.contains("class") && !value.contains("class_")) {
                        classIndex = Integer.parseInt(text);
                        albumItem.setClassIndex(classIndex);
                    }

                    if (value.contains("duration")) {
                        albumItem.setVideoDuration(text);
                    }

                    if (value.contains("fileCount")) {
                        classFileCount = Integer.parseInt(text);
                        albumItem.setCombineFileCount(classFileCount);
                    }

                    if (value.contains("jpgCount")){
                        jpegCount = Integer.parseInt(text);
                        albumItem.setJpegCount(jpegCount);
                    }

                    if (value.contains("rawCount")){
                        rawCount = Integer.parseInt(text);
                        albumItem.setRawCount(rawCount);
                    }

                    if (value.contains("classCount")) {
                        classCount = Integer.parseInt(text);
                    }

                    if (value.contains("dCount")){
                        dCount = Integer.parseInt(text);
                        albumItem.setDisplayFileCount(dCount);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }

            boolean isDisplay = addAlbumToList(fileType, classIndex, albumItem);
            ArrayList<AlbumItem> list = new ArrayList<>();
            if (isDisplay) {
                list.add(albumItem);
                EventBus.getDefault().post(list);
            }

            Log.d(TAG, "getFileClassCountResponse: getFileClassCount albumItem = " + albumItem);
            if (mHolderFileInfoList != null) {
                for (int i = 0; i < mHolderFileInfoList.size(); i++) {
                    HolderFileInfo holderFileInfo = mHolderFileInfoList.get(i);
                    if (holderFileInfo.type == fileType) {
                        Log.d(TAG, "getFileClassCountResponse: classIndex = " + classIndex);

                        ClassItemInfo classItemInfo = new ClassItemInfo(classFileCount, classIndex);
                        int position = holderFileInfo.classItemList.indexOf(classItemInfo);
                        if (position != -1) {
                            holderFileInfo.classItemList.set(position, classItemInfo);
                        } else {
                            holderFileInfo.classItemList.add(classItemInfo);
                        }

                        holderFileInfo.initIndex++;

                        Log.d(TAG, "getFileClassCountResponse: classItemList.size() = " + holderFileInfo.classItemList.size());
                        Log.d(TAG, "getFileClassCountResponse: getFileClassCount holderFileInfo = " + holderFileInfo);

                        if ((holderFileInfo.initIndex) >= classCount) {
                            getFileClassCount(false);
                        }
                        break;
                    }
                }
            }


        }
    }

    //获取配置文件返回信息
    public void getConfigFileResponse(String message){
        if (message != null) {
            String[] data = message.split(";");
            int fileType = -1;
            int fileClass = -1;
            int fileCount = 0;
            int fileIndex = 0;
            String filePath;
            AlbumItem configAlbumItem = new AlbumItem();

            for (String value : data) {
                try {
                    String text = value.substring(value.lastIndexOf(":") + 1).trim();

                    if (value.contains("type")) {
                        fileType = Integer.parseInt(text);
                        configAlbumItem.setFileType(fileType);
                    }

                    if (value.contains("class") && !value.contains("class_")) {
                        fileClass = Integer.parseInt(text);
                        configAlbumItem.setClassIndex(fileClass);
                    }

                    if (value.contains("count")) {
                        fileCount = Integer.parseInt(text);
                    }

                    if (value.contains("index")) {
                        fileIndex = Integer.parseInt(text);
                    }

                    if (value.contains("path")){
                        if (TextUtils.isEmpty(text)) {
                            continue;
                        }

                        String[] pathArray = text.split("/");
                        if (pathArray == null)
                            continue;
                        int pathArrayLength = pathArray.length;
                        if (pathArrayLength < 4) {
                            continue;
                        }


                        filePath = FILE_DEFAULT_FOLDER + "/" +
                                pathArray[pathArrayLength - 4] + "/" +
                                pathArray[pathArrayLength - 3] + "/" +
                                pathArray[pathArrayLength - 2] + "/" +
                                pathArray[pathArrayLength - 1];

                        String result = text.substring(5).trim();
                        configAlbumItem.setUrl(OrderCommunication.getInstance().resourceAddress   + result);

                        File file = new File(filePath);
                        if (file.exists()) {
                            configAlbumItem.setExistLocal(true);
                        }
                        configAlbumItem.setFilePath(filePath);
                        configAlbumItem.setRelativePath(text);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            ArrayList<AlbumItem> arrayList = getListFromType(fileType, false);
            boolean isFind = false;
            AlbumItem albumItem = null;
            for (int i = 0; i < arrayList.size(); i++){
                albumItem = arrayList.get(i);
                Log.d(TAG, "startLoadIspConfigList: albumItem = "+albumItem);
                if (albumItem.getClassIndex() == fileClass) {
                    isFind = true;
                    break;
                }
            }

            if (isFind) {
                int type = albumItem.getFileType();
                int classIndex = albumItem.getClassIndex();
                albumItem.setIspConfigCount(fileCount);
                if (fileCount == 0){
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_YUNTAI_CONFIG_FILE_LOAD_COMPLETE,type,classIndex,false));
                } else{
                    albumItem.addIspConfigItem(configAlbumItem);
                    if (fileCount == albumItem.getIspConfigList().size()){
                        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_YUNTAI_CONFIG_FILE_LOAD_COMPLETE,type,classIndex,true));
                    }
                }
            }

        }
    }

    //推送文件返回信息
    public void getAppAddFileResponse(String message){
        if (message != null) {
            int fileType = -1;
            int classIndex = -1;
            long lastModifiedTime = System.currentTimeMillis();
            long size = 0;
            String appTime = "";
            String cTime = "";
            String[] data = message.split(";");
            for (String value : data) {
                try {
                    String text = value.substring(value.lastIndexOf(":") + 1).trim();

                    if (value.contains("type")) {
                        fileType = Integer.parseInt(text);
                    }

                    if (value.contains("class") && !value.contains("class_")) {
                        classIndex = Integer.parseInt(text);
                    }

                    if (value.contains("appTime")){
                        text = value.substring(value.indexOf(":") + 1).trim();
                        appTime = text;
                    }

                    if (value.contains("cTime")){
                        text = value.substring(value.indexOf(":") + 1).trim();
                        cTime = text;
                    }

                    if (value.contains("size")){
                        size = Long.parseLong(text);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            ArrayList<AlbumItem> arrayList = getListFromType(fileType, false);
            boolean isFind = false;
            AlbumItem albumItem = null;
            AlbumItem outAlbumItem = null;
            for (int i = 0; i < arrayList.size(); i++){
                albumItem = arrayList.get(i);
                Log.d(TAG, "getAppAddFileResponse: albumItem = "+albumItem);
                if (albumItem.getClassIndex() == classIndex) {

                    if (albumItem.getCombineItemList() != null) {
                        for (int j = 0; j < albumItem.getCombineItemList().size(); j++){
                            outAlbumItem = albumItem.getCombineItemList().get(j);
                            if (outAlbumItem.getFilePath().toLowerCase().endsWith("sp_out.jpg")){
                                isFind = true;
                                break;
                            }
                        }
                    }

                    break;
                }
            }

            Log.d(TAG, "getAppAddFileResponse: isFind = "+isFind+" , cTime = "+cTime);
            if (isFind) {
                String filePath = outAlbumItem.getFilePath();
                lastModifiedTime = setLastModifiedTime(cTime, outAlbumItem);
                Log.d(TAG, "getAppAddFileResponse: filePath = "+filePath);
                int index = filePath.lastIndexOf("/");
                String preFileName = filePath.substring(index + 1);
                String fileName = createFileName(lastModifiedTime, preFileName);
                Log.d(TAG, "getAppAddFileResponse: fileName = "+fileName);
                String resultPath = filePath.substring(0,index+1) + fileName;
                if (albumItem.getFilePath().equals(outAlbumItem.getFilePath())){
                    albumItem.setFilePath(resultPath);
                    albumItem.setLastModifiedTime(lastModifiedTime);
                }
                outAlbumItem.setFilePath(resultPath);

                Log.d(TAG, "getAppAddFileResponse: resultPath = "+resultPath);

                File file = new File(filePath);
                if (file.exists()) {
                    File result = new File(resultPath);
                    file.renameTo(result);
                }

            }
        }
    }

    public void sortListFromLocation(boolean isLocal){
        if (isLocal) {
            sortList(localfileInfolocationList);
        }else {
            sortList(yuntaifileInfoList);
        }
    }

    public void getDeletedFileClassResponse(String message) {
        if (message != null) {
            String[] data = message.split(";");

            for (String value : data) {
                String text = value.substring(value.lastIndexOf(":") + 1).trim();


            }
        }
    }

    public void deletedFileResponse(String response) {
        if (response == null) {
            return;
        }

        String[] data = response.split(";");
        for (String value : data) {
            if (value.contains("path")) {

            } else if (value.contains("ret")) {

            }
        }

    }


    private ArrayList<AlbumItem> getListFromType(int fileType, boolean isTemp) {
        if (isTemp) {
            if (fileType == CMD.FILE_TYPE_NORMAL) {
                return yuntaiTempNormalInfoList;
            } else if (fileType == CMD.FILE_TYPE_LAPSE) {
                return yuntaiTempLapseInfoList;
            } else if (fileType == CMD.FILE_TYPE_FOCUS) {
                return yuntaiTempFocusInfoList;
            } else if (fileType == CMD.FILE_TYPE_PAN) {
                return yuntaiTempPanInfoList;
            } else if (fileType == CMD.FILE_TYPE_SUN) {
                return yuntaiTempSunInfoList;
            } else if (fileType == CMD.FILE_TYPE_HDR) {
                return yuntaiTempHDRInfoList;
            } else {
                return yuntaiTempStarSkyInfoList;
            }
        }
        if (fileType == CMD.FILE_TYPE_NORMAL) {
            return yuntaiNormalInfoList;
        } else if (fileType == CMD.FILE_TYPE_LAPSE) {
            return yuntaiLapseInfoList;
        } else if (fileType == CMD.FILE_TYPE_FOCUS) {
            return yuntaiFocusInfoList;
        } else if (fileType == CMD.FILE_TYPE_PAN) {
            return yuntaiPanInfoList;
        } else if (fileType == CMD.FILE_TYPE_SUN) {
            return yuntaiSunInfoList;
        } else if (fileType == CMD.FILE_TYPE_HDR) {
            return yuntaiHDRInfoList;
        } else {
            return yuntaiStarSkyInfoList;
        }
    }

    private String createFileName(long lastModifiedTime, String fileName) {
        String result;
        try {
            result = getDateFileNameString(lastModifiedTime) + fileName;
        } catch (Exception e) {
            result = System.currentTimeMillis() + "_" + fileName;
        }
        return result;
    }

    private String getMediaFileModle(String fileName) {
        if (fileName == null)
            return ModelConstant.UnknownMediaType;
        if (fileName.contains(ModelConstant.SuperPhotoMediaType)) {
            return ModelConstant.SuperPhotoMediaType;
        } else if (fileName.contains(ModelConstant.LightFieldMediaType)) {
            return ModelConstant.LightFieldMediaType;
        } else if (fileName.contains(ModelConstant.LightPaintingMediaType)) {
            return ModelConstant.LightPaintingMediaType;
        } else if (fileName.contains(ModelConstant.NightExposureMediaType)) {
            return ModelConstant.NightExposureMediaType;
        } else if (fileName.contains(ModelConstant.DelayMediaType)) {
            return ModelConstant.DelayMediaType;
        } else if (fileName.contains(ModelConstant.SlowMotionMediaType)) {
            return ModelConstant.SlowMotionMediaType;
        } else {
            return ModelConstant.UnknownMediaType;
        }
    }

    private synchronized long setLastModifiedTime(String createTime, AlbumItem albumItem) {
        long lastModifiedTime;


        if (formatter == null) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        Date date = null;
        try {
            date = formatter.parse(createTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (date == null) {
            lastModifiedTime = System.currentTimeMillis();
        } else {
            int rawOffset = TimeZone.getDefault().getRawOffset();
            lastModifiedTime = date.getTime(); // date类型转成long类型
        }


        albumItem.setLastModifiedTime(lastModifiedTime);
        return lastModifiedTime;
    }

    //再回放界面删除视频文件
    public void deletefile(final String relativePath) {
        Log.d(TAG, "deletefile: 删除单个云台文件 relativePath =" + relativePath);
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (AlbumItem albumItem : yuntaifileInfoList) {
                    if (relativePath.equals(albumItem.getRelativePath())) {
                        delectYuntaiFile(albumItem);
                        return;
                    }
                }
            }
        }).start();
    }

    //只能在子线程里面卡在
    public boolean delectYuntaiFile(AlbumItem albumItem) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean deletedCombine = false;
        if (albumItem.getFileType() == CMD.FILE_TYPE_NORMAL) {
            OrderCommunication.getInstance().SP_DEL_FILE("type:" + albumItem.getFileType() + ";path:" + albumItem.getRelativePath() + ";");
            if (albumItem.getRawAlbumItem() != null) {
                OrderCommunication.getInstance().SP_DEL_FILE("type:" + albumItem.getRawAlbumItem().getFileType()
                        + ";path:" + albumItem.getRawAlbumItem().getRelativePath() + ";");
            }
        } else {
            if (albumItem.isCombine()) {
                OrderCommunication.getInstance().SP_DEL_CLASS("type:" + albumItem.getFileType() + ";class:" + albumItem.getClassIndex() + ";");
            } else {
                OrderCommunication.getInstance().SP_DEL_FILE("type:" + albumItem.getFileType() + ";path:" + albumItem.getRelativePath() + ";");
                if (albumItem.getRawAlbumItem() != null) {
                    OrderCommunication.getInstance().SP_DEL_FILE("type:" + albumItem.getRawAlbumItem().getFileType()
                            + ";path:" + albumItem.getRawAlbumItem().getRelativePath() + ";");
                }
                deletedCombine = true;
            }
        }

        if (albumItem.getFileType() == CMD.FILE_TYPE_NORMAL || !deletedCombine) {
            yuntaifileInfoList.remove(albumItem);
            if (yuntaiNormalInfoList.contains(albumItem)) {
                yuntaiNormalInfoList.remove(albumItem);
            } else if (yuntaiLapseInfoList.contains(albumItem)) {
                yuntaiLapseInfoList.remove(albumItem);
            } else if (yuntaiFocusInfoList.contains(albumItem)) {
                yuntaiFocusInfoList.remove(albumItem);
            } else if (yuntaiPanInfoList.contains(albumItem)) {
                yuntaiPanInfoList.remove(albumItem);
            } else if (yuntaiSunInfoList.contains(albumItem)) {
                yuntaiSunInfoList.remove(albumItem);
            } else if (yuntaiHDRInfoList.contains(albumItem)) {
                yuntaiHDRInfoList.remove(albumItem);
            } else if (yuntaiStarSkyInfoList.contains(albumItem)) {
                yuntaiStarSkyInfoList.remove(albumItem);
            }

            if (yuntaiTempNormalInfoList.contains(albumItem)) {
                yuntaiTempNormalInfoList.remove(albumItem);
            } else if (yuntaiTempLapseInfoList.contains(albumItem)) {
                yuntaiTempLapseInfoList.remove(albumItem);
            } else if (yuntaiTempFocusInfoList.contains(albumItem)) {
                yuntaiTempFocusInfoList.remove(albumItem);
            } else if (yuntaiTempPanInfoList.contains(albumItem)) {
                yuntaiTempPanInfoList.remove(albumItem);
            } else if (yuntaiTempSunInfoList.contains(albumItem)) {
                yuntaiTempSunInfoList.remove(albumItem);
            } else if (yuntaiTempHDRInfoList.contains(albumItem)) {
                yuntaiTempHDRInfoList.remove(albumItem);
            } else if (yuntaiTempStarSkyInfoList.contains(albumItem)) {
                yuntaiTempStarSkyInfoList.remove(albumItem);
            }
        }else {
            ArrayList<AlbumItem> arrayList = getListFromType(albumItem.getFileType(), false);
            removeCombineItem(arrayList,albumItem);
        }



        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_DELETE_SINGLE_YUNTAI_FILE_COMPLETE));

        if (waitDownloadQueue.contains(albumItem)) {
            waitDownloadQueue.remove(albumItem);
            downloadFileCount--;
            changeDowloadSeekBarUI(false);
        }
        if (albumItem.getFilePath() != null && (DOWNLOAD_STATE_DOWNLOADING.equals(albumItem.getDownloadStatus())
                || DOWNLOAD_STATE_ERROR.equals(albumItem.getDownloadStatus()) || DOWNLOAD_STATE_NO_MEMORY.equals(albumItem.getDownloadStatus()))) {
            File file = new File(albumItem.getFilePath());
            if (file != null && file.exists()) {
                if (file.length() < albumItem.getContentLength())
                    file.delete();
            }
        }
        return true;
    }

    //删除二级目录下单个文件
    public void removeCombineItem(ArrayList<AlbumItem> arrayList, AlbumItem albumItem){
        AlbumItem temp = null;
        int index = -1;
        boolean isFind = false;
        for (int i = 0; i < arrayList.size(); i++){
            temp = arrayList.get(i);
            if (temp.getClassIndex() == albumItem.getClassIndex()){
                isFind = true;
                index = i;
                break;
            }
        }
        if (isFind) {
            boolean isRemoveAll = temp.removeCombineItem(albumItem);
            if (isRemoveAll) {
                arrayList.remove(index);
            }
        }
    }

    //只能在子线程里面卡在
    public ArrayList<AlbumItem> delectYuntaiFiles(ArrayList<AlbumItem> delectList) {

        for (AlbumItem albumItem : delectList) {
            delectYuntaiFile(albumItem);
        }
        return delectList;
    }

    private synchronized void yuntaiFileLoadComplete() {
        Log.d(TAG, "yuntaiFileLoadComplete: ");
        if (mHandler != null) {
            mHandler.removeMessages(GET_FILE_TIME_OUT);
        }

        resetGetYuntaiFileFlag();
        sortList(yuntaifileInfoList);
        sortList(yuntaiNormalInfoList);
        sortList(yuntaiLapseInfoList);
        sortList(yuntaiFocusInfoList);
        sortList(yuntaiPanInfoList);
        sortList(yuntaiSunInfoList);
        sortList(yuntaiHDRInfoList);
        sortList(yuntaiStarSkyInfoList);
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_YUNTAI_FILE_LOAD_COMPLETE));

    }

    public void resetListSelectState() {
        Log.d(TAG, "resetListSelectState: ");
        new Thread(new Runnable() {
            ArrayList<AlbumItem> localfileInfolocationList_tpm;

            @Override
            public void run() {
                clearSelect(localfileInfolocationList);
                clearSelect(localNormalInfoList);
                clearSelect(localFocusInfoList);
                clearSelect(localLapseInfoList);
                clearSelect(localPanInfoList);
                clearSelect(localSunInfoList);
                clearSelect(localHDRInfoList);
                clearSelect(localStarSkyInfoList);

                clearSelect(yuntaifileInfoList);
                clearSelect(yuntaiNormalInfoList);
                clearSelect(yuntaiFocusInfoList);
                clearSelect(yuntaiLapseInfoList);
                clearSelect(yuntaiPanInfoList);
                clearSelect(yuntaiSunInfoList);
                clearSelect(yuntaiHDRInfoList);
                clearSelect(yuntaiStarSkyInfoList);

            }

            private void clearSelect(ArrayList<AlbumItem> list) {
                if (list == null) {
                    return;
                }
                if (localfileInfolocationList_tpm == null) {
                    localfileInfolocationList_tpm = new ArrayList<>();
                } else {
                    localfileInfolocationList_tpm.clear();
                }

                localfileInfolocationList_tpm.addAll(list);

                for (AlbumItem albumItem : localfileInfolocationList_tpm) {
                    if (albumItem != null)
                        albumItem.setSelect(false);
                }
            }
        }).start();
    }

    public void getLoacalFileInfo(Context context) {
        Log.d(TAG, "getLoacalFileInfo: ");
        if (localfileInfolocationList == null)
            localfileInfolocationList = new ArrayList<>();

        if (localfileInfolocationList.size() > 0) {
            sortList(localfileInfolocationList);
            formatLocalFiles();
            EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_LOACAL_FILE_LOAD_COMPLETE));
            return;
        }

        if (loadLoacalDataAsyncTask != null) {
            loadLoacalDataAsyncTask.cancel(true);
            loadLoacalDataAsyncTask = null;
        }
        loadLoacalDataAsyncTask = new LoadLoacalDataAsyncTask(context);
        loadLoacalDataAsyncTask.execute();
        Log.d(TAG, "getLoacalFileInfo: end");
    }

    private void formatLocalFiles() {
        if (localfileInfolocationList == null) {
            return;
        }

        if (localNormalInfoList == null) {
            localNormalInfoList = new ArrayList<>();
        } else {
            localNormalInfoList.clear();
        }

        if (localLapseInfoList == null) {
            localLapseInfoList = new ArrayList<>();
        } else {
            localLapseInfoList.clear();
        }

        if (localFocusInfoList == null) {
            localFocusInfoList = new ArrayList<>();
        } else {
            localFocusInfoList.clear();
        }

        if (localPanInfoList == null) {
            localPanInfoList = new ArrayList<>();
        } else {
            localPanInfoList.clear();
        }

        if (localSunInfoList == null) {
            localSunInfoList = new ArrayList<>();
        } else {
            localSunInfoList.clear();
        }

        if (localHDRInfoList == null) {
            localHDRInfoList = new ArrayList<>();
        } else {
            localHDRInfoList.clear();
        }

        if (localStarSkyInfoList == null) {
            localStarSkyInfoList = new ArrayList<>();
        } else {
            localStarSkyInfoList.clear();
        }

        for (int i = 0; i < localfileInfolocationList.size(); i++) {
            AlbumItem albumItem = localfileInfolocationList.get(i);
            addLocalFile(albumItem);
        }

    }

    private void addLocalFile(AlbumItem albumItem) {
        if (albumItem.getFilePath().contains(CMD.NORMAL)) {
            localNormalInfoList.add(albumItem);
        } else if (albumItem.getFilePath().contains(CMD.LAPSE)) {
            localLapseInfoList.add(albumItem);
        } else if (albumItem.getFilePath().contains(CMD.FOCUS_STACK)) {
            localFocusInfoList.add(albumItem);
        } else if (albumItem.getFilePath().contains(CMD.PANORAMA)) {
            localPanInfoList.add(albumItem);
        } else if (albumItem.getFilePath().contains(CMD.SUN)) {
            localSunInfoList.add(albumItem);
        } else if (albumItem.getFilePath().contains(CMD.HDR)) {
            localHDRInfoList.add(albumItem);
        } else if (albumItem.getFilePath().contains(CMD.STAR_SKY_STACK)) {
            localStarSkyInfoList.add(albumItem);
        }
    }

    public ArrayList<AlbumItem> sortList(ArrayList<AlbumItem> albumItems) {

        try {
            FileComparator fileComparator = new FileComparator();
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            Collections.sort(albumItems, fileComparator);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return albumItems;
    }

    public boolean deleteLocalFile(AlbumItem albumItem) {
        if (albumItem == null || albumItem.getFilePath() == null)
            return false;
        Log.d(TAG,"albumItem:"+albumItem.toString());
        File file = new File(albumItem.getFilePath());
        if (file == null || !file.exists()) {
            deleteFormatList(albumItem);
            return true;
        }
        boolean deleteResult = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                deleteResult = deletePicture(albumItem.getFilePath());
            } else {
                deleteResult = file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.e(TAG, "deleteLocalFile: "+deleteResult );
        if (deleteResult) {
            deleteFormatList(albumItem);
            return true;
        } else {
            ToastUtils.showShort("删除失败");
            return false;
        }
    }

    private boolean deletePicture(String filePath){
        ContentResolver resolver = Utils.getApp().getApplicationContext().getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=?",
                new String[]{filePath}, null);
        int deleteNum = 0;
        if (null != cursor && cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);

            if(uri!=null){
                deleteNum = resolver.delete(uri, null, null);
            }
        }

        return deleteNum > 0 ? true : false;
    }

    private void deleteFormatList(AlbumItem albumItem) {
        localfileInfolocationList.remove(albumItem);
        if (localNormalInfoList.contains(albumItem)) {
            localNormalInfoList.remove(albumItem);
        } else if (localLapseInfoList.contains(albumItem)) {
            localLapseInfoList.remove(albumItem);
        } else if (localFocusInfoList.contains(albumItem)) {
            localFocusInfoList.remove(albumItem);
        } else if (localPanInfoList.contains(albumItem)) {
            localPanInfoList.remove(albumItem);
        } else if (localSunInfoList.contains(albumItem)) {
            localSunInfoList.remove(albumItem);
        } else if (localHDRInfoList.contains(albumItem)) {
            localHDRInfoList.remove(albumItem);
        } else if (localStarSkyInfoList.contains(albumItem)) {
            localStarSkyInfoList.remove(albumItem);
        }
        AlbumItem yuntaiItem;
        if (yuntaifileInfoList.contains(albumItem)) {
            yuntaiItem = yuntaifileInfoList.get(yuntaifileInfoList.indexOf(albumItem));
            yuntaiItem.setExistLocal(false);
            yuntaiItem.setDownloadStatus(null);
            yuntaiItem.setDownloadLength(0);
        }
        yuntaiItem = null;
        if (yuntaiNormalInfoList.contains(albumItem)) {
            yuntaiItem = yuntaiNormalInfoList.get(yuntaiNormalInfoList.indexOf(albumItem));
        } else if (yuntaiLapseInfoList.contains(albumItem)) {
            yuntaiItem = yuntaiLapseInfoList.get(yuntaiLapseInfoList.indexOf(albumItem));
        } else if (yuntaiFocusInfoList.contains(albumItem)) {
            yuntaiItem = yuntaiFocusInfoList.get(yuntaiFocusInfoList.indexOf(albumItem));
        } else if (yuntaiPanInfoList.contains(albumItem)) {
            yuntaiItem = yuntaiPanInfoList.get(yuntaiPanInfoList.indexOf(albumItem));
        } else if (yuntaiSunInfoList.contains(albumItem)) {
            yuntaiItem = yuntaiSunInfoList.get(yuntaiSunInfoList.indexOf(albumItem));
        } else if (yuntaiHDRInfoList.contains(albumItem)) {
            yuntaiItem = yuntaiHDRInfoList.get(yuntaiHDRInfoList.indexOf(albumItem));
        } else if (yuntaiStarSkyInfoList.contains(albumItem)) {
            yuntaiItem = yuntaiStarSkyInfoList.get(yuntaiStarSkyInfoList.indexOf(albumItem));
        }

        if (yuntaiItem != null) {
            yuntaiItem.setExistLocal(false);
            yuntaiItem.setDownloadStatus(null);
            yuntaiItem.setDownloadLength(0);
        }
    }

    private void setAlbumItemSize(AlbumItem albumItem) {
        if (albumItem == null)
            return;
        int height = albumItem.getMediaHeight();
        int width = albumItem.getMediaWidth();
        try {
            if (albumItem.isVideo()) {
                if (height == 0 || width == 0) {
                    MediaMetadataRetriever retr = new MediaMetadataRetriever();
                    retr.setDataSource(albumItem.getFilePath());
                    try {
                        width = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                        height = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else {
                if (height == 0 || width == 0) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(albumItem.getFilePath(), options);
                    height = options.outHeight;
                    width = options.outWidth;
                }
            }

            albumItem.setMediaHeight(height);
            albumItem.setMediaWidth(width);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    private void getAllList(Context context) {
        Uri uriVideo = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri uriPicture = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projectionVideo = new String[]{
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION};

        String[] projectionPicture = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT};
        String orderVideo = MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC," + MediaStore.Video.VideoColumns._ID + " DESC";
        String orderPicture = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC," + MediaStore.Images.ImageColumns._ID + " DESC";

        Cursor cursorVideo = null;
        Cursor cursorPicture = null;

        try {
            cursorVideo = context.getContentResolver().query(uriVideo, projectionVideo, null, null, orderVideo);
            cursorPicture = context.getContentResolver().query(uriPicture, projectionPicture, null, null, orderPicture);

            if (cursorVideo != null) {
                while (cursorVideo.moveToNext()) {
                    @SuppressLint("Range") String filepath = cursorVideo.getString(cursorVideo.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                    Log.d(TAG, "getAllList: video filepath = " + filepath);
                    if (filepath == null || !filepath.contains(FILE_DEFAULT_FOLDER)) {
                        continue;
                    }


                    File file = new File(filepath);
                    if (file == null || !file.exists()) {
                        continue;
                    }
                    long contenLength = file.length();

                    if (contenLength == 0)
                        continue;
                    String fileName = filepath.substring(filepath.lastIndexOf("/") + 1);
                    Log.d(TAG, "getAllList: fileName = " + fileName + " , filepath = " + filepath);
                    AlbumItem albumItem = new AlbumItem();

                    long formatterTime = file.lastModified();
                    int videotime = cursorVideo.getColumnIndex(MediaStore.Video.Media.DURATION);
                    String duration = cursorVideo.getString(videotime);

                    albumItem.setFilePath(filepath);

                    albumItem.setMediaFileModel(getMediaFileModle(fileName));
                    albumItem.setVideo(true);
                    albumItem.setDownloadLength(contenLength);
                    albumItem.setContentLength(contenLength);
                    albumItem.setLastModifiedTime(formatterTime);
                    albumItem.setVideoDuration(duration);
                    albumItem.setMediaFormat(fileName.substring(fileName.lastIndexOf(".") + 1));
                    albumItem.setFileType(getLocalTypeFromPath(filepath));

                    int widthID = cursorVideo.getColumnIndex(MediaStore.Video.Media.WIDTH);
                    int heightID = cursorVideo.getColumnIndex(MediaStore.Video.Media.HEIGHT);
                    int height = 0;
                    int width = 0;
                    try {
                        String heightS = cursorVideo.getString(heightID);
                        String widthS = cursorVideo.getString(widthID);
                        if (heightS == null || widthS == null) {
                            MediaMetadataRetriever retr = new MediaMetadataRetriever();
                            retr.setDataSource(filepath);
                            height = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                            width = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                        } else {
                            height = Integer.parseInt(heightS);
                            width = Integer.parseInt(widthS);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    albumItem.setMediaWidth(width);
                    albumItem.setMediaHeight(height);

                    albumItem.setExistLocal(true);

                    int index = localfileInfolocationList.indexOf(albumItem);
                    if (index < 0) {
                        localfileInfolocationList.add(albumItem);
                    } else {
                        localfileInfolocationList.set(index, albumItem);
                    }
                }
            }

            if (cursorPicture != null) {
                while (cursorPicture.moveToNext()) {
                    String filepath;
                    filepath = cursorPicture.getString(cursorPicture.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                    Log.d(TAG, "getAllList: picture filepath = " + filepath);
                    if (filepath == null || !filepath.contains(FILE_DEFAULT_FOLDER)) {
                        continue;
                    }


                    File file = new File(filepath);
                    if (file == null || !file.exists()) {
                        continue;
                    }
                    long contenLength = file.length();

                    if (contenLength == 0)
                        continue;
                    String fileName = filepath.substring(filepath.lastIndexOf("/") + 1);

                    AlbumItem albumItem = new AlbumItem();

                    long formatterTime = file.lastModified();

                    albumItem.setMediaFileModel(getMediaFileModle(fileName));
                    albumItem.setFilePath(filepath);
                    albumItem.setVideo(false);
                    albumItem.setDownloadLength(contenLength);
                    albumItem.setContentLength(contenLength);
                    albumItem.setLastModifiedTime(formatterTime);
                    albumItem.setVideoDuration("0");
                    albumItem.setMediaFormat(fileName.substring(fileName.lastIndexOf(".") + 1));
                    albumItem.setFileType(getLocalTypeFromPath(filepath));

                    int widthID = cursorPicture.getColumnIndex(MediaStore.Images.Media.WIDTH);
                    int heightID = cursorPicture.getColumnIndex(MediaStore.Images.Media.HEIGHT);


                    int width = 0;
                    int height = 0;

                    String widthS = cursorPicture.getString(widthID);
                    String heightS = cursorPicture.getString(heightID);

                    if (widthS == null || heightS == null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(albumItem.getFilePath(), options);
                        height = options.outHeight;
                        width = options.outWidth;
                    } else {
                        try {
                            height = Integer.parseInt(heightS);
                            width = Integer.parseInt(widthS);
                        } catch (Exception e) {

                        }
                    }

                    albumItem.setMediaWidth(width);
                    albumItem.setMediaHeight(height);

                    if (filepath.endsWith(".dng") || filepath.endsWith(".DNG")) {
                        albumItem.setDNG(true);
                    } else {
                        albumItem.setDNG(false);
                    }
                    albumItem.setExistLocal(true);

                    int index = localfileInfolocationList.indexOf(albumItem);
                    if (index < 0) {
                        localfileInfolocationList.add(albumItem);
                    } else {
                        localfileInfolocationList.set(index, albumItem);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "getSavePath: Exception trying to find latest media");
            e.printStackTrace();
        } finally {
            if (cursorVideo != null) {
                cursorVideo.close();
            }
            if (cursorPicture != null) {
                cursorPicture.close();
            }
        }
    }

    private int getLocalTypeFromPath(String filePath){

        if (filePath.toLowerCase().contains("lapse")){
            return CMD.FILE_TYPE_LAPSE;
        } else if (filePath.toLowerCase().contains("focusstack")){
            return CMD.FILE_TYPE_FOCUS;
        } else if (filePath.toLowerCase().contains("panorama")){
            return CMD.FILE_TYPE_PAN;
        } else if (filePath.toLowerCase().contains("sun")){
            return CMD.FILE_TYPE_SUN;
        } else if (filePath.toLowerCase().contains("hdr")){
            return CMD.FILE_TYPE_HDR;
        } else if (filePath.toLowerCase().contains("starskystack")){
            return CMD.FILE_TYPE_STAR_SKY_STACK;
        }

        return CMD.FILE_TYPE_NORMAL;
    }

    public boolean inWaitQueus(AlbumItem albumItem) {
        if (albumItem == null)
            return false;
        if (waitDownloadQueue.contains(albumItem))
            return true;
        return false;
    }

    public boolean inDownloadQueue(AlbumItem albumItem) {
        if (albumItem == null)
            return false;
        if (downloadQueue.contains(albumItem))
            return true;
        return false;
    }

    public boolean downloadDone() {
        if (waitDownloadQueue.size() == 0 && downCalls.size() == 0)
            return true;
        else
            return false;
    }

    //下载等待下载中的第一条
    public void downNext() {
        if (waitDownloadQueue.size() > 0 && downloadQueue.size() < maxDowloadCount) {
            download(waitDownloadQueue.get(0));
        }
    }

    //取消全部任务
    public void cancelAllDownloadTask() {
        Log.d(TAG, "canCleAllDownloadTask: ");
        downloadCompleteFileCount = 0;
        downloadFileCount = 0;
        changeDowloadSeekBarUI(false);
        if (downCalls != null && downCalls.size() > 0) {
            for (Map.Entry<String, Call> entry : downCalls.entrySet()) {
                Call call = entry.getValue();
                if (call != null) {
                    call.cancel();//取消
                }
            }
        }
        if (waitDownloadQueue != null) {
            for (AlbumItem albumItem : waitDownloadQueue) {
                albumItem.setDownloadStatus(null);
            }
            waitDownloadQueue.clear();
        }

        if (downloadQueue != null) {
            for (AlbumItem albumItem : downloadQueue) {
                albumItem.setDownloadStatus(null);
            }
            downloadQueue.clear();
        }
    }

    public void cancelDownloadTask(AlbumItem albumItem) {
        if (downCalls == null) {
            return;
        }
        for (Map.Entry<String, Call> entry : downCalls.entrySet()) {
            if (albumItem.getUrl().equals(entry.getKey())) {
                Call call = entry.getValue();
                if (call != null) {
                    call.cancel();//取消
                }
                break;
            }
        }

        if (waitDownloadQueue != null) {
            int index = -1;
            for (int i = 0; i < waitDownloadQueue.size(); i++) {
                if (albumItem.getUrl().equals(waitDownloadQueue.get(i).getUrl())) {
                    index = i;
                    waitDownloadQueue.get(i).setDownloadStatus(null);
                    break;
                }
            }
            if (index != -1) {
                waitDownloadQueue.remove(index);
            }
        }

        if (downloadQueue != null) {
            int index = -1;
            for (int i = 0; i < downloadQueue.size(); i++) {
                if (albumItem.getUrl().equals(downloadQueue.get(i).getUrl())) {
                    index = i;
                    downloadQueue.get(i).setDownloadStatus(null);
                    break;
                }
            }
            if (index != -1) {
                downloadQueue.remove(index);
            }
        }

    }

    public void changeDowloadSeekBarUI(boolean init) {
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_YUNTAI_DOWNLOAD_UI_CHANGE, downloadFileCount, downloadCompleteFileCount, init));
    }

    public int getDownloadFileCount() {
        return downloadFileCount;
    }

    public int getDownloadCompleteFileCount() {
        return downloadCompleteFileCount;
    }

    public boolean isNeedPostAlbumItem() {
        return isNeedPostAlbumItem;
    }

    public void setNeedPostAlbumItem(boolean needPostAlbumItem) {
        isNeedPostAlbumItem = needPostAlbumItem;
    }

    public synchronized void download(AlbumItem albumItem) {
        Log.d(TAG, " download: waitDownloadQueue.size() =" + waitDownloadQueue.size());

        if (albumItem == null) {
            downNext();
            return;
        }

        String url = albumItem.getUrl();
        String filePath = albumItem.getFilePath();

        if (url == null || filePath == null) {
            downNext();
            return;
        }


        File file = new File(albumItem.getFilePath());
        if (file != null && file.exists() && file.length() >= albumItem.getContentLength()) {
            albumItem.setExistLocal(true);
            if (!localfileInfolocationList.contains(albumItem)) {
                setAlbumItemSize(albumItem);
                localfileInfolocationList.add(albumItem);
                addLocalFile(albumItem);
            }
            downNext();
            return;
        }

        if (inDownloadQueue(albumItem)) {
            downNext();
            return;
        }

        if (!inWaitQueus(albumItem)) {
            downloadFileCount++;
            waitDownloadQueue.add(albumItem);
        }


        if (downloadQueue.size() >= maxDowloadCount) {
            albumItem.setDownloadStatus(DOWNLOAD_STATE_WAIT);

            if (isNeedPostAlbumItem())
                EventBus.getDefault().post(albumItem);
            return;
        }


        downloadQueue.add(albumItem);
        waitDownloadQueue.remove(albumItem);

//        Log.d(TAG, "MediaDownload startOneDownload: isMainThread:"+ ThreadUtils.isMainThread());
        boolean needDownload = checkDownload(albumItem);
        if(needDownload){
            Observable.create(new DownloadSubscribe(albumItem))
                    .subscribeOn(Schedulers.io()) //控制上游发射者，即被观察者执行线程
                    .observeOn(AndroidSchedulers.mainThread()) //指定了下游观察者执行线程
                    .subscribe(new DownloadObserver(albumItem)); //添加观察者，监听下载进度
        }
    }

    private boolean checkDownload(AlbumItem albumItem){
        long downloadLength = 0;
        long contentLength = albumItem.getContentLength();

        String filePath = albumItem.getFilePath();
        File file = new File(filePath);

        if (file.exists())
            downloadLength = file.length();
        else {
            try {
                createFile(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (downloadLength >= contentLength) {//文件已经下载完成
            downloadCompleteFileCount++;
            changeDowloadSeekBarUI(false);
            albumItem.setDownloadStatus(DOWNLOAD_STATE_OVER);
            updateDownloadingUI(albumItem);
            updateDownloadCompleteUI(albumItem);
            downloadQueue.remove(albumItem);
            if (!localfileInfolocationList.contains(albumItem)) {
                albumItem.setExistLocal(true);
                setAlbumItemSize(albumItem);
                localfileInfolocationList.add(albumItem);
                sortList(localfileInfolocationList);
                formatLocalFiles();
            }
            downNext();
            return false;
        }
        if (getSDAvailableSize() - (contentLength - downloadLength) < 1 * 1024 * 1024 * 1024) {
            albumItem.setDownloadStatus(DOWNLOAD_STATE_NO_MEMORY);
            updateDownloadingUI(albumItem);
            updateDownloadCompleteUI(albumItem);
            downloadQueue.remove(albumItem);

            downNext();
            downloadCompleteFileCount++;
            changeDowloadSeekBarUI(false);

            EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_PHONE_IS_OUT_OF_MEMORY));
            return false;
        }

        albumItem.setDownloadLength(downloadLength);
        albumItem.setDownloadStatus(DOWNLOAD_STATE_DOWNLOADING);
        updateDownloadingUI(albumItem);
        return true;
    }

    private long downLoadUpdateTime;
    private void updateDownloadingUI(AlbumItem downloadFileModel){
        if (System.currentTimeMillis() - downLoadUpdateTime > 50) {
            downLoadUpdateTime = System.currentTimeMillis();
            EventBus.getDefault().post(downloadFileModel);
        } else if (downloadFileModel != null && downloadFileModel.getDownloadLength() >= downloadFileModel.getContentLength()) {
            EventBus.getDefault().post(downloadFileModel);
        }
    }

    private void updateDownloadCompleteUI(AlbumItem downloadFileModel){
        if (downloadFileModel != null) {
            if (isNeedPostAlbumItem()) {
                EventBus.getDefault().post(downloadFileModel);
            }
            EventBus.getDefault().post(new BroadcastActionEvent(
                    MyMessage.ACTION_DOWNLOAD_COMPLETED,
                    downloadQueue.size(),
                    waitDownloadQueue.size()));
        }
    }

    private void createDir(String dirPath) throws Exception {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                file.mkdir();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.mkdir();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void createFile(String path) throws Exception {
        try {
            File file = new File(path);
            if (file.getParentFile().exists()) {
                file.createNewFile();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public synchronized void MediaScannerUpdate(String filepath, boolean needAddLocalMedia, boolean isVideo) {
        if (filepath == null)
            return;

        Log.e(TAG, "MediaScannerUpdate: needAddLocalMedia =" + needAddLocalMedia + ",filepath =" + filepath + ",isVideo =" + isVideo);
        EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_YUNTAI_DOWNLOAD_MEDIA_UPDATE, filepath));

        if (needAddLocalMedia) {
            File file = new File(filepath);
            if (file == null || !file.exists()) {
                return;
            }
            long contenLength = file.length();

            String fileName = filepath.substring(filepath.lastIndexOf("/") + 1);
            AlbumItem albumItem = new AlbumItem();

            long formatterTime = file.lastModified();


            albumItem.setFilePath(filepath);

            albumItem.setMediaFileModel(getMediaFileModle(fileName));
            albumItem.setVideo(isVideo);
            albumItem.setDownloadLength(contenLength);
            albumItem.setContentLength(contenLength);
            albumItem.setLastModifiedTime(formatterTime);
            albumItem.setMediaFormat(fileName.substring(fileName.lastIndexOf(".") + 1));
            albumItem.setFileType(getLocalTypeFromPath(filepath));

            int height = 0;
            int width = 0;
            String duration = "0";
            if (isVideo) {
                try {
                    MediaMetadataRetriever retr = new MediaMetadataRetriever();
                    retr.setDataSource(albumItem.getFilePath());
                    albumItem.setMediaHeight(Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)));
                    albumItem.setMediaWidth(Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)));
                    duration = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                } catch (Exception e) {

                }

            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(albumItem.getFilePath(), options);
                height = options.outHeight;
                width = options.outWidth;
                if (filepath.endsWith(".dng") || filepath.endsWith(".DNG")) {
                    albumItem.setDNG(true);
                } else {
                    albumItem.setDNG(false);
                }
            }

            albumItem.setVideoDuration(duration);
            albumItem.setMediaWidth(width);
            albumItem.setMediaHeight(height);

            albumItem.setExistLocal(true);
            Log.d(TAG, "MediaScannerUpdate: " + albumItem.toString());
            Log.d(TAG, "MediaScannerUpdate: " + albumItem.getFilePath());
            if (!localfileInfolocationList.contains(albumItem)) {
                localfileInfolocationList.add(0, albumItem);
                addLocalFile(albumItem);
                Log.d(TAG, "MediaScannerUpdate: --------------");
            }
        }

    }

    private long getSDAvailableSize() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File sdcard_filedir = new File(path);
        long availablesize = sdcard_filedir.getUsableSpace();//获取文件目录对象剩余空间

        Log.e(TAG, "getSDAvailableSize: availablesize ="+availablesize );
        return availablesize;
    }

    static class FileComparator implements Comparator<AlbumItem> {
        @Override
        public int compare(AlbumItem file1, AlbumItem file2) {
            return file2.getLastModifiedTime() > file1.getLastModifiedTime() ? 1 : (file2.getLastModifiedTime() == file1.getLastModifiedTime() ? 0 : -1);
        }
    }

    private class LoadLoacalDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context context;

        public LoadLoacalDataAsyncTask(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (context != null)
                getAllList(context);
            sortList(localfileInfolocationList);
            formatLocalFiles();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_LOACAL_FILE_LOAD_COMPLETE));
        }
    }

    private class DownloadSubscribe implements ObservableOnSubscribe<AlbumItem> {
        private AlbumItem albumItem;

        public DownloadSubscribe(AlbumItem albumItem) {
            this.albumItem = albumItem;
        }

        @Override
        public void subscribe(ObservableEmitter<AlbumItem> e) throws Exception {

            String url = albumItem.getUrl();

            long downloadLength = 0;
            long contentLength = albumItem.getContentLength();

            String filePath = albumItem.getFilePath();
            File file = new File(filePath);

            if (file.exists())
                downloadLength = file.length();
            else {
                createFile(filePath);
            }

            Request request = new Request.Builder()
                    //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
                    .addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength)
                    .url(url)
                    .build();
            Call call = mDowloadClient.newCall(request);
            Log.d(TAG, "--------------- subscribe: " + albumItem.getContentLength());
            Log.d(TAG, "download downCalls put: url = " + url);
            downCalls.put(url, call);//把这个添加到call里,方便取消
            Response response = call.execute();
            InputStream is = null;
            FileOutputStream fileOutputStream = null;
            try {
//                //@todo test download failed
//                if(albumItem.getUrl().equals("http://192.168.0.1/sd/panorama/class_35/SP_0004.jpg"))
//                    throw new Exception("test download failed");

                is = response.body().byteStream();

                fileOutputStream = new FileOutputStream(file, true);
                byte[] buffer = new byte[2048 * 8];//缓冲数组2kB
                int len;
                while ((len = is.read(buffer)) != -1) {
                    // TODO: 2021/8/28
//                    Thread.sleep(20);
                    fileOutputStream.write(buffer, 0, len);
                    downloadLength += len;
                    albumItem.setDownloadLength(downloadLength);
                    e.onNext(albumItem);
                }
                fileOutputStream.flush();

                file.setLastModified(albumItem.getLastModifiedTime());

                MediaScannerUpdate(albumItem.getFilePath(), false, albumItem.isVideo());

                if (!localfileInfolocationList.contains(albumItem)) {
                    albumItem.setExistLocal(true);
                    setAlbumItemSize(albumItem);
                    localfileInfolocationList.add(0, albumItem);
                    sortList(localfileInfolocationList);
                    formatLocalFiles();
                }

            } finally {
                closeAll(is, fileOutputStream);//关闭IO流
            }

            downloadCompleteFileCount++;
            downCalls.remove(url);
            downloadQueue.remove(albumItem);
            albumItem.setDownloadStatus(DOWNLOAD_STATE_OVER);
            if (downloadDone()) {
                downloadFileCount = 0;
                downloadCompleteFileCount = 0;
            }
            changeDowloadSeekBarUI(false);
            downNext();
            e.onComplete();//完成
        }
    }

    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    public static byte[] getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置连接超时为5秒
        conn.setConnectTimeout(5000);
        // 设置请求类型为Get类型
        conn.setRequestMethod("GET");
        // 判断请求Url是否成功
        Log.d(TAG, "getImage: path = " + path);
        Log.d(TAG, "getImage: conn.getResponseCode() = " + conn.getResponseCode());
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("请求url失败");
        }
        InputStream inStream = conn.getInputStream();
        byte[] bt = read(inStream);
        inStream.close();
        return bt;
    }

    public void addAlbumItemToLocal(AlbumItem albumItem){
        if (albumItem == null) {
            return;
        }
        int type = albumItem.getFileType();
        ArrayList<AlbumItem> arrayList = null;
        if (type == CMD.FILE_TYPE_NORMAL) {
            arrayList = getLocalNormalInfoList();
        } else if (type == CMD.FILE_TYPE_LAPSE) {
            arrayList = getLocalLapseInfoList();
        } else if (type == CMD.FILE_TYPE_FOCUS) {
            arrayList = getLocalFocusInfoList();
        } else if (type == CMD.FILE_TYPE_PAN) {
            arrayList = getLocalPanInfoList();
        } else if (type == CMD.FILE_TYPE_SUN) {
            arrayList = getLocalSunInfoList();
        } else if (type == CMD.FILE_TYPE_HDR) {
            arrayList = getLocalHDRInfoList();
        } else if (type == CMD.FILE_TYPE_STAR_SKY_STACK) {
            arrayList = getLocalStarSkyInfoList();
        }

        if (arrayList != null) {
            arrayList.add(albumItem);
            sortList(arrayList);
        }

        if (localfileInfolocationList == null)
            localfileInfolocationList = new ArrayList<>();
        arrayList = localfileInfolocationList;

        arrayList.add(albumItem);
        sortList(arrayList);

    }

    private class DownloadObserver implements Observer<AlbumItem> {

        public Disposable d;//可以用于取消注册的监听者
        public AlbumItem downloadFileModel;

        public DownloadObserver(AlbumItem downloadFileModel){
            this.downloadFileModel = downloadFileModel;
        }

        @Override
        public void onSubscribe(Disposable d) {
            this.d = d;
        }


        @Override
        public void onNext(AlbumItem value) {
            this.downloadFileModel = value;
//            Log.e(TAG, "MediaDownload onNext: set downloadFileModel:"+((downloadFileModel == null)?"null":("url:"+downloadFileModel.getUrl())));

            updateDownloadingUI(downloadFileModel);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "MediaDownload onError: downloadFileModel =" + downloadFileModel + ",\n e =" + e);
            if (downloadFileModel == null)
                return;

            downloadFileModel.setDownloadStatus(DOWNLOAD_STATE_ERROR);

            if (isNeedPostAlbumItem())
                EventBus.getDefault().post(downloadFileModel);

            String url = downloadFileModel.getUrl();
            Call call = downCalls.get(url);

            if (call != null) {
                call.cancel();//取消
            }
            downloadCompleteFileCount++;
            changeDowloadSeekBarUI(false);
            Log.d(TAG, "download onError: url = " + url);
            downCalls.remove(url);
            downloadQueue.remove(downloadFileModel);

            if (downloadDone()) {
                downloadFileCount = 0;
                downloadCompleteFileCount = 0;
                changeDowloadSeekBarUI(false);
            }

            downNext();
        }

        @Override
        public void onComplete() {
            Log.d(TAG, "MediaDownload download_onComplete: downloadFileModel = " + downloadFileModel);
            updateDownloadCompleteUI(downloadFileModel);
        }
    }

    /**
     * 使用HTTP请求的PUT方式上传图片。
     */
    public boolean uploadFlie(String mediaType, String filePath, String urlStr) {
        boolean isOk = false;
        File file = new File(filePath);
        if (!file.exists()) {
            return isOk;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            URL url = new URL(urlStr);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("PUT");
            urlConn.setRequestProperty("Content-Type", mediaType);
            urlConn.setConnectTimeout(10000); //设置连接超时时间。
            urlConn.setReadTimeout(1000000); //设置读取超时时间。

            OutputStream output = urlConn.getOutputStream();
            byte[] buffer = new byte[2048 * 8];//缓冲数组2kB
            int position;
            int currentPosition = 0;
            int totalLenght = fileInputStream.available();

            while ((position = fileInputStream.read(buffer)) != -1) {
                output.write(buffer, 0, position);
                output.flush();
                currentPosition += position;
            }
            output.flush();
            output.close();
            int responseCode = urlConn.getResponseCode();

            if (responseCode == 201) {
                //TODO 上传成功
                isOk = true;
            } else {

            }
            urlConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOk;
    }



    private static final int GET_FILE_TIME_OUT = 0;
    private static final int GET_COMBINE_FILE_TIME_OUT = 1;

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case GET_FILE_TIME_OUT:
                    if (msg.obj == null) {
                        break;
                    }
                    boolean isFirstGetFile = (boolean) msg.obj;
//                    getFileListItem(false,msg.arg1,msg.arg2);
                    getFileMenus(false, msg.arg1, msg.arg2);
                    break;
                case GET_COMBINE_FILE_TIME_OUT:
                    if (msg.obj == null) {
                        break;
                    }
//                    getFileListItem(false,msg.arg1,msg.arg2);
                    getFileCombine(false, msg.arg1, msg.arg2);
                    break;
                default:
                    break;
            }
        }
    }

    private class HolderFileInfo {
        int type;
        int classSize;
        int initIndex;//已获取长度的个数
        ArrayList<ClassItemInfo> classItemList;

        public HolderFileInfo(int type, int classSize) {
            this.type = type;
            this.classSize = classSize;
            classItemList = new ArrayList<>();
            initIndex = 0;
        }

        public void setClassSize(int size) {
            classSize = size;
            initIndex = 0;
            if (classItemList == null) {
                classItemList = new ArrayList<>();
            } else {
                classItemList.clear();
            }
        }

        @Override
        public String toString() {
            return "HolderFileInfo{" +
                    "type=" + type +
                    ", classSize=" + classSize +
                    ", initIndex=" + initIndex +
                    ", classItemList=" + classItemList +
                    '}';
        }
    }

    private class ClassItemInfo {
        boolean prepareInit;
        int itemCount;
        int initIndex;//已经获取的个数
        int addCount;
        int prepareAddCount;
        int classIndex;//class名称

        public ClassItemInfo(int count, int index) {
            itemCount = count;
            initIndex = 0;
            prepareInit = false;
            addCount = 0;
            prepareAddCount = 0;
            classIndex = index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClassItemInfo that = (ClassItemInfo) o;
            return classIndex == that.classIndex;
        }

        @Override
        public int hashCode() {
            return Objects.hash(classIndex);
        }

        @Override
        public String toString() {
            return "ClassItemInfo{" +
                    "itemCount=" + itemCount +
                    ", initIndex=" + initIndex +
                    ", prepareInit=" + prepareInit +
                    '}';
        }
    }

    public class DownLoadInfo {
        int downloadSize;
        int waitDownloadSize;

        public DownLoadInfo(int downloadSize, int waitDownloadSize) {
            this.downloadSize = downloadSize;
            this.waitDownloadSize = waitDownloadSize;
        }
    }

}
