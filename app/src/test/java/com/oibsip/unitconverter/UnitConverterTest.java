package com.oibsip.unitconverter;

import com.oibsip.unitconverter.converter.UnitConverter;
import com.oibsip.unitconverter.model.Category;
import com.oibsip.unitconverter.model.Unit;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Comprehensive Unit tests for UnitConverter calculation logic, unit registries,
 * boundary limits, formatting, and mathematical explanations.
 */
public class UnitConverterTest {

    private static final double DELTA = 1e-4;

    private Unit findUnit(Category category, String unitId) {
        List<Unit> units = UnitConverter.getUnitsForCategory(category);
        for (Unit u : units) {
            if (u.getId().equals(unitId)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Unit not found: " + unitId + " in " + category);
    }

    // ==========================================
    // 1. REGISTRY INTEGRITY TESTS
    // ==========================================

    @Test
    public void testRegistryHasAllCategoriesAndUnits() {
        Set<String> uniqueIds = new HashSet<>();
        int totalUnits = 0;

        for (Category category : Category.values()) {
            List<Unit> units = UnitConverter.getUnitsForCategory(category);
            assertNotNull("Category list should never be null: " + category, units);
            assertFalse("Category list should not be empty: " + category, units.isEmpty());

            for (Unit u : units) {
                totalUnits++;
                assertEquals("Unit category must match parent category", category, u.getCategory());
                assertNotNull("Unit ID must not be null", u.getId());
                assertNotNull("Unit Name must not be null", u.getName());
                assertNotNull("Unit Symbol must not be null", u.getSymbol());

                if (category != Category.TEMPERATURE) {
                    assertTrue("Linear factor to base must be positive", u.getFactorToBase() > 0.0);
                }

                assertTrue("Unit ID must be unique: " + u.getId(), uniqueIds.add(u.getId()));
            }
        }

        assertEquals("Total registered units across all categories should be 32", 32, totalUnits);
    }

    @Test
    public void testGetUnitsForNullCategoryReturnsEmptyList() {
        List<Unit> result = UnitConverter.getUnitsForCategory(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==========================================
    // 2. LINEAR CONVERSIONS ACROSS ALL CATEGORIES
    // ==========================================

    @Test
    public void testLengthConversions() {
        Unit mm = findUnit(Category.LENGTH, "len_mm");
        Unit cm = findUnit(Category.LENGTH, "len_cm");
        Unit m = findUnit(Category.LENGTH, "len_m");
        Unit km = findUnit(Category.LENGTH, "len_km");
        Unit in = findUnit(Category.LENGTH, "len_in");
        Unit ft = findUnit(Category.LENGTH, "len_ft");
        Unit yd = findUnit(Category.LENGTH, "len_yd");
        Unit mi = findUnit(Category.LENGTH, "len_mi");

        // Metric inter-conversions
        assertEquals(1.0, UnitConverter.convert(1000.0, mm, m), DELTA);
        assertEquals(100.0, UnitConverter.convert(1.0, m, cm), DELTA);
        assertEquals(1.0, UnitConverter.convert(100.0, cm, m), DELTA);
        assertEquals(1000.0, UnitConverter.convert(1.0, km, m), DELTA);
        assertEquals(0.001, UnitConverter.convert(1.0, m, km), DELTA);

        // Imperial & cross-system conversions
        assertEquals(0.0254, UnitConverter.convert(1.0, in, m), DELTA);
        assertEquals(12.0, UnitConverter.convert(1.0, ft, in), DELTA);
        assertEquals(3.0, UnitConverter.convert(1.0, yd, ft), DELTA);
        assertEquals(1760.0, UnitConverter.convert(1.0, mi, yd), DELTA);
        assertEquals(1609.344, UnitConverter.convert(1.0, mi, m), DELTA);
        assertEquals(5280.0, UnitConverter.convert(1.0, mi, ft), DELTA);
    }

    @Test
    public void testWeightConversions() {
        Unit mg = findUnit(Category.WEIGHT, "wt_mg");
        Unit g = findUnit(Category.WEIGHT, "wt_g");
        Unit kg = findUnit(Category.WEIGHT, "wt_kg");
        Unit ton = findUnit(Category.WEIGHT, "wt_ton");
        Unit oz = findUnit(Category.WEIGHT, "wt_oz");
        Unit lb = findUnit(Category.WEIGHT, "wt_lb");

        // Metric conversions
        assertEquals(1.0, UnitConverter.convert(1000000.0, mg, kg), DELTA);
        assertEquals(1000.0, UnitConverter.convert(1.0, kg, g), DELTA);
        assertEquals(1.0, UnitConverter.convert(1000.0, g, kg), DELTA);
        assertEquals(1000.0, UnitConverter.convert(1.0, ton, kg), DELTA);

        // Imperial & cross-system conversions
        assertEquals(16.0, UnitConverter.convert(1.0, lb, oz), DELTA);
        assertEquals(0.45359237, UnitConverter.convert(1.0, lb, kg), DELTA);
        assertEquals(2.20462, UnitConverter.convert(1.0, kg, lb), 0.001);
    }

    @Test
    public void testVolumeConversions() {
        Unit ml = findUnit(Category.VOLUME, "vol_ml");
        Unit l = findUnit(Category.VOLUME, "vol_l");
        Unit floz = findUnit(Category.VOLUME, "vol_floz");
        Unit cup = findUnit(Category.VOLUME, "vol_cup");
        Unit pt = findUnit(Category.VOLUME, "vol_pt");
        Unit gal = findUnit(Category.VOLUME, "vol_gal");

        // Metric
        assertEquals(1.0, UnitConverter.convert(1000.0, ml, l), DELTA);
        assertEquals(500.0, UnitConverter.convert(0.5, l, ml), DELTA);

        // US Customary
        assertEquals(3.785411784, UnitConverter.convert(1.0, gal, l), DELTA);
        assertEquals(8.0, UnitConverter.convert(1.0, gal, pt), DELTA);
        assertEquals(2.0, UnitConverter.convert(1.0, pt, cup), DELTA);
        assertEquals(8.0, UnitConverter.convert(1.0, cup, floz), DELTA);
        assertEquals(128.0, UnitConverter.convert(1.0, gal, floz), DELTA);
    }

    @Test
    public void testSpeedConversions() {
        Unit ms = findUnit(Category.SPEED, "spd_ms");
        Unit kmh = findUnit(Category.SPEED, "spd_kmh");
        Unit mph = findUnit(Category.SPEED, "spd_mph");
        Unit knot = findUnit(Category.SPEED, "spd_knot");

        // 36 km/h = 10 m/s
        assertEquals(10.0, UnitConverter.convert(36.0, kmh, ms), DELTA);
        assertEquals(36.0, UnitConverter.convert(10.0, ms, kmh), DELTA);

        // 60 mph = 96.56064 km/h
        assertEquals(96.56064, UnitConverter.convert(60.0, mph, kmh), 0.01);

        // 1 knot = 1.852 km/h
        assertEquals(1.852, UnitConverter.convert(1.0, knot, kmh), 0.001);
    }

    @Test
    public void testTimeConversions() {
        Unit ms = findUnit(Category.TIME, "time_ms");
        Unit s = findUnit(Category.TIME, "time_s");
        Unit min = findUnit(Category.TIME, "time_min");
        Unit hr = findUnit(Category.TIME, "time_h");
        Unit day = findUnit(Category.TIME, "time_d");

        assertEquals(1.0, UnitConverter.convert(1000.0, ms, s), DELTA);
        assertEquals(60.0, UnitConverter.convert(1.0, min, s), DELTA);
        assertEquals(1.0, UnitConverter.convert(60.0, s, min), DELTA);
        assertEquals(3600.0, UnitConverter.convert(1.0, hr, s), DELTA);
        assertEquals(24.0, UnitConverter.convert(1.0, day, hr), DELTA);
        assertEquals(86400.0, UnitConverter.convert(1.0, day, s), DELTA);
        assertEquals(7.0, UnitConverter.convert(168.0, hr, day), DELTA);
    }

    // ==========================================
    // 3. COMPLETE TEMPERATURE 6-WAY MATRIX
    // ==========================================

    @Test
    public void testTemperatureCompleteMatrix() {
        Unit c = findUnit(Category.TEMPERATURE, "temp_c");
        Unit f = findUnit(Category.TEMPERATURE, "temp_f");
        Unit k = findUnit(Category.TEMPERATURE, "temp_k");

        // 1. Celsius <-> Fahrenheit
        assertEquals(32.0, UnitConverter.convert(0.0, c, f), DELTA);
        assertEquals(212.0, UnitConverter.convert(100.0, c, f), DELTA);
        assertEquals(98.6, UnitConverter.convert(37.0, c, f), DELTA);
        assertEquals(-40.0, UnitConverter.convert(-40.0, c, f), DELTA);

        assertEquals(0.0, UnitConverter.convert(32.0, f, c), DELTA);
        assertEquals(100.0, UnitConverter.convert(212.0, f, c), DELTA);
        assertEquals(37.0, UnitConverter.convert(98.6, f, c), DELTA);
        assertEquals(-40.0, UnitConverter.convert(-40.0, f, c), DELTA);

        // 2. Celsius <-> Kelvin
        assertEquals(273.15, UnitConverter.convert(0.0, c, k), DELTA);
        assertEquals(373.15, UnitConverter.convert(100.0, c, k), DELTA);
        assertEquals(0.0, UnitConverter.convert(-273.15, c, k), DELTA);

        assertEquals(0.0, UnitConverter.convert(273.15, k, c), DELTA);
        assertEquals(100.0, UnitConverter.convert(373.15, k, c), DELTA);
        assertEquals(-273.15, UnitConverter.convert(0.0, k, c), DELTA);

        // 3. Fahrenheit <-> Kelvin
        assertEquals(273.15, UnitConverter.convert(32.0, f, k), DELTA);
        assertEquals(373.15, UnitConverter.convert(212.0, f, k), DELTA);
        assertEquals(0.0, UnitConverter.convert(-459.67, f, k), DELTA);

        assertEquals(32.0, UnitConverter.convert(273.15, k, f), DELTA);
        assertEquals(212.0, UnitConverter.convert(373.15, k, f), DELTA);
        assertEquals(-459.67, UnitConverter.convert(0.0, k, f), DELTA);
    }

    // ==========================================
    // 4. IDENTITY CONVERSIONS
    // ==========================================

    @Test
    public void testIdentityConversions() {
        for (Category category : Category.values()) {
            for (Unit u : UnitConverter.getUnitsForCategory(category)) {
                assertEquals("Identity conversion must preserve value",
                        123.456, UnitConverter.convert(123.456, u, u), 1e-9);
            }
        }
    }

    // ==========================================
    // 5. ABSOLUTE ZERO PHYSICAL LIMITS
    // ==========================================

    @Test
    public void testAbsoluteZeroBoundaries() {
        Unit c = findUnit(Category.TEMPERATURE, "temp_c");
        Unit k = findUnit(Category.TEMPERATURE, "temp_k");
        Unit f = findUnit(Category.TEMPERATURE, "temp_f");
        Unit m = findUnit(Category.LENGTH, "len_m");

        // Celsius
        assertTrue(UnitConverter.isBelowAbsoluteZero(-273.150001, c));
        assertFalse(UnitConverter.isBelowAbsoluteZero(-273.15, c));
        assertFalse(UnitConverter.isBelowAbsoluteZero(-273.149999, c));
        assertFalse(UnitConverter.isBelowAbsoluteZero(0.0, c));

        // Kelvin
        assertTrue(UnitConverter.isBelowAbsoluteZero(-0.00001, k));
        assertFalse(UnitConverter.isBelowAbsoluteZero(0.0, k));
        assertFalse(UnitConverter.isBelowAbsoluteZero(0.00001, k));
        assertFalse(UnitConverter.isBelowAbsoluteZero(300.0, k));

        // Fahrenheit
        assertTrue(UnitConverter.isBelowAbsoluteZero(-459.670001, f));
        assertFalse(UnitConverter.isBelowAbsoluteZero(-459.67, f));
        assertFalse(UnitConverter.isBelowAbsoluteZero(-459.669999, f));
        assertFalse(UnitConverter.isBelowAbsoluteZero(32.0, f));

        // Non-temperature units must never trigger absolute zero violation
        assertFalse(UnitConverter.isBelowAbsoluteZero(-9999.0, m));
    }

    // ==========================================
    // 6. ZERO, NEGATIVE, AND SCALING VALUES
    // ==========================================

    @Test
    public void testZeroAndNegativeLinearConversions() {
        Unit m = findUnit(Category.LENGTH, "len_m");
        Unit cm = findUnit(Category.LENGTH, "len_cm");

        // Zero value
        assertEquals(0.0, UnitConverter.convert(0.0, m, cm), DELTA);

        // Negative value
        assertEquals(-5000.0, UnitConverter.convert(-50.0, m, cm), DELTA);

        // Large number
        assertEquals(1000000.0, UnitConverter.convert(1000000000.0, mm_unit(), m), DELTA);
    }

    private Unit mm_unit() {
        return findUnit(Category.LENGTH, "len_mm");
    }

    // ==========================================
    // 7. FORMATTING LOGIC
    // ==========================================

    @Test
    public void testResultFormatting() {
        // Whole numbers formatted as integers without trailing zeroes
        assertEquals("100", UnitConverter.formatResult(100.0));
        assertEquals("0", UnitConverter.formatResult(0.0));
        assertEquals("-42", UnitConverter.formatResult(-42.0));
        assertEquals("1000000", UnitConverter.formatResult(1000000.0));

        // Decimals formatted cleanly with US thousands grouping and up to 6 decimals
        assertEquals("1.25", UnitConverter.formatResult(1.25));
        assertEquals("0.123456", UnitConverter.formatResult(0.123456));
        assertEquals("1,000.5", UnitConverter.formatResult(1000.5));
    }

    // ==========================================
    // 8. FORMULA EXPLANATIONS
    // ==========================================

    @Test
    public void testFormulaExplanations() {
        Unit m = findUnit(Category.LENGTH, "len_m");
        Unit cm = findUnit(Category.LENGTH, "len_cm");
        Unit c = findUnit(Category.TEMPERATURE, "temp_c");
        Unit f = findUnit(Category.TEMPERATURE, "temp_f");
        Unit k = findUnit(Category.TEMPERATURE, "temp_k");

        // Identity
        assertEquals("1 m = 1 m", UnitConverter.getFormulaExplanation(1.0, m, 1.0, m));

        // Linear ratio
        assertEquals("1 m = 100 cm", UnitConverter.getFormulaExplanation(1.0, m, 100.0, cm));

        // All 6 Temperature formulas
        assertEquals("Formula: (°C × 9/5) + 32 = °F", UnitConverter.getFormulaExplanation(0.0, c, 32.0, f));
        assertEquals("Formula: (°F − 32) × 5/9 = °C", UnitConverter.getFormulaExplanation(32.0, f, 0.0, c));
        assertEquals("Formula: °C + 273.15 = K", UnitConverter.getFormulaExplanation(0.0, c, 273.15, k));
        assertEquals("Formula: K − 273.15 = °C", UnitConverter.getFormulaExplanation(273.15, k, 0.0, c));
        assertEquals("Formula: (°F − 32) × 5/9 + 273.15 = K", UnitConverter.getFormulaExplanation(32.0, f, 273.15, k));
        assertEquals("Formula: (K − 273.15) × 9/5 + 32 = °F", UnitConverter.getFormulaExplanation(273.15, k, 32.0, f));
    }
}
