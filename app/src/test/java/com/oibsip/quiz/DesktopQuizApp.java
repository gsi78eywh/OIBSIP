package com.oibsip.quiz;

import com.oibsip.quiz.data.QuestionBank;
import com.oibsip.quiz.engine.QuizEngine;
import com.oibsip.quiz.model.Question;
import com.oibsip.quiz.model.QuizResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Modern Windows Desktop GUI application for TASK 4: Quiz Application.
 * 
 * Provides an authentic Material-styled desktop experience runnable directly on Windows:
 * 1. Welcome Screen with rules, topic summary, and Personal Best tracking.
 * 2. Question Screen with 4 styled options, instant green/red answer highlighting,
 *    explanation reveal, question counter, progress bar, and score badge.
 * 3. Results Screen with score percentage, grade, stat breakdowns, and restart.
 */
public class DesktopQuizApp extends JFrame {

    private final QuizEngine engine = new QuizEngine();
    private static final int QUIZ_QUESTION_COUNT = 10;
    private int personalBestPercentage = -1;

    // Card Layout Container
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainContainer = new JPanel(cardLayout);

    private static final String CARD_WELCOME = "welcome";
    private static final String CARD_QUESTION = "question";
    private static final String CARD_RESULT = "result";

    // Palette Colors matching Android colors.xml
    private static final Color COLOR_PRIMARY = new Color(0x43, 0x38, 0xCA);      // Indigo #4338CA
    private static final Color COLOR_PRIMARY_DARK = new Color(0x31, 0x2E, 0x81); // Deep Indigo #312E81
    private static final Color COLOR_PRIMARY_BG = new Color(0xEE, 0xF2, 0xFF);   // Indigo 50 #EEF2FF
    private static final Color COLOR_BG = new Color(0xF8, 0xFA, 0xFC);           // Slate 50 #F8FAFC
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_BORDER = new Color(0xE2, 0xE8, 0xF0);        // Slate 200 #E2E8F0
    private static final Color COLOR_TEXT_MAIN = new Color(0x0F, 0x17, 0x2A);     // Slate 900 #0F172A
    private static final Color COLOR_TEXT_MUTED = new Color(0x64, 0x74, 0x8B);    // Slate 500 #64748B
    private static final Color COLOR_CORRECT = new Color(0x10, 0xB9, 0x81);       // Emerald 500 #10B981
    private static final Color COLOR_CORRECT_BG = new Color(0xD1, 0xFA, 0xE5);    // Emerald 100 #D1FAE5
    private static final Color COLOR_WRONG = new Color(0xEF, 0x44, 0x44);         // Red 500 #EF4444
    private static final Color COLOR_WRONG_BG = new Color(0xFE, 0xE2, 0xE2);      // Red 100 #FEE2E2
    private static final Color COLOR_GOLD = new Color(0xF5, 0x9E, 0x0B);          // Amber 500 #F59E0B

    // --- Welcome Screen Widgets ---
    private JLabel lblPersonalBest;

    // --- Question Screen Widgets ---
    private JLabel lblQuestionCounter;
    private JLabel lblScoreBadge;
    private JProgressBar progressBar;
    private JLabel lblCategoryDifficulty;
    private JLabel lblQuestionText;
    private final JButton[] btnOptions = new JButton[4];
    private JPanel panelExplanation;
    private JLabel lblExplanationHeader;
    private JTextArea txtExplanation;
    private JButton btnNext;

    // --- Result Screen Widgets ---
    private JLabel lblResultPercentage;
    private JLabel lblResultGrade;
    private JLabel lblResultFeedback;
    private JLabel lblNewHighScore;
    private JLabel lblStatCorrect;
    private JLabel lblStatWrong;
    private JLabel lblStatTotal;

    public DesktopQuizApp() {
        super("OIBSIP · Task 4: Computer Science & Tech Quiz");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 750);
        setMinimumSize(new Dimension(460, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        initWelcomePanel();
        initQuestionPanel();
        initResultPanel();

        add(mainContainer);
        cardLayout.show(mainContainer, CARD_WELCOME);
    }

    // =========================================================================
    // 1. WELCOME SCREEN
    // =========================================================================

    private void initWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(24, 28, 28, 28));

        // Top Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setOpaque(false);

        JLabel lblTag = new JLabel("OASIS INFOBYTE · TASK 4");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTag.setForeground(COLOR_PRIMARY);

        JLabel lblTitle = new JLabel("Computer Science Quiz");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT_MAIN);

        headerPanel.add(lblTag);
        headerPanel.add(lblTitle);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Center Content Box
        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setOpaque(false);
        centerBox.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Hero Card
        JPanel heroCard = new JPanel();
        heroCard.setLayout(new BoxLayout(heroCard, BoxLayout.Y_AXIS));
        heroCard.setBackground(COLOR_CARD_BG);
        heroCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(24, 20, 24, 20)
        ));

        JLabel lblHeroIcon = new JLabel("🧠", SwingConstants.CENTER);
        lblHeroIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblHeroIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblHeroTitle = new JLabel("Test Your Knowledge", SwingConstants.CENTER);
        lblHeroTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeroTitle.setForeground(COLOR_TEXT_MAIN);
        lblHeroTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblHeroSub = new JLabel("<html><center>Answer 10 randomized multiple-choice questions on programming, architecture, and CS fundamentals!</center></html>");
        lblHeroSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblHeroSub.setForeground(COLOR_TEXT_MUTED);
        lblHeroSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPersonalBest = new JLabel("🏆 Personal Best: None yet");
        lblPersonalBest.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPersonalBest.setForeground(COLOR_PRIMARY_DARK);
        lblPersonalBest.setAlignmentX(Component.CENTER_ALIGNMENT);

        heroCard.add(lblHeroIcon);
        heroCard.add(Box.createVerticalStrut(10));
        heroCard.add(lblHeroTitle);
        heroCard.add(Box.createVerticalStrut(6));
        heroCard.add(lblHeroSub);
        heroCard.add(Box.createVerticalStrut(16));
        heroCard.add(lblPersonalBest);
        centerBox.add(heroCard);

        centerBox.add(Box.createVerticalStrut(16));

        // Rules Card
        JPanel rulesCard = new JPanel(new GridLayout(3, 1, 0, 6));
        rulesCard.setBackground(COLOR_CARD_BG);
        rulesCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel r1 = new JLabel("• 10 questions randomly sampled from QuestionBank");
        JLabel r2 = new JLabel("• Instant visual feedback (Green = Correct, Red = Wrong)");
        JLabel r3 = new JLabel("• Explanations provided after each answer");
        r1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        r2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        r3.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        r1.setForeground(COLOR_TEXT_MUTED);
        r2.setForeground(COLOR_TEXT_MUTED);
        r3.setForeground(COLOR_TEXT_MUTED);

        rulesCard.add(r1);
        rulesCard.add(r2);
        rulesCard.add(r3);
        centerBox.add(rulesCard);

        panel.add(centerBox, BorderLayout.CENTER);

        // Bottom Action Button
        JButton btnStart = createStyledButton("🚀 Start Quiz", COLOR_PRIMARY, Color.WHITE);
        btnStart.setPreferredSize(new Dimension(0, 52));
        btnStart.addActionListener(e -> startQuizSession());
        panel.add(btnStart, BorderLayout.SOUTH);

        mainContainer.add(panel, CARD_WELCOME);
    }

    // =========================================================================
    // 2. QUESTION SCREEN
    // =========================================================================

    private void initQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Top Status Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel statusRow = new JPanel(new BorderLayout());
        statusRow.setOpaque(false);

        lblQuestionCounter = new JLabel("Question 1 of 10");
        lblQuestionCounter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQuestionCounter.setForeground(COLOR_PRIMARY);

        lblScoreBadge = new JLabel(" Score: 0 ");
        lblScoreBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblScoreBadge.setForeground(COLOR_PRIMARY_DARK);
        lblScoreBadge.setBackground(COLOR_PRIMARY_BG);
        lblScoreBadge.setOpaque(true);
        lblScoreBadge.setBorder(new EmptyBorder(4, 10, 4, 10));

        statusRow.add(lblQuestionCounter, BorderLayout.WEST);
        statusRow.add(lblScoreBadge, BorderLayout.EAST);
        topPanel.add(statusRow);
        topPanel.add(Box.createVerticalStrut(10));

        progressBar = new JProgressBar(1, QUIZ_QUESTION_COUNT);
        progressBar.setValue(1);
        progressBar.setForeground(COLOR_PRIMARY);
        progressBar.setBackground(COLOR_BORDER);
        progressBar.setPreferredSize(new Dimension(0, 8));
        topPanel.add(progressBar);
        topPanel.add(Box.createVerticalStrut(8));

        lblCategoryDifficulty = new JLabel("COMPUTER HARDWARE · EASY");
        lblCategoryDifficulty.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCategoryDifficulty.setForeground(COLOR_TEXT_MUTED);
        topPanel.add(lblCategoryDifficulty);

        panel.add(topPanel, BorderLayout.NORTH);

        // Center Content Box (Question Card + 4 Option Buttons + Explanation)
        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setOpaque(false);
        centerBox.setBorder(new EmptyBorder(12, 0, 12, 0));

        // Question Prompt Card
        JPanel questionCard = new JPanel(new BorderLayout());
        questionCard.setBackground(COLOR_CARD_BG);
        questionCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        lblQuestionText = new JLabel("<html><b>Sample Question Prompt?</b></html>");
        lblQuestionText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblQuestionText.setForeground(COLOR_TEXT_MAIN);
        questionCard.add(lblQuestionText, BorderLayout.CENTER);
        centerBox.add(questionCard);
        centerBox.add(Box.createVerticalStrut(14));

        // 4 Option Buttons
        for (int i = 0; i < 4; i++) {
            final int index = i;
            btnOptions[i] = new JButton("Option " + (char)('A' + i));
            btnOptions[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnOptions[i].setHorizontalAlignment(SwingConstants.LEFT);
            btnOptions[i].setBackground(COLOR_CARD_BG);
            btnOptions[i].setForeground(COLOR_TEXT_MAIN);
            btnOptions[i].setFocusPainted(false);
            btnOptions[i].setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COLOR_BORDER, 1, true),
                    new EmptyBorder(12, 16, 12, 16)
            ));
            btnOptions[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            btnOptions[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            btnOptions[i].addActionListener(e -> onOptionClicked(index));

            centerBox.add(btnOptions[i]);
            centerBox.add(Box.createVerticalStrut(8));
        }

        // Explanation Panel (Initially Hidden)
        panelExplanation = new JPanel(new BorderLayout());
        panelExplanation.setBackground(COLOR_CARD_BG);
        panelExplanation.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        panelExplanation.setVisible(false);

        lblExplanationHeader = new JLabel("✅ Correct Answer");
        lblExplanationHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblExplanationHeader.setForeground(COLOR_CORRECT);

        txtExplanation = new JTextArea("Explanation goes here...");
        txtExplanation.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtExplanation.setForeground(COLOR_TEXT_MUTED);
        txtExplanation.setLineWrap(true);
        txtExplanation.setWrapStyleWord(true);
        txtExplanation.setEditable(false);
        txtExplanation.setOpaque(false);

        panelExplanation.add(lblExplanationHeader, BorderLayout.NORTH);
        panelExplanation.add(Box.createVerticalStrut(4), BorderLayout.CENTER);
        panelExplanation.add(txtExplanation, BorderLayout.SOUTH);

        centerBox.add(panelExplanation);
        panel.add(centerBox, BorderLayout.CENTER);

        // Next Button
        btnNext = createStyledButton("Next Question", COLOR_PRIMARY, Color.WHITE);
        btnNext.setPreferredSize(new Dimension(0, 48));
        btnNext.setEnabled(false);
        btnNext.addActionListener(e -> onNextQuestionClicked());
        panel.add(btnNext, BorderLayout.SOUTH);

        mainContainer.add(panel, CARD_QUESTION);
    }

    // =========================================================================
    // 3. RESULT SCREEN
    // =========================================================================

    private void initResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(24, 28, 28, 28));

        // Center Box
        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setOpaque(false);

        JLabel lblTag = new JLabel("OASIS INFOBYTE · TASK 4", SwingConstants.CENTER);
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTag.setForeground(COLOR_PRIMARY);
        lblTag.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Quiz Completed!", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(COLOR_TEXT_MAIN);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerBox.add(lblTag);
        centerBox.add(Box.createVerticalStrut(4));
        centerBox.add(lblTitle);
        centerBox.add(Box.createVerticalStrut(20));

        // Hero Score Card
        JPanel scoreCard = new JPanel();
        scoreCard.setLayout(new BoxLayout(scoreCard, BoxLayout.Y_AXIS));
        scoreCard.setBackground(COLOR_CARD_BG);
        scoreCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(24, 20, 24, 20)
        ));

        JLabel lblIcon = new JLabel("🏆", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 44));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblResultPercentage = new JLabel("80%", SwingConstants.CENTER);
        lblResultPercentage.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblResultPercentage.setForeground(COLOR_PRIMARY);
        lblResultPercentage.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblResultGrade = new JLabel("Great Job!", SwingConstants.CENTER);
        lblResultGrade.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblResultGrade.setForeground(COLOR_TEXT_MAIN);
        lblResultGrade.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblResultFeedback = new JLabel("<html><center>Solid performance! You answered most questions accurately.</center></html>", SwingConstants.CENTER);
        lblResultFeedback.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblResultFeedback.setForeground(COLOR_TEXT_MUTED);
        lblResultFeedback.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNewHighScore = new JLabel("🎉 NEW PERSONAL BEST!");
        lblNewHighScore.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNewHighScore.setForeground(COLOR_GOLD);
        lblNewHighScore.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNewHighScore.setVisible(false);

        scoreCard.add(lblIcon);
        scoreCard.add(Box.createVerticalStrut(8));
        scoreCard.add(lblResultPercentage);
        scoreCard.add(Box.createVerticalStrut(4));
        scoreCard.add(lblResultGrade);
        scoreCard.add(Box.createVerticalStrut(6));
        scoreCard.add(lblResultFeedback);
        scoreCard.add(Box.createVerticalStrut(10));
        scoreCard.add(lblNewHighScore);
        centerBox.add(scoreCard);

        centerBox.add(Box.createVerticalStrut(18));

        // Stats Row (Correct, Wrong, Total)
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 10, 0));
        statsRow.setOpaque(false);

        lblStatCorrect = new JLabel("8", SwingConstants.CENTER);
        lblStatWrong = new JLabel("2", SwingConstants.CENTER);
        lblStatTotal = new JLabel("10", SwingConstants.CENTER);

        statsRow.add(createStatCard("Correct", lblStatCorrect, COLOR_CORRECT, COLOR_CORRECT_BG));
        statsRow.add(createStatCard("Incorrect", lblStatWrong, COLOR_WRONG, COLOR_WRONG_BG));
        statsRow.add(createStatCard("Total", lblStatTotal, COLOR_TEXT_MAIN, COLOR_CARD_BG));
        centerBox.add(statsRow);

        panel.add(centerBox, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel bottomButtons = new JPanel(new GridLayout(2, 1, 0, 8));
        bottomButtons.setOpaque(false);

        JButton btnRestart = createStyledButton("🔄 Restart Quiz", COLOR_PRIMARY, Color.WHITE);
        btnRestart.setPreferredSize(new Dimension(0, 48));
        btnRestart.addActionListener(e -> startQuizSession());

        JButton btnMenu = createStyledButton("Back to Menu", Color.WHITE, COLOR_TEXT_MAIN);
        btnMenu.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        btnMenu.setPreferredSize(new Dimension(0, 44));
        btnMenu.addActionListener(e -> cardLayout.show(mainContainer, CARD_WELCOME));

        bottomButtons.add(btnRestart);
        bottomButtons.add(btnMenu);
        panel.add(bottomButtons, BorderLayout.SOUTH);

        mainContainer.add(panel, CARD_RESULT);
    }

    private JPanel createStatCard(String label, JLabel valueLabel, Color textColor, Color bgColor) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2));
        p.setBackground(bgColor);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(10, 8, 10, 8)
        ));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(textColor);

        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(COLOR_TEXT_MUTED);

        p.add(valueLabel);
        p.add(l);
        return p;
    }

    // =========================================================================
    // GAMEPLAY CONTROLS & EVENT HANDLERS
    // =========================================================================

    private void startQuizSession() {
        engine.start(QUIZ_QUESTION_COUNT, true);
        progressBar.setMaximum(engine.getTotalQuestions());
        displayCurrentQuestion();
        cardLayout.show(mainContainer, CARD_QUESTION);
    }

    private void displayCurrentQuestion() {
        Question q = engine.getCurrentQuestion();
        if (q == null) return;

        int currentNum = engine.getCurrentQuestionNumber();
        int total = engine.getTotalQuestions();

        lblQuestionCounter.setText("Question " + currentNum + " of " + total);
        lblScoreBadge.setText(" Score: " + engine.getScore() + " ");
        progressBar.setValue(currentNum);
        lblCategoryDifficulty.setText(q.getCategory().toUpperCase() + " · " + q.getDifficulty().toUpperCase());
        lblQuestionText.setText("<html><b>" + q.getQuestionText() + "</b></html>");

        List<String> options = q.getOptions();
        for (int i = 0; i < 4; i++) {
            btnOptions[i].setText("   " + (char)('A' + i) + ".   " + options.get(i));
            btnOptions[i].setBackground(COLOR_CARD_BG);
            btnOptions[i].setForeground(COLOR_TEXT_MAIN);
            btnOptions[i].setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COLOR_BORDER, 1, true),
                    new EmptyBorder(12, 16, 12, 16)
            ));
            btnOptions[i].setEnabled(true);
        }

        panelExplanation.setVisible(false);
        btnNext.setEnabled(false);

        if (engine.hasNextQuestion()) {
            btnNext.setText("Next Question");
        } else {
            btnNext.setText("View Results");
        }
    }

    private void onOptionClicked(int selectedIndex) {
        if (engine.getState() != QuizEngine.State.QUESTION_ACTIVE) return;

        Question q = engine.getCurrentQuestion();
        boolean isCorrect = engine.submitAnswer(selectedIndex);
        int correctIndex = q.getCorrectOptionIndex();

        // Lock options
        for (JButton b : btnOptions) {
            b.setEnabled(false);
        }

        if (isCorrect) {
            btnOptions[selectedIndex].setBackground(COLOR_CORRECT);
            btnOptions[selectedIndex].setForeground(Color.WHITE);
            btnOptions[selectedIndex].setText("✅ " + btnOptions[selectedIndex].getText().trim());

            lblExplanationHeader.setText("✅ Correct Answer");
            lblExplanationHeader.setForeground(COLOR_CORRECT);
        } else {
            btnOptions[selectedIndex].setBackground(COLOR_WRONG);
            btnOptions[selectedIndex].setForeground(Color.WHITE);
            btnOptions[selectedIndex].setText("❌ " + btnOptions[selectedIndex].getText().trim());

            btnOptions[correctIndex].setBackground(COLOR_CORRECT_BG);
            btnOptions[correctIndex].setForeground(new Color(0x06, 0x5F, 0x46));
            btnOptions[correctIndex].setBorder(new LineBorder(COLOR_CORRECT, 2, true));
            btnOptions[correctIndex].setText("✅ " + btnOptions[correctIndex].getText().trim());

            lblExplanationHeader.setText("❌ Incorrect");
            lblExplanationHeader.setForeground(COLOR_WRONG);
        }

        txtExplanation.setText(q.getExplanation());
        panelExplanation.setVisible(true);

        lblScoreBadge.setText(" Score: " + engine.getScore() + " ");
        btnNext.setEnabled(true);
    }

    private void onNextQuestionClicked() {
        if (engine.hasNextQuestion()) {
            engine.nextQuestion();
            displayCurrentQuestion();
        } else {
            engine.nextQuestion(); // Transitions to COMPLETED
            displayResultScreen();
        }
    }

    private void displayResultScreen() {
        QuizResult result = engine.getResult();
        int percentage = (int) Math.round(result.getScorePercentage());

        lblResultPercentage.setText(percentage + "%");
        lblResultGrade.setText(result.getGradeTitle());
        lblResultFeedback.setText("<html><center>" + result.getFeedbackMessage() + "</center></html>");

        lblStatCorrect.setText(String.valueOf(result.getCorrectAnswers()));
        lblStatWrong.setText(String.valueOf(result.getWrongAnswers()));
        lblStatTotal.setText(String.valueOf(result.getTotalQuestions()));

        if (percentage > personalBestPercentage && percentage > 0) {
            personalBestPercentage = percentage;
            lblNewHighScore.setVisible(true);
            lblPersonalBest.setText("🏆 Personal Best: " + personalBestPercentage + "%");
        } else {
            lblNewHighScore.setVisible(false);
        }

        cardLayout.show(mainContainer, CARD_RESULT);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================
    // MAIN DESKTOP LAUNCHER
    // =========================================================================

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            DesktopQuizApp app = new DesktopQuizApp();
            app.setVisible(true);
        });
    }
}
