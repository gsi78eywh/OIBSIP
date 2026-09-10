package com.oibsip.quiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.oibsip.unitconverter.R;

import java.util.Locale;

/**
 * Result & Score Summary Screen for TASK 4: Quiz Application.
 * 
 * Responsibilities:
 * 1. Display total score, accuracy percentage, and qualitative feedback grade.
 * 2. Show detailed breakdown (number correct, number incorrect, total).
 * 3. Update and celebrate new high score records in SharedPreferences.
 * 4. Offer "Restart Quiz" (immediate replay with fresh shuffle) and "Back to Menu".
 */
public class QuizResultActivity extends AppCompatActivity {

    private static final String TAG = "QuizResultActivity";

    public static final String EXTRA_TOTAL_QUESTIONS = "extra_total_questions";
    public static final String EXTRA_CORRECT_ANSWERS = "extra_correct_answers";
    public static final String EXTRA_WRONG_ANSWERS = "extra_wrong_answers";
    public static final String EXTRA_SCORE_PERCENTAGE = "extra_score_percentage";
    public static final String EXTRA_GRADE_TITLE = "extra_grade_title";
    public static final String EXTRA_FEEDBACK_MESSAGE = "extra_feedback_message";

    private TextView tvScorePercentage;
    private TextView tvGradeTitle;
    private TextView tvFeedbackMessage;
    private TextView tvNewHighScoreBadge;
    private TextView tvCorrectCount;
    private TextView tvWrongCount;
    private TextView tvTotalQuestionsCount;

    private MaterialButton btnRestartQuiz;
    private MaterialButton btnBackToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);
        Log.d(TAG, "QuizResultActivity displayed");

        initViews();
        displayResults();
        setupListeners();
    }

    private void initViews() {
        tvScorePercentage     = findViewById(R.id.tvScorePercentage);
        tvGradeTitle          = findViewById(R.id.tvGradeTitle);
        tvFeedbackMessage     = findViewById(R.id.tvFeedbackMessage);
        tvNewHighScoreBadge   = findViewById(R.id.tvNewHighScoreBadge);
        tvCorrectCount        = findViewById(R.id.tvCorrectCount);
        tvWrongCount          = findViewById(R.id.tvWrongCount);
        tvTotalQuestionsCount = findViewById(R.id.tvTotalQuestionsCount);

        btnRestartQuiz        = findViewById(R.id.btnRestartQuiz);
        btnBackToMenu         = findViewById(R.id.btnBackToMenu);
    }

    private void displayResults() {
        Intent intent = getIntent();
        int total = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 10);
        int correct = intent.getIntExtra(EXTRA_CORRECT_ANSWERS, 0);
        int wrong = intent.getIntExtra(EXTRA_WRONG_ANSWERS, 0);
        double percentage = intent.getDoubleExtra(EXTRA_SCORE_PERCENTAGE, 0.0);
        String grade = intent.getStringExtra(EXTRA_GRADE_TITLE);
        String feedback = intent.getStringExtra(EXTRA_FEEDBACK_MESSAGE);

        tvScorePercentage.setText(String.format(Locale.US, "%.0f%%", percentage));
        tvGradeTitle.setText(grade != null ? grade : "Quiz Completed!");
        tvFeedbackMessage.setText(feedback != null ? feedback : "");

        tvCorrectCount.setText(String.valueOf(correct));
        tvWrongCount.setText(String.valueOf(wrong));
        tvTotalQuestionsCount.setText(String.valueOf(total));

        checkAndSaveHighScore((int) Math.round(percentage));
    }

    /**
     * Checks if current score beats previous best and saves to SharedPreferences.
     */
    private void checkAndSaveHighScore(int currentPercentage) {
        SharedPreferences prefs = getSharedPreferences(QuizWelcomeActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int previousHigh = prefs.getInt(QuizWelcomeActivity.KEY_HIGH_SCORE, -1);

        if (currentPercentage > previousHigh && currentPercentage > 0) {
            prefs.edit().putInt(QuizWelcomeActivity.KEY_HIGH_SCORE, currentPercentage).apply();
            tvNewHighScoreBadge.setVisibility(View.VISIBLE);
            Log.d(TAG, "New personal best record achieved: " + currentPercentage + "% (previous: " + previousHigh + "%)");
        } else {
            tvNewHighScoreBadge.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnRestartQuiz.setOnClickListener(v -> {
            Log.d(TAG, "Restarting quiz session");
            Intent intent = new Intent(this, QuizActivity.class);
            startActivity(intent);
            finish();
        });

        btnBackToMenu.setOnClickListener(v -> {
            Log.d(TAG, "Navigating back to Quiz Welcome screen");
            Intent intent = new Intent(this, QuizWelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
