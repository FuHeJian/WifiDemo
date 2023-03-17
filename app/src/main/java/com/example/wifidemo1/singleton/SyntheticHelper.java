package com.example.wifidemo1.singleton;

import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.example.wifidemo1.utils.OrderCommunication;
import com.example.wifidemo1.utils.Panorama;
import com.example.wifidemo1.model.AlbumItem;
import com.example.wifidemo1.model.BroadcastActionEvent;
import com.example.wifidemo1.oksocket.common.interfaces.utils.TextUtils;
import com.example.wifidemo1.utils.CMD;
import com.example.wifidemo1.utils.FileUtil;
import com.example.wifidemo1.utils.MyMessage;
import com.example.wifidemo1.utils.UrlUtils;
import com.example.wifidemo1.utils.UtilFunction;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SyntheticHelper {
    private static final String TAG = "SyntheticHelper";
    private static SyntheticHelper syntheticHelper;
    private String FILE_DEFAULT_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + UrlUtils.FOLDER_PREFIX;
    private String temporaryPath;
    private OkHttpClient mDowloadClient;

    private int fileCount_panorama;
    private int mergeType_panorama;
    private int st_level_panorama;
    private String warp_type_panorama;
    private String warp_type_1_panorama;
    private String srcPath;
    private String outputPath;

    private HashMap<String,AlbumItem> syntheticMap;
    private Vector<AlbumItem> syntheticVector;
    private Vector<UpLoadInfo> mUpLoadInfoVector;
    private boolean isDestroy;
    private boolean isStarting;
    private boolean isUpLoading;
    private SyntheticThread mSyntheticThread;
    private UpLoadThread mUpLoadThread;

    private static final String connectHost = "192.168.0.1";
    private static final int connectPort = 80;
    private static final String BOUNDARY = "WebKitFormBoundaryCR3ePJ61mAc18ElXfy";
    private String targetFilepath = "/sd";

    private Socket socket;
    private OnSyntheticListener mOnSyntheticListener;
    private OnAvailableMemoryListener mOnAvailableMemoryListener;

    public static SyntheticHelper getInstance() {
        if (syntheticHelper == null) {
            synchronized (SyntheticHelper.class) {
                if (syntheticHelper == null) {
                    syntheticHelper = new SyntheticHelper();
                }
            }
        }
        return syntheticHelper;
    }

    public SyntheticHelper(){
        syntheticMap = new HashMap<>();
        syntheticVector = new Vector<>();
        mUpLoadInfoVector = new Vector<>();

        this.fileCount_panorama = 4;
        this.mergeType_panorama = 1;
        this.st_level_panorama = 1;
        this.warp_type_panorama = "plane";
        this.warp_type_1_panorama = "spherical";
        this.outputPath = null;
        this.isDestroy = false;
        this.isStarting = false;
        this.isUpLoading = false;
    }

    public OnSyntheticListener getOnSyntheticListener() {
        return mOnSyntheticListener;
    }

    public void setOnSyntheticListener(OnSyntheticListener onSyntheticListener) {
        mOnSyntheticListener = onSyntheticListener;
    }

    public void setOnAvailableMemoryListener(OnAvailableMemoryListener onAvailableMemoryListener) {
        mOnAvailableMemoryListener = onAvailableMemoryListener;
    }

    public void onCreated(OnAvailableMemoryListener onAvailableMemoryListener){
        isDestroy = false;
        mOnAvailableMemoryListener = onAvailableMemoryListener;
    }

    public void onDestroy(){
        isDestroy = true;
        mOnAvailableMemoryListener = null;
    }

    public void configPanorama(int count, int type, int level, String warp_type, String warp_type_1, String outputPath){
        this.fileCount_panorama = count;
        this.mergeType_panorama = type;
        this.st_level_panorama = level;
        this.warp_type_panorama = warp_type;
        this.warp_type_1_panorama = warp_type_1;
        this.outputPath = outputPath;
    }

    public void addSyntheticItem(AlbumItem outputAlbumItem,long available_memory){
        Log.d(TAG, "addSyntheticItem: 111 outputAlbumItem.toString() = "+outputAlbumItem.toString());
        Log.d(TAG, "addSyntheticItem: syntheticMap.size() = "+syntheticMap.size());
        if (syntheticMap.containsKey(outputAlbumItem.getFilePath())){
            Log.e(TAG, "addSyntheticItem: has add...");
            return;
        }
        Log.d(TAG, "addSyntheticItem: 222 albumItem.toString() = "+outputAlbumItem.toString());
        syntheticMap.put(outputAlbumItem.getFilePath(),outputAlbumItem);
        syntheticVector.add(outputAlbumItem);
        outputAlbumItem.setSyntheticStatus(AlbumItem.SyntheticStatus.SYNTHETIC_START);
        EventBus.getDefault().post(new SyntheticInfo(outputAlbumItem));
        if (!isStarting) {
            mSyntheticThread = new SyntheticThread(available_memory);
            mSyntheticThread.start();
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
        Log.d(TAG, "createFile: path = "+path);
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

    private Socket getSocket() throws IOException {
        if (this.socket == null) {
            this.socket = new Socket();
            socket.connect(new InetSocketAddress(connectHost, connectPort), 60000);
        }
        return socket;
    }

    private void closeUploadSocket() throws IOException {
        if (this.socket != null){
            this.socket.close();
            this.socket = null;
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

    private boolean upLoad(String filePath, String relativePath){
        boolean flag = false;
        ByteArrayOutputStream bos = null;
        ByteArrayInputStream bis = null;
        OutputStream os = null;
        try {
//            String filePath = albumItem.getFilePath();
            File sp_out_file = new File(filePath);
            if (!sp_out_file.exists()) {
                Log.e(TAG, "upLoad: sp_out_file.exists() = false.");
                return false;
            }

            // TODO: 2021/11/9 上传文件路径
//            String relativePath = albumItem.getRelativePath();
            Log.d(TAG, "upLoad: relativePath = "+relativePath);
            int index = relativePath.lastIndexOf("/");
            targetFilepath = relativePath.substring(4,index);

            index = filePath.lastIndexOf("/");
            String fileName = filePath.substring(index+1);
            os = getSocket().getOutputStream();
            String disposition = getDisposition(fileName, 0);
            String end = "\r\n------" + BOUNDARY + "--";
            long contentLength = disposition.length() + end.length() + sp_out_file.length();
            String header = getHeader(String.valueOf(contentLength));

            byte[] headerBytes = header.getBytes();
            byte[] dispositionBytes = disposition.getBytes();
            byte[] fileBytes = getBytesFromFile(sp_out_file);
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
                    int test = (int) ((position * 1.0f / totalLen * 100) / 2 + 50);
                    Log.d(TAG, "upLoad: test = "+test);
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
                Log.e(TAG, "upLoad: error ----- uploaderrormessage totalLen != position");
                return false;
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
                closeUploadSocket();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "upload: Exception222 e =" + e);
            }
        }

        return flag;
    }

    private void copyFile(Vector<Pair<String,String>> pathList){
        for (Pair<String,String> pair : pathList){
            try{
                if (pair.first == null || pair.second == null) {
                    continue;
                }
                File tempFile = new File(pair.first);
                if (!tempFile.exists()) {
                    continue;
                }
                File copyFile = new File(pair.second);
                if (copyFile.exists()){
                    continue;
                }
                createFile(pair.second);

                InputStream inputStream = new FileInputStream(tempFile);
                FileOutputStream outputStream = new FileOutputStream(copyFile);
                byte[] buffer = new byte[1024];
                int c;
                while ((c = inputStream.read(buffer)) > 0)
                {
                    outputStream.write(buffer, 0, c);
                }
                outputStream.flush();
                inputStream.close();
                outputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean copyFile(String src, String dst){
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(dst)){
            Log.e(TAG, "copyFile: src = "+src+" , dst = "+dst);
            return false;
        }

        File srcFile = new File(src);
        File dstFile = new File(dst);

        if (!srcFile.exists()){
            Log.e(TAG, "copyFile: srcFile.exists = false");
            return false;
        }

        boolean error = false;
        try {
            if (!dstFile.exists()) {
                createFile(dst);
            }

            InputStream inputStream = new FileInputStream(srcFile);
            FileOutputStream outputStream = new FileOutputStream(dstFile);
            byte[] buffer = new byte[1024];
            int c;
            while ((c = inputStream.read(buffer)) > 0)
            {
                outputStream.write(buffer, 0, c);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
            error = true;
        }

        return !error;
    }

    private void deleteDirWithFile(File dir) {
        Log.d(TAG, "deleteDirWihtFile: ");
        try {
            if (dir == null || !dir.exists() || !dir.isDirectory()) {
                return;
            }
            if (dir.listFiles() != null) {
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        file.delete();
                    } else if (file.isDirectory()) {
                        deleteDirWithFile(file);
                    }
                }
            }
            dir.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Pair<String,String> download(AlbumItem albumItem){
        if (albumItem == null) {
            return new Pair<>(null,null);
        }
        String tempPath = null;
        String copyPath = null;
        try {
            if (mDowloadClient == null)
                mDowloadClient = new OkHttpClient.Builder().build();

            copyPath = albumItem.getFilePath();

            long downloadLength = 0;
            long contentLength = albumItem.getContentLength();
            String url = albumItem.getUrl();

            String name = albumItem.getRelativePath().substring(albumItem.getRelativePath().lastIndexOf("/")+1);
            tempPath = temporaryPath + name;
            File file = new File(tempPath);
            createFile(tempPath);

            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength)
                    .url(url)
                    .build();

            Call call = mDowloadClient.newCall(request);
            Response response = call.execute();
            InputStream is = null;
            FileOutputStream fileOutputStream = null;

            is = response.body().byteStream();
            Log.d(TAG, "DownloadThread run: is.available() = "+is.available());
            fileOutputStream = new FileOutputStream(file, true);
            byte[] buffer = new byte[2048 * 8];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
                downloadLength += len;
                Log.d(TAG, "DownloadThread run: downloadLength = "+downloadLength);
            }
            fileOutputStream.flush();

            file.setLastModified(albumItem.getLastModifiedTime());
            albumItem.setDownloadLength(albumItem.getContentLength());
            albumItem.setExistLocal(true);
            Log.d(TAG, "DownloadThread run: isDestroy = "+isDestroy+" , tempPath = "+tempPath+" , copyPath = "+copyPath);
        }catch (Exception e){
            e.printStackTrace();
            tempPath = null;
        }
        return new Pair<>(tempPath,copyPath);
    }

    private boolean download(ArrayList<AlbumItem> ispConfigList){
        if (ispConfigList == null) {
            return false;
        }
        boolean isError = false;

        for (int i = 0; i < ispConfigList.size(); i++){
            AlbumItem albumItem = ispConfigList.get(i);
            String url = albumItem.getUrl();
            String path = albumItem.getFilePath();
            try {
                URL downloadurl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) downloadurl.openConnection();
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "download Pair : responseCode = "+responseCode);

                String name = path.substring(path.lastIndexOf("/")+1);
                String configFilePath = temporaryPath + name;
                createFile(configFilePath);
                File file = new File(configFilePath);
                if (responseCode == 200 || responseCode == 206) {
                    InputStream inputStream = connection.getInputStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[2048 * 8];//缓冲数组2kB
                    int downloadLen;
                    long totalLength = connection.getContentLength();
                    long downloadLength = 0;
                    while ((downloadLen = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, downloadLen);
                        downloadLength += downloadLen;
                    }
                    fileOutputStream.flush();
                    inputStream.close();
                    fileOutputStream.close();
                }else {
                    isError = true;
                    break;
                }
            }catch (Exception e){
                e.printStackTrace();
                isError = true;
                break;
            }
        }

        return !isError;
    }

    private boolean syntheticFocusStack(){
        try {
            int cpu = 4;
            try {
                cpu = FileUtil.getNumCores();
            }catch (Exception e){
                e.printStackTrace();
            }

            cpu = 2;

            long memory = -1;
            if (mOnAvailableMemoryListener != null) {
//                memory = mOnAvailableMemoryListener.getAvailableMemory();
                // TODO: 2021/12/29
                memory = mOnAvailableMemoryListener.getAvailableMemory()/2;
            }else {
                return false;
            }

            if (memory <= 0) {
                return false;
            }

            long handler = Panorama.initfromyamlFocus(srcPath,cpu,58,memory);
            Log.d(TAG, "syntheticFocusStack: srcPath = "+srcPath+" , warp_type_1_panorama = "+warp_type_1_panorama+" , outputPath = "+outputPath);

            if (handler == -1) {
                Log.e(TAG, "------ 1 syntheticFocusStack: handler = "+handler);
                return false;
            }

            Log.d(TAG, "syntheticFocusStack: start blend ...........");
            int blendResult = Panorama.blendFocus(handler);

            if (isDestroy) {
                Log.d(TAG, "------ syntheticFocusStack: isDestroy = "+isDestroy);
//                onCompleted(false);
                return false;
            }
            if (blendResult == 0){
                Log.d(TAG, "------ syntheticFocusStack: blend success");
//                onCompleted(true);
                String src = temporaryPath+"all_in_focus.jpg";//hdr_ldr_final
                return copyFile(src,outputPath);
            }else {
                Log.e(TAG, "------ syntheticFocusStack: blend failed");
//                onCompleted(false);
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean syntheticHDR(){
        try {
            int cpu = 4;
            try {
                cpu = FileUtil.getNumCores();
            }catch (Exception e){
                e.printStackTrace();
            }

            long handler = Panorama.initfromyamlHDR(srcPath,cpu);
            Log.d(TAG, "syntheticHDR: srcPath = "+srcPath+" , warp_type_1_panorama = "+warp_type_1_panorama+" , outputPath = "+outputPath);

            if (handler == -1) {
                Log.e(TAG, "------ 1 syntheticHDR: handler = "+handler);
                return false;
            }

            Log.d(TAG, "syntheticHDR: start blend ...........");
            int blendResult = Panorama.blendHDR(handler);

            if (isDestroy) {
                Log.d(TAG, "------ syntheticHDR: isDestroy = "+isDestroy);
//                onCompleted(false);
                return false;
            }
            if (blendResult == 0){
                Log.d(TAG, "------ syntheticHDR: blend success");
//                onCompleted(true);
                String src = temporaryPath+"hdr_ldr_final.jpg";
                return copyFile(src,outputPath);
            }else {
                Log.e(TAG, "------ syntheticHDR: blend failed");
//                onCompleted(false);
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean syntheticPanorama(Vector<Pair<String,String>> pathList, long available_memory){
        try {

            createFile(outputPath);
            int cpu = 4;
            try {
                cpu = FileUtil.getNumCores();
            }catch (Exception e){
                e.printStackTrace();
            }

            // TODO: 2021/12/29
            cpu = 2;

            long memory = -1;
            if (mOnAvailableMemoryListener != null) {
//                memory = mOnAvailableMemoryListener.getAvailableMemory();
                // TODO: 2021/12/29
                memory = mOnAvailableMemoryListener.getAvailableMemory()/2;
            }else {
                return false;
            }

            if (memory <= 0) {
                return false;
            }
            Log.d(TAG, "syntheticPanorama: memory = "+memory);
            long handler = Panorama.initfromyaml(srcPath,warp_type_1_panorama,cpu,memory);
            Log.d(TAG, "syntheticPanorama: srcPath = "+srcPath+" , warp_type_1_panorama = "+warp_type_1_panorama+" , outputPath = "+outputPath);

            if (handler == -1) {
                Log.e(TAG, "------ 1 syntheticPanorama: handler = "+handler);
                return false;
            }

            int ret = Panorama.pushimg(handler,cpu);

            Log.d(TAG, "------ syntheticPanorama: ret = "+ret);
            if (ret != 0) {
                return false;
            }

            Log.d(TAG, "syntheticPanorama: start blend ...........");
            int blendResult = Panorama.blend(handler,outputPath,true);
            if (isDestroy) {
                Log.d(TAG, "------ syntheticPanorama: isDestroy = "+isDestroy);
//                onCompleted(false);
                return false;
            }
            if (blendResult == 0){
                Log.d(TAG, "------ syntheticPanorama: blend success");
//                onCompleted(true);
                return true;
            }else {
                Log.e(TAG, "------ syntheticPanorama: blend failed");
//                onCompleted(false);
                return false;
            }

        } catch (Exception e){
            e.printStackTrace();
//            onCompleted(false);
            return false;
        }
    }

    private boolean syntheticStarStack(Vector<Pair<String,String>> pathList, long available_memory){
        try {

            if (pathList == null || pathList.size() < 1) {
                Log.e(TAG, "syntheticStarStack: pathList = "+pathList);
                return false;
            }

            createFile(outputPath);

            int cpu = 4;
            try {
                cpu = FileUtil.getNumCores();
            }catch (Exception e){
                e.printStackTrace();
            }

            // TODO: 2021/12/29
            cpu = 2;

            long memory = -1;
            if (mOnAvailableMemoryListener != null) {
//                memory = mOnAvailableMemoryListener.getAvailableMemory();
                // TODO: 2021/12/29
                memory = mOnAvailableMemoryListener.getAvailableMemory()/2;
            }else {
                return false;
            }

            if (memory <= 0) {
                return false;
            }

            Log.d(TAG, "syntheticStarStack: memory = "+memory);
            long handler = Panorama.initStarStack(outputPath);
            Log.d(TAG, "syntheticStarStack: srcPath = "+srcPath+" , warp_type_1_panorama = "+warp_type_1_panorama+" , outputPath = "+outputPath);

            if (handler == -1) {
                Log.e(TAG, "------ 1 syntheticStarStack: handler = "+handler);
                return false;
            }


            boolean isError = false;
            while (true){
                if (pathList.size() < 1) {
                    break;
                }
                boolean isEnd = false;
                Pair<String,String> pair = pathList.remove(0);
                if (pathList.size() < 1){
                    isEnd = true;
                }

                int ret = Panorama.addStarStack(handler,pair.first,isEnd);
                Log.d(TAG, "syntheticStarStack: addStarStack ret = "+ret);
                if (ret != 0) {
                    isError = true;
                    break;
                }
            }

            if (isError) {
                Log.e(TAG, "syntheticStarStack: isError = "+isError);
                return false;
            }else if (isDestroy) {
                Log.d(TAG, "------ syntheticStarStack: isDestroy = "+isDestroy);
//                onCompleted(false);
                return false;
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private ArrayList<AlbumItem> getItemList(ArrayList<AlbumItem> list){
        if (list == null || list.size() < 1) {
            return null;
        }
        ArrayList<AlbumItem> output = new ArrayList<>();
        for (AlbumItem albumItem : list){
            if (albumItem.getRelativePath().toLowerCase().endsWith("_out.jpg")){
                continue;
            }else if (albumItem.getRelativePath().toLowerCase().endsWith(".jpg")){
                output.add(albumItem);
            }
        }
        return output;
    }

    private class SyntheticThread extends Thread{
        private long available_memory;
        private String syntheticMapKey;
        public SyntheticThread(long available_memory){
            this.available_memory = available_memory;
            this.syntheticMapKey = "";
        }

        @Override
        public void run() {
            isStarting = true;

            while (true){
                if (isDestroy) {
                    break;
                } else if (syntheticVector.size() < 1) {
                    break;
                }

                AlbumItem outputAlbumItem = new AlbumItem();
                AlbumItem albumItem = syntheticVector.remove(0);
                syntheticMapKey = albumItem.getFilePath();
                Log.d(TAG, "run: syntheticMap.size() = "+syntheticMap.size());
                Log.d(TAG, "SyntheticThread run: start --- "+albumItem);
                albumItem.setSyntheticStatus(AlbumItem.SyntheticStatus.SYNTHETIC_START);
                postEventbus(albumItem,false);

                if (!albumItem.isCombine()) {
                    Log.e(TAG, "SyntheticThread run: albumItem.isCombine() = "+albumItem.isCombine());
                    albumItem.setSyntheticStatus(AlbumItem.SyntheticStatus.SYNTHETIC_FAILED);
                    albumItem.setShowSyntheticStatus(true);
                    postEventbus(albumItem,true);
                    String what = "albumItem.isCombine() = false";
                    onCallBack(false,what,albumItem);
                    continue;
                }

                ArrayList<AlbumItem> itemList = getItemList(albumItem.getCombineItemList());

                if (itemList == null || itemList.size() < 1) {
                    Log.e(TAG, "SyntheticThread run: itemList = "+itemList);
                    albumItem.setSyntheticStatus(AlbumItem.SyntheticStatus.SYNTHETIC_FAILED);
                    albumItem.setShowSyntheticStatus(true);
                    postEventbus(albumItem,true);
                    String what = "itemList = "+itemList;
                    onCallBack(false,what,albumItem);
                    continue;
                }

                fileCount_panorama = itemList.size();
                String filePath = albumItem.getFilePath();
                outputPath = filePath.substring(0, filePath.lastIndexOf("/")) + "/SP_out.jpg";
                int index = outputPath.lastIndexOf("/");
                String text1 = outputPath.substring(0,index + 1);
                String text2 = outputPath.substring(index + 1,outputPath.length());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(text1).append("temporary/");
//                stringBuilder.append(text1);
                temporaryPath = stringBuilder.toString();
                File file = new File(temporaryPath);
                if (file.exists()) {
                    deleteDirWithFile(file);
                }
                outputAlbumItem.setFilePath(outputPath);

                if (!download(albumItem.getIspConfigList())){
                    Log.e(TAG, "SyntheticThread run: download(itemList) failed.");
                    albumItem.setSyntheticStatus(AlbumItem.SyntheticStatus.SYNTHETIC_FAILED);
                    albumItem.setShowSyntheticStatus(true);
                    postEventbus(albumItem,true);
                    String what = "configList download failed";
                    onCallBack(false,what,albumItem);
                    continue;
                }

//                Vector<String> pathList = new Vector<>();
                Vector<Pair<String,String>> pairVector = new Vector<>();
                for (AlbumItem albumItem1 : itemList){
                    Pair<String,String> pair = download(albumItem1);
                    if (pair.first != null && pair.second != null) {
                        pairVector.add(pair);
                    }
                }

                if (pairVector.size() < 1) {
                    Log.e(TAG, "SyntheticThread run: pathList.size() < 1");
                    albumItem.setSyntheticStatus(AlbumItem.SyntheticStatus.SYNTHETIC_FAILED);
                    albumItem.setShowSyntheticStatus(true);
                    postEventbus(albumItem,true);
                    String what = "pairVector.size() < 1";
                    onCallBack(false,what,albumItem);
                    continue;
                }

                srcPath = temporaryPath;
                Vector<Pair<String, String>> tempList = new Vector<>(pairVector);
                boolean success = false;
                if (albumItem.getFileType() == CMD.FILE_TYPE_PAN) {
                    success = syntheticPanorama(tempList,available_memory);
                } else if (albumItem.getFileType() == CMD.FILE_TYPE_FOCUS) {
                    success = syntheticFocusStack();
                } else if (albumItem.getFileType() == CMD.FILE_TYPE_HDR) {
                    success = syntheticHDR();
                } else if (albumItem.getFileType() == CMD.FILE_TYPE_STAR_SKY_STACK){
                    success = syntheticStarStack(tempList,available_memory);
                }

                String what = "success = "+success;
                if (success) {
                    String relativePath = albumItem.getRelativePath();
                    index = relativePath.lastIndexOf("/");
                    String start = relativePath.substring(0,index+1);
                    filePath = albumItem.getFilePath();
                    index = filePath.lastIndexOf("/");
//                    String end = filePath.substring(index+1);
                    String end = "SP_out.jpg";

                    relativePath = start+end;
                    String result = relativePath.substring(5).trim();
                    String urlString = OrderCommunication.getInstance().resourceAddress   + result;
                    outputAlbumItem.setUrl(urlString);
                    outputAlbumItem.setThumbnailurl(urlString);
                    outputAlbumItem.setRelativePath(relativePath);
                    outputAlbumItem.setExistLocal(true);
                    outputAlbumItem.setExistYunTai(true);
                    outputAlbumItem.setRaw(false);
                    outputAlbumItem.setVideo(false);
                    outputAlbumItem.setDNG(false);
                    outputAlbumItem.setFileType(albumItem.getFileType());
                    outputAlbumItem.setClassIndex(albumItem.getClassIndex());
                    file = new File(outputAlbumItem.getFilePath());
                    Log.d(TAG, "run:---------- outputAlbumItem = "+outputAlbumItem);
                    Log.d(TAG, "run:---------- file.lastModified = "+file.lastModified());
//run:---------- file.lastModified = 1638601112000
                    try {
                        String bbb = UtilFunction.getDateFileNameString(file.lastModified());
                        Log.d(TAG, "run: bbb = "+bbb);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    String appFileTime = formatter.format(file.lastModified());
                    Log.d(TAG, "run: appFileTime = "+appFileTime);

                    outputAlbumItem.setLastModifiedTime(file.lastModified());
                    outputAlbumItem.setContentLength(file.length());
                    Log.d(TAG, "run: 111  albumItem = "+albumItem);
                    MediaFileLoad.getInstance().addAlbumItemToLocal(outputAlbumItem);
                    albumItem.addCombineItem(outputAlbumItem);
                    albumItem.changeParameter(outputAlbumItem);
                    albumItem.setLastModifiedTime(file.lastModified());
                    Log.d(TAG, "run: 222  albumItem = "+albumItem);


                    albumItem.setSyntheticStatus(AlbumItem.SyntheticStatus.SYNTHETIC_SUCCESS);
                    albumItem.setShowSyntheticStatus(true);

                    postEventbus(albumItem,true);
                    onCallBack(true,"",albumItem);

                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.MEDIA_FILE_SYNTHETIC_COMPLETED,true));

                    mUpLoadInfoVector.add(new UpLoadInfo(outputAlbumItem.getFileType(),
                            outputAlbumItem.getFilePath(),outputAlbumItem.getRelativePath(),appFileTime));
                    startUpload();
                }else {
                    file = new File(outputPath);
                    deleteDirWithFile(file);

                    MediaFileLoad.getInstance().delectYuntaiFiles(albumItem.getIspConfigList());

                    albumItem.setSyntheticStatus(AlbumItem.SyntheticStatus.SYNTHETIC_FAILED);
                    albumItem.setShowSyntheticStatus(true);
                    postEventbus(albumItem,true);
                    onCallBack(false,what,albumItem);

                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.MEDIA_FILE_SYNTHETIC_COMPLETED,false));
                }

//                copyFile(pairVector);
                file = new File(temporaryPath);
                deleteDirWithFile(file);
                Log.d(TAG, "SyntheticThread run: end --- "+albumItem);
            }

            isStarting = false;
        }

        private void postEventbus(AlbumItem albumItem, boolean isRemove){
            EventBus.getDefault().post(new SyntheticInfo(albumItem));
            Log.d(TAG, "postEventbus: isRemove = "+isRemove+" , syntheticMap = "+syntheticMap);
            if (syntheticMap != null && isRemove) {
                syntheticMap.remove(syntheticMapKey);
                Log.d(TAG, "postEventbus: syntheticMap.size() = "+syntheticMap.size());
            }
        }

        private void onCallBack(boolean succeed, String what, AlbumItem albumItem){
            if (succeed) {
                if (mOnSyntheticListener != null) {
                    mOnSyntheticListener.onSyntheticCompleted(albumItem);
                }
            }else {
                if (mOnSyntheticListener != null) {
                    mOnSyntheticListener.onSyntheticFailed(what,albumItem);
                }
            }
        }
    }

    private void startUpload(){
        if (!isUpLoading) {
            mUpLoadThread = new UpLoadThread();
            mUpLoadThread.start();
        }
    }

    private class UpLoadThread extends Thread{

        public UpLoadThread(){

        }

        @Override
        public void run() {
            isUpLoading = true;
            while (true){
                try {

                    if (isDestroy) {
                        break;
                    } else if (mUpLoadInfoVector.size() < 1) {
                        break;
                    }

                    UpLoadInfo upLoadInfo = mUpLoadInfoVector.remove(0);
                    boolean isOk = false;
                    // TODO: 2022/1/13 上传文件
                    if (OrderCommunication.getInstance().svFlag==1) {
                        String mediaType = "image/jpeg";
                        String filePath = upLoadInfo.filePath;
                        String urlStr = null;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("http://192.168.0.1/dav");
                        String[] relative = upLoadInfo.relativePath.split("/");
                        boolean isAdd = false;
                        for (int i = 0; i < relative.length; i++){
                            if (relative[i].equals("sd")){
                                isAdd = true;
                                continue;
                            }else if (!isAdd){
                                continue;
                            }
                            stringBuilder.append("/").append(relative[i]);
                        }
                        urlStr = stringBuilder.toString();
                        Log.d(TAG, "UpLoadThread run: ------ urlStr = "+urlStr);
                        isOk = MediaFileLoad.getInstance().uploadFlie(mediaType,filePath,urlStr);
                    }else {
                        isOk = upLoad(upLoadInfo.filePath,upLoadInfo.relativePath);
                    }
//                    boolean isOk = upLoad(upLoadInfo.filePath,upLoadInfo.relativePath);

                    Log.d(TAG, "UpLoadThread run: isOk = "+isOk);
                    if (isOk) {
                        try {
                            Log.d(TAG, "UpLoadThread run: SP_APP_ADD_FILE");
                            OrderCommunication.getInstance().SP_APP_ADD_FILE(
                                    ""+upLoadInfo.fileType,upLoadInfo.relativePath,upLoadInfo.appTime);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            isUpLoading = false;
        }
    }

    private class UpLoadInfo{
        public String filePath;
        public String relativePath;
        public String appTime;
        public int fileType;
        public UpLoadInfo(int fileType, String filePath, String relativePath, String appTime){
            this.fileType = fileType;
            this.filePath = filePath;
            this.relativePath = relativePath;
            this.appTime = appTime;
        }
    }

    public class SyntheticInfo{
        public AlbumItem mAlbumItem;

        public SyntheticInfo(AlbumItem albumItem){
            mAlbumItem = albumItem;
        }
    }



}
