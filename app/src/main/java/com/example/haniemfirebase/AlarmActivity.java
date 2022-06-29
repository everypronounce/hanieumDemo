package com.example.haniemfirebase;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class AlarmActivity extends AppCompatActivity {

    Button button;
    Calendar calendar;
    TextView timeText;
    MediaPlayer mediaPlayer;
    boolean flag = true;

    // 현재 시간 가져오기
    public String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd EE HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));
        return sdf.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        calendar = Calendar.getInstance();
        button = findViewById(R.id.btn);
        timeText = findViewById(R.id.time);

        // 화면 위에 Alarm Activity 띄우기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        //알람음
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.lovemeloveme);
        mediaPlayer.setLooping(true); //무한반복
        mediaPlayer.start();

        //현재 시간 띄우기
        timeText.setText(getTime());

        // START 버튼 눌러서 영어 학습 화면 넘어가기
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent learningIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(learningIntent);
                mediaPlayer.stop();
            }
        });

    }
}
