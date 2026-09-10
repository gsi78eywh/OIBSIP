package com.oibsip.quiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.oibsip.unitconverter.MainActivity;
import com.oibsip.unitconverter.R;
import com.oibsip.stopwatch.StopwatchActivity;

import java.util.Locale;

/**
 * Welcome / Start screen for TASK 4: Quiz Application.
 * 
 * Responsibilities:
 * 1. Introduce quiz topic, format, and rules.
 * 2. Display persistent high score from SharedPreferences.
 * 3. Launch QuizActivity on "Start Quiz" click.
 * 4. Provide fast switching between other internship tasks (Stopwatch & Converter).
 */
public class QuizWelcomeActivity extends AppCompatActivity {

    private static final String TAG = "QuizWelcomeActivity";
    public static final String PREFS_NAME = "quiz_app_preferences";
    public static final String KEY_HIGH_SCORE = "key_high_score_percentage";

    private TextView tvPersonalBest;
    private MaterialButton btnStartQuiz;
    private MaterialButton btnOpenConverter;
    private MaterialButton btnOpenStopwatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_welcome);
        Log.d(TAG, "QuizWelcomeActivity initialized");

        initViews();
        loadHighScore();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh high score in case a new one was achieved in QuizResultActivity
        loadHighScore();
    }

    private void initViews() {
        tvPersonalBest     = findViewById(R.id.tvPersonalBest);
        btnStartQuiz       = findViewById(R.id.btnStartQuiz);
        btnOpenConverter   = findViewById(R.id.btnOpenConverter);
        btnOpenStopwatch   = findViewById(R.id.btnOpenStopwatch);
    }

    private void loadHighScore() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int highScore = prefs.getInt(KEY_HIGH_SCORE, -1);

        if (highScore >= 0) {
            tvPersonalBest.setText(String.format(Locale.US, "Personal Best: %d%%", highScore));
        } else {
            tvPersonalBest.setText("Personal Best: None yet");
        }
    }

    private void setupListeners() {
        btnStartQuiz.setOnClickListener(v -> {
            Log.d(TAG, "Starting new Quiz session");
            Intent intent = new Intent(this, QuizActivity.class);
            startActivity(intent);
        });

        btnOpenConverter.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to Unit Converter (Task 1)");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        btnOpenStopwatch.setOnClickListener(v -> {
            Log.d(TAG, "Navigating to Stopwatch (Task 5)");
            Intent intent = new Intent(this, StopwatchActivity.class);
            startActivity(intent);
        });
    }
}
