package com.oibsip.stopwatch;

import com.oibsip.stopwatch.engine.StopwatchEngine;
import com.oibsip.stopwatch.model.LapItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Modern Desktop GUI application for TASK 5: Stopwatch & Lap Timer.
 * Provides an authentic Material-styled desktop experience runnable directly on Windows.
 */
public class DesktopStopwatchApp extends JFrame {

    private final StopwatchEngine engine = new StopwatchEngine();
    private final Timer timer;

    private final JLabel lblMainDigits;
    private final JLabel lblCentiseconds;
    private final JLabel lblStateBadge;
    private final JButton btnStart;
    private final JButton btnPause;
    private final JButton btnReset;
    private final JButton btnLap;
    private final DefaultListModel<String> lapListModel;
    private final JLabel lblLapCount;

    // Design System Colors matching Android colors.xml
    private static final Color COLOR_PRIMARY = new Color(0x43, 0x38, 0xCA);       // Deep Indigo #4338CA
    private static final Color COLOR_BG = new Color(0xF8, 0xFA, 0xFC);            // Slate 50 #F8FAFC
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_BORDER = new Color(0xE2, 0xE8, 0xF0);         // Slate 200 #E2E8F0
    private static final Color COLOR_TEXT_MAIN = new Color(0x0F, 0x17, 0x2A);      // Slate 900 #0F172A
    private static final Color COLOR_TEXT_MUTED = new Color(0x64, 0x74, 0x8B);     // Slate 500 #64748B
    private static final Color COLOR_START = new Color(0x10, 0xB9, 0x81);          // Emerald 500 #10B981
    private static final Color COLOR_PAUSE = new Color(0xF5, 0x9E, 0x0B);          // Amber 500 #F59E0B
    private static final Color COLOR_RESET = new Color(0xEF, 0x44, 0x44);          // Red 500 #EF4444

    public DesktopStopwatchApp() {
        super("OIBSIP · Task 5: Stopwatch & Lap Timer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 720);
        setMinimumSize(new Dimension(420, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_CARD_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER),
                new EmptyBorder(18, 24, 18, 24)
        ));

        JLabel lblTag = new JLabel("OASIS INFOBYTE · TASK 5");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTag.setForeground(COLOR_PRIMARY);

        JLabel lblTitle = new JLabel("Stopwatch & Lap Timer");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_TEXT_MAIN);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        titleBox.add(lblTag);
        titleBox.add(lblTitle);
        headerPanel.add(titleBox, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center Panel (Display + Buttons + Lap List)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(COLOR_BG);
        centerPanel.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Time Card
        JPanel timeCard = new JPanel();
        timeCard.setLayout(new BoxLayout(timeCard, BoxLayout.Y_AXIS));
        timeCard.setBackground(COLOR_CARD_BG);
        timeCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(24, 20, 24, 20)
        ));
        timeCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblStateBadge = new JLabel("READY");
        lblStateBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStateBadge.setForeground(COLOR_PRIMARY);
        lblStateBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeCard.add(lblStateBadge);
        timeCard.add(Box.createVerticalStrut(12));

        JPanel digitsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        digitsPanel.setOpaque(false);

        lblMainDigits = new JLabel("00:00");
        lblMainDigits.setFont(new Font("Consolas", Font.BOLD, 54));
        lblMainDigits.setForeground(COLOR_TEXT_MAIN);

        lblCentiseconds = new JLabel(".00");
        lblCentiseconds.setFont(new Font("Consolas", Font.BOLD, 32));
        lblCentiseconds.setForeground(COLOR_PRIMARY);

        digitsPanel.add(lblMainDigits);
        digitsPanel.add(lblCentiseconds);
        timeCard.add(digitsPanel);
        centerPanel.add(timeCard);
        centerPanel.add(Box.createVerticalStrut(16));

        // 3. Action Buttons Row
        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        btnReset = createStyledButton("Reset", COLOR_RESET);
        btnLap   = createStyledButton("Lap", COLOR_PRIMARY);
        btnPause = createStyledButton("Pause", COLOR_PAUSE);
        btnStart = createStyledButton("Start", COLOR_START);

        btnPanel.add(btnReset);
        btnPanel.add(btnLap);
        btnPanel.add(btnPause);
        btnPanel.add(btnStart);
        centerPanel.add(btnPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // 4. Lap History Section
        JPanel lapHeader = new JPanel(new BorderLayout());
        lapHeader.setOpaque(false);
        JLabel lblLapTitle = new JLabel("LAP HISTORY");
        lblLapTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLapTitle.setForeground(COLOR_TEXT_MUTED);

        lblLapCount = new JLabel("(0)");
        lblLapCount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLapCount.setForeground(COLOR_TEXT_MUTED);

        lapHeader.add(lblLapTitle, BorderLayout.WEST);
        lapHeader.add(lblLapCount, BorderLayout.EAST);
        centerPanel.add(lapHeader);
        centerPanel.add(Box.createVerticalStrut(8));

        lapListModel = new DefaultListModel<>();
        JList<String> lapList = new JList<>(lapListModel);
        lapList.setFont(new Font("Consolas", Font.PLAIN, 14));
        lapList.setBackground(COLOR_CARD_BG);
        lapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lapList.setBorder(new EmptyBorder(8, 12, 8, 12));

        JScrollPane scrollPane = new JScrollPane(lapList);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // Timer Loop (~30ms for 33 FPS smooth refresh)
        timer = new Timer(30, e -> updateClock());

        setupListeners();
        updateButtonStates();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setupListeners() {
        btnStart.addActionListener(e -> {
            long now = System.currentTimeMillis();
            engine.start(now);
            timer.start();
            updateButtonStates();
        });

        btnPause.addActionListener(e -> {
            long now = System.currentTimeMillis();
            engine.pause(now);
            timer.stop();
            updateClock();
            updateButtonStates();
        });

        btnReset.addActionListener(e -> {
            engine.reset();
            timer.stop();
            lblMainDigits.setText("00:00");
            lblCentiseconds.setText(".00");
            lapListModel.clear();
            lblLapCount.setText("(0)");
            updateButtonStates();
        });

        btnLap.addActionListener(e -> {
            long now = System.currentTimeMillis();
            LapItem lap = engine.recordLap(now);
            if (lap != null) {
                String row = String.format("Lap %02d     +%-10s    Total: %s",
                        lap.getLapNumber(), lap.getFormattedLapDuration(), lap.getFormattedTotalElapsed());
                lapListModel.add(0, row);
                lblLapCount.setText("(" + lapListModel.size() + ")");
            }
        });
    }

    private void updateClock() {
        long now = System.currentTimeMillis();
        long elapsed = engine.getElapsedTime(now);
        lblMainDigits.setText(StopwatchEngine.formatMainDigits(elapsed));
        lblCentiseconds.setText(StopwatchEngine.formatCentiseconds(elapsed));
    }

    private void updateButtonStates() {
        StopwatchEngine.State state = engine.getState();
        switch (state) {
            case RUNNING:
                lblStateBadge.setText("RUNNING");
                lblStateBadge.setForeground(COLOR_START);
                btnStart.setEnabled(false);
                btnPause.setEnabled(true);
                btnReset.setEnabled(false);
                btnLap.setEnabled(true);
                break;
            case PAUSED:
                lblStateBadge.setText("PAUSED");
                lblStateBadge.setForeground(COLOR_PAUSE);
                btnStart.setEnabled(true);
                btnStart.setText("Resume");
                btnPause.setEnabled(false);
                btnReset.setEnabled(true);
                btnLap.setEnabled(false);
                break;
            case STOPPED:
            default:
                lblStateBadge.setText("READY");
                lblStateBadge.setForeground(COLOR_PRIMARY);
                btnStart.setEnabled(true);
                btnStart.setText("Start");
                btnPause.setEnabled(false);
                btnReset.setEnabled(false);
                btnLap.setEnabled(false);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new DesktopStopwatchApp().setVisible(true);
        });
    }
}
