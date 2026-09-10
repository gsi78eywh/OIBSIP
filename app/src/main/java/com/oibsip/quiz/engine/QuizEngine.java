package com.oibsip.quiz.engine;

import com.oibsip.quiz.data.QuestionBank;
import com.oibsip.quiz.model.Question;
import com.oibsip.quiz.model.QuizResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages quiz session state, answer verification, and scoring.
 */
public class QuizEngine {

    public enum State {
        NOT_STARTED,
        QUESTION_ACTIVE,
        ANSWER_SUBMITTED,
        COMPLETED
    }

    private final List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;
    private int selectedOptionIndex = -1;
    private boolean currentAnswerCorrect = false;
    private State state = State.NOT_STARTED;

    public void start(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Question list cannot be null or empty.");
        }
        this.questions.clear();
        this.questions.addAll(questions);
        this.currentIndex = 0;
        this.score = 0;
        this.selectedOptionIndex = -1;
        this.currentAnswerCorrect = false;
        this.state = State.QUESTION_ACTIVE;
    }

    public void start(int questionCount, boolean shuffle) {
        start(QuestionBank.getShuffledQuestions(questionCount, shuffle));
    }

    public boolean submitAnswer(int optionIndex) {
        if (state != State.QUESTION_ACTIVE) {
            throw new IllegalStateException("Cannot submit answer when state is " + state);
        }
        if (optionIndex < 0 || optionIndex >= 4) {
            throw new IllegalArgumentException("Option index must be between 0 and 3.");
        }

        Question current = getCurrentQuestion();
        this.selectedOptionIndex = optionIndex;
        this.currentAnswerCorrect = current.isCorrect(optionIndex);

        if (this.currentAnswerCorrect) {
            this.score++;
        }

        this.state = State.ANSWER_SUBMITTED;
        return this.currentAnswerCorrect;
    }

    public boolean nextQuestion() {
        if (state != State.ANSWER_SUBMITTED) {
            throw new IllegalStateException("Must submit an answer before advancing.");
        }

        if (hasNextQuestion()) {
            this.currentIndex++;
            this.selectedOptionIndex = -1;
            this.currentAnswerCorrect = false;
            this.state = State.QUESTION_ACTIVE;
            return true;
        } else {
            this.state = State.COMPLETED;
            return false;
        }
    }

    public boolean hasNextQuestion() {
        return currentIndex + 1 < questions.size();
    }

    public Question getCurrentQuestion() {
        if (questions.isEmpty() || currentIndex < 0 || currentIndex >= questions.size()) {
            return null;
        }
        return questions.get(currentIndex);
    }

    public int getCurrentQuestionIndex() {
        return currentIndex;
    }

    public int getCurrentQuestionNumber() {
        return currentIndex + 1;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public int getScore() {
        return score;
    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public boolean isCurrentAnswerCorrect() {
        return currentAnswerCorrect;
    }

    public State getState() {
        return state;
    }

    public boolean isQuizCompleted() {
        return state == State.COMPLETED;
    }

    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public QuizResult getResult() {
        if (questions.isEmpty()) {
            return new QuizResult(1, 0);
        }
        return new QuizResult(questions.size(), score);
    }

    public void reset() {
        this.questions.clear();
        this.currentIndex = 0;
        this.score = 0;
        this.selectedOptionIndex = -1;
        this.currentAnswerCorrect = false;
        this.state = State.NOT_STARTED;
    }
}
