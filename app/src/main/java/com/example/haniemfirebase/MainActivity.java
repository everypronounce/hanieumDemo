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

    // random script
    TextView eng_text; // tts 로 읽을 부분
    TextView kor_text;

    // tts
    TextToSpeech tts;
    Button btn_tts;

    // stt
    Intent intent;
    final SpeechRecognizer[] mRecognizer = new SpeechRecognizer[1];
    Button btn_stt;
    TextView stt_text;
    final int PERMISSION = 1;

    // random script 설정
    String key = Integer.toString(new Random().nextInt(5) + 1);  // DB의 키 (1~5)

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
        eng_text = findViewById(R.id.eng_text);
        kor_text = findViewById(R.id.kor_text);

        myRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Script data = snapshot.getValue(Script.class);
                eng_text.setText(data.getEnglish());
                kor_text.setText(data.getKorean());
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
        btn_tts = findViewById(R.id.btnTTS);
        btn_tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = eng_text.getText().toString();

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
        btn_stt = (Button) findViewById(R.id.btnSTT);

        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");

        btn_stt.setOnClickListener(v ->{
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
        String userText = stt_text.getText().toString(); // 사용자가 말한 문장
        String compText = eng_text.getText().toString(); // 비교할 정답 문장

        StringTokenizer userSt = new StringTokenizer(userText, " "); // 단어가 순서대로 일치하는지 검사하기 위해 StringTokenizer 사용
        StringTokenizer compSt = new StringTokenizer(compText, " .,!?");

        while ((userSt.hasMoreTokens()) && (compSt.hasMoreTokens())) {
            if (!(userSt.nextToken().equalsIgnoreCase(compSt.nextToken()))) {
                return; // 일치하지 않을 시 메소드 종료
            }
        }

        // while문을 수행했다면 사용자는 올바르게 스피킹한 것이므로 다음 화면으로 전환
        Intent resultPage = new Intent(MainActivity.this, ResultActivity.class);
        startActivity(resultPage);
    }
}