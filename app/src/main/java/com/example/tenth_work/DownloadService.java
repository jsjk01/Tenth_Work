package com.example.tenth_work;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;

public class DownloadService extends Service {

    private String downloadUrl;
    //创建一个DownloadListener并实现其中的方法


    private DownloadListener downloadListener = new DownloadListener() {

        @Override//以通知的方式显示进度条
        public void onProgress(int progress) {
            //使用getNotificationManager函数构建一个用于显示下载进度的通知
            //使用notify去触发这个通知
//            Toast.makeText(getApplicationContext(),progress,Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onSuccess() {
            downloadTask=null;
            //关闭前台服务通知
            //下面自己写了一个通知用于通知下载成功
            Toast.makeText(DownloadService.this,"下载成功",Toast.LENGTH_LONG).show();
            String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        }

        @Override
        public void onFailed() {
            downloadTask=null;
            //关闭前台服务通知
            Toast.makeText(DownloadService.this,"Download failed",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPaused() {
            downloadTask=null;
            Toast.makeText(DownloadService.this,"Download Paused",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCanceled() {
            downloadTask=null;
            //关闭前台服务通知
            Toast.makeText(DownloadService.this,"Download canceled",Toast.LENGTH_LONG).show();
        }
    };

    //用于使服务可以和活动通信
    private DownloadBinder mBinder=new DownloadBinder();
    private DownloadTask downloadTask;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
    //用于使服务可以和活动通信
    public class DownloadBinder extends Binder {

        //开始下载
        public void startDownload(String url){
            if(downloadTask==null){
                downloadUrl=url;
                //创建一个DownLoadTask，将上面的downloadListener传入
//                downLoadTask=new DownLoadTask(downloadListener);
                downloadTask = new DownloadTask(downloadListener);
                //使用execute开启下载
                downloadTask.execute(downloadUrl);
                //startForeground使服务成为一个前台服务以创建持续运行的通知
//                startForeground(1,getNotification("download...",0),1);
                Toast.makeText(DownloadService.this,"下载服务被调用，下载中...",Toast.LENGTH_LONG).show();
            }
        }
        //暂停下载
        public void pauseDownload(){
            if (downloadTask!=null){
                downloadTask.pauseDownload();
            }
        }
        //取消下载后需要将下载中的任务取消
        public void cancelDownload(){
            if(downloadTask!=null){
                downloadTask.cancelDownload();
            }else {
                if (downloadUrl!=null)
                {
                    //取消需要将文件删除并将通知关闭
                    String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file=new File(directory+fileName);
                    if(file.exists()){
                        file.delete();
                    }
                    Toast.makeText(DownloadService.this,"Canceled",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}