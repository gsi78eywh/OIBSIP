package com.oibsip.unitconverter;

import com.oibsip.unitconverter.converter.UnitConverter;
import com.oibsip.unitconverter.model.Category;
import com.oibsip.unitconverter.model.Unit;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for UnitConverter calculation logic, boundary conditions,
 * and formula generation.
 */
public class UnitConverterTest {

    private static final double DELTA = 1e-5;

    private Unit findUnit(Category category, String unitId) {
        List<Unit> units = UnitConverter.getUnitsForCategory(category);
        for (Unit u : units) {
            if (u.getId().equals(unitId)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Unit not found: " + unitId);
    }

    @Test
    public void testLengthConversions() {
        Unit cm = findUnit(Category.LENGTH, "len_cm");
        Unit m = findUnit(Category.LENGTH, "len_m");
        Unit km = findUnit(Category.LENGTH, "len_km");
        Unit in = findUnit(Category.LENGTH, "len_in");
        Unit mi = findUnit(Category.LENGTH, "len_mi");

        // 100 cm = 1 m
        assertEquals(1.0, UnitConverter.convert(100.0, cm, m), DELTA);
        // 1 km = 1000 m
        assertEquals(1000.0, UnitConverter.convert(1.0, km, m), DELTA);
        // 1 m = 100 cm
        assertEquals(100.0, UnitConverter.convert(1.0, m, cm), DELTA);
        // 1 inch = 0.0254 m
        assertEquals(0.0254, UnitConverter.convert(1.0, in, m), DELTA);
        // 1 mile = 1609.344 m
        assertEquals(1609.344, UnitConverter.convert(1.0, mi, m), DELTA);
    }

    @Test
    public void testWeightConversions() {
        Unit g = findUnit(Category.WEIGHT, "wt_g");
        Unit kg = findUnit(Category.WEIGHT, "wt_kg");
        Unit mg = findUnit(Category.WEIGHT, "wt_mg");
        Unit ton = findUnit(Category.WEIGHT, "wt_ton");
        Unit lb = findUnit(Category.WEIGHT, "wt_lb");

        // 1 kg = 1000 g
        assertEquals(1000.0, UnitConverter.convert(1.0, kg, g), DELTA);
        // 1000 mg = 1 g
        assertEquals(1.0, UnitConverter.convert(1000.0, mg, g), DELTA);
        // 1 ton = 1000 kg
        assertEquals(1000.0, UnitConverter.convert(1.0, ton, kg), DELTA);
        // 1 lb = 0.45359237 kg
        assertEquals(0.45359237, UnitConverter.convert(1.0, lb, kg), DELTA);
    }

    @Test
    public void testTemperatureConversions() {
        Unit celsius = findUnit(Category.TEMPERATURE, "temp_c");
        Unit fahrenheit = findUnit(Category.TEMPERATURE, "temp_f");
        Unit kelvin = findUnit(Category.TEMPERATURE, "temp_k");

        // 0 °C = 32 °F
        assertEquals(32.0, UnitConverter.convert(0.0, celsius, fahrenheit), DELTA);
        // 100 °C = 212 °F
        assertEquals(212.0, UnitConverter.convert(100.0, celsius, fahrenheit), DELTA);
        // -40 °C = -40 °F (equal point)
        assertEquals(-40.0, UnitConverter.convert(-40.0, celsius, fahrenheit), DELTA);
        // 0 °C = 273.15 K
        assertEquals(273.15, UnitConverter.convert(0.0, celsius, kelvin), DELTA);
        // 373.15 K = 100 °C
        assertEquals(100.0, UnitConverter.convert(373.15, kelvin, celsius), DELTA);
        // 212 °F = 373.15 K
        assertEquals(373.15, UnitConverter.convert(212.0, fahrenheit, kelvin), DELTA);
    }

    @Test
    public void testVolumeConversions() {
        Unit ml = findUnit(Category.VOLUME, "vol_ml");
        Unit l = findUnit(Category.VOLUME, "vol_l");
        Unit gal = findUnit(Category.VOLUME, "vol_gal");

        // 1000 mL = 1 L
        assertEquals(1.0, UnitConverter.convert(1000.0, ml, l), DELTA);
        // 1 gal = 3.785411784 L
        assertEquals(3.785411784, UnitConverter.convert(1.0, gal, l), DELTA);
    }

    @Test
    public void testSpeedConversions() {
        Unit ms = findUnit(Category.SPEED, "spd_ms");
        Unit kmh = findUnit(Category.SPEED, "spd_kmh");
        Unit mph = findUnit(Category.SPEED, "spd_mph");

        // 36 km/h = 10 m/s
        assertEquals(10.0, UnitConverter.convert(36.0, kmh, ms), DELTA);
        // 60 mph in km/h is ~96.56064 km/h
        assertEquals(96.56064, UnitConverter.convert(60.0, mph, kmh), 0.01);
    }

    @Test
    public void testTimeConversions() {
        Unit s = findUnit(Category.TIME, "time_s");
        Unit min = findUnit(Category.TIME, "time_min");
        Unit hr = findUnit(Category.TIME, "time_h");
        Unit day = findUnit(Category.TIME, "time_d");

        // 60 s = 1 min
        assertEquals(1.0, UnitConverter.convert(60.0, s, min), DELTA);
        // 1 hr = 3600 s
        assertEquals(3600.0, UnitConverter.convert(1.0, hr, s), DELTA);
        // 1 day = 24 hr
        assertEquals(24.0, UnitConverter.convert(1.0, day, hr), DELTA);
    }

    @Test
    public void testAbsoluteZeroValidation() {
        Unit celsius = findUnit(Category.TEMPERATURE, "temp_c");
        Unit kelvin = findUnit(Category.TEMPERATURE, "temp_k");
        Unit fahrenheit = findUnit(Category.TEMPERATURE, "temp_f");
        Unit meter = findUnit(Category.LENGTH, "len_m");

        assertTrue(UnitConverter.isBelowAbsoluteZero(-273.16, celsius));
        assertFalse(UnitConverter.isBelowAbsoluteZero(-273.15, celsius));
        assertFalse(UnitConverter.isBelowAbsoluteZero(25.0, celsius));

        assertTrue(UnitConverter.isBelowAbsoluteZero(-0.01, kelvin));
        assertFalse(UnitConverter.isBelowAbsoluteZero(0.0, kelvin));

        assertTrue(UnitConverter.isBelowAbsoluteZero(-460.0, fahrenheit));
        assertFalse(UnitConverter.isBelowAbsoluteZero(-459.67, fahrenheit));

        assertFalse(UnitConverter.isBelowAbsoluteZero(-10.0, meter)); // Non-temperature unit
    }

    @Test
    public void testFormattingAndFormulas() {
        assertEquals("100", UnitConverter.formatResult(100.0));
        assertEquals("0", UnitConverter.formatResult(0.0));
        assertEquals("1.25", UnitConverter.formatResult(1.25));

        Unit m = findUnit(Category.LENGTH, "len_m");
        Unit cm = findUnit(Category.LENGTH, "len_cm");
        String formula = UnitConverter.getFormulaExplanation(1.0, m, 100.0, cm);
        assertNotNull(formula);
        assertTrue(formula.contains("1 m = 100 cm"));
    }
}
