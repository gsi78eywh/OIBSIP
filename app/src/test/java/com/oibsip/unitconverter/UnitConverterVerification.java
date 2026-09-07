package com.oibsip.unitconverter;

import com.oibsip.unitconverter.converter.UnitConverter;
import com.oibsip.unitconverter.model.Category;
import com.oibsip.unitconverter.model.Unit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Standalone, zero-dependency verification runner that validates all conversion calculations,
 * category and unit models, physical limits, formatting, and edge cases.
 * Can be executed anywhere directly on standard Java without needing JUnit.
 */
public class UnitConverterVerification {

    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static final double DELTA = 1e-4;

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("     OIBSIP Task 1 - Unit Converter Comprehensive Verification      ");
        System.out.println("====================================================================");

        testCategoryModel();
        testUnitModel();
        testRegistryIntegrity();
        testLength();
        testWeight();
        testTemperatureMatrix();
        testVolume();
        testSpeed();
        testTime();
        testAbsoluteZeroValidation();
        testIdentityConversions();
        testFormatting();
        testFormulas();

        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("Summary: " + testsPassed + " passed, " + testsFailed + " failed.");
        System.out.println("Status:  " + (testsFailed == 0 ? "[SUCCESS] All tests passed cleanly!" : "[FAILURE] Some tests failed!"));
        System.out.println("====================================================================");

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

    private static void assertEquals(String testName, int expected, int actual) {
        if (expected == actual) {
            System.out.println("  [PASS] " + testName + " -> " + actual);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName + " -> Expected: " + expected + ", Got: " + actual);
            testsFailed++;
        }
    }

    private static void assertEquals(String testName, String expected, String actual) {
        if (expected != null && expected.equals(actual)) {
            System.out.println("  [PASS] " + testName + " -> \"" + actual + "\"");
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName + " -> Expected: \"" + expected + "\", Got: \"" + actual + "\"");
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

    private static void testCategoryModel() {
        System.out.println("\n1. Testing Category Model:");
        assertEquals("6 measurement categories", 6.0, Category.values().length, 0.0);
        for (Category c : Category.values()) {
            assertTrue("Category display name non-empty: " + c.name(), c.getDisplayName() != null && !c.getDisplayName().isEmpty());
            assertTrue("Category description non-empty: " + c.name(), c.getDescription() != null && !c.getDescription().isEmpty());
            assertEquals("toString matches display name", c.getDisplayName(), c.toString());
        }
    }

    private static void testUnitModel() {
        System.out.println("\n2. Testing Unit Model & Equals Contract:");
        Unit u1 = new Unit("u_test", "Test Unit", "tu", Category.LENGTH, 1.0);
        Unit u2 = new Unit("u_test", "Test Unit Duplicate", "tu", Category.LENGTH, 1.0);
        Unit u3 = new Unit("u_diff", "Different Unit", "du", Category.LENGTH, 2.0);

        assertTrue("Unit equals itself", u1.equals(u1));
        assertTrue("Units with same ID equal", u1.equals(u2));
        assertFalse("Units with different ID not equal", u1.equals(u3));
        assertFalse("Unit not equal to null", u1.equals(null));
        assertEquals("Equal units share hashcode", u1.hashCode(), u2.hashCode());
        assertEquals("Display label formatting", "Test Unit (tu)", u1.getDisplayLabel());
    }

    private static void testRegistryIntegrity() {
        System.out.println("\n3. Testing Unit Registry Integrity:");
        Set<String> uniqueIds = new HashSet<>();
        int count = 0;
        for (Category c : Category.values()) {
            List<Unit> units = UnitConverter.getUnitsForCategory(c);
            assertTrue("Category " + c.name() + " has units", units != null && !units.isEmpty());
            for (Unit u : units) {
                count++;
                assertTrue("Unit ID unique: " + u.getId(), uniqueIds.add(u.getId()));
            }
        }
        assertEquals("Total units registered = 32", 32.0, count, 0.0);
        assertTrue("getUnitsForCategory(null) is empty list", UnitConverter.getUnitsForCategory(null).isEmpty());
    }

    private static void testLength() {
        System.out.println("\n4. Testing Length Conversions:");
        Unit mm = findUnit(Category.LENGTH, "len_mm");
        Unit cm = findUnit(Category.LENGTH, "len_cm");
        Unit m = findUnit(Category.LENGTH, "len_m");
        Unit km = findUnit(Category.LENGTH, "len_km");
        Unit in = findUnit(Category.LENGTH, "len_in");
        Unit ft = findUnit(Category.LENGTH, "len_ft");
        Unit yd = findUnit(Category.LENGTH, "len_yd");
        Unit mi = findUnit(Category.LENGTH, "len_mi");

        assertEquals("1000 mm to m", 1.0, UnitConverter.convert(1000.0, mm, m), DELTA);
        assertEquals("100 cm to m", 1.0, UnitConverter.convert(100.0, cm, m), DELTA);
        assertEquals("1 km to m", 1000.0, UnitConverter.convert(1.0, km, m), DELTA);
        assertEquals("1 in to m", 0.0254, UnitConverter.convert(1.0, in, m), DELTA);
        assertEquals("1 ft to in", 12.0, UnitConverter.convert(1.0, ft, in), DELTA);
        assertEquals("1 yd to ft", 3.0, UnitConverter.convert(1.0, yd, ft), DELTA);
        assertEquals("1 mi to yd", 1760.0, UnitConverter.convert(1.0, mi, yd), DELTA);
        assertEquals("1 mi to m", 1609.344, UnitConverter.convert(1.0, mi, m), DELTA);
    }

    private static void testWeight() {
        System.out.println("\n5. Testing Weight Conversions:");
        Unit mg = findUnit(Category.WEIGHT, "wt_mg");
        Unit g = findUnit(Category.WEIGHT, "wt_g");
        Unit kg = findUnit(Category.WEIGHT, "wt_kg");
        Unit ton = findUnit(Category.WEIGHT, "wt_ton");
        Unit oz = findUnit(Category.WEIGHT, "wt_oz");
        Unit lb = findUnit(Category.WEIGHT, "wt_lb");

        assertEquals("1,000,000 mg to kg", 1.0, UnitConverter.convert(1000000.0, mg, kg), DELTA);
        assertEquals("1000 g to kg", 1.0, UnitConverter.convert(1000.0, g, kg), DELTA);
        assertEquals("1 ton to kg", 1000.0, UnitConverter.convert(1.0, ton, kg), DELTA);
        assertEquals("1 lb to oz", 16.0, UnitConverter.convert(1.0, lb, oz), DELTA);
        assertEquals("1 lb to kg", 0.45359237, UnitConverter.convert(1.0, lb, kg), DELTA);
    }

    private static void testTemperatureMatrix() {
        System.out.println("\n6. Testing Temperature 6-Way Matrix:");
        Unit c = findUnit(Category.TEMPERATURE, "temp_c");
        Unit f = findUnit(Category.TEMPERATURE, "temp_f");
        Unit k = findUnit(Category.TEMPERATURE, "temp_k");

        // C <-> F
        assertEquals("0 °C to °F", 32.0, UnitConverter.convert(0.0, c, f), DELTA);
        assertEquals("100 °C to °F", 212.0, UnitConverter.convert(100.0, c, f), DELTA);
        assertEquals("-40 °C to °F", -40.0, UnitConverter.convert(-40.0, c, f), DELTA);
        assertEquals("32 °F to °C", 0.0, UnitConverter.convert(32.0, f, c), DELTA);
        assertEquals("212 °F to °C", 100.0, UnitConverter.convert(212.0, f, c), DELTA);

        // C <-> K
        assertEquals("0 °C to K", 273.15, UnitConverter.convert(0.0, c, k), DELTA);
        assertEquals("-273.15 °C to K", 0.0, UnitConverter.convert(-273.15, c, k), DELTA);
        assertEquals("273.15 K to °C", 0.0, UnitConverter.convert(273.15, k, c), DELTA);
        assertEquals("0 K to °C", -273.15, UnitConverter.convert(0.0, k, c), DELTA);

        // F <-> K
        assertEquals("32 °F to K", 273.15, UnitConverter.convert(32.0, f, k), DELTA);
        assertEquals("273.15 K to °F", 32.0, UnitConverter.convert(273.15, k, f), DELTA);
        assertEquals("-459.67 °F to K", 0.0, UnitConverter.convert(-459.67, f, k), DELTA);
        assertEquals("0 K to °F", -459.67, UnitConverter.convert(0.0, k, f), DELTA);
    }

    private static void testVolume() {
        System.out.println("\n7. Testing Volume Conversions:");
        Unit ml = findUnit(Category.VOLUME, "vol_ml");
        Unit l = findUnit(Category.VOLUME, "vol_l");
        Unit floz = findUnit(Category.VOLUME, "vol_floz");
        Unit cup = findUnit(Category.VOLUME, "vol_cup");
        Unit pt = findUnit(Category.VOLUME, "vol_pt");
        Unit gal = findUnit(Category.VOLUME, "vol_gal");

        assertEquals("1000 mL to L", 1.0, UnitConverter.convert(1000.0, ml, l), DELTA);
        assertEquals("1 gal to L", 3.785411784, UnitConverter.convert(1.0, gal, l), DELTA);
        assertEquals("1 gal to pt", 8.0, UnitConverter.convert(1.0, gal, pt), DELTA);
        assertEquals("1 pt to cup", 2.0, UnitConverter.convert(1.0, pt, cup), DELTA);
        assertEquals("1 cup to fl oz", 8.0, UnitConverter.convert(1.0, cup, floz), DELTA);
    }

    private static void testSpeed() {
        System.out.println("\n8. Testing Speed Conversions:");
        Unit ms = findUnit(Category.SPEED, "spd_ms");
        Unit kmh = findUnit(Category.SPEED, "spd_kmh");
        Unit mph = findUnit(Category.SPEED, "spd_mph");
        Unit knot = findUnit(Category.SPEED, "spd_knot");

        assertEquals("36 km/h to m/s", 10.0, UnitConverter.convert(36.0, kmh, ms), DELTA);
        assertEquals("10 m/s to km/h", 36.0, UnitConverter.convert(10.0, ms, kmh), DELTA);
        assertEquals("60 mph to km/h", 96.56064, UnitConverter.convert(60.0, mph, kmh), 0.01);
        assertEquals("1 knot to km/h", 1.852, UnitConverter.convert(1.0, knot, kmh), 0.01);
    }

    private static void testTime() {
        System.out.println("\n9. Testing Time Conversions:");
        Unit ms = findUnit(Category.TIME, "time_ms");
        Unit s = findUnit(Category.TIME, "time_s");
        Unit min = findUnit(Category.TIME, "time_min");
        Unit hr = findUnit(Category.TIME, "time_h");
        Unit day = findUnit(Category.TIME, "time_d");

        assertEquals("1000 ms to s", 1.0, UnitConverter.convert(1000.0, ms, s), DELTA);
        assertEquals("60 s to min", 1.0, UnitConverter.convert(60.0, s, min), DELTA);
        assertEquals("3600 s to hr", 1.0, UnitConverter.convert(3600.0, s, hr), DELTA);
        assertEquals("1 day to hr", 24.0, UnitConverter.convert(1.0, day, hr), DELTA);
        assertEquals("1 day to s", 86400.0, UnitConverter.convert(1.0, day, s), DELTA);
    }

    private static void testAbsoluteZeroValidation() {
        System.out.println("\n10. Testing Absolute Zero Boundaries:");
        Unit c = findUnit(Category.TEMPERATURE, "temp_c");
        Unit k = findUnit(Category.TEMPERATURE, "temp_k");
        Unit f = findUnit(Category.TEMPERATURE, "temp_f");
        Unit m = findUnit(Category.LENGTH, "len_m");

        assertTrue("-273.16 °C is below abs zero", UnitConverter.isBelowAbsoluteZero(-273.16, c));
        assertFalse("-273.15 °C is valid boundary", UnitConverter.isBelowAbsoluteZero(-273.15, c));
        assertFalse("25 °C is above abs zero", UnitConverter.isBelowAbsoluteZero(25.0, c));

        assertTrue("-0.01 K is below abs zero", UnitConverter.isBelowAbsoluteZero(-0.01, k));
        assertFalse("0 K is valid boundary", UnitConverter.isBelowAbsoluteZero(0.0, k));

        assertTrue("-460 °F is below abs zero", UnitConverter.isBelowAbsoluteZero(-460.0, f));
        assertFalse("-459.67 °F is valid boundary", UnitConverter.isBelowAbsoluteZero(-459.67, f));

        assertFalse("Length -10 is not temperature", UnitConverter.isBelowAbsoluteZero(-10.0, m));
    }

    private static void testIdentityConversions() {
        System.out.println("\n11. Testing Identity Conversions:");
        for (Category c : Category.values()) {
            for (Unit u : UnitConverter.getUnitsForCategory(c)) {
                assertEquals("Identity " + u.getId(), 42.0, UnitConverter.convert(42.0, u, u), 1e-9);
            }
        }
    }

    private static void testFormatting() {
        System.out.println("\n12. Testing Result Formatting:");
        assertEquals("Format whole number 100", "100", UnitConverter.formatResult(100.0));
        assertEquals("Format whole number 0", "0", UnitConverter.formatResult(0.0));
        assertEquals("Format negative whole -42", "-42", UnitConverter.formatResult(-42.0));
        assertEquals("Format decimal 1.25", "1.25", UnitConverter.formatResult(1.25));
        assertEquals("Format decimal 0.123456", "0.123456", UnitConverter.formatResult(0.123456));
        assertEquals("Format large number 1,000.5", "1,000.5", UnitConverter.formatResult(1000.5));
    }

    private static void testFormulas() {
        System.out.println("\n13. Testing Formula Generation:");
        Unit m = findUnit(Category.LENGTH, "len_m");
        Unit cm = findUnit(Category.LENGTH, "len_cm");
        Unit c = findUnit(Category.TEMPERATURE, "temp_c");
        Unit f = findUnit(Category.TEMPERATURE, "temp_f");

        assertEquals("Identity formula", "1 m = 1 m", UnitConverter.getFormulaExplanation(1.0, m, 1.0, m));
        assertEquals("Linear ratio formula", "1 m = 100 cm", UnitConverter.getFormulaExplanation(1.0, m, 100.0, cm));
        assertEquals("C to F formula", "Formula: (°C × 9/5) + 32 = °F", UnitConverter.getFormulaExplanation(0.0, c, 32.0, f));
        assertEquals("F to C formula", "Formula: (°F − 32) × 5/9 = °C", UnitConverter.getFormulaExplanation(32.0, f, 0.0, c));
    }
}
