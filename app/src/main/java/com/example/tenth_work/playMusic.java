package com.example.tenth_work;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.lauzy.freedom.library.Lrc;
import com.lauzy.freedom.library.LrcHelper;
import com.lauzy.freedom.library.LrcView;

import java.io.IOException;
import java.util.List;


public class playMusic extends AppCompatActivity {

    private SeekBar seekBar;
    private ImageView btnPlayPause;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler = new Handler();
    private Uri path;
    private ImageView album;
    private LrcView lrcView;
    private ImageView together;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        seekBar = findViewById(R.id.progress_bar);
        btnPlayPause = findViewById(R.id.playOrPause);
        album = findViewById(R.id.album);
        lrcView = findViewById(R.id.lrc_view);
        together = findViewById(R.id.together);

        List<Lrc> lrcs = LrcHelper.parseLrcFromAssets(this, "yiqichifanba_cn.lrc");
        lrcView.setLrcData(lrcs);
        lrcView.setOnPlayIndicatorLineListener(new LrcView.OnPlayIndicatorLineListener() {
            @Override
            public void onPlay(long time, String content) {
                mediaPlayer.seekTo((int) time);
            }
        });

        Glide.with(this).load("http://jk01.top:8080/media/yiqichifan.webp").into(album);
        path = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/ごはんを食べよう.mp3");
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(String.valueOf(path));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    seekBar.setProgress(0);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    seekBar.setProgress(0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 加载歌词文本


        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    Glide.with(playMusic.this).load(R.drawable.play).into(btnPlayPause);
                    mediaPlayer.start();
                    lrcView.resume();
                    handler.post(runnable);
                } else {
                    Glide.with(playMusic.this).load(R.drawable.pause).into(btnPlayPause);
                    mediaPlayer.pause();
                    lrcView.pause();
                    handler.removeCallbacks(runnable);
                }
            }
        });

        together.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(playMusic.this).load(R.drawable.fullheart).into(together);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(),EatTogether.class));
                    }
                },1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                long time = mediaPlayer.getCurrentPosition();
                lrcView.updateTime(time);
                seekBar.setProgress((int) time);
            }

            handler.postDelayed(this, 300);
        }
    };

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }
}
