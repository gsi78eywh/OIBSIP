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

/**
 * Android Activity for TASK 5: Stopwatch Application.
 * 
 * Features:
 * - Large high-contrast digital display (MM:SS.cs / HH:MM:SS.cs).
 * - Start / Resume, Pause, Reset, and Lap recording controls.
 * - Dynamic visual button state indicators (enabled/disabled styles).
 * - Android Handler & Runnable 30ms high-precision UI thread updater.
 * - Activity lifecycle handling (pause/resume & onSaveInstanceState rotation support).
 */
public class StopwatchActivity extends AppCompatActivity {

    private final StopwatchEngine engine = new StopwatchEngine();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTimerRunnable;

    // UI Widgets
    private TextView tvMainDigits;
    private TextView tvCentiseconds;
    private TextView tvStateBadge;
    private TextView tvLapCount;
    private TextView tvNoLaps;
    private ListView lvLaps;

    // Buttons
    private MaterialButton btnStart;
    private MaterialButton btnPause;
    private MaterialButton btnReset;
    private MaterialButton btnLap;
    private MaterialButton btnSwitchToConverter;

    private LapAdapter lapAdapter;
    private final ArrayList<LapItem> lapList = new ArrayList<>();

    // Keys for lifecycle state saving
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
        setupTickerRunnable();

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            updateUiState();
        }
    }

    private void initViews() {
        tvMainDigits           = findViewById(R.id.tvMainDigits);
        tvCentiseconds         = findViewById(R.id.tvCentiseconds);
        tvStateBadge           = findViewById(R.id.tvStateBadge);
        tvLapCount             = findViewById(R.id.tvLapCount);
        tvNoLaps               = findViewById(R.id.tvNoLaps);
        lvLaps                 = findViewById(R.id.lvLaps);

        btnStart               = findViewById(R.id.btnStart);
        btnPause               = findViewById(R.id.btnPause);
        btnReset               = findViewById(R.id.btnReset);
        btnLap                 = findViewById(R.id.btnLap);
        btnSwitchToConverter   = findViewById(R.id.btnSwitchToConverter);
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

        btnSwitchToConverter.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void setupTickerRunnable() {
        updateTimerRunnable = new Runnable() {
            @Override
            public void run() {
                if (engine.isRunning()) {
                    long now = SystemClock.uptimeMillis();
                    long elapsed = engine.getElapsedTime(now);
                    tvMainDigits.setText(StopwatchEngine.formatMainDigits(elapsed));
                    tvCentiseconds.setText(StopwatchEngine.formatCentiseconds(elapsed));
                    handler.postDelayed(this, 30); // ~33 FPS smooth refresh
                }
            }
        };
    }

    // =========================================================================
    // USER ACTIONS
    // =========================================================================

    private void onStartClicked() {
        long now = SystemClock.uptimeMillis();
        engine.start(now);
        handler.removeCallbacks(updateTimerRunnable);
        handler.post(updateTimerRunnable);
        updateUiState();
    }

    private void onPauseClicked() {
        long now = SystemClock.uptimeMillis();
        engine.pause(now);
        handler.removeCallbacks(updateTimerRunnable);

        long elapsed = engine.getElapsedTime(now);
        tvMainDigits.setText(StopwatchEngine.formatMainDigits(elapsed));
        tvCentiseconds.setText(StopwatchEngine.formatCentiseconds(elapsed));
        updateUiState();
    }

    private void onResetClicked() {
        engine.reset();
        handler.removeCallbacks(updateTimerRunnable);

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

    // =========================================================================
    // DYNAMIC UI & BUTTON STATE
    // =========================================================================

    private void updateUiState() {
        StopwatchEngine.State state = engine.getState();

        switch (state) {
            case RUNNING:
                tvStateBadge.setText("RUNNING");
                tvStateBadge.setTextColor(getColor(R.color.stopwatch_start));

                btnStart.setEnabled(false);
                btnStart.setAlpha(0.4f);

                btnPause.setEnabled(true);
                btnPause.setAlpha(1.0f);

                btnReset.setEnabled(false);
                btnReset.setAlpha(0.4f);

                btnLap.setEnabled(true);
                btnLap.setAlpha(1.0f);
                break;

            case PAUSED:
                tvStateBadge.setText("PAUSED");
                tvStateBadge.setTextColor(getColor(R.color.stopwatch_pause));

                btnStart.setEnabled(true);
                btnStart.setAlpha(1.0f);
                btnStart.setText(R.string.btn_resume);

                btnPause.setEnabled(false);
                btnPause.setAlpha(0.4f);

                btnReset.setEnabled(true);
                btnReset.setAlpha(1.0f);

                btnLap.setEnabled(false);
                btnLap.setAlpha(0.4f);
                break;

            case STOPPED:
            default:
                tvStateBadge.setText("READY");
                tvStateBadge.setTextColor(getColor(R.color.primary));

                btnStart.setEnabled(true);
                btnStart.setAlpha(1.0f);
                btnStart.setText(R.string.btn_start);

                btnPause.setEnabled(false);
                btnPause.setAlpha(0.4f);

                btnReset.setEnabled(false);
                btnReset.setAlpha(0.4f);

                btnLap.setEnabled(false);
                btnLap.setAlpha(0.4f);
                break;
        }

        updateLapUi();
    }

    private void updateLapUi() {
        int count = lapList.size();
        tvLapCount.setText(String.format("(%d)", count));
        tvNoLaps.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        lvLaps.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
    }

    // =========================================================================
    // LIFECYCLE & ORIENTATION PERSISTENCE
    // =========================================================================

    @Override
    protected void onResume() {
        super.onResume();
        if (engine.isRunning()) {
            handler.removeCallbacks(updateTimerRunnable);
            handler.post(updateTimerRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove callbacks to save CPU when activity is in background
        handler.removeCallbacks(updateTimerRunnable);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_STATE, engine.getState().name());
        outState.putLong(KEY_ACCUMULATED, engine.getAccumulatedElapsedMillis());
        outState.putLong(KEY_START_TIME, engine.getStartTimeMillis());
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
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
            handler.post(updateTimerRunnable);
        }
    }
}
