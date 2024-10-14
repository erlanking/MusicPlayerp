package com.example.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private int[] tracks = {R.raw.sheeran, R.raw.biber, R.raw.jacson, R.raw.weekand};
    private int[] albumCovers = {R.drawable.eddsheran, R.drawable.biba, R.drawable.jack, R.drawable.week};
    private String[] trackNames = {"Perfect", "Baby", "Billie Jean", "Blinding Lights"};
    private String[] trackArtists = {"Ed Sheeran", "Justin Bieber", "Michael Jackson", "The Weeknd"};
    private int currentTrackIndex = 0;

    private ImageButton btnPlayPause;
    private TextView trackTitle;
    private TextView trackArtist; // Добавлено для имени исполнителя
    private ImageView albumCover;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlayPause = findViewById(R.id.btnPlayPause);
        trackTitle = findViewById(R.id.trackTitle);
        trackArtist = findViewById(R.id.trackArtist);
        albumCover = findViewById(R.id.albumCover);
        seekBar = findViewById(R.id.seekBar);

        mediaPlayer = MediaPlayer.create(this, tracks[currentTrackIndex]);
        seekBar.setMax(mediaPlayer.getDuration());
        updateTrackInfo();

        btnPlayPause.setImageResource(R.drawable.play_arrow_24); // Значок "Play"
        seekBar.setProgress(0);


        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 1000);
                }
            }
        };

        // Управляем перемоткой
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.postDelayed(updateSeekBar, 0);
            }
        });

        // Логика для кнопки Play/Pause
        btnPlayPause.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlayPause.setImageResource(R.drawable.play_arrow_24); // изображение "Play"
                handler.removeCallbacks(updateSeekBar);
            } else {
                mediaPlayer.start();
                btnPlayPause.setImageResource(R.drawable.pause_24); //  изображение "Pause"
                handler.postDelayed(updateSeekBar, 0);
            }
        });

        // Кнопка вперед
        findViewById(R.id.btnNext).setOnClickListener(view -> changeTrack(1));

        // Кнопка назад
        findViewById(R.id.btnPrevious).setOnClickListener(view -> changeTrack(-1));
    }

    private void changeTrack(int direction) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        handler.removeCallbacks(updateSeekBar);

        currentTrackIndex = (currentTrackIndex + direction) % tracks.length;
        if (currentTrackIndex < 0) {
            currentTrackIndex += tracks.length;
        }

        mediaPlayer = MediaPlayer.create(this, tracks[currentTrackIndex]);
        seekBar.setMax(mediaPlayer.getDuration());
        updateTrackInfo();
        mediaPlayer.start();
        btnPlayPause.setImageResource(R.drawable.pause_24);
        handler.postDelayed(updateSeekBar, 0);
    }

    private void updateTrackInfo() {
        trackTitle.setText(trackNames[currentTrackIndex]); // Обновление названия трека
        albumCover.setImageResource(albumCovers[currentTrackIndex]); // Обновление обложки
        trackArtist.setText(trackArtists[currentTrackIndex]); // Обновление имени исполнителя
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            handler.removeCallbacksAndMessages(null);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
