package com.oibsip.quiz.model;

import java.io.Serializable;
import java.util.Locale;

/**
 * Encapsulates the overall outcome of a completed quiz session.
 */
public class QuizResult implements Serializable {

    private final int totalQuestions;
    private final int correctAnswers;
    private final int wrongAnswers;
    private final double scorePercentage;
    private final String gradeTitle;
    private final String feedbackMessage;

    public QuizResult(int totalQuestions, int correctAnswers) {
        if (totalQuestions <= 0) {
            throw new IllegalArgumentException("Total questions must be greater than zero.");
        }
        if (correctAnswers < 0 || correctAnswers > totalQuestions) {
            throw new IllegalArgumentException("Correct answers must be between 0 and total questions.");
        }

        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = totalQuestions - correctAnswers;
        this.scorePercentage = ((double) correctAnswers / totalQuestions) * 100.0;

        if (this.scorePercentage >= 90.0) {
            this.gradeTitle = "Outstanding!";
            this.feedbackMessage = "Mastery achieved! You have an exceptional grasp of the material.";
        } else if (this.scorePercentage >= 70.0) {
            this.gradeTitle = "Great Job!";
            this.feedbackMessage = "Solid performance! You answered most questions accurately.";
        } else if (this.scorePercentage >= 50.0) {
            this.gradeTitle = "Good Effort!";
            this.feedbackMessage = "Decent start! A quick review will push your score even higher.";
        } else {
            this.gradeTitle = "Keep Practicing!";
            this.feedbackMessage = "Don't give up! Try again to reinforce your knowledge.";
        }
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public double getScorePercentage() {
        return scorePercentage;
    }

    public String getFormattedPercentage() {
        return String.format(Locale.US, "%.0f%%", scorePercentage);
    }

    public String getGradeTitle() {
        return gradeTitle;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public boolean isPassed() {
        return scorePercentage >= 60.0;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Score: %d/%d (%s) - %s",
                correctAnswers, totalQuestions, getFormattedPercentage(), gradeTitle);
    }
}
