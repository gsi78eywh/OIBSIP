package com.oibsip.unitconverter;

import com.oibsip.unitconverter.converter.UnitConverter;
import com.oibsip.unitconverter.model.Category;
import com.oibsip.unitconverter.model.Unit;

import java.util.List;
import java.util.Scanner;

/**
 * Lightweight, easy-to-read console runner for the Unit Converter.
 * Supports both an interactive terminal menu and direct command-line arguments.
 */
public class InteractiveConsoleRunner {

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("   OIBSIP Task 1 - Unit Converter Application (CLI)      ");
        System.out.println("=========================================================");

        // Mode 1: Quick CLI conversion (e.g., "100 m cm" or "0 °C °F")
        if (args.length == 3) {
            handleDirectConversion(args[0], args[1], args[2]);
            return;
        }

        // Mode 2: Interactive Menu
        runInteractiveMenu();
    }

    /**
     * Interactive terminal menu loop.
     */
    private static void runInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        Category[] categories = Category.values();

        while (true) {
            System.out.println("\n--- Select Measurement Category ---");
            for (int i = 0; i < categories.length; i++) {
                System.out.printf("  [%d] %s (%s)%n", i + 1, categories[i].getDisplayName(), categories[i].getDescription());
            }
            System.out.println("  [0] Exit");
            System.out.print("Enter choice: ");

            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();
            if (input.equals("0") || input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("q")) {
                System.out.println("Goodbye!");
                break;
            }

            int catIdx = parseIndex(input, categories.length);
            if (catIdx == -1) {
                System.out.println("Invalid category choice. Please try again.");
                continue;
            }

            Category selectedCategory = categories[catIdx];
            List<Unit> units = UnitConverter.getUnitsForCategory(selectedCategory);

            System.out.println("\nAvailable Units for " + selectedCategory.getDisplayName() + ":");
            for (int i = 0; i < units.size(); i++) {
                System.out.printf("  [%d] %s%n", i + 1, units.get(i).getDisplayLabel());
            }

            System.out.print("Select From Unit: ");
            if (!scanner.hasNextLine()) break;
            int fromIdx = parseIndex(scanner.nextLine(), units.size());

            System.out.print("Select To Unit: ");
            if (!scanner.hasNextLine()) break;
            int toIdx = parseIndex(scanner.nextLine(), units.size());

            if (fromIdx == -1 || toIdx == -1) {
                System.out.println("Invalid unit selection. Please try again.");
                continue;
            }

            System.out.print("Enter value to convert: ");
            if (!scanner.hasNextLine()) break;
            String valStr = scanner.nextLine().trim();

            handleDirectConversion(valStr, units.get(fromIdx).getId(), units.get(toIdx).getId());
        }
    }

    /**
     * Executes conversion between two units, validates limits, and prints results.
     */
    public static void handleDirectConversion(String valStr, String fromStr, String toStr) {
        try {
            double val = Double.parseDouble(valStr);
            Unit fromUnit = findUnit(fromStr);
            Unit toUnit = findUnit(toStr);

            if (fromUnit == null || toUnit == null) {
                System.out.println("[ERROR] Could not recognize one of the units: '" + fromStr + "' or '" + toStr + "'");
                return;
            }

            if (fromUnit.getCategory() != toUnit.getCategory()) {
                System.out.println("[ERROR] Cannot convert between different categories: " +
                        fromUnit.getCategory() + " and " + toUnit.getCategory());
                return;
            }

            if (UnitConverter.isBelowAbsoluteZero(val, fromUnit)) {
                System.out.println("[ERROR] Value is below absolute zero!");
                return;
            }

            double result = UnitConverter.convert(val, fromUnit, toUnit);
            String formatted = UnitConverter.formatResult(result);
            String formula = UnitConverter.getFormulaExplanation(val, fromUnit, result, toUnit);

            System.out.printf("%s %s = %s %s%n", UnitConverter.formatResult(val), fromUnit.getSymbol(), formatted, toUnit.getSymbol());
            if (formula.startsWith("Formula: ")) {
                System.out.println(formula);
            } else {
                System.out.println("Formula: " + formula);
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric value: " + valStr);
        }
    }

    /**
     * Finds a Unit matching by ID, symbol, or name (case-insensitive).
     */
    private static Unit findUnit(String search) {
        if (search == null || search.trim().isEmpty()) return null;
        for (Category category : Category.values()) {
            for (Unit unit : UnitConverter.getUnitsForCategory(category)) {
                if (unit.getId().equalsIgnoreCase(search) ||
                    unit.getSymbol().equalsIgnoreCase(search) ||
                    unit.getName().equalsIgnoreCase(search)) {
                    return unit;
                }
            }
        }
        return null;
    }

    /**
     * Safely parses user 1-based index input into a 0-based integer.
     */
    private static int parseIndex(String text, int max) {
        try {
            int index = Integer.parseInt(text.trim()) - 1;
            return (index >= 0 && index < max) ? index : -1;
        } catch (Exception e) {
            return -1;
        }
    }
}
