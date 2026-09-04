package com.oibsip.unitconverter;

import com.oibsip.unitconverter.converter.UnitConverter;
import com.oibsip.unitconverter.model.Category;
import com.oibsip.unitconverter.model.Unit;

import java.util.List;

/**
 * Standalone verification runner that validates all conversion calculations,
 * physical limits, formatting, and edge cases without requiring external test runners.
 */
public class UnitConverterVerification {

    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static final double DELTA = 1e-5;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  OIBSIP Task 1 - Unit Converter Engine Tests    ");
        System.out.println("=================================================");

        testLength();
        testWeight();
        testTemperature();
        testVolume();
        testSpeed();
        testTime();
        testAbsoluteZeroValidation();
        testFormatting();

        System.out.println("-------------------------------------------------");
        System.out.println("Summary: " + testsPassed + " passed, " + testsFailed + " failed.");
        System.out.println("=================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void assertEquals(String testName, double expected, double actual, double delta) {
        if (Math.abs(expected - actual) <= delta) {
            System.out.println("  [PASS] " + testName + " -> " + actual);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName + " -> Expected: " + expected + ", Got: " + actual);
            testsFailed++;
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName + " -> Expected TRUE, got FALSE");
            testsFailed++;
        }
    }

    private static void assertFalse(String testName, boolean condition) {
        assertTrue(testName, !condition);
    }

    private static Unit findUnit(Category category, String unitId) {
        List<Unit> units = UnitConverter.getUnitsForCategory(category);
        for (Unit u : units) {
            if (u.getId().equals(unitId)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Unit not found: " + unitId);
    }

    private static void testLength() {
        System.out.println("\nTesting Length Conversions:");
        Unit cm = findUnit(Category.LENGTH, "len_cm");
        Unit m = findUnit(Category.LENGTH, "len_m");
        Unit km = findUnit(Category.LENGTH, "len_km");
        Unit in = findUnit(Category.LENGTH, "len_in");
        Unit mi = findUnit(Category.LENGTH, "len_mi");

        assertEquals("100 cm to m", 1.0, UnitConverter.convert(100.0, cm, m), DELTA);
        assertEquals("1 km to m", 1000.0, UnitConverter.convert(1.0, km, m), DELTA);
        assertEquals("1 m to cm", 100.0, UnitConverter.convert(1.0, m, cm), DELTA);
        assertEquals("1 in to m", 0.0254, UnitConverter.convert(1.0, in, m), DELTA);
        assertEquals("1 mi to m", 1609.344, UnitConverter.convert(1.0, mi, m), DELTA);
    }

    private static void testWeight() {
        System.out.println("\nTesting Weight Conversions:");
        Unit g = findUnit(Category.WEIGHT, "wt_g");
        Unit kg = findUnit(Category.WEIGHT, "wt_kg");
        Unit mg = findUnit(Category.WEIGHT, "wt_mg");
        Unit ton = findUnit(Category.WEIGHT, "wt_ton");
        Unit lb = findUnit(Category.WEIGHT, "wt_lb");

        assertEquals("1 kg to g", 1000.0, UnitConverter.convert(1.0, kg, g), DELTA);
        assertEquals("1000 mg to g", 1.0, UnitConverter.convert(1000.0, mg, g), DELTA);
        assertEquals("1 ton to kg", 1000.0, UnitConverter.convert(1.0, ton, kg), DELTA);
        assertEquals("1 lb to kg", 0.45359237, UnitConverter.convert(1.0, lb, kg), DELTA);
    }

    private static void testTemperature() {
        System.out.println("\nTesting Temperature Conversions:");
        Unit c = findUnit(Category.TEMPERATURE, "temp_c");
        Unit f = findUnit(Category.TEMPERATURE, "temp_f");
        Unit k = findUnit(Category.TEMPERATURE, "temp_k");

        assertEquals("0 °C to °F", 32.0, UnitConverter.convert(0.0, c, f), DELTA);
        assertEquals("100 °C to °F", 212.0, UnitConverter.convert(100.0, c, f), DELTA);
        assertEquals("-40 °C to °F", -40.0, UnitConverter.convert(-40.0, c, f), DELTA);
        assertEquals("0 °C to K", 273.15, UnitConverter.convert(0.0, c, k), DELTA);
        assertEquals("373.15 K to °C", 100.0, UnitConverter.convert(373.15, k, c), DELTA);
        assertEquals("212 °F to K", 373.15, UnitConverter.convert(212.0, f, k), DELTA);
    }

    private static void testVolume() {
        System.out.println("\nTesting Volume Conversions:");
        Unit ml = findUnit(Category.VOLUME, "vol_ml");
        Unit l = findUnit(Category.VOLUME, "vol_l");
        Unit gal = findUnit(Category.VOLUME, "vol_gal");

        assertEquals("1000 mL to L", 1.0, UnitConverter.convert(1000.0, ml, l), DELTA);
        assertEquals("1 gal to L", 3.785411784, UnitConverter.convert(1.0, gal, l), DELTA);
    }

    private static void testSpeed() {
        System.out.println("\nTesting Speed Conversions:");
        Unit ms = findUnit(Category.SPEED, "spd_ms");
        Unit kmh = findUnit(Category.SPEED, "spd_kmh");
        Unit mph = findUnit(Category.SPEED, "spd_mph");

        assertEquals("36 km/h to m/s", 10.0, UnitConverter.convert(36.0, kmh, ms), DELTA);
        assertEquals("60 mph to km/h", 96.56064, UnitConverter.convert(60.0, mph, kmh), 0.01);
    }

    private static void testTime() {
        System.out.println("\nTesting Time Conversions:");
        Unit s = findUnit(Category.TIME, "time_s");
        Unit min = findUnit(Category.TIME, "time_min");
        Unit hr = findUnit(Category.TIME, "time_h");
        Unit day = findUnit(Category.TIME, "time_d");

        assertEquals("60 s to min", 1.0, UnitConverter.convert(60.0, s, min), DELTA);
        assertEquals("1 hr to s", 3600.0, UnitConverter.convert(1.0, hr, s), DELTA);
        assertEquals("1 day to hr", 24.0, UnitConverter.convert(1.0, day, hr), DELTA);
    }

    private static void testAbsoluteZeroValidation() {
        System.out.println("\nTesting Absolute Zero Validation:");
        Unit c = findUnit(Category.TEMPERATURE, "temp_c");
        Unit k = findUnit(Category.TEMPERATURE, "temp_k");
        Unit f = findUnit(Category.TEMPERATURE, "temp_f");
        Unit m = findUnit(Category.LENGTH, "len_m");

        assertTrue("-273.16 °C is below abs zero", UnitConverter.isBelowAbsoluteZero(-273.16, c));
        assertFalse("-273.15 °C is valid abs zero", UnitConverter.isBelowAbsoluteZero(-273.15, c));
        assertFalse("25 °C is above abs zero", UnitConverter.isBelowAbsoluteZero(25.0, c));
        assertTrue("-0.01 K is below abs zero", UnitConverter.isBelowAbsoluteZero(-0.01, k));
        assertFalse("0 K is valid", UnitConverter.isBelowAbsoluteZero(0.0, k));
        assertTrue("-460 °F is below abs zero", UnitConverter.isBelowAbsoluteZero(-460.0, f));
        assertFalse("-459.67 °F is valid", UnitConverter.isBelowAbsoluteZero(-459.67, f));
        assertFalse("Length -10 is not temperature", UnitConverter.isBelowAbsoluteZero(-10.0, m));
    }

    private static void testFormatting() {
        System.out.println("\nTesting Formatting and Formulas:");
        assertTrue("Format 100.0", "100".equals(UnitConverter.formatResult(100.0)));
        assertTrue("Format 0.0", "0".equals(UnitConverter.formatResult(0.0)));
        assertTrue("Format 1.25", "1.25".equals(UnitConverter.formatResult(1.25)));

        Unit m = findUnit(Category.LENGTH, "len_m");
        Unit cm = findUnit(Category.LENGTH, "len_cm");
        String formula = UnitConverter.getFormulaExplanation(1.0, m, 100.0, cm);
        assertTrue("Formula non-null", formula != null && formula.contains("1 m = 100 cm"));
    }
}
