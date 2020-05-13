package com.example.tenth_work;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialButton bind;

    private MaterialButton play;

    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection serviceConnection;
    private MediaPlayer mediaPlayer;
    private ImageView album;

    private Handler handler = new Handler();
    private SeekBar seekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                downloadBinder = (DownloadService.DownloadBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.FOREGROUND_SERVICE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.FOREGROUND_SERVICE},1);
        }
        initView();
    }

    private void initView() {

        bind = findViewById(R.id.bind);
        play = findViewById(R.id.play);
        album = findViewById(R.id.album);
        seekBar = findViewById(R.id.progress_bar);

        Intent downloadService = new Intent(this, DownloadService.class);
        startService(downloadService);
        bindService(downloadService,serviceConnection,BIND_AUTO_CREATE);
        bind.setOnClickListener(this);
        play.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(downloadBinder == null){
            return;
        }
        switch (v.getId()){
            case R.id.bind:
                String url = "http://jk01.top:8080/media/ごはんを食べよう.mp3";
                downloadBinder.startDownload(url);
                break;
            case R.id.play:
                startActivity(new Intent(getApplicationContext(),playMusic.class));
                finish();
                }
        }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
