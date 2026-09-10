package com.oibsip.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.oibsip.unitconverter.R;
import com.oibsip.quiz.engine.QuizEngine;
import com.oibsip.quiz.model.Question;
import com.oibsip.quiz.model.QuizResult;

import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private static final int DEFAULT_QUESTION_COUNT = 10;
    private final QuizEngine engine = new QuizEngine();

    private TextView tvQuestionCounter;
    private TextView tvScoreBadge;
    private ProgressBar progressBarQuiz;
    private TextView tvCategoryDifficulty;
    private TextView tvQuestionText;

    private final MaterialCardView[] cardOptions = new MaterialCardView[4];
    private final TextView[] tvOptionTexts = new TextView[4];
    private final ImageView[] ivOptionIcons = new ImageView[4];

    private MaterialCardView cardExplanation;
    private TextView tvExplanationHeader;
    private TextView tvExplanationText;

    private MaterialButton btnNextQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initViews();
        setupOptionListeners();
        startQuiz();
    }

    private void initViews() {
        tvQuestionCounter    = findViewById(R.id.tvQuestionCounter);
        tvScoreBadge          = findViewById(R.id.tvScoreBadge);
        progressBarQuiz       = findViewById(R.id.progressBarQuiz);
        tvCategoryDifficulty  = findViewById(R.id.tvCategoryDifficulty);
        tvQuestionText        = findViewById(R.id.tvQuestionText);

        cardOptions[0]        = findViewById(R.id.cardOption0);
        cardOptions[1]        = findViewById(R.id.cardOption1);
        cardOptions[2]        = findViewById(R.id.cardOption2);
        cardOptions[3]        = findViewById(R.id.cardOption3);

        tvOptionTexts[0]      = findViewById(R.id.tvOption0Text);
        tvOptionTexts[1]      = findViewById(R.id.tvOption1Text);
        tvOptionTexts[2]      = findViewById(R.id.tvOption2Text);
        tvOptionTexts[3]      = findViewById(R.id.tvOption3Text);

        ivOptionIcons[0]      = findViewById(R.id.ivOption0Icon);
        ivOptionIcons[1]      = findViewById(R.id.ivOption1Icon);
        ivOptionIcons[2]      = findViewById(R.id.ivOption2Icon);
        ivOptionIcons[3]      = findViewById(R.id.ivOption3Icon);

        cardExplanation       = findViewById(R.id.cardExplanation);
        tvExplanationHeader   = findViewById(R.id.tvExplanationHeader);
        tvExplanationText     = findViewById(R.id.tvExplanationText);

        btnNextQuestion       = findViewById(R.id.btnNextQuestion);
        btnNextQuestion.setOnClickListener(v -> onNextClicked());
    }

    private void setupOptionListeners() {
        for (int i = 0; i < 4; i++) {
            final int index = i;
            cardOptions[i].setOnClickListener(v -> onOptionSelected(index));
        }
    }

    private void startQuiz() {
        engine.start(DEFAULT_QUESTION_COUNT, true);
        progressBarQuiz.setMax(engine.getTotalQuestions());
        displayQuestion();
    }

    private void displayQuestion() {
        Question q = engine.getCurrentQuestion();
        if (q == null) return;

        int currentNum = engine.getCurrentQuestionNumber();
        int total = engine.getTotalQuestions();

        tvQuestionCounter.setText(getString(R.string.quiz_question_counter, currentNum, total));
        tvScoreBadge.setText(getString(R.string.quiz_score_prefix) + engine.getScore());
        progressBarQuiz.setProgress(currentNum);
        tvCategoryDifficulty.setText(String.format(Locale.US, "%s · %s", q.getCategory(), q.getDifficulty()));
        tvQuestionText.setText(q.getQuestionText());

        List<String> options = q.getOptions();
        for (int i = 0; i < 4; i++) {
            cardOptions[i].setEnabled(true);
            cardOptions[i].setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface));
            cardOptions[i].setStrokeColor(ContextCompat.getColor(this, R.color.quiz_option_border));
            cardOptions[i].setStrokeWidth((int) (1.5f * getResources().getDisplayMetrics().density));

            tvOptionTexts[i].setText(options.get(i));
            tvOptionTexts[i].setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            ivOptionIcons[i].setVisibility(View.GONE);
        }

        cardExplanation.setVisibility(View.GONE);
        btnNextQuestion.setEnabled(false);

        if (engine.hasNextQuestion()) {
            btnNextQuestion.setText(R.string.quiz_btn_next);
        } else {
            btnNextQuestion.setText(R.string.quiz_btn_finish);
        }
    }

    private void onOptionSelected(int selectedIndex) {
        if (engine.getState() != QuizEngine.State.QUESTION_ACTIVE) {
            return;
        }

        Question q = engine.getCurrentQuestion();
        boolean isCorrect = engine.submitAnswer(selectedIndex);
        int correctIndex = q.getCorrectOptionIndex();

        for (MaterialCardView card : cardOptions) {
            card.setEnabled(false);
        }

        int strokeWidth = (int) (2.0f * getResources().getDisplayMetrics().density);
        if (isCorrect) {
            styleOption(selectedIndex, R.color.quiz_correct_light, R.color.quiz_correct_border,
                    R.color.quiz_correct_border, R.drawable.ic_check_circle, strokeWidth);
            tvExplanationHeader.setText("Correct Answer");
            tvExplanationHeader.setTextColor(ContextCompat.getColor(this, R.color.quiz_correct));
        } else {
            styleOption(selectedIndex, R.color.quiz_wrong_light, R.color.quiz_wrong_border,
                    R.color.quiz_wrong_border, R.drawable.ic_cancel, strokeWidth);
            styleOption(correctIndex, R.color.quiz_correct_light, R.color.quiz_correct_border,
                    R.color.quiz_correct_border, R.drawable.ic_check_circle, strokeWidth);
            tvExplanationHeader.setText("Incorrect");
            tvExplanationHeader.setTextColor(ContextCompat.getColor(this, R.color.quiz_wrong));
        }

        tvExplanationText.setText(q.getExplanation());
        cardExplanation.setVisibility(View.VISIBLE);

        tvScoreBadge.setText(getString(R.string.quiz_score_prefix) + engine.getScore());
        btnNextQuestion.setEnabled(true);
    }

    private void styleOption(int index, int bgColorRes, int strokeColorRes,
                            int textColorRes, int iconRes, int strokeWidth) {
        cardOptions[index].setCardBackgroundColor(ContextCompat.getColor(this, bgColorRes));
        cardOptions[index].setStrokeColor(ContextCompat.getColor(this, strokeColorRes));
        cardOptions[index].setStrokeWidth(strokeWidth);

        tvOptionTexts[index].setTextColor(ContextCompat.getColor(this, textColorRes));
        ivOptionIcons[index].setImageResource(iconRes);
        ivOptionIcons[index].setVisibility(View.VISIBLE);
    }

    private void onNextClicked() {
        if (engine.hasNextQuestion()) {
            engine.nextQuestion();
            displayQuestion();
        } else {
            engine.nextQuestion();
            QuizResult result = engine.getResult();

            Intent intent = new Intent(this, QuizResultActivity.class);
            intent.putExtra(QuizResultActivity.EXTRA_TOTAL_QUESTIONS, result.getTotalQuestions());
            intent.putExtra(QuizResultActivity.EXTRA_CORRECT_ANSWERS, result.getCorrectAnswers());
            intent.putExtra(QuizResultActivity.EXTRA_WRONG_ANSWERS, result.getWrongAnswers());
            intent.putExtra(QuizResultActivity.EXTRA_SCORE_PERCENTAGE, result.getScorePercentage());
            intent.putExtra(QuizResultActivity.EXTRA_GRADE_TITLE, result.getGradeTitle());
            intent.putExtra(QuizResultActivity.EXTRA_FEEDBACK_MESSAGE, result.getFeedbackMessage());

            startActivity(intent);
            finish();
        }
    }
}
