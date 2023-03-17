package com.example.wifidemo1.model;

import android.util.Log;

import com.litesuits.orm.db.annotation.Table;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by aq on 2018/7/7.
 */

@Table("AlbumItem")
public class AlbumItem extends BaseModel {
    private static final String TAG = "AlbumItem_log";

    private String filePath;
    private String relativePath;
    private String url;
    private String thumbnailurl;
    private boolean isVideo;
    private boolean isSelect;
    private boolean isExistLocal;
    private boolean isExistYunTai;

    private boolean isDNG;
    private boolean isRaw;

    private String downloadStatus;
    private long contentLength;//文件总长度
    private long downloadLength;//已下载的长度

    private long lastModifiedTime;//文件修改时间

    private String videoDuration;

    private String mediaFormat;

    private int mediaHeight = 0;
    private int mediaWidth = 0;

    private int fileType;

    private boolean isCombine;

    private int classIndex;

    private int combineFileCount;
    private int displayFileCount;
    private int ispConfigCount;
    private int jpegCount;
    private int rawCount;

    private ArrayList<AlbumItem> combineItemList;//二级目录列表

    private ArrayList<AlbumItem> ispConfigList;//配置文件列表

    private String mediaModel = ModelConstant.UnknownMediaType;//下载状态

    private AlbumItem rawAlbumItem;

    public enum SyntheticStatus{
        SYNTHETIC_NONE,
        SYNTHETIC_START,
        SYNTHETIC_SUCCESS,
        SYNTHETIC_FAILED,
    }
    private SyntheticStatus syntheticStatus = SyntheticStatus.SYNTHETIC_NONE;
    private int syntheticProgress;
    private boolean showSyntheticStatus;

    public String getMediaFileModel() {
        return mediaModel;
    }

    public void setMediaFileModel(String mediaModel) {
        this.mediaModel = mediaModel;
    }


    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getDownloadLength() {
        return downloadLength;
    }

    public void setDownloadLength(long downloadLength) {
        this.downloadLength = downloadLength;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getThumbnailurl() {
        return thumbnailurl + "?" + lastModifiedTime;
    }

    public void setThumbnailurl(String thumbnailurl) {
        this.thumbnailurl = thumbnailurl;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }


    public int getMediaHeight() {
        return mediaHeight;
    }

    public void setMediaHeight(int mediaHeight) {

        this.mediaHeight = mediaHeight;
    }

    public int getMediaWidth() {
        return mediaWidth;
    }

    public void setMediaWidth(int mediaWidth) {

        this.mediaWidth = mediaWidth;
    }


    public String getMediaFormat() {
        return mediaFormat;
    }

    public void setMediaFormat(String mediaFormat) {
        this.mediaFormat = mediaFormat;
    }


    public boolean isExistLocal() {
//        if (filePath != null && !isExistLocal) {
//            File file = new File(filePath);
//            if (file.exists() && file.length() >= contentLength) {
//                isExistLocal = true;
//            }
//        }else if (filePath != null){
//            File file = new File(filePath);
//            if (!file.exists() || file.length() < contentLength) {
//                isExistLocal = false;
//            }
//        }
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists() && file.length() >= contentLength) {
                isExistLocal = true;
            }else {
                isExistLocal = false;
            }
        }
        return isExistLocal;
    }

    public void setExistLocal(boolean existLocal) {
        isExistLocal = existLocal;
    }

    public boolean isDNG() {
        return isDNG;
    }


    public boolean isExistYunTai() {
        return isExistYunTai;
    }

    public void setExistYunTai(boolean existYunTai) {
        isExistYunTai = existYunTai;
    }

    public void setDNG(boolean DNG) {
        isDNG = DNG;
    }


    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
        if (isCombine) {
            for (AlbumItem albumItem : combineItemList){
                albumItem.setSelect(isSelect);
            }
        }
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }


    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    public boolean isRaw() {
        return isRaw;
    }

    public void setRaw(boolean raw) {
        isRaw = raw;
    }

    public AlbumItem getRawAlbumItem() {
        return rawAlbumItem;
    }

    public void setRawAlbumItem(AlbumItem rawAlbumItem) {
        this.rawAlbumItem = rawAlbumItem;
    }

    public SyntheticStatus getSyntheticStatus() {
        return syntheticStatus;
    }

    public void setSyntheticStatus(SyntheticStatus syntheticStatus) {
        this.syntheticStatus = syntheticStatus;
    }

    public int getSyntheticProgress() {
        return syntheticProgress;
    }

    public void setSyntheticProgress(int syntheticProgress) {
        this.syntheticProgress = syntheticProgress;
    }

    public int getCombineFileCount() {
        return combineFileCount;
    }

    public void setCombineFileCount(int combineFileCount) {
        this.combineFileCount = combineFileCount;
    }

    public int getDisplayFileCount() {
        if (combineItemList != null) {
            if (displayFileCount < combineItemList.size()) {
                return combineItemList.size();
            }
            return displayFileCount;
        }else {
            return displayFileCount;
        }
    }

    public void setDisplayFileCount(int displayFileCount) {
        this.displayFileCount = displayFileCount;
    }

    public int getIspConfigCount() {
        return ispConfigCount;
    }

    public void setIspConfigCount(int ispConfigCount) {
        this.ispConfigCount = ispConfigCount;
    }

    public int getJpegCount() {
        return jpegCount;
    }

    public void setJpegCount(int jpegCount) {
        this.jpegCount = jpegCount;
    }

    public int getRawCount() {
        return rawCount;
    }

    public void setRawCount(int rawCount) {
        this.rawCount = rawCount;
    }

    public boolean isShowSyntheticStatus() {
        return showSyntheticStatus;
    }

    public void setShowSyntheticStatus(boolean showSyntheticStatus) {
        this.showSyntheticStatus = showSyntheticStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (obj instanceof  AlbumItem) {
            AlbumItem other = ( AlbumItem) obj;
            if (this.isCombine() && !other.isCombine()) {
                return false;
            } else if (!this.isCombine() && other.isCombine()) {
                return false;
            } else if (filePath != null && filePath.equals(other.getFilePath())) {
                return true;
            }
        }
        return false;
    }

    public AlbumItem(){
        ispConfigCount = -1;
        showSyntheticStatus = false;
    }

    public static AlbumItem Created(AlbumItem albumItem){
        AlbumItem result = new AlbumItem();

        if (albumItem != null) {
            result.setFilePath(albumItem.filePath);
            result.setRelativePath(albumItem.relativePath);
            result.setUrl(albumItem.url);
            result.setThumbnailurl(albumItem.thumbnailurl);
            result.setVideo(albumItem.isVideo);
            result.setSelect(albumItem.isSelect);
            result.setExistLocal(albumItem.isExistLocal);
            result.setExistYunTai(albumItem.isExistYunTai);
            result.setDNG(albumItem.isDNG);
            result.setDownloadStatus(albumItem.downloadStatus);
            result.setContentLength(albumItem.contentLength);
            result.setDownloadLength(albumItem.downloadLength);
            result.setLastModifiedTime(albumItem.lastModifiedTime);
            result.setVideoDuration(albumItem.videoDuration);
            result.setMediaFormat(albumItem.mediaFormat);
            result.setMediaWidth(albumItem.mediaWidth);
            result.setMediaHeight(albumItem.mediaHeight);
            result.setMediaFileModel(albumItem.mediaModel);
            result.setFileType(albumItem.fileType);
            result.setClassIndex(albumItem.classIndex);
            result.setCombine(albumItem.isCombine);
            result.setCombineItemList(albumItem.combineItemList);
            result.setIspConfigList(albumItem.getIspConfigList());
            result.setRaw(albumItem.isRaw);
            result.setRawAlbumItem(albumItem.rawAlbumItem);
            result.setSyntheticStatus(albumItem.syntheticStatus);
            result.setSyntheticProgress(albumItem.syntheticProgress);
            result.setCombineFileCount(albumItem.combineFileCount);
            result.setDisplayFileCount(albumItem.displayFileCount);
            result.setIspConfigCount(albumItem.getIspConfigCount());
            result.setJpegCount(albumItem.jpegCount);
            result.setRawCount(albumItem.rawCount);
            result.setShowSyntheticStatus(albumItem.isShowSyntheticStatus());
        }

        return result;
    }

    public boolean isCombine() {
        return combineItemList != null && combineItemList.size() >= 1;
    }

    public void setCombine(boolean combine) {
        isCombine = combine;
    }

    public ArrayList<AlbumItem> getCombineItemList() {
        if (combineItemList == null) {
            combineItemList = new ArrayList<>();
        }
        return combineItemList;
    }

    public void setCombineItemList(ArrayList<AlbumItem> combineItemList) {
        if (combineItemList == null) {
            this.isCombine = false;
            this.combineItemList = null;
            return;
        }
        this.isCombine = true;
        if (this.combineItemList == null) {
            this.combineItemList = new ArrayList<>();
        }else {
            this.combineItemList.clear();
        }
        this.combineItemList.addAll(combineItemList);
    }

    public void addCombineItem(AlbumItem albumItem){
        if (albumItem == null) {
            return;
        }
        if (this.combineItemList == null) {
            initCombineItemList();
        }
        if (!this.combineItemList.contains(albumItem)){

            int position = isEqualsName(this.combineItemList,albumItem);
            if (position == -1) {
                this.combineItemList.add(albumItem);
            }else {
                this.combineItemList.get(position).setRawAlbumItem(albumItem);
                this.combineItemList.set(position,formatRawAlbum(this.combineItemList.get(position)));
            }

            if (albumItem.getFilePath() != null) {
                int jpg = 0;
                int raw = 0;
                for (int i = 0; i < combineItemList.size(); i++){
                    if (combineItemList.get(i).getFilePath() == null) {
                        continue;
                    }
                    if (combineItemList.get(i).getFilePath().toLowerCase().endsWith(".jpg")){
                        jpg++;
                        if (combineItemList.get(i).getRawAlbumItem() != null) {
                            raw++;
                        }
                    }else {
                        raw++;
                        if (combineItemList.get(i).getRawAlbumItem() != null) {
                            jpg++;
                        }
                    }
                }
                if (jpegCount < jpg && albumItem.getFilePath().toLowerCase().endsWith(".jpg")){
                    jpegCount++;
                }else if (rawCount < raw && !albumItem.getFilePath().toLowerCase().endsWith(".jpg")){
                    rawCount++;
                }

                if (combineFileCount < (jpegCount+rawCount)){
                    combineFileCount++;
                }
            }
        }
        if (displayFileCount < combineItemList.size()) {
            displayFileCount = combineItemList.size();
        }
    }

    public boolean removeCombineItem(AlbumItem albumItem){
        Log.d(TAG, "removeCombineItem: this.toString() = "+this.toString());
        Log.d(TAG, "removeCombineItem: albumItem = "+albumItem);
        Log.d(TAG, "removeCombineItem: combineFileCount = "+combineFileCount+" , combineItemList.size() = "+combineItemList.size());
        Log.d(TAG, "removeCombineItem: jpegCount = "+jpegCount+" , rawCount = "+rawCount);
        boolean removeAll = false;
        if (combineItemList == null) {
            return removeAll;
        }

        combineItemList.remove(albumItem);

        if (combineItemList.size() < 1) {
            return true;
        }

        if (this.filePath.equals(albumItem.getFilePath())){
            AlbumItem temp = combineItemList.get(0);
            changeParameter(temp);
        }
        combineFileCount--;
        if (albumItem.getFilePath().toLowerCase().endsWith("jpg")){
            jpegCount--;
        }else {
            rawCount--;
        }

        displayFileCount = combineItemList.size();

        Log.d(TAG, "removeCombineItem: end combineFileCount = "+combineFileCount+" , combineItemList.size() = "+combineItemList.size());
        Log.d(TAG, "removeCombineItem: end jpegCount = "+jpegCount+" , rawCount = "+rawCount);

        return false;
    }

    public void changeParameter(AlbumItem albumItem){
        this.setFilePath(albumItem.filePath);
        this.setRelativePath(albumItem.relativePath);
        this.setUrl(albumItem.url);
        this.setThumbnailurl(albumItem.thumbnailurl);
        this.setVideo(albumItem.isVideo);
        this.setSelect(albumItem.isSelect);
        this.setExistLocal(albumItem.isExistLocal);
        this.setExistYunTai(albumItem.isExistYunTai);
        this.setDNG(albumItem.isDNG);
        this.setDownloadStatus(albumItem.downloadStatus);
        this.setContentLength(albumItem.contentLength);
        this.setDownloadLength(albumItem.downloadLength);
        this.setLastModifiedTime(albumItem.lastModifiedTime);
        this.setVideoDuration(albumItem.videoDuration);
        this.setMediaFormat(albumItem.mediaFormat);
        this.setMediaWidth(albumItem.mediaWidth);
        this.setMediaHeight(albumItem.mediaHeight);
        this.setMediaFileModel(albumItem.mediaModel);
        this.setFileType(albumItem.fileType);
        this.setClassIndex(albumItem.classIndex);
    }

    public void setCombineItem(int index, AlbumItem albumItem){
        if (albumItem != null) {
            this.combineItemList.set(index,albumItem);
        }
    }

    //初始化二级目录
    public void initCombineItemList(){
        this.combineItemList = new ArrayList<>();
        AlbumItem copy = null;

        copy = AlbumItem.Created(this);
        copy.setCombineFileCount(0);
        copy.setDisplayFileCount(0);
        copy.setJpegCount(0);
        copy.setRawCount(0);
        copy.setCombineItemList(null);
        copy.setCombine(false);

        this.isCombine = true;
        this.combineItemList.add(copy);
        if (jpegCount == 0 && this.filePath.toLowerCase().endsWith(".jpg")){
            jpegCount++;
        } else if (rawCount == 0 && !this.filePath.toLowerCase().endsWith(".jpg")) {
            rawCount++;
        }
        if (combineFileCount == 0) {
            combineFileCount++;
        }
        if (displayFileCount < combineItemList.size()) {
            displayFileCount = combineItemList.size();
        }
    }

    public ArrayList<AlbumItem> getIspConfigList() {
        return ispConfigList;
    }

    public void setIspConfigList(ArrayList<AlbumItem> ispConfigList) {
        this.ispConfigList = ispConfigList;
    }

    public void addIspConfigItem(AlbumItem albumItem){
        if (ispConfigList == null) {
            ispConfigList = new ArrayList<>();
        }
        ispConfigList.add(albumItem);
    }

    @Override
    public String toString() {
        return "url =" + url +
                ",filePath =" + filePath +
                ",isExistLocal =" + isExistLocal +
                ",isExistYunTai =" + isExistYunTai +
                ",thumbnailurl =" + thumbnailurl +
                ",relativePath =" + relativePath +
                ",isVideo =" + isVideo +
                ",isSelect =" + isSelect +
                ",isDNG =" + isDNG +
                ",isRaw =" + isRaw +
                ",downloadStatus =" + downloadStatus +
                ",contentLength =" + contentLength +
                ",downloadLength =" + downloadLength +
                ",lastModifiedTime =" + lastModifiedTime +
                ",videoDuration =" + videoDuration +
                ",mediaFormat =" + mediaFormat +
                ",mediaHeight =" + mediaHeight +
                ",mediaWidth =" + mediaWidth+
                ",isCombine =" + isCombine+
                ",mediaModel =" + mediaModel+
                ",fileType =" + fileType+
                ",classIndex =" + classIndex+
                ",rawAlbumItem =" + rawAlbumItem+
                ",syntheticStatus =" + syntheticStatus+
                ",syntheticProgress =" + syntheticProgress+
                ",combineFileCount =" + combineFileCount+
                ",displayFileCount =" + displayFileCount+
                ",ispConfigCount =" + ispConfigCount+
                ",jpegCount =" + jpegCount+
                ",rawCount =" + rawCount+
                ",showSyntheticStatus =" + showSyntheticStatus+
                ",combineItemList =" + combineItemList+
                ",ispConfigList =" + ispConfigList
                ;
    }

    private int isEqualsName(ArrayList<AlbumItem> arrayList, AlbumItem albumItem){
        int position = -1;

        if (arrayList == null || albumItem == null) {
            return position;
        }

        String srcPath = albumItem.getRelativePath().substring(0,albumItem.getRelativePath().lastIndexOf("."));
        for (int i = 0; i < arrayList.size(); i++){
            String temp = arrayList.get(i).getRelativePath().substring(0,arrayList.get(i).getRelativePath().lastIndexOf("."));
            if (srcPath.equals(temp)){
                position = i;
                break;
            }
        }

        return position;
    }

    //互换当前albumItem和 rawItem
    private AlbumItem formatRawAlbum(AlbumItem albumItem){
        AlbumItem result = AlbumItem.Created(albumItem);

        if (result.getRawAlbumItem() != null) {
            if (result.getMediaFormat().toLowerCase().contains("jpg") ||
                    result.getMediaFormat().toLowerCase().contains("mov")) {
                result.setRaw(false);
                result.getRawAlbumItem().setRaw(true);
            }else {
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

    public boolean isSupportDownload(){
        boolean supportDownload = false;
        if (combineItemList == null || combineItemList.size() < 1) {
            if (rawAlbumItem == null) {
                supportDownload = !this.isExistLocal();
            }else {
                supportDownload = !this.isExistLocal() && !this.getRawAlbumItem().isExistLocal();
            }
        }else {
            // TODO: 2021/12/30 一级目录下载图标显示
//            for (int i = 0; i < combineItemList.size(); i++){
//                AlbumItem albumItem = combineItemList.get(i);
//                boolean support = false;
//                if (albumItem.getRawAlbumItem() == null) {
//                    support = !albumItem.isExistLocal;
//                }else {
//                    support = !albumItem.isExistLocal && !albumItem.getRawAlbumItem().isExistLocal;
//                }
//                if (!support) {
//                    supportDownload = false;
//                    break;
//                }
//            }

            for (int i = 0; i < combineItemList.size(); i++){
                AlbumItem albumItem = combineItemList.get(i);
                if (albumItem.getRawAlbumItem() == null) {
                    supportDownload = !albumItem.isExistLocal();
                }else {
                    supportDownload = !albumItem.isExistLocal() && !albumItem.getRawAlbumItem().isExistLocal();
                }

                if (supportDownload){
                    break;
                }
            }

        }

        return supportDownload;
    }

}
