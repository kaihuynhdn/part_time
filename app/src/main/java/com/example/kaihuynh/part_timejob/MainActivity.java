package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private SeekBar mSeekBar;
    private ProgressBar mProgressBar;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addComponents();
        addEvents();
    }

    private void addEvents() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mProgressBar.getProgress() < 100) {
                    android.os.SystemClock.sleep(30);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mSeekBar.setProgress(mSeekBar.getProgress() + 1);
                            mProgressBar.setProgress(mProgressBar.getProgress() + 1);
                        }
                    });
                }

                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }).start();
    }

    private void addComponents() {
        mSeekBar = findViewById(R.id.seekBar);
        mProgressBar = findViewById(R.id.progressBar);
        mHandler = new Handler();
    }
}
