package com.oibsip.stopwatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.oibsip.unitconverter.MainActivity;
import com.oibsip.unitconverter.R;
import com.oibsip.stopwatch.adapter.LapAdapter;
import com.oibsip.stopwatch.engine.StopwatchEngine;
import com.oibsip.stopwatch.model.LapItem;

import java.util.ArrayList;

public class StopwatchActivity extends AppCompatActivity {

    private final StopwatchEngine engine = new StopwatchEngine();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable tickerRunnable;

    private TextView tvMainDigits;
    private TextView tvCentiseconds;
    private TextView tvStateBadge;
    private TextView tvLapCount;
    private TextView tvNoLaps;
    private ListView lvLaps;

    private MaterialButton btnStart;
    private MaterialButton btnPause;
    private MaterialButton btnReset;
    private MaterialButton btnLap;
    private MaterialButton btnSwitchToConverter;

    private LapAdapter lapAdapter;
    private final ArrayList<LapItem> lapList = new ArrayList<>();

    private static final String KEY_STATE = "stopwatch_state";
    private static final String KEY_ACCUMULATED = "stopwatch_accumulated";
    private static final String KEY_START_TIME = "stopwatch_start_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        initViews();
        setupLapList();
        setupButtons();
        setupTicker();

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            updateUiState();
        }
    }

    private void initViews() {
        tvMainDigits         = findViewById(R.id.tvMainDigits);
        tvCentiseconds       = findViewById(R.id.tvCentiseconds);
        tvStateBadge         = findViewById(R.id.tvStateBadge);
        tvLapCount           = findViewById(R.id.tvLapCount);
        tvNoLaps             = findViewById(R.id.tvNoLaps);
        lvLaps               = findViewById(R.id.lvLaps);

        btnStart             = findViewById(R.id.btnStart);
        btnPause             = findViewById(R.id.btnPause);
        btnReset             = findViewById(R.id.btnReset);
        btnLap               = findViewById(R.id.btnLap);
        btnSwitchToConverter = findViewById(R.id.btnSwitchToConverter);
    }

    private void setupLapList() {
        lapAdapter = new LapAdapter(this, lapList);
        lvLaps.setAdapter(lapAdapter);
    }

    private void setupButtons() {
        btnStart.setOnClickListener(v -> onStartClicked());
        btnPause.setOnClickListener(v -> onPauseClicked());
        btnReset.setOnClickListener(v -> onResetClicked());
        btnLap.setOnClickListener(v -> onLapClicked());

        if (btnSwitchToConverter != null) {
            btnSwitchToConverter.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
            });
        }
    }

    private void setupTicker() {
        tickerRunnable = new Runnable() {
            @Override
            public void run() {
                if (engine.isRunning()) {
                    long now = SystemClock.uptimeMillis();
                    long elapsed = engine.getElapsedTime(now);

                    tvMainDigits.setText(StopwatchEngine.formatMainDigits(elapsed));
                    tvCentiseconds.setText(StopwatchEngine.formatCentiseconds(elapsed));

                    handler.postDelayed(this, 30);
                }
            }
        };
    }

    private void onStartClicked() {
        long now = SystemClock.uptimeMillis();
        engine.start(now);
        handler.removeCallbacks(tickerRunnable);
        handler.post(tickerRunnable);
        updateUiState();
    }

    private void onPauseClicked() {
        long now = SystemClock.uptimeMillis();
        engine.pause(now);
        handler.removeCallbacks(tickerRunnable);

        long elapsed = engine.getElapsedTime(now);
        tvMainDigits.setText(StopwatchEngine.formatMainDigits(elapsed));
        tvCentiseconds.setText(StopwatchEngine.formatCentiseconds(elapsed));
        updateUiState();
    }

    private void onResetClicked() {
        engine.reset();
        handler.removeCallbacks(tickerRunnable);

        tvMainDigits.setText(R.string.stopwatch_initial_time);
        tvCentiseconds.setText(R.string.stopwatch_initial_ms);

        lapList.clear();
        lapAdapter.notifyDataSetChanged();
        updateUiState();
    }

    private void onLapClicked() {
        long now = SystemClock.uptimeMillis();
        LapItem lap = engine.recordLap(now);
        if (lap != null) {
            lapList.add(0, lap);
            lapAdapter.notifyDataSetChanged();
            updateLapUi();
        }
    }

    private void updateUiState() {
        StopwatchEngine.State state = engine.getState();

        switch (state) {
            case RUNNING:
                tvStateBadge.setText("RUNNING");
                tvStateBadge.setTextColor(getColor(R.color.stopwatch_start));
                setButton(btnStart, false, 0.4f, R.string.btn_start);
                setButton(btnPause, true, 1.0f, R.string.btn_pause);
                setButton(btnReset, false, 0.4f, R.string.btn_reset);
                setButton(btnLap, true, 1.0f, R.string.btn_lap);
                break;

            case PAUSED:
                tvStateBadge.setText("PAUSED");
                tvStateBadge.setTextColor(getColor(R.color.stopwatch_pause));
                setButton(btnStart, true, 1.0f, R.string.btn_resume);
                setButton(btnPause, false, 0.4f, R.string.btn_pause);
                setButton(btnReset, true, 1.0f, R.string.btn_reset);
                setButton(btnLap, false, 0.4f, R.string.btn_lap);
                break;

            case STOPPED:
            default:
                tvStateBadge.setText("READY");
                tvStateBadge.setTextColor(getColor(R.color.primary));
                setButton(btnStart, true, 1.0f, R.string.btn_start);
                setButton(btnPause, false, 0.4f, R.string.btn_pause);
                setButton(btnReset, false, 0.4f, R.string.btn_reset);
                setButton(btnLap, false, 0.4f, R.string.btn_lap);
                break;
        }

        updateLapUi();
    }

    private void setButton(MaterialButton button, boolean enabled, float alpha, int textRes) {
        if (button != null) {
            button.setEnabled(enabled);
            button.setAlpha(alpha);
            button.setText(textRes);
        }
    }

    private void updateLapUi() {
        int count = lapList.size();
        tvLapCount.setText(String.format("(%d)", count));
        tvNoLaps.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        lvLaps.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (engine.isRunning()) {
            handler.removeCallbacks(tickerRunnable);
            handler.post(tickerRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(tickerRunnable);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_STATE, engine.getState().name());
        outState.putLong(KEY_ACCUMULATED, engine.getAccumulatedElapsedMillis());
        outState.putLong(KEY_START_TIME, engine.getStartTimeMillis());
    }

    private void restoreState(Bundle savedInstanceState) {
        String stateStr = savedInstanceState.getString(KEY_STATE, StopwatchEngine.State.STOPPED.name());
        long accumulated = savedInstanceState.getLong(KEY_ACCUMULATED, 0L);
        long startTime = savedInstanceState.getLong(KEY_START_TIME, 0L);

        StopwatchEngine.State state = StopwatchEngine.State.valueOf(stateStr);
        engine.restoreState(state, accumulated, startTime, 0L, null);

        long now = SystemClock.uptimeMillis();
        long elapsed = engine.getElapsedTime(now);
        tvMainDigits.setText(StopwatchEngine.formatMainDigits(elapsed));
        tvCentiseconds.setText(StopwatchEngine.formatCentiseconds(elapsed));

        updateUiState();
        if (engine.isRunning()) {
            handler.post(tickerRunnable);
        }
    }
}
