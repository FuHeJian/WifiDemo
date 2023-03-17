package com.example.wifidemo1.util;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.wifidemo1.oksocket.common.interfaces.utils.TextUtils;

import java.io.File;
import java.io.OutputStream;

public class FileHelper {
    private static final String TAG = "FileHelper";

    private Activity mContext;

    private static FileHelper mFileHelper;

    private static final String RELATIVE_PATH = Environment.DIRECTORY_PICTURES;

    private static final String ExternalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();


    public static FileHelper getInstance() {

        if (mFileHelper == null) {
            synchronized (FileHelper.class) {
                if (mFileHelper == null) {
                    mFileHelper = new FileHelper();
                }
            }
        }
        return mFileHelper;
    }

    public void onCreated(Activity context){
        mContext = context;
    }

    public void onDestroy(){
        mContext = null;
    }

    public Uri selectSingle() {
        Uri queryUri = null;
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Media.DISPLAY_NAME+"=?";
        String[] args = new String[]{"111(1).jpg"};
        String[] projection = new String[]{MediaStore.Images.Media._ID};
        Cursor cursor = null;
        Log.d(TAG, "selectSingle: mContext = "+mContext);
        if (mContext != null) {
            cursor = mContext.getContentResolver().query(external, projection, selection, args, null);
        }
        Log.d(TAG, "selectSingle: cursor = "+cursor);
        if (cursor != null && cursor.moveToFirst()) {
            queryUri = ContentUris.withAppendedId(external, cursor.getLong(0));
            Log.d(TAG, "selectSingle 查询成功，Uri路径 : "+queryUri);
            cursor.close();
        }else{
            Log.d(TAG, "selectSingle 查询失败");
        }

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            OutputStream aaa = getPictureFileOutputStreamAboveQ(mContext,"Polaris/sd/Lapse","111.jpg");
//            Log.d(TAG, "selectSingle: aaa = "+aaa);
//        }
        return queryUri;
    }

    public void queryImages() {
        // 先拿到图片数据表的uri
        Uri tableUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // 需要获取数据表中的哪几列信息
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.RELATIVE_PATH,
//                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.MIME_TYPE};
//        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME +"= ? and "+MediaStore.Images.Media.DISPLAY_NAME+"= ?";
//        String selection = MediaStore.Images.Media.DATA+"= ?";
        String selection = MediaStore.Images.Media.RELATIVE_PATH+"= ?";
//        String[] args = new String[] {"Lapse","111.jpg"};
//        String[] args = new String[] {"/storage/emulated/0/Polaris/sd/Lapse/class_11/202112301639SP_0002.jpg"};
        String[] args = new String[] {"Pictures/Polaris/sd/Lapse/class_11/"};
//        String[] args = new String[] {"/storage/emulated/0/Pictures/Polaris/sd/Lapse/111(1).jpg"};
        String order = MediaStore.Files.FileColumns._ID+"DESC";
        String aaa = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d(TAG, "queryImages: aaa = "+aaa);
//        mContext.getExternalFilesDir();

        Cursor cursor = mContext.getContentResolver().query(tableUri,projection,selection,args, null);

        if (cursor != null) {
            // 获取id字段是第几列，该方法最好在循环之前做好
            int idIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
            // 获取data字段是第几列，该方法最好在循环之前做好
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            int relativeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIndex);
                // 获取到每张图片的uri
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id);
                // 获取到每张图片的绝对路径
                String path = cursor.getString(dataIndex);

                String relative = cursor.getString(relativeIndex);
                // 做保存工作
                // todo
                Log.d(TAG, "queryImages: path = "+path);
                Log.d(TAG, "queryImages: relative = "+relative);
            }

            cursor.close();
        }
    }

    public boolean isExists(String filePath){
        if (TextUtils.isEmpty(filePath) || mContext == null){
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            String path = ExternalStorageDirectory + File.separator + filePath;
            File file = new File(path);
            return file.exists();
        }else {
            Uri tableUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.RELATIVE_PATH
            };
            String selection = MediaStore.Images.Media.RELATIVE_PATH+"= ?";
            String[] args = new String[] {filePath};
            ContentResolver contentResolver = mContext.getContentResolver();
            if (contentResolver == null) {
                Log.e(TAG, "isExists: contentResolver = "+contentResolver);
                return false;
            }
            Cursor cursor = contentResolver.query(tableUri,projection,selection,args, null);
            Uri imageUri = null;
            if (cursor != null) {
                int idIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idIndex);
                    // 获取到每张图片的uri
                    imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id);
                }
                cursor.close();
            }
            return imageUri != null;
        }
    }

    public void deleted(String filePath){
//        Uri tableUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        String selection = MediaStore.Images.Media.DATA+"= ?";
//        String[] args = new String[] {"/storage/emulated/0/Polaris/sd/Lapse/class_11/202112301639SP_0002.jpg"};
//
//        ContentResolver resolve = mContext.getContentResolver();
//
//        try {
//            int result = resolve.delete(tableUri,selection,args);
//            Log.d(TAG, "deleted: result = "+result);
//        }catch (Exception e){
//            e.printStackTrace();
//            Log.e(TAG, "deleted: "+e.getMessage());
//        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return;
        }

        Uri tableUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE};
        String selection = MediaStore.Images.Media.DATA+"= ?";
        String[] args = new String[] {"/storage/emulated/0/Pictures/Polaris/sd/Lapse/class_11/202112301639SP_0002.jpg"};
        ContentResolver resolve = mContext.getContentResolver();
        Cursor cursor = resolve.query(tableUri,projection,selection,args, null);
        Uri imageUri = null;
        if (cursor != null) {
            // 获取id字段是第几列，该方法最好在循环之前做好
            int idIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
            // 获取data字段是第几列，该方法最好在循环之前做好
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIndex);
                // 获取到每张图片的uri
                imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id);
                // 获取到每张图片的绝对路径
                String path = cursor.getString(dataIndex);
                // 做保存工作
                // todo
                Log.d(TAG, "queryImages: path = "+path);
            }

            cursor.close();
        }

        if (imageUri != null) {
            try {
                int deleteResult = resolve.delete(imageUri,null,null);
                Log.d(TAG, "deleted: deleteResult = "+deleteResult);
            }catch (Exception e){
                if (e instanceof RecoverableSecurityException) {
                    try {
                        mContext.startIntentSenderForResult(((RecoverableSecurityException) e).getUserAction().getActionIntent().getIntentSender(),11,null,0,0,0);
                    } catch (IntentSender.SendIntentException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public OutputStream getDownLoadFileOutputStreamAboveQ(Context context, String yourPath, String fileName) {
        return getPublicFileOutputStreamAboveQ(context,MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                Environment.DIRECTORY_DOWNLOADS,
                yourPath,
                fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public OutputStream getDocumentFileOutputStreamAboveQ(Context context, String yourPath, String fileName) {
        return getPublicFileOutputStreamAboveQ(context,MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                Environment.DIRECTORY_DOCUMENTS,
                yourPath,
                fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public OutputStream getDCIMFileOutputStreamAboveQ(Context context, String yourPath, String fileName) {
        return getPublicFileOutputStreamAboveQ(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                Environment.DIRECTORY_DCIM,
                yourPath,
                fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public OutputStream getPictureFileOutputStreamAboveQ(Context context, String yourPath, String fileName) {
        return getPublicFileOutputStreamAboveQ(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                Environment.DIRECTORY_PICTURES,
                yourPath,
                fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public OutputStream getPublicFileOutputStreamAboveQ(Context context,Uri publicUri, String publicFolder, String yourPath, String fileName) {
        String insertPath = publicFolder + File.separator + yourPath;
        ContentValues fileInfo = new ContentValues();
        fileInfo.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
        // fileInfo.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain");
        fileInfo.put(MediaStore.Files.FileColumns.RELATIVE_PATH, insertPath);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(publicUri, fileInfo);
        try {
            // assert uri != null;
            return resolver.openOutputStream(uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
