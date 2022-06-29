package com.example.haniemfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Random;

import android.speech.tts.TextToSpeech;

// stt에 필요한 ...
import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    // tts
    TextToSpeech tts;
    TextView tts_TextBtn; // 클릭 시 영어 문장을 tts로 들려준다

    // stt
    Intent intent;
    final SpeechRecognizer[] mRecognizer = new SpeechRecognizer[1];
    Button stt_btn;
    TextView stt_text;
    final int PERMISSION = 1;

    // random script
    String eng_text; // tts 로 읽을 부분
    String kor_text;

    TextView com_text; // 정확도 등 화면에 출력 위함
    
    // random script 설정
    String key = Integer.toString(new Random().nextInt(11756) + 1);  // DB의 키 (1~11756) 문장 개수가 늘어나면 계속 수정해야 하는 번거로움

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(key); // key에 해당하는 DB의 참조 값

    // tts 객체 정리
    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // random script
        myRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Script data = snapshot.getValue(Script.class);
                eng_text = data.getEnglish();
                kor_text = data.getKorean();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // tts
        // tts 객체 초기화
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH); // English 로 음성변환 언어 지정
                }
            }
        });
        // tts 버튼 클릭 리스너 설정
        tts_TextBtn = findViewById(R.id.tts_textBtn);
        tts_TextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = eng_text;

                tts.setPitch(1.0f); // 음성 높낮이
                tts.setSpeechRate(1.0f); // 음성 빠르기
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }
        stt_text = (TextView)findViewById(R.id.stt_text);
        stt_btn = (Button) findViewById(R.id.stt_btn);

        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");

        stt_btn.setOnClickListener(v ->{
            mRecognizer[0] =SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer[0].setRecognitionListener(listener);
            mRecognizer[0].startListening(intent);
        });
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i = 0; i < matches.size() ; i++){
                stt_text.setText(matches.get(i));
            }

            moveNext(); // 결과 화면 이동
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}

    };

    public void moveNext() { // 사용자가 말한 문장 = 정답 문장 이면 결과 화면으로 이동하는 메소드
        String user_text = stt_text.getText().toString(); // 사용자가 말한 문장
        // 비교할 정답 문장은 eng_text

        com_text = findViewById(R.id.comment_text);

        StringTokenizer user_st = new StringTokenizer(user_text, " "); // 단어가 순서대로 일치하는지 검사하기 위해 StringTokenizer 사용
        StringTokenizer eng_st = new StringTokenizer(eng_text, " .,!?");

        int denominator = eng_st.countTokens(); // 정확도 분석에 필요
        double numerator = 0.0; // 정확도 분석에 필요

        while ((user_st.hasMoreTokens()) && (eng_st.hasMoreTokens())) {
            if (user_st.nextToken().equalsIgnoreCase(eng_st.nextToken())) {
                numerator++;
            }
        }
        if (user_st.hasMoreTokens()) {
            numerator -= (double)user_st.countTokens() / (denominator + user_st.countTokens());
        }

        if (numerator < 0) {
            numerator = 0;
        }

        double percentage = numerator / denominator * 100;

        com_text.setText("정확도: " + percentage + "%");

        if (percentage > 80) {
            Intent resultPageIntent = new Intent(MainActivity.this, ResultActivity.class);
            resultPageIntent.putExtra("eng", eng_text);
            resultPageIntent.putExtra("kor", kor_text);
            startActivity(resultPageIntent);
        }

        /* 정확도 분석x, 100% 여야 통과
        while ((user_st.hasMoreTokens()) && (eng_st.hasMoreTokens())) {
            if (user_st.countTokens() != eng_st.countTokens()) {
                com_text.setText("다시 말하세요.");
                return; // 두 문장의 형태가(단어 개수) 다를 시 메소드 종료
            }
            if (!(user_st.nextToken().equalsIgnoreCase(eng_st.nextToken()))) {
                com_text.setText("다시 말하세요.");
                return; // 단어 및 순서가 일치하지 않을 시 메소드 종료
            }
        }
        // while문을 수행했다면 사용자는 올바르게 스피킹한 것이므로 다음 화면으로 전환
        Intent resultPageIntent = new Intent(MainActivity.this, ResultActivity.class);
        resultPageIntent.putExtra("eng", eng_text);
        resultPageIntent.putExtra("kor", kor_text);
        startActivity(resultPageIntent);
        */
    }
}