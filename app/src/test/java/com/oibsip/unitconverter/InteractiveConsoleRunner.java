package com.oibsip.unitconverter;

import com.oibsip.unitconverter.converter.UnitConverter;
import com.oibsip.unitconverter.model.Category;
import com.oibsip.unitconverter.model.Unit;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console Runner for testing and demonstrating the Unit Converter application
 * directly in the terminal without needing an Android emulator or device.
 */
public class InteractiveConsoleRunner {

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("   OIBSIP Task 1 - Unit Converter Application (CLI)      ");
        System.out.println("=========================================================");

        if (args.length == 3) {
            // Quick CLI mode: <value> <fromUnitSymbolOrName> <toUnitSymbolOrName>
            handleDirectConversion(args[0], args[1], args[2]);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nSelect a Measurement Category:");
            Category[] categories = Category.values();
            for (int i = 0; i < categories.length; i++) {
                System.out.printf("  [%d] %s (%s)%n", i + 1, categories[i].getDisplayName(), categories[i].getDescription());
            }
            System.out.println("  [T] Run All 34 Unit Verification Tests");
            System.out.println("  [0] Exit");
            System.out.print("\nEnter choice: ");

            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine().trim();

            if (input.equals("0") || input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("q")) {
                System.out.println("\nThank you for using the Unit Converter. Goodbye!");
                break;
            }

            if (input.equalsIgnoreCase("T")) {
                System.out.println();
                UnitConverterVerification.main(new String[0]);
                continue;
            }

            int catIndex;
            try {
                catIndex = Integer.parseInt(input) - 1;
                if (catIndex < 0 || catIndex >= categories.length) {
                    System.out.println("Invalid category selection. Please try again.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number from the menu.");
                continue;
            }

            Category selectedCategory = categories[catIndex];
            List<Unit> units = UnitConverter.getUnitsForCategory(selectedCategory);

            System.out.println("\nAvailable Units for " + selectedCategory.getDisplayName() + ":");
            for (int i = 0; i < units.size(); i++) {
                Unit u = units.get(i);
                System.out.printf("  [%d] %s%n", i + 1, u.getDisplayLabel());
            }

            System.out.print("\nSelect Source (From) Unit [1-" + units.size() + "]: ");
            int fromIndex = readUnitChoice(scanner, units.size());
            if (fromIndex == -1) continue;

            System.out.print("Select Target (To) Unit [1-" + units.size() + "]: ");
            int toIndex = readUnitChoice(scanner, units.size());
            if (toIndex == -1) continue;

            Unit fromUnit = units.get(fromIndex);
            Unit toUnit = units.get(toIndex);

            System.out.printf("\nEnter value to convert from %s to %s: ", fromUnit.getName(), toUnit.getName());
            if (!scanner.hasNextLine()) break;
            String valStr = scanner.nextLine().trim();

            try {
                double val = Double.parseDouble(valStr);

                if (UnitConverter.isBelowAbsoluteZero(val, fromUnit)) {
                    System.out.println("\n[ERROR] Value is below absolute zero! Physical temperature limit reached.");
                    continue;
                }

                double result = UnitConverter.convert(val, fromUnit, toUnit);
                String formatted = UnitConverter.formatResult(result);
                String formula = UnitConverter.getFormulaExplanation(val, fromUnit, result, toUnit);

                System.out.println("\n---------------------------------------------------------");
                System.out.println("  CONVERSION RESULT");
                System.out.println("---------------------------------------------------------");
                System.out.printf("  Input:   %s %s%n", UnitConverter.formatResult(val), fromUnit.getDisplayLabel());
                System.out.printf("  Result:  %s %s%n", formatted, toUnit.getSymbol());
                System.out.printf("  Formula: %s%n", formula);
                System.out.println("---------------------------------------------------------");

            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Invalid number format. Please enter a valid decimal number.");
            }
        }
    }

    private static int readUnitChoice(Scanner scanner, int max) {
        if (!scanner.hasNextLine()) return -1;
        String line = scanner.nextLine().trim();
        try {
            int val = Integer.parseInt(line) - 1;
            if (val >= 0 && val < max) {
                return val;
            }
        } catch (NumberFormatException ignored) {}
        System.out.println("Invalid unit choice.");
        return -1;
    }

    private static void handleDirectConversion(String valStr, String fromStr, String toStr) {
        try {
            double val = Double.parseDouble(valStr);
            Unit fromUnit = null;
            Unit toUnit = null;

            for (Category c : Category.values()) {
                for (Unit u : UnitConverter.getUnitsForCategory(c)) {
                    if (u.getId().equalsIgnoreCase(fromStr) ||
                        u.getSymbol().equalsIgnoreCase(fromStr) ||
                        u.getName().equalsIgnoreCase(fromStr)) {
                        fromUnit = u;
                    }
                    if (u.getId().equalsIgnoreCase(toStr) ||
                        u.getSymbol().equalsIgnoreCase(toStr) ||
                        u.getName().equalsIgnoreCase(toStr)) {
                        toUnit = u;
                    }
                }
            }

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
            System.out.println("Formula: " + formula);

        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric value: " + valStr);
        }
    }
}
