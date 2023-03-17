package com.example.wifidemo1.singleton;

import com.example.wifidemo1.model.AlbumItem;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadHelper {
    private static final String TAG = "DownloadHelper";
    private static DownloadHelper downloadHelper;

    private ArrayList<AlbumItem> mDownloadItemList;
    private HashMap<String,AlbumItem> mDownloadHashMap;

    public static DownloadHelper getInstance() {
        if (downloadHelper == null) {
            synchronized (DownloadHelper.class) {
                if (downloadHelper == null) {
                    downloadHelper = new DownloadHelper();
                }
            }
        }
        return downloadHelper;
    }

    public DownloadHelper(){
        mDownloadItemList = new ArrayList<>();
    }

    public void addOnceDownload(ArrayList<AlbumItem> list){
        if (list == null) {
            return;
        } else if (mDownloadItemList == null) {
            mDownloadItemList = new ArrayList<>();
        }
        for (int i = 0; i < list.size(); i++){
            AlbumItem albumItem = list.get(i);
            if (mDownloadItemList.contains(albumItem)){
                continue;
            }
            mDownloadItemList.add(albumItem);
        }
        if (mDownloadHashMap == null) {
            mDownloadHashMap = new HashMap<>(mDownloadItemList.size());
        }
    }

    public void cancelDownload(){
        if (mDownloadItemList != null) {
            mDownloadItemList.clear();
        }
        if (mDownloadHashMap != null) {
            mDownloadHashMap.clear();
        }
    }

    public void skipItemDownload(AlbumItem albumItem){
        if (mDownloadItemList != null) {
            mDownloadItemList.remove(albumItem);
        }
    }

    public ArrayList<AlbumItem> getDownloadItemList(){
        return mDownloadItemList;
    }

    public boolean isShowDownloadLayout(){
        if (mDownloadItemList == null) {
            return false;
        } else {
            return mDownloadItemList.size() > 0;
        }
    }

    public void addHashMap(AlbumItem albumItem){
        if (albumItem == null) {
            return;
        }
        if (mDownloadHashMap == null) {
            mDownloadHashMap = new HashMap<>();
        }
        if (!mDownloadHashMap.containsKey(albumItem.getFilePath())){
            mDownloadHashMap.put(albumItem.getFilePath(),albumItem);
        }
    }

    public int getHashMapSize(){
        if (mDownloadHashMap != null) {
            return mDownloadHashMap.size();
        }
        return 0;
    }

    public boolean isAddDownloadItemList(){
        if (mDownloadItemList == null) {
            return false;
        }
        boolean supportDownload = false;
        for (int i = 0; i < mDownloadItemList.size(); i++){
            AlbumItem albumItem = mDownloadItemList.get(i);
            if (albumItem.isSupportDownload()) {
                supportDownload = true;
                break;
            }
        }
        return supportDownload;
    }

}
