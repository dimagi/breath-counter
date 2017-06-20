package org.commcare.respiratory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.Locale;

import static android.R.drawable.ic_lock_silent_mode_off;

public class BreathCounterActivity extends Activity implements OnInitListener {
    private Integer mAnswer;
    private final int mAnswerFontsize = 18;
    private Button mBreathButton;
    private TextView mBreathCountView;
    private int mBreaths;
    private TextView mBreathsPerMinView;
    private final int mButtonFontSize = 25;
    private Counter mHandler;
    private ImageView mQuestionButton;
    private Button mRecordAnswerButton;
    private Button mResetButton1;
    private Button mResetButton2;
    private Double mSeconds;
    private final int mTextFontSize = 30;
    private TextView mTimeView;
    private final int paddingInt = 0;
    private boolean playAudio = true;
    private LinearLayout recordContainer;
    private final int reportButtonInt = 3;
    private LinearLayout reportContainer;
    private final int textPaddingInt = 0;
    private TextToSpeech tts;

    class C00012 implements OnClickListener {
        C00012() {
        }

        public void onClick(View arg0) {
            AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0.3f);
            AlphaAnimation alphaUp = new AlphaAnimation(0.3f, 1.0f);
            alphaDown.setDuration(1000);
            alphaUp.setDuration(500);
            alphaDown.setFillAfter(true);
            alphaUp.setFillAfter(true);
            BreathCounterActivity.this.setToRecordScreen();
            BreathCounterActivity.this.clearAnswer();
        }
    }

    class C00023 implements OnClickListener {
        C00023() {
        }

        public void onClick(View arg0) {
            AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0.3f);
            AlphaAnimation alphaUp = new AlphaAnimation(0.3f, 1.0f);
            alphaDown.setDuration(1000);
            alphaUp.setDuration(500);
            alphaDown.setFillAfter(true);
            alphaUp.setFillAfter(true);
            BreathCounterActivity.this.setToRecordScreen();
            BreathCounterActivity.this.clearAnswer();
        }
    }

    class C00034 implements OnClickListener {
        C00034() {
        }

        public void onClick(View arg0) {
            BreathCounterActivity.this.sendAnswerBackToApp();
        }
    }

    class C00045 implements OnClickListener {
        C00045() {
        }

        public void onClick(View arg0) {
            MediaPlayer.create(BreathCounterActivity.this, R.raw.beep).start();
        }
    }

    private class Counter extends Handler {
        boolean stop;

        private Counter() {
            this.stop = true;
        }

        public void handleMessage(Message msg) {
            BreathCounterActivity.this.updateSeconds();
        }

        public void count(long delayMillis) {
            if (!this.stop) {
                removeMessages(0);
                sendMessageDelayed(obtainMessage(0), delayMillis);
            }
        }

        public void stop() {
            this.stop = true;
        }

        public void start() {
            this.stop = false;
            BreathCounterActivity.this.updateSeconds();
        }

        public boolean running() {
            return !this.stop;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            String playAudioString = mBundle.getString("play-audio");
            if (playAudioString != null && playAudioString.equals("no")) {
                this.playAudio = false;
            }
        }
        this.recordContainer = new LinearLayout(this);
        this.reportContainer = new LinearLayout(this);
        LayoutParams params = new LayoutParams(-1, -1);
        this.recordContainer.setLayoutParams(params);
        this.reportContainer.setLayoutParams(params);
        this.recordContainer.setWeightSum(1.0f);
        this.reportContainer.setWeightSum(1.0f);
        this.recordContainer.setOrientation(LinearLayout.VERTICAL);
        this.reportContainer.setOrientation(LinearLayout.VERTICAL);
        setContentView(this.recordContainer);
        LayoutParams textParams = new LayoutParams(-1, 0);
        textParams.weight = 0.08f;
        textParams.setMargins(0, 0, 0, 0);
        LayoutParams quarterParams = new LayoutParams(-1, 0);
        quarterParams.weight = 0.25f;
        quarterParams.setMargins(0, 0, 0, 0);
        LayoutParams halfParams = new LayoutParams(-1, 0);
        halfParams.weight = 0.6f;
        halfParams.setMargins(0, 0, 0, 0);
        LayoutParams thirdParams = new LayoutParams(-1, 0);
        thirdParams.weight = 0.33f;
        thirdParams.setMargins(0, 0, 0, 0);
        LayoutParams tenthParams = new LayoutParams(-1, 0);
        tenthParams.weight = 0.1f;
        tenthParams.setMargins(0, 0, 0, 0);
        LayoutParams fourthParams = new LayoutParams(-1, 0);
        fourthParams.weight = 0.3f;
        fourthParams.setMargins(0, 0, 0, 0);
        LayoutParams resetParams = new LayoutParams(-1, 0);
        resetParams.weight = 0.15f;
        resetParams.setMargins(0, 0, 0, 0);
        LayoutParams secondParams = new LayoutParams(-1, 0);
        secondParams.weight = 0.2f;
        secondParams.setMargins(3, 3, 3, 3);
        this.mSeconds = Double.valueOf(0.0d);
        this.mBreaths = 0;
        this.tts = new TextToSpeech(this, this);
        this.mBreathCountView = new TextView(this);
        this.mTimeView = new TextView(this);
        this.mBreathsPerMinView = new TextView(this);
        this.mBreathButton = new Button(this);
        this.mResetButton1 = new Button(this);
        this.mResetButton2 = new Button(this);
        this.mRecordAnswerButton = new Button(this);
        this.mQuestionButton = new ImageView(this);
        this.mBreathCountView.setTextSize(1, 18.0f);
        this.mTimeView.setTextSize(1, 18.0f);
        this.mBreathsPerMinView.setTextSize(1, 30.0f);
        this.mBreathButton.setLayoutParams(halfParams);
        this.mTimeView.setLayoutParams(textParams);
        this.mBreathCountView.setLayoutParams(textParams);
        this.mResetButton1.setLayoutParams(resetParams);
        this.mQuestionButton.setLayoutParams(tenthParams);
        this.mRecordAnswerButton.setLayoutParams(secondParams);
        this.mBreathsPerMinView.setLayoutParams(halfParams);
        this.mResetButton2.setLayoutParams(secondParams);
        this.mBreathsPerMinView.setGravity(17);
        this.mBreathButton.setText(getResources().getString(R.string.start_button));
        this.mResetButton1.setText(getResources().getString(R.string.reset_button));
        this.mResetButton2.setText(getResources().getString(R.string.reset_button));
        this.mRecordAnswerButton.setText(getResources().getString(R.string.record_button));
        this.mQuestionButton.setImageResource(ic_lock_silent_mode_off);
        this.mRecordAnswerButton.setTextSize(25.0f);
        this.mBreathButton.setTextSize(25.0f);
        this.mResetButton1.setTextSize(25.0f);
        this.mResetButton2.setTextSize(25.0f);
        this.mBreathButton.setPadding(0, 0, 0, 0);
        this.mResetButton1.setPadding(0, 0, 0, 0);
        this.mResetButton2.setPadding(3, 3, 3, 3);
        this.mRecordAnswerButton.setPadding(3, 3, 3, 3);
        this.mBreathCountView.setPadding(0, 0, 0, 0);
        this.mTimeView.setPadding(0, 0, 0, 0);
        this.mHandler = new Counter();
        if (this.mAnswer != null) {
            disableBreathButton();
        }
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        this.mBreathButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0.3f);
                AlphaAnimation alphaUp = new AlphaAnimation(0.3f, 1.0f);
                alphaDown.setDuration(1000);
                alphaUp.setDuration(500);
                alphaDown.setFillAfter(true);
                alphaUp.setFillAfter(true);
                BreathCounterActivity.this.mBreathButton.startAnimation(alphaUp);
                BreathCounterActivity.this.mBreathButton.setText(BreathCounterActivity.this.getResources().getString(R.string.breath_button));
                vibrator.vibrate(75);
                BreathCounterActivity breathCounterActivity = BreathCounterActivity.this;
                breathCounterActivity.mBreaths = breathCounterActivity.mBreaths + 1;
                BreathCounterActivity.this.setBreaths(BreathCounterActivity.this.mBreaths);
                if (BreathCounterActivity.this.mAnswer == null && !BreathCounterActivity.this.mHandler.running()) {
                    BreathCounterActivity.this.mHandler.start();
                    BreathCounterActivity.this.enableResetButton();
                }
            }
        });
        if (!this.mHandler.running()) {
            disableResetButton();
        }
        this.mResetButton1.setOnClickListener(new C00012());
        if (!this.mHandler.running()) {
            disableResetButton();
        }
        this.mResetButton2.setOnClickListener(new C00023());
        if (this.mAnswer == null || this.mAnswer.intValue() == -1) {
            disableReturnValueButton();
        }
        this.mRecordAnswerButton.setOnClickListener(new C00034());
        this.mQuestionButton.setOnClickListener(new C00045());
        this.recordContainer.addView(this.mBreathButton);
        this.recordContainer.addView(this.mBreathCountView);
        this.recordContainer.addView(this.mTimeView);
        this.recordContainer.addView(this.mResetButton1);
        this.recordContainer.addView(this.mQuestionButton);
        this.reportContainer.addView(this.mBreathsPerMinView);
        this.reportContainer.addView(this.mRecordAnswerButton);
        this.reportContainer.addView(this.mResetButton2);
        registerForContextMenu(this.mBreathCountView);
        registerForContextMenu(this.mTimeView);
        registerForContextMenu(this.mBreathButton);
        registerForContextMenu(this.mBreathsPerMinView);
        registerForContextMenu(this.mResetButton1);
        registerForContextMenu(this.mResetButton2);
        registerForContextMenu(this.mRecordAnswerButton);
        registerForContextMenu(this.mQuestionButton);
        clearAnswer();
    }

    public void onDestroy() {
        if (this.tts != null) {
            this.tts.stop();
            this.tts.shutdown();
        }
        super.onDestroy();
    }

    public void onInit(int status) {
        if (status == 0) {
            int result = this.tts.setLanguage(new Locale("en"));
            if (result == -1 || result == -2) {
                System.out.println("TTS This Language is not supported");
                return;
            }
            return;
        }
        System.out.println("Initilization Failed!");
    }

    private void speakOut(String speakString) {
        this.tts.speak(speakString, 0, null);
    }

    private void enableBreathButton() {
        this.mBreathButton.setBackgroundColor(Color.rgb(153, 255, 153));
        this.mBreathButton.setTextColor(Color.BLACK);
        this.mBreathButton.setEnabled(true);
    }

    private void disableBreathButton() {
        this.mBreathButton.setTextColor(Color.DKGRAY);
        this.mBreathButton.setEnabled(false);
    }

    private void enableResetButton() {
        this.mResetButton1.setBackgroundColor(Color.rgb(255, 102, 102));
        this.mResetButton1.setEnabled(true);
        this.mResetButton2.setBackgroundColor(Color.rgb(255, 102, 102));
        this.mResetButton2.setEnabled(true);
    }

    private void disableResetButton() {
        this.mResetButton1.setBackgroundColor(Color.DKGRAY);
        this.mResetButton1.setEnabled(false);
        this.mResetButton2.setBackgroundColor(Color.DKGRAY);
        this.mResetButton2.setEnabled(false);
    }

    private void enableReturnValueButton() {
        this.mRecordAnswerButton.setBackgroundColor(Color.rgb(245, 246, 206));
        this.mRecordAnswerButton.setEnabled(true);
    }

    private void disableReturnValueButton() {
        this.mRecordAnswerButton.setBackgroundColor(Color.DKGRAY);
        this.mRecordAnswerButton.setEnabled(false);
    }

    private void updateSeconds() {
        if (this.mHandler.running()) {
            if (this.mSeconds.doubleValue() <= 60.0d) {
                this.mSeconds = Double.valueOf(this.mSeconds.doubleValue() + 0.1d);
                setSeconds(this.mSeconds.doubleValue());
                this.mHandler.count(100);
            }
            if (this.mSeconds.doubleValue() > 59.9d) {
                this.mHandler.stop();
                MediaPlayer.create(this, R.raw.beep).start();
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
                this.mAnswer = Integer.valueOf(this.mBreaths);
                setSeconds(60.0d);
                setAnswer(this.mAnswer.intValue());
                setToReportScreen();
            }
        }
    }

    private void setSeconds(double count) {
        this.mSeconds = Double.valueOf(count);
        this.mTimeView.setText(new StringBuilder(String.valueOf(getResources().getString(R.string.seconds_label))).append(new DecimalFormat("0.0").format(this.mSeconds)).toString());
    }

    private void setBreaths(int count) {
        this.mBreaths = count;
        this.mBreathCountView.setText(new StringBuilder(String.valueOf(getResources().getString(R.string.breaths_label))).append(this.mBreaths).toString());
    }

    private void setAnswer(int count) {
        if (count == -1) {
            this.mBreathsPerMinView.setText(getResources().getString(R.string.bpm_label));
            this.mAnswer = null;
            enableBreathButton();
            return;
        }
        this.mBreathsPerMinView.setText(new StringBuilder(String.valueOf(getResources().getString(R.string.bpm_label))).append(count).toString());
        this.mAnswer = Integer.valueOf(count);
        disableBreathButton();
        enableReturnValueButton();
    }

    public void setToRecordScreen() {
        setContentView(this.recordContainer);
    }

    public void setToReportScreen() {
        speakResult();
        setContentView(this.reportContainer);
    }

    public void speakResult() {
        if (this.playAudio) {
            speakOut(getResources().getString(R.string.speak_result, new Object[]{this.mAnswer.toString()}));
        }
    }

    private void clearAnswer() {
        this.mHandler.stop();
        disableResetButton();
        disableReturnValueButton();
        setSeconds(0.0d);
        setBreaths(0);
        setAnswer(-1);
    }

    private void sendAnswerBackToApp() {
        Intent intent = new Intent();
        intent.putExtra("odk_intent_data", String.valueOf(this.mAnswer));
        setResult(-1, intent);
        finish();
    }
}
