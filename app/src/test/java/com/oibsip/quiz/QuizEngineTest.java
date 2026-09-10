package com.oibsip.quiz;

import com.oibsip.quiz.data.QuestionBank;
import com.oibsip.quiz.engine.QuizEngine;
import com.oibsip.quiz.model.Question;
import com.oibsip.quiz.model.QuizResult;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test suite for Task 4: Quiz Application models, QuestionBank, and QuizEngine.
 */
public class QuizEngineTest {

    private QuizEngine engine;

    @Before
    public void setUp() {
        engine = new QuizEngine();
    }

    @Test
    public void testInitialState() {
        assertEquals(QuizEngine.State.NOT_STARTED, engine.getState());
        assertEquals(0, engine.getScore());
        assertEquals(0, engine.getCurrentQuestionIndex());
        assertEquals(0, engine.getTotalQuestions());
        assertFalse(engine.isQuizCompleted());
    }

    @Test
    public void testStartWithQuestionList() {
        List<Question> questions = QuestionBank.getAllQuestions().subList(0, 5);
        engine.start(questions);

        assertEquals(QuizEngine.State.QUESTION_ACTIVE, engine.getState());
        assertEquals(5, engine.getTotalQuestions());
        assertEquals(0, engine.getCurrentQuestionIndex());
        assertEquals(1, engine.getCurrentQuestionNumber());
        assertEquals(0, engine.getScore());
        assertNotNull(engine.getCurrentQuestion());
    }

    @Test
    public void testCorrectAnswerIncrementsScore() {
        List<Question> questions = QuestionBank.getAllQuestions().subList(0, 3);
        engine.start(questions);

        Question q1 = engine.getCurrentQuestion();
        int correctIndex = q1.getCorrectOptionIndex();

        boolean isCorrect = engine.submitAnswer(correctIndex);
        assertTrue(isCorrect);
        assertTrue(engine.isCurrentAnswerCorrect());
        assertEquals(1, engine.getScore());
        assertEquals(QuizEngine.State.ANSWER_SUBMITTED, engine.getState());
        assertEquals(correctIndex, engine.getSelectedOptionIndex());
    }

    @Test
    public void testWrongAnswerDoesNotIncrementScore() {
        List<Question> questions = QuestionBank.getAllQuestions().subList(0, 3);
        engine.start(questions);

        Question q1 = engine.getCurrentQuestion();
        int wrongIndex = (q1.getCorrectOptionIndex() + 1) % 4;

        boolean isCorrect = engine.submitAnswer(wrongIndex);
        assertFalse(isCorrect);
        assertFalse(engine.isCurrentAnswerCorrect());
        assertEquals(0, engine.getScore());
        assertEquals(QuizEngine.State.ANSWER_SUBMITTED, engine.getState());
        assertEquals(wrongIndex, engine.getSelectedOptionIndex());
    }

    @Test(expected = IllegalStateException.class)
    public void testDoubleAnswerSubmissionThrowsException() {
        engine.start(QuestionBank.getAllQuestions().subList(0, 2));
        engine.submitAnswer(0);
        // Attempting to submit answer again on the same question
        engine.submitAnswer(1);
    }

    @Test
    public void testProgressionThroughQuestions() {
        List<Question> questions = QuestionBank.getAllQuestions().subList(0, 2);
        engine.start(questions);

        // Q1
        engine.submitAnswer(engine.getCurrentQuestion().getCorrectOptionIndex());
        assertTrue(engine.hasNextQuestion());
        assertTrue(engine.nextQuestion());

        // Q2
        assertEquals(1, engine.getCurrentQuestionIndex());
        assertEquals(2, engine.getCurrentQuestionNumber());
        assertEquals(QuizEngine.State.QUESTION_ACTIVE, engine.getState());
        assertEquals(-1, engine.getSelectedOptionIndex());

        // Q2 submit wrong
        int wrongIndex = (engine.getCurrentQuestion().getCorrectOptionIndex() + 1) % 4;
        engine.submitAnswer(wrongIndex);
        assertFalse(engine.hasNextQuestion());

        // Completing quiz
        assertFalse(engine.nextQuestion());
        assertEquals(QuizEngine.State.COMPLETED, engine.getState());
        assertTrue(engine.isQuizCompleted());

        // Results
        QuizResult result = engine.getResult();
        assertEquals(2, result.getTotalQuestions());
        assertEquals(1, result.getCorrectAnswers());
        assertEquals(1, result.getWrongAnswers());
        assertEquals(50.0, result.getScorePercentage(), 0.001);
    }

    @Test
    public void testShuffledQuestionPreservesCorrectAnswer() {
        Question original = new Question(
                99,
                "Sample question?",
                Arrays.asList("A", "B", "C", "D"),
                2, // "C" is correct
                "Explanation",
                "Testing",
                "Easy"
        );

        // Shuffle multiple times to test consistency
        for (int i = 0; i < 20; i++) {
            Question copy = original.createShuffledCopy();
            assertEquals("Sample question?", copy.getQuestionText());
            assertEquals(4, copy.getOptions().size());
            assertEquals("C", copy.getCorrectAnswerText());
            assertTrue(copy.isCorrect(copy.getCorrectOptionIndex()));
        }
    }

    @Test
    public void testQuestionBankIntegrity() {
        assertTrue(QuestionBank.getBankSize() >= 15);

        for (Question q : QuestionBank.getAllQuestions()) {
            assertNotNull(q.getQuestionText());
            assertFalse(q.getQuestionText().isEmpty());
            assertEquals(4, q.getOptions().size());
            assertTrue(q.getCorrectOptionIndex() >= 0 && q.getCorrectOptionIndex() < 4);
            assertNotNull(q.getCorrectAnswerText());
            assertNotNull(q.getExplanation());
        }

        List<Question> sampled = QuestionBank.getShuffledQuestions(10, true);
        assertEquals(10, sampled.size());
    }

    @Test
    public void testQuizResultCalculations() {
        QuizResult rPerfect = new QuizResult(10, 10);
        assertEquals(100.0, rPerfect.getScorePercentage(), 0.001);
        assertEquals("100%", rPerfect.getFormattedPercentage());
        assertEquals("Outstanding!", rPerfect.getGradeTitle());
        assertTrue(rPerfect.isPassed());

        QuizResult rGood = new QuizResult(10, 7);
        assertEquals(70.0, rGood.getScorePercentage(), 0.001);
        assertEquals("70%", rGood.getFormattedPercentage());
        assertEquals("Great Job!", rGood.getGradeTitle());
        assertTrue(rGood.isPassed());

        QuizResult rLow = new QuizResult(10, 3);
        assertEquals(30.0, rLow.getScorePercentage(), 0.001);
        assertEquals("Keep Practicing!", rLow.getGradeTitle());
        assertFalse(rLow.isPassed());
    }

    @Test
    public void testEngineReset() {
        engine.start(5, false);
        engine.submitAnswer(0);
        engine.reset();

        assertEquals(QuizEngine.State.NOT_STARTED, engine.getState());
        assertEquals(0, engine.getScore());
        assertEquals(0, engine.getTotalQuestions());
    }
}
