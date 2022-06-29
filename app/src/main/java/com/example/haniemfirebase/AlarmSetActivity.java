package com.example.haniemfirebase;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmSetActivity extends AppCompatActivity {

    private Button save;
    private TimePicker timePicker;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        timePicker=(TimePicker)findViewById(R.id.time_picker);
        save=(Button)findViewById(R.id.save);

        save.setOnClickListener(v->{

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int hour=timePicker.getHour();
            int minute=timePicker.getMinute();
            calendar.set(Calendar.HOUR_OF_DAY,hour);
            calendar.set(Calendar.MINUTE,minute);
            calendar.set(Calendar.SECOND, 0);

            // 알람 시간이 현재시간보다 빠를 때 하루 뒤로 맞춤
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
            alarmIntent.setAction(AlarmReceiver.ACTION_RESTART_SERVICE);
            PendingIntent alarmRingIntent = PendingIntent.getBroadcast(AlarmSetActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmRingIntent);

            Toast.makeText(AlarmSetActivity.this,"알람이 저장되었습니다.",Toast.LENGTH_LONG).show();
        });
    }
}