package team.one.lwe.crash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Locale;

import team.one.lwe.R;

public class CrashActivity extends Activity {

    private TextView prompt;
    private final CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            prompt.setText(String.format(Locale.getDefault(), "Warning!\nnuclear missile will be launched in %1s second", millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            ActivityCollector.finishAll();
            System.exit(1);
        }
    };
    private String exceptionOfCrash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        initIntent();
        initView();
        countDownTimer.start();
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent == null) return;
        exceptionOfCrash = intent.getStringExtra("exceptionOfCrash");
    }

    private void initView() {
        prompt = findViewById(R.id.prompt);
        TextView crashMessage = findViewById(R.id.crashMessage);
        crashMessage.setText(exceptionOfCrash);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
