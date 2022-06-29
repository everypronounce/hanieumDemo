package com.example.haniemfirebase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    TextView eng_TextView;
    TextView kor_TextView;

    Button exit_btn;
    Button restart_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        eng_TextView = findViewById(R.id.eng_text);
        kor_TextView = findViewById(R.id.kor_text);

        exit_btn = findViewById(R.id.exitApp_btn);
        restart_btn = findViewById(R.id.restart_btn);

        Intent resultIntent = getIntent();
        eng_TextView.setText(resultIntent.getStringExtra("eng"));
        kor_TextView.setText(resultIntent.getStringExtra("kor"));

        exit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                builder.setMessage("정말로 종료하시겠습니까?");
                builder.setTitle("종료 알림창")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("종료 알림창");
                alert.show();
            }
        });

        restart_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent speakingPageIntent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(speakingPageIntent);
            }
        });
    }

    @Override
    public void onBackPressed() { // 뒤로 가기 막기
        //super.onBackPressed();
    }
}