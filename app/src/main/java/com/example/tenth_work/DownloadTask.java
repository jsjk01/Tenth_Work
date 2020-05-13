package com.example.tenth_work;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String,Integer,Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private DownloadListener downloadListener;
    private boolean isCanceled=false;
    private boolean isPaused=false;
    private int lastProgress;
    private long contentLength;
    private InputStream is;
    private RandomAccessFile accessFile;
    private File file;
    private int progress;


    public DownloadTask(DownloadListener listener){
        downloadListener = listener;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        is = null;
        accessFile = null;
        this.file = null;
        File file = this.file;
        try {
            long downloadLength = 0;
            String downloadUrl = strings[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/'));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

            file = new File(directory + fileName);
            if(file.exists()){
                downloadLength = file.length();
            }
            try {
                contentLength = getContentLength(downloadUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(contentLength == 0){
                return TYPE_FAILED;
            }else if(contentLength == downloadLength){
                return TYPE_SUCCESS;
            }
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().addHeader("RANGE", "bytes=" + downloadLength + "-").url(downloadUrl).build();
            Response response = okHttpClient.newCall(request).execute();
            if(response != null){
                is = response.body().byteStream();
                accessFile = new RandomAccessFile(file, "rw");
                accessFile.seek(downloadLength);
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1){
                    if(isCanceled){
                        return TYPE_CANCELED;
                    }else if(isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total += len;
                        accessFile.write(b,0,len);
                        progress = (int) ((total + downloadLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(is != null){
                    is.close();
                }else if(accessFile != null){
                    accessFile.close();
                }else if(isCanceled && file != null){
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];//获取下载进度
        if (progress > lastProgress){//如果进度发生了变化，那么更新进度显示
            downloadListener.onProgress(progress);
            lastProgress=progress;
        }
    }

    @Override//根据传入的参数进行回调
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case TYPE_SUCCESS:
                downloadListener.onSuccess();
                break;
            case TYPE_FAILED:
                downloadListener.onFailed();
                break;
            case TYPE_PAUSED:
                downloadListener.onPaused();
                break;
            case TYPE_CANCELED:
                downloadListener.onCanceled();
            default:
                break;
        };
    }

    public void pauseDownload(){
        isPaused=true;
    }
    public void cancelDownload(){
        isCanceled=true;
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = okHttpClient.newCall(request).execute();
        if(response != null && response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }
}
