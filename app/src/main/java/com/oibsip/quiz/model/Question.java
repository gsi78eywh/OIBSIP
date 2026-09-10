package com.oibsip.quiz.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single multiple-choice quiz question.
 * 
 * Each question contains:
 * - A unique identifier and prompt text
 * - Exactly 4 answer options
 * - The index of the correct option (0 to 3)
 * - An explanation describing why the correct answer is right
 * - Category and difficulty metadata
 */
public class Question {

    private final int id;
    private final String questionText;
    private final List<String> options;
    private final int correctOptionIndex;
    private final String explanation;
    private final String category;
    private final String difficulty;

    public Question(int id, String questionText, List<String> options,
                    int correctOptionIndex, String explanation,
                    String category, String difficulty) {
        if (options == null || options.size() != 4) {
            throw new IllegalArgumentException("A quiz question must have exactly 4 options.");
        }
        if (correctOptionIndex < 0 || correctOptionIndex >= options.size()) {
            throw new IllegalArgumentException("Correct option index must be between 0 and " + (options.size() - 1));
        }

        this.id = id;
        this.questionText = questionText;
        this.options = Collections.unmodifiableList(new ArrayList<>(options));
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation != null ? explanation : "";
        this.category = category != null ? category : "General Knowledge";
        this.difficulty = difficulty != null ? difficulty : "Medium";
    }

    public int getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public String getCorrectAnswerText() {
        return options.get(correctOptionIndex);
    }

    public String getExplanation() {
        return explanation;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Checks if the provided option index matches the correct answer.
     *
     * @param selectedIndex Zero-based index of chosen option
     * @return true if correct, false otherwise
     */
    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctOptionIndex;
    }

    /**
     * Returns a new Question instance where the 4 options are shuffled randomly,
     * while correctly updating the correctOptionIndex to point to the new location.
     */
    public Question createShuffledCopy() {
        String correctOptionText = options.get(correctOptionIndex);
        List<String> shuffledOptions = new ArrayList<>(options);
        Collections.shuffle(shuffledOptions);
        int newCorrectIndex = shuffledOptions.indexOf(correctOptionText);

        return new Question(
                this.id,
                this.questionText,
                shuffledOptions,
                newCorrectIndex,
                this.explanation,
                this.category,
                this.difficulty
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id == question.id &&
                correctOptionIndex == question.correctOptionIndex &&
                Objects.equals(questionText, question.questionText) &&
                Objects.equals(options, question.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, questionText, options, correctOptionIndex);
    }

    @Override
    public String toString() {
        return "Question #" + id + ": " + questionText + " (Correct: [" + correctOptionIndex + "] " + getCorrectAnswerText() + ")";
    }
}
