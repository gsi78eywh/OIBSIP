package com.oibsip.unitconverter.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the Unit data model, display label formatting, and equality contracts.
 */
public class UnitTest {

    @Test
    public void testUnitGetters() {
        Unit unit = new Unit("len_m", "Meter", "m", Category.LENGTH, 1.0);

        assertEquals("len_m", unit.getId());
        assertEquals("Meter", unit.getName());
        assertEquals("m", unit.getSymbol());
        assertEquals(Category.LENGTH, unit.getCategory());
        assertEquals(1.0, unit.getFactorToBase(), 1e-9);
    }

    @Test
    public void testDisplayLabelWithSymbol() {
        Unit unit = new Unit("len_km", "Kilometer", "km", Category.LENGTH, 1000.0);
        assertEquals("Kilometer (km)", unit.getDisplayLabel());
        assertEquals("Kilometer (km)", unit.toString());
    }

    @Test
    public void testDisplayLabelWithoutSymbol() {
        Unit unitWithNull = new Unit("custom_1", "Custom Unit", null, Category.LENGTH, 1.0);
        assertEquals("Custom Unit", unitWithNull.getDisplayLabel());
        assertEquals("Custom Unit", unitWithNull.toString());

        Unit unitWithEmpty = new Unit("custom_2", "Custom Unit", "", Category.LENGTH, 1.0);
        assertEquals("Custom Unit", unitWithEmpty.getDisplayLabel());

        Unit unitWithSpaces = new Unit("custom_3", "Custom Unit", "   ", Category.LENGTH, 1.0);
        assertEquals("Custom Unit", unitWithSpaces.getDisplayLabel());
    }

    @Test
    public void testEqualsAndHashCodeContract() {
        Unit unit1 = new Unit("len_m", "Meter", "m", Category.LENGTH, 1.0);
        Unit unit1Duplicate = new Unit("len_m", "Metre", "m", Category.LENGTH, 1.0);
        Unit unit2 = new Unit("len_cm", "Centimeter", "cm", Category.LENGTH, 0.01);

        // Reflexivity
        assertTrue("An object must equal itself", unit1.equals(unit1));

        // Symmetry & Equality based on ID
        assertTrue("Units with identical IDs should be equal", unit1.equals(unit1Duplicate));
        assertTrue("Equality must be symmetric", unit1Duplicate.equals(unit1));
        assertEquals("Equal units must have equal hash codes", unit1.hashCode(), unit1Duplicate.hashCode());

        // Inequality
        assertFalse("Units with different IDs should not be equal", unit1.equals(unit2));
        assertNotEquals("Different units usually have different hash codes", unit1.hashCode(), unit2.hashCode());

        // Null comparison
        assertFalse("An object must not equal null", unit1.equals(null));

        // Type comparison
        assertFalse("An object must not equal an object of different type", unit1.equals("len_m"));
    }
}
