package com.oibsip.unitconverter;

import com.oibsip.unitconverter.converter.UnitConverter;
import com.oibsip.unitconverter.model.Category;
import com.oibsip.unitconverter.model.Unit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.util.List;

/**
 * Modern Desktop GUI application for the OIBSIP Unit Converter.
 * Provides a pixel-aligned Material Design desktop interface allowing users to run
 * and interact with the application directly on Windows without an Android emulator.
 */
public class DesktopUnitConverterApp extends JFrame {

    private final JComboBox<Category> cbCategory;
    private final JComboBox<Unit> cbFromUnit;
    private final JComboBox<Unit> cbToUnit;
    private final JTextField txtInput;
    private final JLabel lblResultValue;
    private final JLabel lblResultUnit;
    private final JLabel lblFormula;
    private final JButton btnCopy;
    private String lastResultFormatted = "";

    // Design System Colors matching Android colors.xml
    private static final Color COLOR_PRIMARY = new Color(0x43, 0x38, 0xCA);       // Deep Indigo #4338CA
    private static final Color COLOR_PRIMARY_DARK = new Color(0x37, 0x30, 0xA3);  // Dark Indigo #3730A3
    private static final Color COLOR_BG = new Color(0xF8, 0xFA, 0xFC);            // Slate 50 #F8FAFC
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_BORDER = new Color(0xCB, 0xD5, 0xE1);         // Slate 300 #CBD5E1
    private static final Color COLOR_TEXT_MAIN = new Color(0x0F, 0x17, 0x2A);      // Slate 900 #0F172A
    private static final Color COLOR_TEXT_MUTED = new Color(0x64, 0x74, 0x8B);     // Slate 500 #64748B
    private static final Color COLOR_SUCCESS_BG = new Color(0xF0, 0xFD, 0xF4);     // Green 50 #F0FDF4
    private static final Color COLOR_SUCCESS_BORDER = new Color(0xBB, 0xF7, 0xD0); // Green 200 #BBF7D0
    private static final Color COLOR_SUCCESS_TEXT = new Color(0x15, 0x80, 0x3D);   // Green 700 #15803D

    public DesktopUnitConverterApp() {
        super("OIBSIP · Unit Converter Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 750);
        setMinimumSize(new Dimension(420, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Center Content (Cards inside ScrollPane)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. Category Card
        JPanel categoryCard = createCardPanel();
        categoryCard.setLayout(new BorderLayout(8, 8));
        JLabel lblCatTitle = new JLabel("MEASUREMENT CATEGORY");
        lblCatTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCatTitle.setForeground(COLOR_TEXT_MUTED);
        categoryCard.add(lblCatTitle, BorderLayout.NORTH);

        cbCategory = new JComboBox<>(Category.values());
        styleComboBox(cbCategory);
        categoryCard.add(cbCategory, BorderLayout.CENTER);
        contentPanel.add(categoryCard);
        contentPanel.add(Box.createVerticalStrut(16));

        // 2. Conversion Input & Unit Selection Card
        JPanel inputCard = createCardPanel();
        inputCard.setLayout(new BoxLayout(inputCard, BoxLayout.Y_AXIS));

        JLabel lblInputTitle = new JLabel("ENTER VALUE TO CONVERT");
        lblInputTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblInputTitle.setForeground(COLOR_TEXT_MUTED);
        lblInputTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.add(lblInputTitle);
        inputCard.add(Box.createVerticalStrut(8));

        txtInput = new JTextField("1");
        txtInput.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtInput.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        txtInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.add(txtInput);
        inputCard.add(Box.createVerticalStrut(16));

        // From Unit
        JLabel lblFrom = new JLabel("FROM");
        lblFrom.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFrom.setForeground(COLOR_TEXT_MUTED);
        lblFrom.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.add(lblFrom);
        inputCard.add(Box.createVerticalStrut(6));

        cbFromUnit = new JComboBox<>();
        styleComboBox(cbFromUnit);
        cbFromUnit.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.add(cbFromUnit);
        inputCard.add(Box.createVerticalStrut(10));

        // Swap Button
        JButton btnSwap = new JButton("⇅  Swap Units");
        btnSwap.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSwap.setForeground(COLOR_PRIMARY);
        btnSwap.setBackground(new Color(0xEE, 0xF2, 0xFF));
        btnSwap.setBorder(new LineBorder(new Color(0xC7, 0xD2, 0xFE), 1, true));
        btnSwap.setFocusPainted(false);
        btnSwap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSwap.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSwap.setMaximumSize(new Dimension(140, 34));
        btnSwap.addActionListener(e -> swapUnits());
        inputCard.add(btnSwap);
        inputCard.add(Box.createVerticalStrut(10));

        // To Unit
        JLabel lblTo = new JLabel("TO");
        lblTo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTo.setForeground(COLOR_TEXT_MUTED);
        lblTo.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.add(lblTo);
        inputCard.add(Box.createVerticalStrut(6));

        cbToUnit = new JComboBox<>();
        styleComboBox(cbToUnit);
        cbToUnit.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.add(cbToUnit);
        inputCard.add(Box.createVerticalStrut(20));

        // Action Buttons: Convert & Reset
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonRow.setBackground(COLOR_CARD_BG);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnConvert = new JButton("Convert Now");
        btnConvert.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConvert.setBackground(COLOR_PRIMARY);
        btnConvert.setForeground(Color.WHITE);
        btnConvert.setFocusPainted(false);
        btnConvert.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnConvert.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConvert.addActionListener(e -> performConversion());

        JButton btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReset.setBackground(new Color(0xF1, 0xF5, 0xF9));
        btnReset.setForeground(COLOR_TEXT_MAIN);
        btnReset.setFocusPainted(false);
        btnReset.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> resetForm());

        buttonRow.add(btnConvert);
        buttonRow.add(btnReset);
        inputCard.add(buttonRow);

        contentPanel.add(inputCard);
        contentPanel.add(Box.createVerticalStrut(16));

        // 3. Result Card
        JPanel resultCard = createCardPanel();
        resultCard.setBackground(COLOR_SUCCESS_BG);
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_SUCCESS_BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));

        JLabel lblResHeading = new JLabel("CONVERTED RESULT");
        lblResHeading.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblResHeading.setForeground(COLOR_SUCCESS_TEXT);
        lblResHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultCard.add(lblResHeading);
        resultCard.add(Box.createVerticalStrut(8));

        JPanel resultValRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        resultValRow.setBackground(COLOR_SUCCESS_BG);
        resultValRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblResultValue = new JLabel("---");
        lblResultValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblResultValue.setForeground(COLOR_SUCCESS_TEXT);

        lblResultUnit = new JLabel("");
        lblResultUnit.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblResultUnit.setForeground(COLOR_SUCCESS_TEXT);

        resultValRow.add(lblResultValue);
        resultValRow.add(lblResultUnit);
        resultCard.add(resultValRow);
        resultCard.add(Box.createVerticalStrut(6));

        lblFormula = new JLabel("Enter a value and tap Convert to see results.");
        lblFormula.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFormula.setForeground(COLOR_TEXT_MUTED);
        lblFormula.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultCard.add(lblFormula);
        resultCard.add(Box.createVerticalStrut(12));

        btnCopy = new JButton("📋 Copy Result");
        btnCopy.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCopy.setBackground(Color.WHITE);
        btnCopy.setForeground(COLOR_SUCCESS_TEXT);
        btnCopy.setBorder(new LineBorder(COLOR_SUCCESS_BORDER, 1, true));
        btnCopy.setFocusPainted(false);
        btnCopy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCopy.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCopy.addActionListener(e -> copyResultToClipboard());
        resultCard.add(btnCopy);

        contentPanel.add(resultCard);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Setup Category change listener
        cbCategory.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateUnitDropdowns((Category) cbCategory.getSelectedItem());
            }
        });

        // Initialize with default category
        updateUnitDropdowns((Category) cbCategory.getSelectedItem());
        performConversion();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel lblBadge = new JLabel("OASIS INFOBYTE · TASK 1");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setForeground(new Color(0xC7, 0xD2, 0xFE));
        lblBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Unit Converter");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Instant, accurate conversions across 6 domains");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(0xE0, 0xE7, 0xFF));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblBadge);
        header.add(Box.createVerticalStrut(6));
        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblSub);

        return header;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(14, 16, 16, 16)
        ));
        return card;
    }

    private <T> void styleComboBox(JComboBox<T> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(COLOR_TEXT_MAIN);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private void updateUnitDropdowns(Category category) {
        if (category == null) return;
        List<Unit> units = UnitConverter.getUnitsForCategory(category);

        cbFromUnit.removeAllItems();
        cbToUnit.removeAllItems();

        for (Unit u : units) {
            cbFromUnit.addItem(u);
            cbToUnit.addItem(u);
        }

        if (units.size() > 1) {
            cbToUnit.setSelectedIndex(1);
        }
    }

    private void swapUnits() {
        int fromIdx = cbFromUnit.getSelectedIndex();
        int toIdx = cbToUnit.getSelectedIndex();
        cbFromUnit.setSelectedIndex(toIdx);
        cbToUnit.setSelectedIndex(fromIdx);
        performConversion();
    }

    private void performConversion() {
        String inputStr = txtInput.getText().trim();
        if (inputStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a numeric value to convert.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double value = Double.parseDouble(inputStr);
            Unit fromUnit = (Unit) cbFromUnit.getSelectedItem();
            Unit toUnit = (Unit) cbToUnit.getSelectedItem();

            if (fromUnit == null || toUnit == null) return;

            if (UnitConverter.isBelowAbsoluteZero(value, fromUnit)) {
                JOptionPane.showMessageDialog(this,
                        "Value is below Absolute Zero (" + (fromUnit.getId().equals("temp_k") ? "0 K" : "-273.15 °C") + ").\nPhysical limit reached.",
                        "Absolute Zero Limit", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double result = UnitConverter.convert(value, fromUnit, toUnit);
            String formattedResult = UnitConverter.formatResult(result);
            String formula = UnitConverter.getFormulaExplanation(value, fromUnit, result, toUnit);

            lblResultValue.setText(formattedResult);
            lblResultUnit.setText(toUnit.getSymbol());
            lblFormula.setText("Formula: " + formula);
            lastResultFormatted = formattedResult + " " + toUnit.getSymbol();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid decimal number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtInput.setText("1");
        lblResultValue.setText("---");
        lblResultUnit.setText("");
        lblFormula.setText("Enter a value and tap Convert to see results.");
        lastResultFormatted = "";
        cbCategory.setSelectedIndex(0);
        updateUnitDropdowns((Category) cbCategory.getSelectedItem());
        performConversion();
    }

    private void copyResultToClipboard() {
        if (lastResultFormatted == null || lastResultFormatted.isEmpty() || lastResultFormatted.contains("---")) {
            JOptionPane.showMessageDialog(this, "No result available to copy.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(lastResultFormatted), null);
        JOptionPane.showMessageDialog(this, "Copied \"" + lastResultFormatted + "\" to clipboard!", "Copied", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new DesktopUnitConverterApp().setVisible(true);
        });
    }
}
